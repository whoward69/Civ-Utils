package me.civ5.modtools.mod.modinfo.actions;

import java.io.PrintStream;

public class UpdateDatabase {
	private String path;
	
	public UpdateDatabase(String path) {
		setPath(path);
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = (path == null ? "" : path.replaceAll("\\\\", "/"));
	}

	public void writeAsMod(PrintStream out, String indent) {
		out.print(indent);
		
		out.print("<UpdateDatabase>");
		
		out.print(path);
		
		out.println("</UpdateDatabase>");
	}
}
