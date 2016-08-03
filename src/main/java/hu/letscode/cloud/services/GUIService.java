package hu.letscode.cloud.services;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.logging.Logger;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import hu.letscode.cloud.events.CommunicationExceptionEvent;

@Component
public class GUIService extends Thread implements ApplicationListener<CommunicationExceptionEvent> {

	private static final Logger logger = Logger.getLogger("cloud-client");
	private SystemTray tray;
	private TrayIcon trayIcon;
	
	public GUIService(TrayIcon trayIcon) {
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


	public void onApplicationEvent(CommunicationExceptionEvent event) {
		logger.warning("Event caught!");
		trayIcon.displayMessage(event.getCaption(), event.getMessage(), MessageType.ERROR);
	}
	
}
