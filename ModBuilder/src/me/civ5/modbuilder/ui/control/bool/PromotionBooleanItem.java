package me.civ5.modbuilder.ui.control.bool;

import org.jdom.Element;

public class PromotionBooleanItem extends FalseBooleanItem {
	public PromotionBooleanItem(String id, String tag) {
		super(id, tag);
	}

	@Override
	public void buildXml(Element gamedata, Element row, String type) {
		// Handled by PromotionMultiSelectItem
	}
}
