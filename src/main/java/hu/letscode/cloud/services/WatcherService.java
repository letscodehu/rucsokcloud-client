package hu.letscode.cloud.services;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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
	private Map<String, Long> readFiles = new HashMap<String, Long>();
	private BlockingQueue<String> fileQueue;
	private static final Logger logger = Logger.getLogger("cloud-client");
	private MessageDigest md5;

	public WatcherService(Path directory, BlockingQueue<String> fileQueue) {
		this.directory = directory;
		this.fileQueue = fileQueue;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
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
	
	private boolean isExists(Path file, BasicFileAttributes attrs) {
		return readFiles.containsKey(generateHash(file));
	}
	
	private boolean isRecent(Path file, BasicFileAttributes attrs) {
		return readFiles.get(generateHash(file)).longValue() < attrs.size();
	}
	
	private void registerRecursive(final Path root) throws IOException {
	    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
	    	 @Override
	    	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	    	        throws IOException
	    	    {	
	    		 	try {
						Thread.sleep(100);
						add(root.resolve(file), attrs);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    	        return FileVisitResult.CONTINUE;
	    	    }
	    	 
	    	 @Override
	    	    public FileVisitResult visitFileFailed(Path file,
	    	                                       IOException exc) {
	    	        System.err.println("csecsrefutott a visit!");
	    	        return FileVisitResult.CONTINUE;
	    	    }
	    });
	}

	
	private void add(Path file, BasicFileAttributes attrs) throws InterruptedException {
		if (!isExists(file, attrs) || isRecent(file, attrs)) {
			logger.info("Putting path on the queue " + directory.resolve(file).toString());
			fileQueue.put(directory.resolve(file).toString());	
			readFiles.put(generateHash(file), attrs.lastModifiedTime().toMillis());
		}
	}

	private String generateHash(Path file) {
		md5.update(StandardCharsets.UTF_8.encode(file.toString()));
		return String.format("%032x", new BigInteger(1, md5.digest()));
	}
	
}
