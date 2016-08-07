package hu.letscode.cloud.events;

import org.springframework.context.ApplicationEvent;

public class FileVisitExceptionEvent extends NotificationEvent {

	private Exception ex;
	
	public FileVisitExceptionEvent(Object source, Exception ex) {
		super(source);
		this.ex = ex;
	}
	
	public String getCaption() {
		return "File cannot be opened!";
	}
	
	public String getMessage() {
		return "In directory " + ex.getMessage();
	}

	private static final long serialVersionUID = 5742266467968602335L;
	
	
}
