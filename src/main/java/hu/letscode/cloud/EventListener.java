package hu.letscode.cloud;

import java.nio.file.WatchEvent;

public interface EventListener {
	
	public void onChange(WatchEvent<?> event);
	public void onCreate(WatchEvent<?> event);
	public void onDelete(WatchEvent<?> event);
	public void onCreateDirectory(WatchEvent<?> event);
	
}
