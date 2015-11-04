package me.civ5.modbuilder.ui.control;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;

import me.civ5.modbuilder.ui.ValidityListener;
import me.civ5.modutils.log.ModReporter;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public abstract class ModBuilderControl {
	private String id;
	protected Element language;
	
	protected boolean loaded = false;

	private JComponent component;
	private JLabel label;
	
	private String title;
	private String tip;
	private String tag;

	protected String linkedTag = null;
	protected ModBuilderControl linkedControl = null;
	protected ModBuilderControl invertedLinkedControl = null;
	
	protected static Color labelInvalid = Color.red;
	protected static Color labelValid = (new JLabel()).getForeground();
	protected static Border borderInvalid = BorderFactory.createLineBorder(Color.RED, 2);
	protected Border borderValid;

	private List<ValidityListener> listeners = new ArrayList<ValidityListener>();
	
	public ModBuilderControl(String id, JComponent component, String tag) {
		this.id = id;
		this.tag = tag;
		
		this.label = new JLabel();
		this.component = component;
		borderValid = component.getBorder();
	}

	public String getId() {
		return id;
	}
	
	public void setLanguage(Element parentLanguage) {
		language = XpathHelper.getElement(parentLanguage, id);
		
		title = XpathHelper.getString(language, "./@name");
		tip = XpathHelper.getString(language, ".", XpathHelper.getString(language, "./@tip"));
		
		label.setText(title);
		label.setToolTipText(tip);
		component.setToolTipText(tip);
	}
	
	public String getTitle() {
		return title;
	}

	public String getTip() {
		return tip;
	}
	
	public String getTag() {
		return tag;
	}
	
	public abstract String getText();
	
	public void setEnabled(boolean enabled) {
		getLabel().setEnabled(enabled);
		getControl().setEnabled(enabled);
	}
	
	public void setLinkedControl(ModBuilderControl linkedControl, String linkedTag) {
		setLinkedControl(linkedControl);
		this.linkedTag = linkedTag;
	}
	
	public void setLinkedControl(ModBuilderControl linkedControl) {
		this.linkedControl = linkedControl;
	}
	
	public void setInvertedLinkedControl(ModBuilderControl invertedLinkedControl) {
		this.invertedLinkedControl = invertedLinkedControl;
	}
	
	public JLabel getLabel() {
		return label;
	}

	public JComponent getControl() {
		return component;
	}
	
	public void addValidityListener(ValidityListener listener) {
		listeners.add(listener);
	}
	
	protected void fireValidityChange(boolean valid) {
		for (ValidityListener listener : listeners) {
			listener.validityUpdate(valid);
		}
	}

	protected void setValid(boolean valid) {
		if (valid) {
			getLabel().setForeground(labelValid);
			getControl().setBorder(borderValid);
		} else {
			getLabel().setForeground(labelInvalid);
			getControl().setBorder(borderInvalid);
		}
	}

	public void serialise(Element container) {
		Element me = XmlHelper.newIdElement(container, "control", id);
		serialiseImpl(me);
	}
	
	protected abstract void serialiseImpl(Element me);
	
	public void deserialise(Element container) {
		Element me = XpathHelper.getElement(container, "./control[@id='" + id + "']");
		deserialiseImpl(me);
	}
	
	protected abstract void deserialiseImpl(Element me);
	
	public boolean verify(boolean loaded) {
		this.loaded = loaded;
		
		return true;
	}
	
	public abstract boolean isDefault();

	public void saveFiles(File dir, ModReporter reporter) throws IOException {}
		
	public void buildXml(Element gamedata, Element row, String type) {
		if (getTag() != null) {
			if (!isDefault()) {
				buildXmlImpl(gamedata, row, type);
			}
		}
	}
	
	protected abstract void buildXmlImpl(Element gamedata, Element row, String type);
	public String preBuildXml(Element gamedata, String type) {return type;}
	public void postBuildXml(Element gamedata, Element row, String type) {}
	
	private String getTxtKey(String key) {
		return "TXT_KEY_" + key;
	}
	
	protected void addTxtKeyTag(Element row, String tag, String key) {
		XmlHelper.newTextElement(row, tag, getTxtKey(key));
	}
	
	protected void addEnUsText(Element gamedata, String key, String text) {
		addLangText(gamedata, "Language_en_US", key, text);
	}
	private void addLangText(Element gamedata, String lang, String key, String text) {
		Element langTable = XmlHelper.getChildElement(gamedata, lang);
		Element langRow = XmlHelper.newElement(langTable, "Row");
		langRow.setAttribute("Tag", getTxtKey(key));
		XmlHelper.newTextElement(langRow, "Text", text);
	}
}
