package me.civ5.modddsconverter.ui.view.icon;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import me.civ5.image.ModImage;
import me.civ5.modutils.utils.ModUtilsImages;

public class IconPanel extends JPanel {
	private int maxWidth, maxHeight;
	private ModImage image = null;

	public IconPanel(int maxWidth, int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;

		setBorder(new EtchedBorder());
	}

	public void setImage(ModImage image) {
		this.image = image;
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(maxWidth, maxHeight);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			float scale = Math.min(maxWidth / (width * 1.0f), maxHeight / (height * 1.0f));

			g.drawImage(ModUtilsImages.getCenteredImage(ModUtilsImages.getScaledImage(image.getData(), (int) (width * scale), (int) (height * scale)), maxWidth, maxHeight), 0, 0, null);
		}
	}
}
