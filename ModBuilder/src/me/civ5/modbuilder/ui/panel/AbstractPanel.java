package me.civ5.modbuilder.ui.panel;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public abstract class AbstractPanel extends JPanel {
	String id;
	Element language;
	
	private String title;
	private String tip;
	
	public AbstractPanel(LayoutManager layout, String id) {
		super(layout);

		this.id = id;
	}

	public AbstractPanel(String id) {
		super();

		this.id = id;
	}
	
	public void setLanguage(Element parentLanguage) {
		language = XpathHelper.getElement(parentLanguage, id);
		
		title = XpathHelper.getString(language, "./@name");
		tip = XpathHelper.getString(language, "./@tip", title);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getTip() {
		return tip;
	}
}
