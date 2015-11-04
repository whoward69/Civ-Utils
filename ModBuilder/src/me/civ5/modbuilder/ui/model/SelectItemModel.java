package me.civ5.modbuilder.ui.model;

import javax.swing.DefaultComboBoxModel;

import me.civ5.modbuilder.db.ModDb;

public class SelectItemModel extends DefaultComboBoxModel<SelectEntry> {
	public SelectItemModel(ModDb db, String lookupTable, String keyColumn, String valueColumn, String whereClause) {
		if (db != null) {
			for (SelectEntry item : db.getSelectItems(lookupTable, keyColumn, valueColumn, whereClause)) {
				addElement(item);
			}
		}
	}
	
	public String getSelectedKey() {
		Object key = getSelectedItem();

		// Allow for editable lists
		if (key == null) {
			return null;
		} else if (key instanceof SelectEntry) {
			return ((SelectEntry) key).getKey();
		} else if (key instanceof String) {
			return (String) key;
		} else {
			return key.toString();
		}
	}
	
	public void setSelectedKey(String key) {
		for (int i = 0; i < getSize();  ++i) {
			SelectEntry item = (SelectEntry) getElementAt(i); 
			if (item.getKey().equals(key)) {
				setSelectedItem(item);
				return;
			}
		}

		// If we get here, the key didn't match any item, so we're probably in an editable list, so just set the value given
		setSelectedItem(key);
	}
}
