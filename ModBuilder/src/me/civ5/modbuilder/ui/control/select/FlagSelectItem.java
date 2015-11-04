package me.civ5.modbuilder.ui.control.select;

import java.awt.event.ItemEvent;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class FlagSelectItem extends EmptySelectItem {
	private ModDb db;
	
	public FlagSelectItem(String id, ModDb db) {
		super(id, "notag", db, "Units", "Type", "Description", "Cost>0 AND (Combat>0 OR RangedCombat>0)");
		this.db = db;
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		if (!isEmpty()) {
			Object[] cols = db.executeQuery("SELECT UnitFlagAtlas, UnitFlagIconOffset FROM Units WHERE Type=?", new Object[] {getText()}, 2);
			
			if (cols != null) {
				XmlHelper.newTextElement(row, "UnitFlagAtlas", (String) cols[0]);
				XmlHelper.newTextElement(row, "UnitFlagIconOffset", ((Integer) cols[1]).toString());
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (linkedControl != null && linkedControl instanceof SelectItem) {
			((SelectItem) linkedControl).setSelectedIndex(getComboControl().getSelectedIndex());
		}
		
		if (invertedLinkedControl != null) {
			invertedLinkedControl.setEnabled(getComboControl().getSelectedIndex() == 0);
		}
	}
}
