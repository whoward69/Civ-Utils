package me.civ5.modddsconverter.ui.model.icon;

import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class IconType {
	private String id;

	private String name;
	private String desc;

	private Integer[] sizes;
	private boolean mipMaps;
	private boolean square;

	protected IconType(String id, String name, Integer[] sizes, boolean mipMaps, boolean square) {
		this.id = id;
		this.name = name;
		this.sizes = sizes;
		this.mipMaps = mipMaps;
		this.square = square;
	}
	
	public IconType(String name, Element type) {
		this.id = XpathHelper.getString(type, "./@id");
		this.name = name;
		this.mipMaps = XpathHelper.getBoolean(type, "./@mipmaps", false);
		this.square = XpathHelper.getBoolean(type, "./@square", false);

		String icons = type.getTextNormalize();
		
		if (icons.length() == 0) {
			this.sizes = new Integer[] {null};
		} else {
			String[] sizes = icons.split("[, ]+");
			this.sizes = new Integer[sizes.length];
			for (int i = 0; i < sizes.length; i++) {
				this.sizes[i] = new Integer(sizes[i]);
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public Integer[] getSizes() {
		return sizes;
	}

	public boolean isMipMaps() {
		return mipMaps;
	}

	public boolean isSquare() {
		return square;
	}

	@Override
	public String toString() {
		return name;
	}
}
