package hu.letscode.cloud;

import hu.letscode.cloud.events.FileChangeEvent;
import hu.letscode.cloud.events.FileCreateEvent;
import hu.letscode.cloud.events.FileDeleteEvent;

public interface EventListener {
	
	public void onChange(FileChangeEvent event);
	public void onCreate(FileCreateEvent event);
	public void onDelete(FileDeleteEvent event);
	
}
