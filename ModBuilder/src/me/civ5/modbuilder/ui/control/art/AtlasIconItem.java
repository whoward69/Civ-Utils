package me.civ5.modbuilder.ui.control.art;

import java.io.File;
import java.io.IOException;

import me.civ5.modutils.log.ModReporter;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class AtlasIconItem extends IconItem {
	private String type = null;
	
	private int[][] sizes;

	public AtlasIconItem(String id, int[][] sizes) {
		super(id, sizes[1][0], sizes[1][1]);
		this.sizes = sizes;
	}
	
	private String getIconName(int size) {
		return "ICON_" + type + "_" + Integer.toString(size) + ".dds";
	}

	@Override
	public void saveFiles(File dir, ModReporter reporter) throws IOException {
		if (type != null) {
			for (int[] size : sizes) {
				saveIcon(size[0], size[1], new File(dir, getIconName(size[0])), reporter); 
			}
		}
	}
	
	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		this.type = type;

		if (canSave()) {
			String iconAtlas = "ATLAS_ICON_" + type;
			
			XmlHelper.newTextElement(row, "IconAtlas", iconAtlas);
			XmlHelper.newTextElement(row, "PortraitIndex", "0");
	
			Element atlases = XmlHelper.getChildElement(gamedata, "IconTextureAtlases");
	
			for (int[] size : sizes) {
				Element atlas = XmlHelper.newElement(atlases, "Row");
				XmlHelper.newTextElement(atlas, "Atlas", iconAtlas);
				XmlHelper.newTextElement(atlas, "IconSize", Integer.toString(size[0]));
				XmlHelper.newTextElement(atlas, "Filename", getIconName(size[0]));
				XmlHelper.newTextElement(atlas, "IconsPerRow", "1");
				XmlHelper.newTextElement(atlas, "IconsPerColumn", "1");
			}
		}
	}
}
