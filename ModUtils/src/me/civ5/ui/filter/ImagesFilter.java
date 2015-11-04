package me.civ5.ui.filter;


public class ImagesFilter extends ModFileFilter {
	public ImagesFilter() {
		super(null, "Image Files");
		addExtn("gif");
		addExtn("jpg");
		addExtn("jpeg");
		addExtn("png");
		addExtn("dds");
	}
}