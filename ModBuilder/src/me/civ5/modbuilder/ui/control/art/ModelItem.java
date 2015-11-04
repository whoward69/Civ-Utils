package me.civ5.modbuilder.ui.control.art;

import java.util.List;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.select.SelectItem;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class ModelItem extends SelectItem {
	private ModDb db;
	
	public ModelItem(String id, ModDb db) {
		super(id, "notag", db, "Units", "Type", "Description", "UnitArtInfoCulturalVariation=0 AND UnitArtInfoEraVariation=0");
		this.db = db;
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		Object[] cols = db.executeQuery("SELECT UnitArtInfo, MoveRate, Mechanized FROM Units WHERE Type=?", new Object[] {getText()}, 3);

		if (cols != null) {
			String artdefPrefix = "ART_DEF_";
			String copyArtDef = (String) cols[0];
			
			XmlHelper.newTextElement(row, "MoveRate", (String) cols[1]);
			XmlHelper.newTextElement(row, "Mechanized", Boolean.toString(((Integer) cols[2]) == 1));

			XmlHelper.newTextElement(row, "UnitArtInfo", artdefPrefix + type);
			// Don't generate the default false values for the next two items
			// XmlHelper.newTextElement(row, "UnitArtInfoCulturalVariation", "false");
			// XmlHelper.newTextElement(row, "UnitArtInfoEraVariation", "false");

			cols = db.executeQuery("SELECT DamageStates, Formation FROM ArtDefine_UnitInfos WHERE Type=?", new Object[] {copyArtDef}, 2);
			Element artUnit = XmlHelper.newElement(XmlHelper.newElement(gamedata, "ArtDefine_UnitInfos"), "Row");
			XmlHelper.newTextElement(artUnit, "Type", artdefPrefix + type);
			XmlHelper.newTextElement(artUnit, "DamageStates", ((Integer) cols[0]).toString());
			XmlHelper.newTextElement(artUnit, "Formation", ((String) cols[1]));

			List<Object[]> artRows = db.executeQuery("SELECT UnitMemberInfoType, NumMembers FROM ArtDefine_UnitInfoMemberInfos WHERE UnitInfoType=?", new Object[] {copyArtDef}, 2, 10);
			Element artMembers = XmlHelper.newElement(gamedata, "ArtDefine_UnitInfoMemberInfos");
			for (Object[] artCol : artRows) {
				Element artMember = XmlHelper.newElement(artMembers, "Row");
				XmlHelper.newTextElement(artMember, "UnitInfoType", artdefPrefix + type);
				XmlHelper.newTextElement(artMember, "UnitMemberInfoType", ((String) artCol[0]));
				XmlHelper.newTextElement(artMember, "NumMembers", ((Integer) artCol[1]).toString());
			}

			// Get the sounds as well
			cols = db.executeQuery("SELECT SelectionSound, FirstSelectionSound FROM UnitGameplay2DScripts WHERE UnitType=?", new Object[] {getText()}, 2);
			Element sounds = XmlHelper.newElement(XmlHelper.newElement(gamedata, "UnitGameplay2DScripts"), "Row");
			XmlHelper.newTextElement(sounds, "UnitType", type);
			XmlHelper.newTextElement(sounds, "SelectionSound", ((String) cols[0]));
			XmlHelper.newTextElement(sounds, "FirstSelectionSound", ((String) cols[1]));
		}
	}
}
