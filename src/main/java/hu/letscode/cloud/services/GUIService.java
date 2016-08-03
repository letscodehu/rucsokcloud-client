package hu.letscode.cloud.services;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.logging.Logger;

public class GUIService extends Thread {

	private static final Logger logger = Logger.getLogger("cloud-client");
	private SystemTray tray;
	private TrayIcon trayIcon;
	
	public GUIService(TrayIcon trayIcon) {
		if (!SystemTray.isSupported()) {
			return;
		}
		tray = SystemTray.getSystemTray();
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
	
}
