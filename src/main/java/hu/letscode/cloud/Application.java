package hu.letscode.cloud;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Application {
	
	private DownStreamService downStreamService;
	private WatcherService watcherService;
	
	
	public Application(WatcherService watcherService, DownStreamService downStreamService) {
		this.watcherService = watcherService;
		this.downStreamService = downStreamService;
	}
	
	
	public static void main(String[] args) {
		if (args.length < 2) {
			showUsage();
		}
		Path dir = FileSystems.getDefault().getPath(args[0]);
		Application app = new Application(
				new WatcherService(dir, new UpStreamService()), 
						new DownStreamService());
	}

	private static void showUsage() {
		System.out.println("Usage: \n" + 
	"syncservice <directory> <server-url>");
	}
}
