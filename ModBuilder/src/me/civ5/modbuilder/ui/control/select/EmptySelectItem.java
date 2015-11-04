package me.civ5.modbuilder.ui.control.select;

import java.awt.event.ItemEvent;

import javax.swing.JComboBox;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.ModBuilderControl;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.modbuilder.ui.model.SelectItemModel;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class EmptySelectItem extends SelectItem {

	public EmptySelectItem(String id, String tag, ModDb db, String lookupTable, String keyColumn, String valueColumn) {
		this(id, tag, db, lookupTable, keyColumn, valueColumn, null);
	}
	
	public EmptySelectItem(String id, String tag,  ModDb db, String lookupTable, String keyColumn, String valueColumn, String whereClause) {
		super(id, tag, db, lookupTable, keyColumn, valueColumn, whereClause);
		
		JComboBox<SelectEntry> c = getComboControl();
		((SelectItemModel) c.getModel()).insertElementAt(new SelectEntry("NULL", "", ""), 0);
		c.setSelectedIndex(0);
	}
	
	@Override
	public void setLanguage(Element parentLanguage) {
		super.setLanguage(parentLanguage);
		((SelectEntry) ((SelectItemModel) getComboControl().getModel()).getElementAt(0)).setValue(XpathHelper.getString(language, "./@default"));
	}
	
	@Override
	public boolean isDefault() {
		return (getComboControl().getSelectedIndex() == 0);
	}

	@Override
	protected boolean isEmpty() {
		return (getComboControl().getSelectedIndex() < 1);
	}


	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if (!enabled) {
			getComboControl().setSelectedIndex(0);
		}
	}

	@Override
	public void setLinkedControl(ModBuilderControl linkedControl) {
		super.setLinkedControl(linkedControl);
		itemStateChanged(null);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		super.itemStateChanged(e);
		
		if (linkedControl != null) {
			linkedControl.setEnabled(getComboControl().getSelectedIndex() != 0);
		}

		if (invertedLinkedControl != null) {
			invertedLinkedControl.setEnabled(getComboControl().getSelectedIndex() == 0);
		}
	}
}
