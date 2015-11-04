package me.civ5.modtools.ui.model;

import java.io.File;

import javax.swing.DefaultListModel;

import me.civ5.modtools.mod.Mod;
import me.civ5.modutils.log.ModReporter;

public class OrderedModListModel extends DefaultListModel<Object> implements ModListModel {
	@Override
	public void addElement(int index, Object element) {
		super.add(index, element);
	}

	@Override
	public Object removeElement(int index) {
		return super.remove(index);
	}

	@Override
	public void load(ModReporter reporter, File modsDir) {
		if ( modsDir.isDirectory() ) {
			for ( File mod : modsDir.listFiles() ) {
				if ( mod.isDirectory() ) {
					try {
						addElement(new Mod(reporter, modsDir.getAbsolutePath(), mod.getName()));
					} catch (Exception ex) {
						reporter.log(ex);
					}
				}
			}
		}
	}
}
