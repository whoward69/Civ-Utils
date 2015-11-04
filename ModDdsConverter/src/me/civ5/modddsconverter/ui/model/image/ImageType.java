package me.civ5.modddsconverter.ui.model.image;

import gov.nasa.worldwind.formats.dds.DDSConstants;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class ImageType {
	private String id;
	
	private String name;
	
	private int width;
	private int height;
	private int defCompression;
	
	protected ImageType(String id, String name, int width, int height, int defCompression) {
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.defCompression = defCompression;
	}
	
	public ImageType(String name, Element type) {
		this.id = XpathHelper.getString(type, "./@id");
		this.name = name;
		this.width = XpathHelper.getInt(type, "./@width", -1);
		this.height = XpathHelper.getInt(type, "./@height", -1);
		
		int compression = XpathHelper.getInt(type, "./@format", 3);
		this.defCompression = (compression == 1) ? DDSConstants.D3DFMT_DXT1 : (compression == 5) ? DDSConstants.D3DFMT_DXT5 : DDSConstants.D3DFMT_DXT3;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public int getDefCompression() {
		return defCompression;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
