package hu.letscode.cloud.services;

import java.util.logging.Logger;

public class GUIService extends Thread {

	private static final Logger logger = Logger.getLogger("cloud-client");
	
	public GUIService() {
		
	}
	
	
	public void run() {
		logger.info("GUI started");
	}
	
}
