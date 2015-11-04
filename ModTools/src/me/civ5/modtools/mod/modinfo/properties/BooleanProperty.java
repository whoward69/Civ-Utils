package me.civ5.modtools.mod.modinfo.properties;


public abstract class BooleanProperty extends Property {
	public BooleanProperty(String name, boolean value) {
		super(name);
		setValue(value);
	}
	
	public boolean isTrue() {
		return (getValueAsInt() != 0);
	}

	public boolean isFalse() {
		return (getValueAsInt() == 0);
	}

	@Override
	public String getVsValue() {
		return (isTrue() ? "true" : "false");
	}

	public abstract int getValueAsInt();
	
	public void setValue(boolean value) {
		setValue(value ? 1 : 0);
	}

	public void setValue(int value) {
		setValue(Integer.toString(value));
	}
}
