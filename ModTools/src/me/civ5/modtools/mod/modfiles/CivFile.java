package me.civ5.modtools.mod.modfiles;

import java.io.File;

public class CivFile {
	private String path;
	private File location;
	
	public CivFile(File modDir, String path) {
		this.path = path.replaceAll("\\\\", "/");
		this.location = new File(modDir, path);
	}

	public String getPath() {
		return path;
	}
	
	public String getVsPath() {
		return path.replaceAll("/", "\\");
	}

	public File getLocation() {
		return location;
	}
}
