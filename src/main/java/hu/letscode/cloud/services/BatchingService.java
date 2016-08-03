package hu.letscode.cloud.services;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;

import hu.letscode.cloud.model.FileModification;

public class BatchingService extends Thread {

	private BlockingQueue<String> fileQueue;
	private BlockingQueue<FileModification> requestQueue;
	private FileModification currentBatch;
	private Path root;
	private static final Logger logger = Logger.getLogger("cloud-client");

	public BatchingService(BlockingQueue<String> fileQueue, BlockingQueue<FileModification> requestQueue, Path root) {
		this.root = root;
		this.fileQueue = fileQueue;
		this.requestQueue = requestQueue;
		this.currentBatch = new FileModification();
	}

	
	public void run() {
		logger.info("Batchingservice started");
		File currentFile = null;
		try {
			logger.info("waiting for files...");
			currentFile = new File(fileQueue.take());
			logger.info("incoming filehash : " + currentFile.hashCode());
			while (true) {
				Thread.sleep(100);
				if (currentBatch.isItFits(currentFile)) {
					logger.info("adding file to request");
					currentBatch.add(currentFile);
					currentFile = new File(fileQueue.take());
				} else {
					requestQueue.put(currentBatch);
					logger.info("adding request to queue " + requestQueue.size());
					currentBatch = new FileModification();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
