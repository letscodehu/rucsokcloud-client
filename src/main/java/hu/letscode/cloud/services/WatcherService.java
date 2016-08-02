package hu.letscode.cloud.services;

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
import java.util.logging.Logger;

import hu.letscode.cloud.EventListener;

import static java.nio.file.StandardWatchEventKinds.*;


public class WatcherService {

	private WatchService watcher;
	private Path directory;
	private EventListener listener;
	private static final Logger logger = Logger.getLogger("cloud-client");

	public WatcherService(Path directory, UpStreamService listener) throws IOException {
		this.directory = directory;
		this.listener = listener;
		watcher = FileSystems.getDefault().newWatchService();
		listener.consume();
	}
	
	private void registerRecursive(final Path root) throws IOException {
	    // register all subfolders
	    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
	        @Override
	        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	        	
	            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	            logger.info(dir.toAbsolutePath().toString() + " directory registered");
	            return FileVisitResult.CONTINUE;
	        }
	    });
	}

	public void watch() {
		try {
			registerRecursive(directory);
			WatchKey key = watcher.take();

			while(true) {
				for (WatchEvent<?> event : key.pollEvents()) {
					Path file = (Path) event.context();
					if (Files.isDirectory(file) && !event.kind().equals(ENTRY_DELETE)) {
						registerRecursive(file);
					}
					throwEvents(event);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void throwEvents(WatchEvent<?> event) {
		logger.info(event.kind().name() + " : " + event.context().toString());
		if (event.kind().equals(ENTRY_CREATE) && Files.isDirectory((Path) event.context())) {
			listener.onCreateDirectory(event);
		} else if (event.kind().equals(ENTRY_CREATE)) {
			listener.onCreate(event);
		} else if (event.kind().equals(ENTRY_MODIFY)) {
			listener.onChange(event);
		} else {
			listener.onDelete(event);
		}
	}

}
