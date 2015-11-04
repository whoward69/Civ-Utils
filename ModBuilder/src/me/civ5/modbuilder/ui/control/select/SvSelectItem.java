package me.civ5.modbuilder.ui.control.select;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class SvSelectItem extends EmptySelectItem {
	private ModDb db;
	
	public SvSelectItem(String id, ModDb db) {
		super(id, "notag", db, "Units", "UnitArtInfo", "Description", "Cost>0 AND (Combat>0 OR RangedCombat>0)");
		this.db = db;
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		if (!isEmpty()) {
			Object[] cols = db.executeQuery("SELECT Asset FROM ArtDefine_StrategicView WHERE StrategicViewType=? AND TileType='Unit'", new Object[] {getText()}, 1);
	
			if (cols != null) {
				Element artSV = XmlHelper.newElement(XmlHelper.newElement(gamedata, "ArtDefine_StrategicView"), "Row");
				XmlHelper.newTextElement(artSV, "StrategicViewType", "ART_DEF_" + type);
				XmlHelper.newTextElement(artSV, "TileType", "Unit");
				XmlHelper.newTextElement(artSV, "Asset", (String) cols[0]);
			}
		}
	}
}
