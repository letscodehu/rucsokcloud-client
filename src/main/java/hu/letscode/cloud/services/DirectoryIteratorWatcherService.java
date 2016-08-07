package hu.letscode.cloud.services;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import hu.letscode.cloud.events.FileVisitExceptionEvent;
import hu.letscode.cloud.model.FileModel;

@Component
public class DirectoryIteratorWatcherService extends Thread implements WatcherService {

	private WatchService watcher;
	private Path directory;
	private ConcurrentMap<String, FileModel> fileMap;
	private BlockingQueue<String> fileQueue;
	private static final Logger logger = Logger.getLogger("cloud-client");
	private MessageDigest md5;
	private ApplicationEventPublisher publisher;

	
	@Autowired
	public DirectoryIteratorWatcherService(BlockingQueue<String> fileQueue, Path directory, ConcurrentMap<String, FileModel> fileMap) {
		this.directory = directory;
		this.fileMap = fileMap;
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


	   public void setApplicationEventPublisher
	              (ApplicationEventPublisher publisher){
		   System.err.println("befut" + publisher.toString());
	      this.publisher = publisher;
	   }
	
	private boolean isExists(Path file, BasicFileAttributes attrs) {
		return fileMap.containsKey(generateHash(file));
	}
	
	private boolean isRecent(Path file, BasicFileAttributes attrs) {
		return fileMap.get(generateHash(file)).getLastModification() < attrs.size();
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
	    	        publisher.publishEvent(new FileVisitExceptionEvent(this, exc));
	    	        return FileVisitResult.CONTINUE;
	    	    }
	    });
	}

	
	private void add(Path file, BasicFileAttributes attrs) throws InterruptedException {
		if (!isExists(file, attrs) || isRecent(file, attrs)) {
			logger.info("Putting path on the queue " + directory.resolve(file).toString());
			fileQueue.put(directory.resolve(file).toString());	
			fileMap.put(generateHash(file), new FileModel(attrs.lastModifiedTime().toMillis()));
		}
	}

	private String generateHash(Path file) {
		md5.update(StandardCharsets.UTF_8.encode(file.toString()));
		return String.format("%032x", new BigInteger(1, md5.digest()));
	}
	
}
