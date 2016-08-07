package hu.letscode.cloud.services;

import org.springframework.context.ApplicationEventPublisherAware;

public interface WatcherService extends ApplicationEventPublisherAware {
	public void start();
}
