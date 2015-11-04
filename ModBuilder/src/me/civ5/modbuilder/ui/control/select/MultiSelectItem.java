package me.civ5.modbuilder.ui.control.select;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.ComplexControl;
import me.civ5.modbuilder.ui.model.MultiSelectItemModel;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.modbuilder.ui.view.bar.MultiSelectItemList;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public abstract class MultiSelectItem extends ComplexControl {
	protected MultiSelectItemList list;
	
	public MultiSelectItem(String id, String tag, ModDb db, String lookupTable, String keyColumn, String valueColumn, String whereClause) {
		super(id, new JScrollPane(), tag);
		
		list = new MultiSelectItemList(new MultiSelectItemModel(db, lookupTable, keyColumn, valueColumn, whereClause));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(5);
		list.setToolTipText(getTip()); // Tooltip for the main control

		JScrollPane c = (JScrollPane) getControl();
		c.setViewportView(list);
		c.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	protected boolean isEmpty() {
		return (list.getSelectedIndex() == -1);
	}

	@Override
	public boolean isDefault() {
		return isEmpty();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		list.setEnabled(enabled);
	}

	@Override
	protected void serialiseImpl(Element me) {
		for (int index : list.getSelectedIndices()) {
			XmlHelper.newTextElement(me, "key", ((SelectEntry) list.getModel().getElementAt(index)).getKey());
		}
	}

	@Override
	protected void deserialiseImpl(Element me) {
		try {
			list.selectKeys(XpathHelper.getStrings(me, "./key"));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
