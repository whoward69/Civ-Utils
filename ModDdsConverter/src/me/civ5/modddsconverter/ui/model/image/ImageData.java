package me.civ5.modddsconverter.ui.model.image;

import java.nio.ByteBuffer;

public class ImageData {
	private String name, qualifier, extn;
	private ByteBuffer data;
	
	public ImageData(String name, String qualifier, String extn, ByteBuffer data) {
		this.name = name;
		this.qualifier = (qualifier != null) ? qualifier.startsWith("_") ? qualifier : ("_" + qualifier) : "";
		this.extn = extn.startsWith(".") ? extn : ("." + extn);
		
		this.data = data;
	}

	public ImageData(String base, String qualifier, String extn, byte[] data) {
		this(base, qualifier, extn, ByteBuffer.wrap(data));
	}

	public String getName() {
		return makeName(name);
	}

	public String getQualifier() {
		return qualifier;
	}

	public String getExtn() {
		return extn;
	}

	public ByteBuffer getData() {
		return data;
	}
	
	public String makeName(String base) {
		return base + qualifier + extn;
	}
}
