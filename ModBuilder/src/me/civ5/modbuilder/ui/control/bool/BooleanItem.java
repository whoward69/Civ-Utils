package me.civ5.modbuilder.ui.control.bool;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import me.civ5.modbuilder.ui.control.ModBuilderControl;
import me.civ5.modbuilder.ui.control.SimpleControl;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public abstract class BooleanItem extends SimpleControl implements ChangeListener {
	private boolean defValue;
	
	public BooleanItem(String id, String tag, boolean defValue, boolean initValue) {
		super(id, new JCheckBox(), tag);
		this.defValue = defValue;
		
		JCheckBox c = (JCheckBox) getControl();
		c.setSelected(initValue);
		c.addChangeListener(this);
	}
	
	@Override
	public String getText() {
		return (isSelected() ? "1" : "0");
	}

	public boolean isSelected() {
		return ((JCheckBox) getControl()).isSelected();
	}
	
	public void setSelected(boolean selected) {
		((JCheckBox) getControl()).setSelected(selected);
	}

	@Override
	public boolean isDefault() {
		return (isSelected() == defValue);
	}

	@Override
	protected void serialiseImpl(Element me) {
		me.setAttribute("value", Boolean.toString(isSelected()));
	}

	@Override
	protected void deserialiseImpl(Element me) {
		((JCheckBox) getControl()).setSelected(Boolean.parseBoolean(me.getAttributeValue("value")));
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		if (!isDefault()) {
			XmlHelper.newTextElement(row, getTag(), Boolean.toString(isSelected()));
		}
	}

	@Override
	public void setLinkedControl(ModBuilderControl linkedControl) {
		super.setLinkedControl(linkedControl);
		stateChanged(null);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (linkedControl != null) {
			linkedControl.setEnabled(isSelected());
		}
	}
}