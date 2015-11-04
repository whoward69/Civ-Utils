package me.civ5.modbuilder.ui.model;

import javax.swing.DefaultListModel;

import me.civ5.modbuilder.db.ModDb;

public class MultiSelectItemModel extends DefaultListModel<SelectEntry> {
	public MultiSelectItemModel(ModDb db, String lookupTable, String keyColumn, String valueColumn, String whereClause) {
		if (db != null) {
			for (SelectEntry item : db.getSelectItems(lookupTable, keyColumn, valueColumn, whereClause)) {
				addElement(item);
			}
		}
	}
}
