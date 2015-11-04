package me.civ5.modtools.mod.modinfo.files;

import java.io.File;
import java.io.PrintStream;

import me.civ5.modutils.log.ModReporter;

public abstract class ModFile implements Comparable<ModFile> {
	private File modDir;
	
	private String path;
	private String md5;
	private int importVFS;

	public ModFile(File modDir, String path, String md5, String importVFS) {
		this.modDir = modDir;
		
		setPath(path);
		this.md5 = md5;

		try {
			this.importVFS = Integer.parseInt(importVFS);
		} catch (NumberFormatException e) {
			this.importVFS = 0;
		}
	}
	
	public String getExtn() {
		int pos = path.lastIndexOf('.');
		return ((pos == -1) ? "" : path.substring(pos));
	}

	public void setPath(String path) {
		this.path = ((path == null) ? "" : path.replaceAll("\\\\", "/"));
	}

	public String getPath() {
		return path;
	}
	
	public String getVsPath() {
		return path.replaceAll("/", "\\\\");
	}

	public String getMd5() {
		return md5;
	}

	public void setImportVFS(int importVFS) {
		this.importVFS = importVFS;
	}
	
	public boolean isImportVFS() {
		return (importVFS == 1);
	}
	
	public int getImportVFS() {
		return importVFS;
	}
	
	public String getVsImportVFS() {
		return (isImportVFS() ? "True" : "False");
	}

	public File getFullPath() {
		return new File(modDir, getPath());
	}

	@Override
	public int compareTo(ModFile that) {
		return this.path.compareTo(that.path);
	}

	public void writeAsMod(PrintStream out, String indent) {
		out.print(indent);

		out.print("<File md5=\"");
		out.print(md5);
		out.print("\" import=\"");
		out.print(importVFS);
		out.print("\">");

		out.print(path);

		out.println("</File>");
	}
	
	public abstract boolean verify(ModReporter reporter);
}
