package hu.letscode.cloud.config;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hu.letscode.cloud.Application;
import hu.letscode.cloud.FileModificationTransformer;
import hu.letscode.cloud.model.FileModification;
import hu.letscode.cloud.services.BatchingService;
import hu.letscode.cloud.services.DownStreamService;
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
	
	@Bean
	public Application application() {
		return new Application(
				new WatcherService(fileQueue, rootDirectory()), 
				new UpStreamService(requestQueue, new FileModificationTransformer(serverUrl), client()), 
						new DownStreamService(), new BatchingService(fileQueue, requestQueue, rootDirectory()));
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
