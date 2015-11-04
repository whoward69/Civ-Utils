package me.civ5.modddsconverter.ui.view.grid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import me.civ5.modddsconverter.ui.model.grid.AtlasCellData;
import me.civ5.modddsconverter.ui.model.grid.AtlasCellModel;
import me.civ5.modutils.utils.ModUtilsImages;

public class AtlasCell extends JComboBox<AtlasCellData> implements ItemListener {
	public static int standardSize = 45;
	public static int standardPorthole = 31;
	
	public static ImageIcon emptyIcon = new ImageIcon(ModUtilsImages.getCenteredImage(ModUtilsImages.getClippedImage(ModUtilsImages.getColouredImage(standardPorthole, standardPorthole, Color.WHITE), standardPorthole), standardSize, standardSize));
	
	/* package */ AtlasCell(AtlasCellModel model) {
		super(model);
		
		setRenderer(new AtlasCellRenderer());
		setMaximumRowCount(8);
		
		addItemListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			((AtlasCellData) e.getItem()).deselect();
		} else {
			((AtlasCellData) e.getItem()).select();
		}
	}

	@Override
	public Dimension getPreferredSize() {
    	return new Dimension(standardSize + 23, standardSize + 4);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public BufferedImage getIconImage(int size) {
		return ((AtlasCellData) getSelectedItem()).getIconImage(size);
	}

	public class AtlasCellRenderer extends JLabel implements ListCellRenderer<AtlasCellData> {
		public AtlasCellRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		public Component getListCellRendererComponent(JList<? extends AtlasCellData> list, AtlasCellData value, int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(Color.DARK_GRAY);
				setForeground(Color.WHITE);
			} else {
				setBackground(Color.BLACK);
				setForeground(Color.WHITE);
			}

			if (value != null) {
				BufferedImage image = value.getIconImage(standardSize);
				setIcon((image == null) ? emptyIcon : new ImageIcon(image));
			} else {
				setIcon(null);
			}

			return this;
		}
	}
}
