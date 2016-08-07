package hu.letscode.cloud.events;

import org.springframework.context.ApplicationEvent;

public class NotificationEvent extends ApplicationEvent {

	public NotificationEvent(Object source) {
		super(source);
	}
	
	public String getCaption() {
		return "";
	}
	
	public String getMessage() {
		return "";
	}

}
