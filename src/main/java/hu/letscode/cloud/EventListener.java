package hu.letscode.cloud;

import java.nio.file.WatchEvent;

import hu.letscode.cloud.events.FileChangeEvent;
import hu.letscode.cloud.events.FileCreateEvent;
import hu.letscode.cloud.events.FileDeleteEvent;

public interface EventListener {
	
	public void onChange(WatchEvent<?> event);
	public void onCreate(WatchEvent<?> event);
	public void onDelete(WatchEvent<?> event);
	public void onCreateDirectory(WatchEvent<?> event);
	
}
