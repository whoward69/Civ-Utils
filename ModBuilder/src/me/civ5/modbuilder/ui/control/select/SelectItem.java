package me.civ5.modbuilder.ui.control.select;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.ComplexControl;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.modbuilder.ui.model.SelectItemModel;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class SelectItem extends ComplexControl implements ItemListener {
	public SelectItem(String id, String tag, ModDb db, String lookupTable, String keyColumn, String valueColumn) {
		this(id, tag, db, lookupTable, keyColumn, valueColumn, null);
	}
	
	@SuppressWarnings("unchecked")
	public SelectItem(String id, String tag, ModDb db, String lookupTable, String keyColumn, String valueColumn, String whereClause) {
		super(id, new JComboBox<SelectEntry>(new SelectItemModel(db, lookupTable, keyColumn, valueColumn, whereClause)), tag);

		JComboBox<SelectEntry> c = getComboControl();
		c.setEditable(false);
		c.setRenderer(new ToolTipComboBoxRenderer()); // Tooltips for each entry in the selection list
		c.addItemListener(this);
	}
	
	@SuppressWarnings("unchecked")
	protected JComboBox<SelectEntry> getComboControl() {
		return (JComboBox<SelectEntry>) getControl();
	}
	
	protected void setSelectedIndex(int index) {
		getComboControl().setSelectedIndex(index);
	}
	
	protected boolean isEmpty() {
		return false;
	}
	
	@Override
	public boolean isDefault() {
		return isEmpty();
	}
	
	@Override
	public String getText() {
		return ((SelectEntry) getComboControl().getSelectedItem()).getKey();
	}

	@Override
	protected void serialiseImpl(Element me) {
		me.setAttribute("key", ((SelectItemModel) getComboControl().getModel()).getSelectedKey());
	}

	@Override
	protected void deserialiseImpl(Element me) {
		((SelectItemModel) getComboControl().getModel()).setSelectedKey(XpathHelper.getString(me, "./@key"));
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		XmlHelper.newTextElement(row, getTag(), getText());
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (linkedControl != null && linkedControl instanceof SelectItem) {
			((SelectItem) linkedControl).setSelectedIndex(getComboControl().getSelectedIndex());
		}
	}
	
	public class ToolTipComboBoxRenderer extends BasicComboBoxRenderer {
	    @SuppressWarnings("rawtypes")
		@Override
	    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	            if (-1 < index) {
	                list.setToolTipText(((SelectEntry) list.getSelectedValue()).getTip());
	            }
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }

	        setFont(list.getFont());
	        setText((value == null) ? "" : value.toString());
	        
	        return this;
	    }
	}
}
