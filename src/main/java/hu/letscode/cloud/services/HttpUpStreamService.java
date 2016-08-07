package hu.letscode.cloud.services;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import hu.letscode.cloud.FileModificationTransformer;
import hu.letscode.cloud.events.CommunicationExceptionEvent;
import hu.letscode.cloud.model.FileModification;

@Component
public class HttpUpStreamService extends Thread implements UpStreamService {

	private static final Logger logger = Logger.getLogger("cloud-client");
	private BlockingQueue<FileModification> queue;
	private FileModificationTransformer transformer;
	private HttpClient httpClient;
	
	private ApplicationEventPublisher publisher;

	   public void setApplicationEventPublisher
	              (ApplicationEventPublisher publisher){
		   System.err.println("befut" + publisher.toString());
	      this.publisher = publisher;
	   }
	
	   @Autowired
	public HttpUpStreamService(BlockingQueue<FileModification> fileQueue, FileModificationTransformer transformer, HttpClient client) {
		this.queue = fileQueue;
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
				logger.info("hiba dobva");
				this.publisher.publishEvent(new CommunicationExceptionEvent(this, "Server error", "Can't synchronize"));
			}
		}
	}
	
	

}
