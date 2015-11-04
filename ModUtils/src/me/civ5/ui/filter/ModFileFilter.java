package me.civ5.ui.filter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

public abstract class ModFileFilter extends FileFilter {
	protected String desc;
	private Set<String> extns = new HashSet<String>();
	
	
	public ModFileFilter(String extn, String desc) {
		this.desc = desc;
		addExtn(extn);
	}
	
	@Override
	public String getDescription() {
		return desc;
	}
	
	@Override
	public boolean accept(File f) {
		return (f != null && (f.isDirectory() || extns.contains(getExtension(f))));
	}

	public String getExtension(File f) {
		String s = f.getName();

		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			return s.substring(i + 1).toLowerCase();
		}

		return null;
	}

	public String[] getExtns() {
		return extns.toArray(new String[extns.size()]);
	}
	
	protected void addExtn(String extn) {
		if (extn != null) {
			this.extns.add(extn);
		}
	}
}
