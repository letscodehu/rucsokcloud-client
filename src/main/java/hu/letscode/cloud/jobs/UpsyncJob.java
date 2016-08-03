package hu.letscode.cloud.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class UpsyncJob implements Runnable {

	private String serverUrl;
	public List<String> keys;
	public HashMap<String, WatchEvent> batch;
	
	public UpsyncJob(HashMap<String, WatchEvent> batch, List<String> keys,  String serverUrl) {
		this.keys = keys;
		this.batch = batch;
		this.serverUrl = serverUrl;
	}

	public void run() {
		String hash; 
		WatchEvent<Path> event = null;
		synchronized (batch) {
			if (keys.size() > 0) {
				hash = keys.remove(keys.size() - 1);
				event = batch.remove(hash);	
			}	
		}
		if (event != null) {
			Path filePath = event.context();
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(serverUrl);
			FileEntity file = new FileEntity(filePath.toAbsolutePath().toFile());
			post.setEntity(file);

			try {
				HttpResponse response = client.execute(post);
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
