package me.civ5.modbuilder.ui.control.bool;


public class TrueBooleanItem extends BooleanItem {
	public TrueBooleanItem(String id, String tag) {
		this(id, tag, true);
	}

	public TrueBooleanItem(String id, String tag, boolean initValue) {
		super(id, tag, true, initValue);
	}
}
