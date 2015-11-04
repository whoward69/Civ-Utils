package me.civ5.modtools.ui.model;

import java.io.File;

import javax.swing.ListModel;

import me.civ5.modutils.log.ModReporter;

public interface ModListModel extends ListModel<Object> {
	public boolean isEmpty();
	
	public void addElement(Object element);
	public void addElement(int index, Object element);
	public Object removeElement(int index);
	
	public void load(ModReporter reporter, File modsDir);
}
