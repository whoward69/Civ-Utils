package me.civ5.modtools.ui.view.bar;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import me.civ5.modtools.ui.ModToolsFrame;

public class ButtonBar extends JPanel {
	
	public ButtonBar(ModToolsFrame owner) {
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		add(Box.createVerticalGlue());
		add(new ButtonItem(owner.getVerifyAction()));
		add(Box.createVerticalStrut(5));
		add(new ButtonItem(owner.getSaveModAction()));
		add(Box.createVerticalStrut(5));
		add(new ButtonItem(owner.getSaveProjectAction()));
		add(Box.createVerticalStrut(10));
		add(new ButtonItem(owner.getExitAction()));
	}
	
	private class ButtonItem extends JButton {
		public ButtonItem(Action action) {
			super(action);

			setHorizontalAlignment(SwingConstants.LEFT);
		}

		@Override
		public Dimension getMaximumSize() {
			return new Dimension(140, 24);
		}
	}
}
