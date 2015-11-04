package me.civ5.modbuilder.ui.control.num;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import me.civ5.modbuilder.ui.control.SimpleControl;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class IntegerItem extends SimpleControl implements CaretListener {
	protected JTextField value;
	
	public IntegerItem(String id, String tag) {
		this(id, tag, "");
	}
	
	public IntegerItem(String id, String tag, String defValue) {
		super(id, new JPanel(new BorderLayout()), tag);

		value = new JTextField(defValue, 5);
		getControl().add(value, BorderLayout.WEST);
		borderValid = value.getBorder();
		value.addCaretListener(this);
	}
	
	@Override
	public String getText() {
		return (value != null) ? value.getText().trim() : "";
	}

	public void setText(String text) {
		value.setText(text.trim());
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		value.setEnabled(enabled);
	}

	@Override
	public boolean isDefault() {
		String text = getText();
		boolean def = (text.length() == 0);
				
		if (!def) {
			try {
				def = (Integer.parseInt(text) == 0);
			} catch (NumberFormatException e) {
				def = false;
			}
		}
		
		return def;
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
		XmlHelper.newTextElement(row, getTag(), value.getText());
	}

	@Override
	public boolean verify(boolean loaded) {
		String text = getText();
		boolean valid = text.equals("");
		
		if (!valid) {
			try {
				Integer.parseInt(text);
				valid = true;
			} catch (NumberFormatException ex) {
				valid = false;
			}
		}

		setValid(valid);
		
		return valid;
	}
	
	@Override
	protected void setValid(boolean valid) {
		if (valid) {
			getLabel().setForeground(labelValid);
			value.setBorder(borderValid);
		} else {
			getLabel().setForeground(labelInvalid);
			value.setBorder(borderInvalid);
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		boolean valid = verify(loaded);
		fireValidityChange(valid);
	}
}