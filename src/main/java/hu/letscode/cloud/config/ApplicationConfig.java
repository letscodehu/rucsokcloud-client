package hu.letscode.cloud.config;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import javax.swing.ImageIcon;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import hu.letscode.cloud.Application;
import hu.letscode.cloud.FileModificationTransformer;
import hu.letscode.cloud.model.FileModel;
import hu.letscode.cloud.model.FileModification;
import hu.letscode.cloud.services.BatchingService;
import hu.letscode.cloud.services.DownStreamService;
import hu.letscode.cloud.services.GUIService;
import hu.letscode.cloud.services.UpStreamService;
import hu.letscode.cloud.services.WatcherService;

@Configuration
public class ApplicationConfig {

	private static volatile BlockingQueue<String> fileQueue = new ArrayBlockingQueue<String>(10);
	private static volatile BlockingQueue<FileModification> requestQueue = new ArrayBlockingQueue<FileModification>(4);
	
	@Value("${watch.directory}")
	private String path;
	
	@Value("${upload.serverUrl}")
	private String serverUrl;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Bean
	public Application application() {
		return new Application(
				new GUIService(trayIcon()),
				watcherService(), 
				upstreamService(), 
						new DownStreamService(), batchingService());
	}

	private BatchingService batchingService() {
		return new BatchingService(fileQueue, requestQueue);
	}

	private WatcherService watcherService() {
		return new WatcherService(fileQueue, rootDirectory(), fileMap());
	}

	private UpStreamService upstreamService() {
		UpStreamService service = new UpStreamService(requestQueue, new FileModificationTransformer(serverUrl), client());
		service.setApplicationEventPublisher(publisher);
		return service;
	}

	@Bean
	public TrayIcon trayIcon() {
		Image image = new ImageIcon("images/robin.jpg").getImage();
		TrayIcon trayIcon = new TrayIcon(image);
		trayIcon.setToolTip("Rücsök cloud");
		trayIcon.setPopupMenu(popupMenu());
		return trayIcon;
	}
	
	@Bean
	public DB dataSource() {
		DB db = DBMaker.fileDB("cloud.db").closeOnJvmShutdown().make();
		return db;
	}
	
	@SuppressWarnings("unchecked")
	@Bean
	public ConcurrentMap<String, FileModel> fileMap() {
		return (ConcurrentMap<String, FileModel>) dataSource().hashMap("files").createOrOpen();
	}
	
	
	
	@Bean
	public PopupMenu popupMenu() {
		PopupMenu popupMenu = new PopupMenu();
		MenuItem close = new MenuItem("Exit");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		popupMenu.add(progressLabel());
		popupMenu.add(close);
		return popupMenu;
	}
	
	public String progressLabel() {
		return "Synchronization in progress... ";
	}


	@Bean
	public Path rootDirectory() {
		return FileSystems.getDefault().getPath(path);
	}
	
	@Bean
	public CloseableHttpClient client() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        // HttpHost host = new HttpHost("localhost",8888, "http");
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                // .setProxy(host)
                .build();
        return httpClient;
	}
	
	
}
