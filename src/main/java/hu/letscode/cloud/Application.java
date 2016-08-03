package hu.letscode.cloud;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.Proxy.Type;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import hu.letscode.cloud.jobs.UpsyncJob;
import hu.letscode.cloud.model.FileModification;
import hu.letscode.cloud.services.BatchingService;
import hu.letscode.cloud.services.DownStreamService;
import hu.letscode.cloud.services.UpStreamService;
import hu.letscode.cloud.services.WatcherService;

public class Application {
	
	private DownStreamService downStreamService;
	private WatcherService watcherService;
	private static final Logger logger = Logger.getLogger("cloud-client");
	private static volatile BlockingQueue<String> fileQueue = new ArrayBlockingQueue<String>(10);
	private static volatile BlockingQueue<FileModification> requestQueue = new ArrayBlockingQueue<FileModification>(4);
	private UpStreamService upStreamService;
	private BatchingService batchingService;
	
	public Application(WatcherService watcherService, UpStreamService upStreamService,  DownStreamService downStreamService, BatchingService batchingService) {
		this.watcherService = watcherService;
		this.batchingService = batchingService;
		this.upStreamService = upStreamService;
		this.downStreamService = downStreamService;
	}
	
	public void start() {
		logger.info("Application started");
		watcherService.start();
		batchingService.start();
		upStreamService.start();
	}
	
	
	public static void main(String[] args) {
		if (args.length < 2) {
			showUsage();
			System.exit(1);
		}
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        HttpHost host = new HttpHost("localhost",8888, "http");
        
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                // .setProxy(host)
                .build();
		Path dir = FileSystems.getDefault().getPath(args[0]);
		Application app = new Application(
				new WatcherService(dir, fileQueue), new UpStreamService(requestQueue, new FileModificationTransformer(args[1]), httpclient), 
						new DownStreamService(), new BatchingService(fileQueue, requestQueue, dir));
		app.start();
	}

	private static void showUsage() {
		System.out.println("Usage: \n" + 
	"syncservice <directory> <server-url>");
	}
}
