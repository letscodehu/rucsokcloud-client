package hu.letscode.cloud.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import hu.letscode.cloud.EventListener;
import hu.letscode.cloud.jobs.UpsyncJob;

import static java.nio.file.StandardWatchEventKinds.*;


public class WatcherService extends Thread {

	private WatchService watcher;
	private Path directory;
	private BlockingQueue<String> fileQueue;
	private static final Logger logger = Logger.getLogger("cloud-client");

	public WatcherService(Path directory, BlockingQueue<String> fileQueue) throws IOException {
		this.directory = directory;
		this.fileQueue = fileQueue;
	}
	
	public void run() {
		while(true) {
			try {
				registerRecursive(directory);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void registerRecursive(final Path root) throws IOException {
	    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
	    	 @Override
	    	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	    	        throws IOException
	    	    {	
	    		 	try {
						Thread.sleep(200);
						
						fileQueue.put(root.resolve(file).toString());
						logger.info("Putting path on the queue : " + file.hashCode());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    	        return FileVisitResult.CONTINUE;
	    	    }
	    });
	}

	

}
