package hu.letscode.cloud;

import java.io.File;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import hu.letscode.cloud.model.FileModification;

@Component
public class FileModificationTransformer {

	
	@Value("${upload.serverUrl}")
	private String serverUrl;
	
	public HttpPost transform(FileModification mod) {
		HttpPost post = new HttpPost(serverUrl);
		fillFileEntities(post, mod);
		return post;
	}

	private void fillFileEntities(HttpPost post, FileModification mod) {
		File file = mod.take();
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();         
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		while (file != null) {
			builder.addBinaryBody("file", file);
			file = mod.take();
		}
		post.setEntity(builder.build());
	}

}
