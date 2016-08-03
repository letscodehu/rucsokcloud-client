package hu.letscode.cloud.events;

import org.springframework.context.ApplicationEvent;

public class CommunicationExceptionEvent extends ApplicationEvent {

	private String caption;
	private String message;
	public String getCaption() {
		return caption;
	}

	public String getMessage() {
		return message;
	}

	public CommunicationExceptionEvent(Object source, String caption, String message) {
		super(source);
		this.caption = caption;
		this.message = message;
	}

	private static final long serialVersionUID = 5742266467968602315L;

	public CommunicationExceptionEvent(Object source) {
		super(source);
	}
	
	

}
