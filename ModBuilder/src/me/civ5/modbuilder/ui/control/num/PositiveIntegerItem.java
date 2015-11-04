package me.civ5.modbuilder.ui.control.num;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import me.civ5.modbuilder.ui.control.ModBuilderControl;

public class PositiveIntegerItem extends IntegerItem implements CaretListener {
	public PositiveIntegerItem(String id, String tag) {
		super(id, tag);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setText("");
	}

	@Override
	public void setLinkedControl(ModBuilderControl linkedControl) {
		super.setLinkedControl(linkedControl);
		caretUpdate(null);
	}

	@Override
	public boolean verify(boolean loaded) {
		String text = getText();
		boolean valid = text.equals("");
		
		if (!valid) {
			try {
				valid = (Integer.parseInt(text) >= 0);
			} catch (NumberFormatException ex) {
				valid = false;
			}
		}

		setValid(valid);
		
		return valid;
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		boolean valid = verify(loaded);
		fireValidityChange(valid);

		if (linkedControl != null) {
			String text = getText();
			boolean enabled = !text.equals("");
			
			if (enabled) {
				try {
					enabled = (Integer.parseInt(text) != 0);
				} catch (NumberFormatException ex) {
					enabled = false;
				}
			}
			
			linkedControl.setEnabled(enabled);
		}
	}
}
