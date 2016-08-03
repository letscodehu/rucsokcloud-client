package hu.letscode.cloud.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileModification {

	private List<File> filesToUpload = new ArrayList<File>();
	private static long LIMIT = 2000000;
	
	
	public void add(File file) {
		filesToUpload.add(file);
	}
	
	public boolean isAboveLimit() {
		return getBatchSize() > LIMIT;
	}
	
	public File take() {
		if (filesToUpload.size() == 0) {
			return null;
		}
		return filesToUpload.remove(filesToUpload.size() -1);
	}
	
	public long getBatchSize() {
		long size = 0;
		for (File file : filesToUpload) {
			size += getFileSize(file);
		}
		System.out.println(size);
		return size;
	}
	
	private long getFileSize(File file) {
		long size = 0; 
		try {
			size = (Long) Files.getAttribute(file.toPath(), "size");
			return size;
		} catch (IOException e) {
			return size;
		}
	}
	
	public boolean isItFits(File currentFile) {
		return (getBatchSize() +  getFileSize(currentFile)) < LIMIT;
	}
	
}
