package hu.letscode.cloud.services;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import hu.letscode.cloud.FileModificationTransformer;
import hu.letscode.cloud.model.FileModification;

public class UpStreamService extends Thread {

	private static final Logger logger = Logger.getLogger("cloud-client");
	private BlockingQueue<FileModification> queue;
	private FileModificationTransformer transformer;
	private HttpClient httpClient;
	
	public UpStreamService(BlockingQueue<FileModification> queue, FileModificationTransformer transformer, HttpClient client) {
		this.queue = queue;
		this.httpClient = client;
		this.transformer = transformer;
	}

	public void run() {
		logger.info("consume started");
		while(true) {
			try {
				Thread.sleep(1000);
				FileModification mod = queue.take();
				logger.info("took an item from the requestqueue, size: " + mod.getBatchSize());
				HttpPost postRequest = transformer.transform(mod);
				System.out.println(postRequest.getEntity().getContentLength());
				HttpResponse resp = httpClient.execute(postRequest);
				logger.info("Response code: " + resp.getStatusLine().getStatusCode());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

}
