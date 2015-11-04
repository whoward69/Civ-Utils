package me.civ5.modbuilder.ui.model;


public class SelectEntry implements Comparable<SelectEntry> {
	String key;
	String value;
	String tip;
	
	public SelectEntry(String key, String value, String tip) {
		this.key = key;
		this.value = value;
		this.tip = tip;
	}
	
	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getTip() {
		return tip;
	}

	@Override
	public int compareTo(SelectEntry that) {
		return (value+key).compareTo(that.value+that.key);
	}

	@Override
	public int hashCode() {
		return (value+key).hashCode();
	}

	@Override
	public boolean equals(Object that) {
		return ((that instanceof SelectEntry) && (this.key == ((SelectEntry) that).key));
	}

	@Override
	public String toString() {
		return getValue();
	}
}
