package hu.letscode.cloud;

import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import hu.letscode.cloud.config.ApplicationConfig;
import hu.letscode.cloud.services.BatchingService;
import hu.letscode.cloud.services.DownStreamService;
import hu.letscode.cloud.services.GUIService;
import hu.letscode.cloud.services.UpStreamService;
import hu.letscode.cloud.services.WatcherService;

public class Application {
	
	private DownStreamService downStreamService;
	private WatcherService watcherService;
	private static final Logger logger = Logger.getLogger("cloud-client");

	private UpStreamService upStreamService;
	private BatchingService batchingService;
	private GUIService guiService;
	
	public Application(GUIService guiService, WatcherService watcherService, UpStreamService upStreamService,  DownStreamService downStreamService, BatchingService batchingService) {
		this.watcherService = watcherService;
		this.guiService = guiService;
		this.batchingService = batchingService;
		this.upStreamService = upStreamService;
		this.downStreamService = downStreamService;
	}
	
	public void start() {
		logger.info("Application started");
		guiService.start();
		watcherService.start();
		batchingService.start();
		upStreamService.start();
	}
	
	
	public static void main(String[] args) {
		if (args.length < 2) {
			showUsage();
			System.exit(1);
		}
		System.getProperties().put("upload.serverUrl", args[1]);
		System.getProperties().put("watch.directory", args[0]);
		ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class); 
		Application app = context.getBean(Application.class);
		app.start();
	}

	private static void showUsage() {
		System.out.println("Usage: \n" + 
	"syncservice <directory> <server-url>");
	}
}
