package me.civ5.modtools.mod.modinfo.actions;

import java.io.PrintStream;

public class SetDllPath {
	private String path;
	
	public SetDllPath(String path) {
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
		
		out.print("<SetDllPath>");
		
		out.print(path);
		
		out.println("</SetDllPath>");
	}
}
