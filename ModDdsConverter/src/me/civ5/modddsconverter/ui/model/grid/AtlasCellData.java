package me.civ5.modddsconverter.ui.model.grid;

import java.awt.image.BufferedImage;

import me.civ5.modddsconverter.ui.panel.IconsPanel;

public class AtlasCellData {
	private IconsPanel panel;
	
	public AtlasCellData(IconsPanel panel) {
		this.panel = panel;
	}
	
	public boolean isPanel(IconsPanel panel) {
		return (this.panel != null && this.panel == panel);
	}
	
	public void select() {
		if (panel != null) {
			panel.select();
		}
	}
	
	public void deselect() {
		if (panel != null) {
			panel.deselect();
		}
	}

	public BufferedImage getIconImage(int size) {
		return (panel == null) ? null : panel.getIconImage(size);
	}
}
