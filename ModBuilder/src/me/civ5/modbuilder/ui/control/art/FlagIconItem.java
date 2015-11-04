package me.civ5.modbuilder.ui.control.art;

import java.io.File;
import java.io.IOException;

import me.civ5.modutils.log.ModReporter;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class FlagIconItem extends IconItem {
	private String flagName = null;
	
	public FlagIconItem(String id) {
		super(id, 32, 0);
	}

	@Override
	public void saveFiles(File dir, ModReporter reporter) throws IOException {
		if (flagName != null) {
			saveIcon(32, 0, new File(dir, flagName), reporter);
		}
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		if (canSave()) {
			flagName = "FLAG_" + type + ".dds";
			String flagAtlas = "ATLAS_FLAG_" + type;
	
			XmlHelper.newTextElement(row, "UnitFlagAtlas", flagAtlas);
			XmlHelper.newTextElement(row, "UnitFlagIconOffset", "0");
	
			Element atlas = XmlHelper.newElement(XmlHelper.getChildElement(gamedata, "IconTextureAtlases"), "Row");
			XmlHelper.newTextElement(atlas, "Atlas", flagAtlas);
			XmlHelper.newTextElement(atlas, "IconSize", "32");
			XmlHelper.newTextElement(atlas, "Filename", flagName);
			XmlHelper.newTextElement(atlas, "IconsPerRow", "1");
			XmlHelper.newTextElement(atlas, "IconsPerColumn", "1");
		}
	}
}

