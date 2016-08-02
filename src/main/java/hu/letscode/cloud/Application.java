package hu.letscode.cloud;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import hu.letscode.cloud.services.DownStreamService;
import hu.letscode.cloud.services.UpStreamService;
import hu.letscode.cloud.services.WatcherService;

public class Application {
	
	private DownStreamService downStreamService;
	private WatcherService watcherService;
	private static final Logger logger = Logger.getLogger("cloud-client");
	
	public Application(WatcherService watcherService, DownStreamService downStreamService) {
		this.watcherService = watcherService;
		this.downStreamService = downStreamService;
	}
	
	public void start() {
		logger.info("WatchService started");
		watcherService.watch();
	}
	
	
	public static void main(String[] args) {
		if (args.length < 2) {
			showUsage();
			System.exit(1);
		}
		Path dir = FileSystems.getDefault().getPath(args[0]);
		try {
			Application app = new Application(
					new WatcherService(dir, new UpStreamService(Executors.newScheduledThreadPool(10), args[1])), 
							new DownStreamService());
			app.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void showUsage() {
		System.out.println("Usage: \n" + 
	"syncservice <directory> <server-url>");
	}
}
