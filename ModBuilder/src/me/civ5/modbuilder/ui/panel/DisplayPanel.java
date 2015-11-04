package me.civ5.modbuilder.ui.panel;

import java.awt.LayoutManager;

public abstract class DisplayPanel extends AbstractPanel {
	public DisplayPanel(LayoutManager layout, String id) {
		super(layout, id);
	}
	
	public abstract void display(String gamedata);
	public abstract String getText();
}
