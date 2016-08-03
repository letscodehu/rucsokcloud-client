package hu.letscode.cloud.services;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
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

public class UpStreamService implements EventListener {

	private static final int schedule = 5000;
	private static final int delay = 5000;
	private static final Logger logger = Logger.getLogger("cloud-client");
	private ScheduledExecutorService executor;
	private List<String> keys = new ArrayList<String>();
	private HashMap<String, WatchEvent> batch = new HashMap<String, WatchEvent>(); 
	private MessageDigest md5;
	private String serverUrl;

	public UpStreamService(ScheduledExecutorService executor, String serverUrl) {
		this.executor = executor;
		this.serverUrl = serverUrl;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public void consume() {
		executor.scheduleAtFixedRate(new UpsyncJob(batch, keys, serverUrl), 1, 1, TimeUnit.SECONDS);
	}
	
	public void addToBatch(WatchEvent<?> event) {
		md5.update(StandardCharsets.UTF_8.encode(event.context().toString()));
		String hash = String.format("%032x", new BigInteger(1, md5.digest()));
		if (!batch.containsKey(hash)) {
			System.out.println("Adding to batch : " + hash);
			batch.put(hash, event);
			keys.add(hash);
		}
	}


	public void onChange(WatchEvent<?> event) {
		logger.info("CHANGE : " + event.context().toString());
		addToBatch(event);
	}

	public void onCreate(WatchEvent<?> event) {
		logger.info("CREATE : " +event.context().toString());
		addToBatch(event);
	}

	public void onCreateDirectory(WatchEvent<?> event) {
		logger.info("CREATE DIRECTORY : " +event.context().toString());
		addToBatch(event);
	}

	public void onDelete(WatchEvent<?> event) {
		logger.info("DELETE : " + event.context().toString());
		addToBatch(event);
	}

}
