package me.civ5.modbuilder.ui.control.unit;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComboBox;

import me.civ5.modbuilder.ui.control.select.SelectItem;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class PrimaryRoleItem extends SelectItem {
	public static Map<String, Role> baseRoles = new LinkedHashMap<String, PrimaryRoleItem.Role>();
	static { // TODO - text - base roles
		baseRoles.put("Melee Attack",  new Role(Role.ROLE_MELEE, "ATTACK", "ATTACK_SEA", "ATTACK_AIR", true));
		baseRoles.put("Ranged Attack", new Role(Role.ROLE_RANGED, "RANGED", "ASSULT_SEA", "ATTACK_AIR"));
		baseRoles.put("City Bombard",  new Role("bombard", "CITY_BOMBARD", "ASSULT_SEA", "ATTACK_AIR"));
		baseRoles.put("Defend",        new Role(Role.ROLE_DEFEND, "DEFENSE", "ATTACK_SEA", "DEFENSE_AIR"));
		baseRoles.put("Counter",       new Role(Role.ROLE_COUNTER, "COUNTER", "ATTACK_SEA", "DEFENSE_AIR"));
		baseRoles.put("Explore",       new Role("explore", "EXPLORE", "EXPLORE_SEA", "DEFENSE_AIR"));
	}
	
	public static Map<String, Role> primaryRoles = new LinkedHashMap<String, PrimaryRoleItem.Role>();
	static { // TODO - text - primary roles
		primaryRoles.putAll(baseRoles);
		primaryRoles.put("Carrier (Sea only)",    new Role("carrier", "UNKNOWN", "CARRIER_SEA", "UNKNOWN"));
		primaryRoles.put("Pirate (Sea only)",     new Role("pirate", "UNKNOWN", "PIRATE_SEA", "UNKNOWN"));
		primaryRoles.put("Missile (Air only)",    new Role("missile", "UNKNOWN", "UNKNOWN", "MISSILE_AIR"));
		primaryRoles.put("Paratroop (Land only)", new Role("paratroop", "PARADROP", "UNKNOWN", "UNKNOWN"));
	}
	
	public PrimaryRoleItem(String id, String tag) {
		super(id, tag, null, null, null, null, null);

		JComboBox<SelectEntry> c = getComboControl();
		for (String role : primaryRoles.keySet()) {
			c.addItem(new SelectEntry(role, role, role));
		}
	}
	
	protected Role getRole() {
		return primaryRoles.get(getText());
	}
	
	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		// We have to do the save last, or at least after Moves have been serialised
		// but add the required tables now, so they come before the language table
		XmlHelper.getChildElement(gamedata, "Unit_AITypes");
		XmlHelper.getChildElement(gamedata, "Unit_Flavors");
	}
	
	@Override
	public void postBuildXml(Element gamedata, Element row, String type) {
		String domain = XpathHelper.getString(row, "./Domain");
		int combat = XpathHelper.getInt(row, "./Combat", 0);
		int rangedCombat = XpathHelper.getInt(row, "./RangedCombat", 0);
		int moves = XpathHelper.getInt(row, "./Moves", 2);
		int nuke = XpathHelper.getInt(row, "./NukeDamageLevel", -1);
		String primaryRole = primaryRoles.get(getText()).getAiType(domain, moves);
		
		XmlHelper.newTextElement(row, getTag(), primaryRole);
		
		Element aiTypes = XmlHelper.getChildElement(gamedata, "Unit_AITypes");
		
		Element aiRow = XmlHelper.newElement(aiTypes, "Row");
		XmlHelper.newTextElement(aiRow, "UnitType", type);
		XmlHelper.newTextElement(aiRow, "UnitAIType", primaryRole);
		
		if ("DOMAIN_SEA".equals(domain)) {
			aiRow = XmlHelper.newElement(aiTypes, "Row");
			XmlHelper.newTextElement(aiRow, "UnitType", type);
			XmlHelper.newTextElement(aiRow, "UnitAIType", "UNITAI_RESERVE_SEA");

			aiRow = XmlHelper.newElement(aiTypes, "Row");
			XmlHelper.newTextElement(aiRow, "UnitType", type);
			XmlHelper.newTextElement(aiRow, "UnitAIType", "UNITAI_ESCORT_SEA");
		}
		
		Element flavors = XmlHelper.getChildElement(gamedata, "Unit_Flavors");
		Element flavorRow;
		
		if (getRole().getId().equals(Role.ROLE_MELEE)) {
			flavorRow = XmlHelper.newElement(flavors, "Row");
			XmlHelper.newTextElement(flavorRow, "UnitType", type);
			XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_OFFENSE");
			XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(1, Math.min(25, (combat*3+rangedCombat*2)/8)));
		} else if (getRole().getId().equals(Role.ROLE_RANGED)) {
			if ("DOMAIN_LAND".equals(domain) && rangedCombat > 0) {
				flavorRow = XmlHelper.newElement(flavors, "Row");
				XmlHelper.newTextElement(flavorRow, "UnitType", type);
				XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_RANGED");
				XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(1, Math.min(15, rangedCombat/2)));
			}
		} else if (getRole().getId().equals(Role.ROLE_DEFEND) || getRole().getId().equals(Role.ROLE_COUNTER)) {
			flavorRow = XmlHelper.newElement(flavors, "Row");
			XmlHelper.newTextElement(flavorRow, "UnitType", type);
			XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_DEFENSE");
			XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(1, Math.min(20, (combat*3+rangedCombat*2)/15)));
		}

		if (primaryRole.equals("UNITAI_EXPLORE")) {
			flavorRow = XmlHelper.newElement(flavors, "Row");
			XmlHelper.newTextElement(flavorRow, "UnitType", type);
			XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_RECON");
			XmlHelper.newTextElement(flavorRow, "Flavor", "8");
		} else if (primaryRole.equals("UNITAI_EXPLORE_SEA")) {
			flavorRow = XmlHelper.newElement(flavors, "Row");
			XmlHelper.newTextElement(flavorRow, "UnitType", type);
			XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_NAVAL_RECON");
			XmlHelper.newTextElement(flavorRow, "Flavor", "8");
		} else if (primaryRole.equals("UNITAI_PARADROP")) {
			Element promoTable = XmlHelper.getChildElement(gamedata, "Unit_FreePromotions");
			Element promoRow = XmlHelper.newElement(promoTable, "Row");
			XmlHelper.newTextElement(promoRow, "UnitType", type);
			XmlHelper.newTextElement(promoRow, "PromotionType", "PROMOTION_PARADROP");
		}
		
		if ("DOMAIN_AIR".equals(domain)) {
			flavorRow = XmlHelper.newElement(flavors, "Row");
			XmlHelper.newTextElement(flavorRow, "UnitType", type);
			XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_AIR");
			XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(10, Math.min(25, rangedCombat/3.5)));

			flavorRow = XmlHelper.newElement(flavors, "Row");
			XmlHelper.newTextElement(flavorRow, "UnitType", type);
			XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_ANTIAIR");
			XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(10, Math.min(15, rangedCombat/3.5)));
		} else if ("DOMAIN_SEA".equals(domain)) {
			flavorRow = XmlHelper.newElement(flavors, "Row");
			XmlHelper.newTextElement(flavorRow, "UnitType", type);
			XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_NAVAL");
			XmlHelper.newTextElement(flavorRow, "Flavor", "" + Math.max(4, Math.min(25, Math.max(combat, rangedCombat)/3)));
		} else {
			if (moves > 2) {
				flavorRow = XmlHelper.newElement(flavors, "Row");
				XmlHelper.newTextElement(flavorRow, "UnitType", type);
				XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_MOBILE");
				XmlHelper.newTextElement(flavorRow, "Flavor", "" + (moves*3));
			}
		}
		
		if (nuke >= 0) {
			flavorRow = XmlHelper.newElement(flavors, "Row");
			XmlHelper.newTextElement(flavorRow, "UnitType", type);
			XmlHelper.newTextElement(flavorRow, "FlavorType", "FLAVOR_NUKE");
			XmlHelper.newTextElement(flavorRow, "Flavor", "" + (nuke+1)*8);
		}
	}
	
	public static class Role {
		public static final String ROLE_MELEE   = "melee";
		public static final String ROLE_RANGED  = "ranged";
		public static final String ROLE_DEFEND  = "defend";
		public static final String ROLE_COUNTER = "counter";
		public static final String ROLE_ANTIAIR = "antiair";
		
		private String id;
		private String land;
		private String sea;
		private String air;
		private boolean fast;
		
		public Role(String id, String land, String sea, String air) {
			this(id, land, air, sea, false);
		}
		
		public Role(String id, String land, String sea, String air, boolean fast) {
			this.id = id;
			this.land = land;
			this.sea = sea;
			this.air = air;
			this.fast = fast;
		}
		
		public String getId() {
			return id;
		}
		
		public String getAiType(String domain, int moves) {
			String aiType;
			
			if ("DOMAIN_AIR".equals(domain)) {
				aiType = air;
			} else if ("DOMAIN_SEA".equals(domain)) {
				aiType = sea;
			} else {
				if (fast && moves > 2) {
					aiType = "FAST_" + land;
				} else {
					aiType = land;
				}
			}
			
			return "UNITAI_" + aiType;
		}
	}
}
