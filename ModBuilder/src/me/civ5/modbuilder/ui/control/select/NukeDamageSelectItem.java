package me.civ5.modbuilder.ui.control.select;

import javax.swing.JComboBox;

import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class NukeDamageSelectItem extends SelectItem {
	public NukeDamageSelectItem(String id, String tag) {
		super(id, tag, null, null, null, null, null);
	}

	@Override
	public boolean isDefault() {
		return (getComboControl().getSelectedIndex() == 0);
	}

	@Override
	public void setLanguage(Element parentLanguage) {
		super.setLanguage(parentLanguage);
		
		JComboBox<SelectEntry> c = getComboControl();
		c.removeAllItems();
		
		for (Element item : XpathHelper.getElements(language, "./item")) {
			c.addItem(new SelectEntry(XpathHelper.getString(item, "./@value"), XpathHelper.getString(item, "./@name"), item.getTextTrim()));
		}
	}
}
