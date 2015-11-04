package me.civ5.modtools.mod.modinfo.properties;


public class CustomProperty extends StringProperty {
	public CustomProperty(String name) {
		super(name);
	}

	public CustomProperty(String name, String value) {
		this(name);
		setValue(value);
	}
}
