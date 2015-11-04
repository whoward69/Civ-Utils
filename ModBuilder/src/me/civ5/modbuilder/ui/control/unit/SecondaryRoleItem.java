package me.civ5.modbuilder.ui.control.unit;

import java.util.LinkedHashMap;
import java.util.Map;

import me.civ5.modbuilder.ui.control.select.MultiSelectItem;
import me.civ5.modbuilder.ui.control.unit.PrimaryRoleItem.Role;
import me.civ5.modbuilder.ui.model.MultiSelectItemModel;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class SecondaryRoleItem extends MultiSelectItem {
	public static Map<String, Role> secondaryRoles = new LinkedHashMap<String, Role>();
	static { // TODO - text - secondary roles
		secondaryRoles.putAll(PrimaryRoleItem.baseRoles);
		secondaryRoles.put("Anti-Air (Land only)",       new Role(Role.ROLE_ANTIAIR, "CITY_SPECIAL", "UNKNOWN", "UNKNOWN"));
		secondaryRoles.put("Missile Carrier (Sea only)", new Role("cruiser", "UNKNOWN", "MISSILE_CARRIER_SEA", "UNKNOWN"));
		secondaryRoles.put("Carrier Based (Air only)",   new Role("carried", "UNKNOWN", "UNKNOWN", "CARRIER_AIR "));
		secondaryRoles.put("Worker (Land & Sea)",        new Role("worker", "WORKER", "WORKER_SEA", "UNKNOWN"));
	}
	
	public SecondaryRoleItem(String id, String tag) {
		super(id, tag, null, null, null, null, null);
		list.setVisibleRowCount(3);
		MultiSelectItemModel model = (MultiSelectItemModel) list.getModel();

		for (String role : secondaryRoles.keySet()) {
			model.addElement(new SelectEntry(role, role, role));
		}
	}
	
	protected Role getRole(String text) {
		return secondaryRoles.get(text);
	}
	
	@Override
	public String getText() {
		throw new RuntimeException("Can't call getText() for a multi-select item!");
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		// We have to do the save last, or at least after Moves have been serialised
	}
	
	@Override
	public void postBuildXml(Element gamedata, Element row, String type) {
		String domain = XpathHelper.getString(row, "./Domain");
		int combat = XpathHelper.getInt(row, "./Combat", 0);
		int rangedCombat = XpathHelper.getInt(row, "./RangedCombat", 0);
		int moves = XpathHelper.getInt(row, "./Moves", 2);
		
		if (!isEmpty()) {
			Element aiTable = XmlHelper.getChildElement(gamedata, "Unit_AITypes");
			Element flavors = XmlHelper.getChildElement(gamedata, "Unit_Flavors");
			Element flavorRow;
			
			for (SelectEntry entry : list.getSelectedValuesList()) {
				String role = entry.getKey();
				String secondaryRole = secondaryRoles.get(role).getAiType(domain, moves);
				
				Element aiRow = XmlHelper.newElement(aiTable, "Row");
				XmlHelper.newTextElement(aiRow, "UnitType", type);
				XmlHelper.newTextElement(aiRow, "UnitAIType", secondaryRole);
				
				if (getRole(role).getId().equals(Role.ROLE_MELEE)) {
					flavorRow = XmlHelper.newElement(flavors, "Row");
					XmlHelper.newTextElement(flavorRow, "UnitType", type);
					XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_OFFENSE");
					XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(1, Math.min(25, (combat*3+rangedCombat*2)/30)));
				} else if (getRole(role).getId().equals(Role.ROLE_RANGED)) {
					if ("DOMAIN_LAND".equals(domain) && rangedCombat > 0) {
						flavorRow = XmlHelper.newElement(flavors, "Row");
						XmlHelper.newTextElement(flavorRow, "UnitType", type);
						XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_RANGED");
						XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(1, Math.min(15, rangedCombat/3)));
					}
				} else if (getRole(role).getId().equals(Role.ROLE_DEFEND) || getRole(role).getId().equals(Role.ROLE_COUNTER)) {
					flavorRow = XmlHelper.newElement(flavors, "Row");
					XmlHelper.newTextElement(flavorRow, "UnitType", type);
					XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_DEFENSE");
					XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(1, Math.min(20, (combat*3+rangedCombat*2)/35)));
				} else if (getRole(role).getId().equals(Role.ROLE_ANTIAIR)) {
					flavorRow = XmlHelper.newElement(flavors, "Row");
					XmlHelper.newTextElement(flavorRow, "UnitType", type);
					XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_ANTIAIR");
					XmlHelper.newTextElement(flavorRow, "Flavor", "25");
				}
				
				if (secondaryRole.equals("UNITAI_EXPLORE")) {
					flavorRow = XmlHelper.newElement(flavors, "Row");
					XmlHelper.newTextElement(flavorRow, "UnitType", type);
					XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_RECON");
					XmlHelper.newTextElement(flavorRow, "Flavor", "4");
				} else if (secondaryRole.equals("UNITAI_EXPLORE_SEA")) {
					flavorRow = XmlHelper.newElement(flavors, "Row");
					XmlHelper.newTextElement(flavorRow, "UnitType", type);
					XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_NAVAL_RECON");
					XmlHelper.newTextElement(flavorRow, "Flavor", "4");
				}
			}
		}
	}
}
