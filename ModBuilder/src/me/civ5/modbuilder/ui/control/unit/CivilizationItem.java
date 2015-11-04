package me.civ5.modbuilder.ui.control.unit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.ModBuilderControl;
import me.civ5.modbuilder.ui.control.select.SelectItem;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.modbuilder.ui.model.SelectItemModel;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class CivilizationItem extends SelectItem implements ActionListener {
	private ModDb db;
	
	public CivilizationItem(String id, String tag, ModDb db) {
		super(id, tag, db, "Civilizations", "Type", "Description", null);
		this.db = db;
		
		JComboBox<SelectEntry> c = getComboControl();
		((SelectItemModel) c.getModel()).insertElementAt(new SelectEntry("NULL", "All civilizations", "Unit is available to all civilizations"), 0);
		c.setSelectedIndex(0);
		c.setEditable(true);
		
		c.addActionListener(this);
	}
	
	@Override
	public void setLanguage(Element parentLanguage) {
		super.setLanguage(parentLanguage);
		
		SelectEntry all = (SelectEntry) getComboControl().getModel().getElementAt(0);
		all.setValue(XpathHelper.getString(language, "./@all"));
		all.setTip(XpathHelper.getString(language, "./@allTip"));
	}

	@Override
	public String getText() {
		JComboBox<SelectEntry> c = getComboControl();
		int index = c.getSelectedIndex();
		
		if (index != -1) {
			return super.getText();
		} else {
			return c.getSelectedItem().toString();
		}
	}

	@Override
	public void setLinkedControl(ModBuilderControl linkedControl) {
		super.setLinkedControl(linkedControl);
		itemStateChanged(null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (getText().trim().length() == 0){
			setSelectedIndex(0);
		} else {
			itemStateChanged(null);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (linkedControl != null) {
			linkedControl.setEnabled(getComboControl().getSelectedIndex() != 0);
		}
	}
	
	@Override
	protected void serialiseImpl(Element me) {
		super.serialiseImpl(me);
		
		me.setText(getText());
	}

	@Override
	protected void deserialiseImpl(Element me) {
		int index = XpathHelper.getInt(me, "./@index", 0);
		
		if (index != -1) {
			super.deserialiseImpl(me);
		} else {
			getComboControl().setSelectedItem(me.getText());
		}
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		Element unitClass = XmlHelper.getChildElement(row, "Class");

		JComboBox<SelectEntry> c = getComboControl();

		if (c.getSelectedIndex() == 0) {
			String unitClassType = type.replace("UNIT_", "UNITCLASS_");
			unitClass.setText(unitClassType);
			
			Element unitClasses = XmlHelper.getChildElement(gamedata, "UnitClasses");
			Element classRow = XmlHelper.newElement(unitClasses, "Row");
			XmlHelper.newTextElement(classRow, "Type", unitClassType);
			addTxtKeyTag(classRow, "Description", type);
			XmlHelper.newTextElement(classRow, "DefaultUnit", type);
		} else {
			String unitClassType = linkedControl.getText();
			unitClass.setText(unitClassType);

			Element civOverrides = XmlHelper.newElement(gamedata, "Civilization_UnitClassOverrides");
			Element civRow = XmlHelper.newElement(civOverrides, "Row");
			XmlHelper.newTextElement(civRow, "CivilizationType", getText());
			XmlHelper.newTextElement(civRow, "UnitClassType", unitClassType);
			XmlHelper.newTextElement(civRow, "UnitType", type);

			Object[] cols = db.executeQuery("SELECT u.Conscription FROM Units u, UnitClasses uc WHERE u.Type=uc.DefaultUnit AND uc.Type=?", new Object[] {unitClassType}, 1);
			if (((Integer) cols[0]) != 0) {
				XmlHelper.newTextElement(row, "Conscription", cols[0].toString());
			}
		}
	}

	@Override
	public String preBuildXml(Element gamedata, String type) {
		if (getComboControl().getSelectedIndex() == 0) {
			// Creating the table now will force it to the top of the file
			XmlHelper.newElement(gamedata, "UnitClasses");
		}
		return type;
	}
}