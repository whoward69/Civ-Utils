package me.civ5.modtools.mod.modinfo.properties;

import java.io.PrintStream;

import me.civ5.modutils.log.ModReporter;

public abstract class Property {
	private String name;
	private String value;
	
	public Property(String name) {
		this.name = name;
		this.value = "";
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	public String getEncodedValue() {
		String val = getValue();
		
		val = val.replaceAll("&", "&amp;");
		val = val.replaceAll("<", "&lt;");
		val = val.replaceAll(">", "&gt;");
		
		return val;
	}

	public String getVsValue() {
		String vsValue = getValue();
		
		if (vsValue.trim().length() == 0) {
			return null;
		}
		
		vsValue = vsValue.replaceAll("&", "&amp;");
		vsValue = vsValue.replaceAll("<", "&lt;");
		vsValue = vsValue.replaceAll(">", "&gt;");
		
		return vsValue;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean verify(ModReporter reporter) {
		return true;
	}
	
	public abstract void merge(ModReporter reporter, Property property);

	public void writeAsMod(PrintStream out, String indent) {
		out.print(indent);
		
		out.print("<");
		out.print(getName());
		out.print(">");
		
		out.print(getEncodedValue());
		
		out.print("</");
		out.print(getName());
		out.println(">");
	}
}
