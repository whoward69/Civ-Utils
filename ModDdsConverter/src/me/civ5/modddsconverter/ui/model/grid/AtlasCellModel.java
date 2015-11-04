package me.civ5.modddsconverter.ui.model.grid;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

public class AtlasCellModel extends DefaultComboBoxModel<AtlasCellData> {
	/* package */ AtlasCellModel(Vector<AtlasCellData> items) {
		super(items);
	}
	
	public void elementAdded(int index) {
		fireIntervalAdded(this, index, index);
	}
	
	public void elementRemoved(int index) {
		fireIntervalRemoved(this, index, index);
	}

	public void elementChanged(int index) {
		fireContentsChanged(this, index, index);
	}
}
