package hu.letscode.cloud.model;

import java.io.Serializable;

public class FileModel implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2313807601582984989L;
	private long lastModification;

	public FileModel(long lastModification) {
		super();
		this.lastModification = lastModification;
	}

	public long getLastModification() {
		return lastModification;
	}

	public void setLastModification(long lastModification) {
		this.lastModification = lastModification;
	}

	
	
}
