package me.civ5.modbuilder.ui.control;

import javax.swing.JComponent;

public abstract class SimpleControl extends ModBuilderControl {
	public SimpleControl(String id, JComponent component, String tag) {
		super(id, component, tag);
	}
}
