package hu.letscode.cloud.services;

import org.springframework.context.ApplicationListener;

import hu.letscode.cloud.events.CommunicationExceptionEvent;
import hu.letscode.cloud.events.FileVisitExceptionEvent;
import hu.letscode.cloud.events.NotificationEvent;

public interface GUIService extends ApplicationListener<NotificationEvent> {

	public void start();
	
}
