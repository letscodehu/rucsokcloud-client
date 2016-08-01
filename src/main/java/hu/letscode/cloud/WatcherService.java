package hu.letscode.cloud;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import static java.nio.file.StandardWatchEventKinds.*;


public class WatcherService {

	private WatchService watcher;
	private Path directory;
	private EventListener listener;


	public WatcherService(Path directory, EventListener listener) throws IOException {
		this.directory = directory;
		this.listener = listener;
		watcher = FileSystems.getDefault().newWatchService();
	}

	public void watch() {
		try {

			WatchKey key = directory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

			while(true) {
				for (WatchEvent<?> event : key.pollEvents()) {
					Path filename = (Path) event.context();
					
					System.out.println(filename.toString());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
