package me.civ5.modddsconverter.ui.view.icon;

import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JCheckBox;

public class IconCheck extends JCheckBox {
	private int diameter;

	public IconCheck(Integer size, int diameter, ActionListener owner, Map<Integer, IconCheck> container) {
		super(size.toString());
		this.diameter = diameter;

		container.put(size, this);

		addActionListener(owner);
	}

	public int getDiameter() {
		return diameter;
	}
}
