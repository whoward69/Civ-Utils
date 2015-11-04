package me.civ5.modbuilder.ui.control.art;

import java.io.File;
import java.io.IOException;

import me.civ5.modutils.log.ModReporter;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class SvIconItem extends IconItem {
	private String svFlagName = null;
	
	public SvIconItem(String id) {
		super(id, 128, 0);
	}

	@Override
	public void saveFiles(File dir, ModReporter reporter) throws IOException {
		if (svFlagName != null) {
			saveIcon(128, 0, new File(dir, svFlagName), reporter);
		}
	}
	
	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		if (canSave()) {
			svFlagName = "SV_FLAG_" + type + ".dds";
	
			Element artSV = XmlHelper.newElement(XmlHelper.newElement(gamedata, "ArtDefine_StrategicView"), "Row");
			XmlHelper.newTextElement(artSV, "StrategicViewType", "ART_DEF_" + type);
			XmlHelper.newTextElement(artSV, "TileType", "Unit");
			XmlHelper.newTextElement(artSV, "Asset", svFlagName);
		}
	}
}
