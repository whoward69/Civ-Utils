package me.civ5.ui.filter;


public class IconFilter extends ModFileFilter {
	public IconFilter() {
		super(null, "Icon Files");
		addExtn("jpg");
		addExtn("jpeg");
		addExtn("png");
	}
}