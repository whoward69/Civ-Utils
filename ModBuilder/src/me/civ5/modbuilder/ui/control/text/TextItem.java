package me.civ5.modbuilder.ui.control.text;

import javax.swing.JTextField;

import me.civ5.modbuilder.ui.control.SimpleControl;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class TextItem extends SimpleControl {
	public TextItem(String id, String tag) {
		super(id, new JTextField(30), tag);
	}

	@Override
	public String getText() {
		return ((JTextField) getControl()).getText().trim();
	}
	
	public void setText(String text) {
		((JTextField) getControl()).setText((text != null) ? text.trim() : "");
	}

	@Override
	public boolean isDefault() {
		return (getText().length() == 0);
	}
	
	@Override
	protected void serialiseImpl(Element me) {
		me.setText(getText());
	}

	@Override
	protected void deserialiseImpl(Element me) {
		setText(me.getText());
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		XmlHelper.newTextElement(row, getTag(), getText());
	}
}