package me.civ5.modbuilder.ui.control.text;

import javax.swing.JTextArea;

import me.civ5.modbuilder.ui.control.SimpleControl;

import org.jdom.Element;

public class TextAreaItem extends SimpleControl {
	private String suffix;
	
	public TextAreaItem(String id, String tag, String suffix) {
		super(id, new JTextArea(5, 30), tag);

		JTextArea c = (JTextArea) getControl();
		c.setLineWrap(true);
		c.setWrapStyleWord(true);
		
		this.suffix = suffix;
	}

	@Override
	public String getText() {
		return ((JTextArea) getControl()).getText();
	}
	
	protected void setText(String text) {
		((JTextArea) getControl()).setText(text);
	}
	
	@Override
	public boolean isDefault() {
		// We need the TXT_KEY_ even if the text is blank
		return false;
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
		addTxtKeyTag(row, getTag(), type+suffix);
		
		// Leave writing the Language_en_US until postBuildXml() as that will get the <Language_en_US> table at the end of the file
	}
	
	@Override
	public void postBuildXml(Element gamedata, Element row, String type) {
		addEnUsText(gamedata, type+suffix, getText());
	}
}