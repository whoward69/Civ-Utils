package me.civ5.modbuilder.ui.control.unit;

import java.util.HashMap;
import java.util.Map;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.select.SelectItem;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class CombatClassItem extends SelectItem {
	private static Map<String, String> domains = new HashMap<String, String>();
	static {
		domains.put("UNITCOMBAT_ARCHER", "DOMAIN_LAND");
		domains.put("UNITCOMBAT_ARMOR", "DOMAIN_LAND");
		domains.put("UNITCOMBAT_BOMBER", "DOMAIN_AIR");
		domains.put("UNITCOMBAT_FIGHTER", "DOMAIN_AIR");
		domains.put("UNITCOMBAT_GUN", "DOMAIN_LAND");
		domains.put("UNITCOMBAT_HELICOPTER", "DOMAIN_LAND");
		domains.put("UNITCOMBAT_MELEE", "DOMAIN_LAND");
		domains.put("UNITCOMBAT_MOUNTED", "DOMAIN_LAND");
		domains.put("UNITCOMBAT_NAVALMELEE", "DOMAIN_SEA");
		domains.put("UNITCOMBAT_NAVALRANGED", "DOMAIN_SEA");
		domains.put("UNITCOMBAT_RECON", "DOMAIN_LAND");
		domains.put("UNITCOMBAT_SIEGE", "DOMAIN_LAND");
	}
	
	public CombatClassItem(String id, String tag, ModDb db, String lookupTable, String keyColumn, String valueColumn, String whereClause) {
		super(id, tag, db, lookupTable, keyColumn, valueColumn, whereClause);
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		super.buildXmlImpl(gamedata, row, type);

		XmlHelper.newTextElement(row, "Domain", domains.get(getText()));
	}
}
