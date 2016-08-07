package hu.letscode.cloud.services;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.logging.Logger;

import javax.swing.JPopupMenu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import hu.letscode.cloud.events.CommunicationExceptionEvent;
import hu.letscode.cloud.events.FileVisitExceptionEvent;
import hu.letscode.cloud.events.NotificationEvent;

@Component
public class SwingGUIService extends Thread implements GUIService  {

	private static final Logger logger = Logger.getLogger("cloud-client");
	private SystemTray tray;
	private static long lastNotification;
	private static long delayBetweenNotifications = 5000L;
	private TrayIcon trayIcon;
	
	@Autowired
	private JPopupMenu popupMenu;
	
	@Autowired
	public SwingGUIService(TrayIcon trayIcon) {
		if (!SystemTray.isSupported()) {
			return;
		}
		tray = SystemTray.getSystemTray();
		this.trayIcon = trayIcon;
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void run() {
		logger.info("GUI started");
	}


	public void onApplicationEvent(NotificationEvent event) {
		logger.info("Event caught!");
		if (event instanceof FileVisitExceptionEvent) {
			showError(event.getCaption(), event.getMessage());
		} else {
			
		}
	}
	
	private void showError(String title, String message) {
		if (System.currentTimeMillis() > (lastNotification + delayBetweenNotifications)) {
			lastNotification = System.currentTimeMillis();
			trayIcon.displayMessage(title, message, MessageType.ERROR);			
		}

	}
	
}
