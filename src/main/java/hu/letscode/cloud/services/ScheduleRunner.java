package hu.letscode.cloud.services;

import java.nio.file.WatchEvent;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import hu.letscode.cloud.jobs.UpsyncJob;

public class ScheduleRunner implements Runnable {
	public ScheduledExecutorService executor;
	public List<String> keys;
	public HashMap<String, WatchEvent> batch;
	public MessageDigest md5;
	public String serverUrl;

	public ScheduleRunner(List<String> keys, HashMap<String, WatchEvent> batch) {
		this.keys = keys;
		this.batch = batch;
		System.out.println("schedulerunner started");
	}
	
	public void run() {
		System.out.println("run start");
		if (keys.size() > 0) {
			String hash = keys.remove(keys.size() - 1);
			WatchEvent event = batch.remove(hash);
			
		}
}

}