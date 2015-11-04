package me.civ5.modbuilder.ui.control.unit;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.text.StaticItem;
import me.civ5.modbuilder.ui.control.text.TextItem;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class UnitItem extends TextItem implements CaretListener {
	private ModDb modDb;
	private String table;
	private String prefix;
	
	public UnitItem(String id, String tag, ModDb modDb, String table, String prefix) {
		super(id, tag);
		this.modDb = modDb;
		this.table = table;
		this.prefix = prefix;
		
		JTextField c = (JTextField) getControl();
		c.addCaretListener(this);
		
		verify(loaded);
	}
	
	public String getType() {
		String text = getText();
		String type = "";
		
		if (text.length() > 0) {
			type = prefix + text;
			type = type.toUpperCase().replaceAll(" ", "_");
			type = type.replaceAll("[^a-zA-Z0-9_]", "");
		}

		return type;
	}
	
	@Override
	public boolean verify(boolean loaded) {
		boolean valid = (getText().length() > 0);
		
		if (valid && !loaded) {
			valid = modDb.availableType(table, getType());
		}

		setValid(valid);
		
		return valid;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		XmlHelper.newTextElement(row, getTag(), type);
		addTxtKeyTag(row, "Description", type);
		
		// Leave writing the Language_en_US until postBuildXml() as that will get the <Language_en_US> table at the end of the file
	}
	
	@Override
	public String preBuildXml(Element gamedata, String type) {
		return getType();
	}

	@Override
	public void postBuildXml(Element gamedata, Element row, String type) {
		if (XpathHelper.getInt(row, "./RangedCombat", 0) == 0 && XpathHelper.getInt(row, "./NukeDamageLevel", -1) == -1) {
			XmlHelper.newTextElement(row, "CombatLimit", "100");
		} else {
			XmlHelper.newTextElement(row, "RangedCombatLimit", "100");
		}

		if (!XpathHelper.getString(row, "./SpecialCargo", "").equals("")) {
			XmlHelper.newTextElement(row, "DomainCargo", "DOMAIN_AIR");
		}
		
		String domain = XpathHelper.getString(row, "./Domain");
		if ("DOMAIN_AIR".equals(domain)) {
			XmlHelper.newTextElement(row, "AirUnitCap", "1");
		} else if ("DOMAIN_SEA".equals(domain)) {
			XmlHelper.newTextElement(row, "MinAreaSize", "10");
		}

		XmlHelper.newTextElement(row, "MilitaryProduction", "true");
		XmlHelper.newTextElement(row, "MilitarySupport", "true");

		addEnUsText(gamedata, type, getText());
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		boolean valid = verify(loaded);
		fireValidityChange(valid);
		
		if (linkedControl != null) {
			if (linkedControl instanceof StaticItem) {
				((StaticItem) linkedControl).setText(getType());
			}
		}
	}
}
