package me.civ5.modbuilder.ui.control.text;

import javax.swing.JTextField;

import org.jdom.Element;


public class StaticItem extends TextItem {
	public StaticItem(String id) {
		super(id, null);
		
		JTextField c = (JTextField) getControl();
		c.setEditable(false);
	}

	@Override
	public boolean isDefault() {
		return true;
	}
	
	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {}
}
