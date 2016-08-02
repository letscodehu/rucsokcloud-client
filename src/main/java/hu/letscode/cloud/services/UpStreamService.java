package hu.letscode.cloud.services;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.nio.file.Watchable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import hu.letscode.cloud.EventListener;
import hu.letscode.cloud.jobs.UpsyncJob;
import hu.letscode.cloud.model.FileModification;

public class UpStreamService extends Thread {

	private static final Logger logger = Logger.getLogger("cloud-client");
	private BlockingQueue<FileModification> queue;
	private List<String> keys = new ArrayList<String>();
	private MessageDigest md5;
	private String serverUrl;

	public UpStreamService(BlockingQueue<FileModification> queue, String serverUrl) {
		this.queue = queue;
		this.serverUrl = serverUrl;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		logger.info("consume started");
		while(true) {
			try {
				Thread.sleep(20000);
				FileModification mod = queue.take();
				logger.info("took an item from the requestqueue, size: " + mod.getBatchSize());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/** public void addToBatch(WatchEvent<?> event) {
		md5.update(StandardCharsets.UTF_8.encode(event.context().toString()));
		String hash = String.format("%032x", new BigInteger(1, md5.digest()));
		if (!batch.containsKey(hash)) {
			System.out.println("Adding to batch : " + hash);
			batch.put(hash, event);
			keys.add(hash);
		}
	} */

}
