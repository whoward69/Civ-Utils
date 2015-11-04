package me.civ5.modbuilder.ui.control.bool;


public class FalseBooleanItem extends BooleanItem {
	public FalseBooleanItem(String id, String tag) {
		this(id, tag, false);
	}

	public FalseBooleanItem(String id, String tag, boolean initValue) {
		super(id, tag, false, initValue);
	}
}
