package me.civ5.modbuilder.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import me.civ5.modbuilder.ui.panel.AbstractPanel;
import me.civ5.modbuilder.ui.panel.InputPanel;
import me.civ5.modutils.log.ModReporter;
import me.civ5.xml.XmlHelper;
import me.civ5.xml.XmlOutputHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Document;
import org.jdom.Element;

public class ModBuilderPane extends JTabbedPane implements ValidityListener {
	private List<InputPanel> panels = new ArrayList<InputPanel>();
	private List<ValidityListener> listeners = new ArrayList<ValidityListener>();
	
	private String id;
	private Element language;
	
	private boolean loaded = false;
	
	public ModBuilderPane(String id) {
		this.id = id;
	}
	
	public void setLanguage(Element parentLanguage) {
		language = XpathHelper.getElement(parentLanguage, id);

		for (InputPanel panel : panels) {
			panel.setLanguage(language);
		}
	}
	
	public AbstractPanel add(AbstractPanel panel) {
		panel.setLanguage(language);
		
		if (panel instanceof InputPanel) {
			InputPanel inPanel = (InputPanel) panel;
			
			panels.add(inPanel);
			inPanel.addValidityListener(this);
		}
		
		addTab(panel.getTitle(), null, panel, panel.getTip());
		
		return panel;
	}
	
	public void serialise(Element container) {
		Element me = XmlHelper.newIdElement(container, "pane", id);
		
		for (InputPanel panel : panels) {
			panel.serialise(me);
		}
	}
	
	public void deserialise(Element container) {
		Element me = XpathHelper.getElement(container, "./pane[@id='" + id + "']");

		for (InputPanel panel : panels) {
			panel.deserialise(me);
		}
	}
	
	public void verify(boolean loaded) {
		this.loaded = loaded;
		
		int current = getSelectedIndex();
		boolean ok = panels.get(current).verify(loaded);

		for (int tab = 0; tab < getTabCount(); ++tab) {
			setEnabledAt(tab, (tab == current) || ok);
		}
		

		for (ValidityListener listener : listeners) {
			listener.validityUpdate(ok);
		}
	}
	
	public void saveFiles(File dir, ModReporter reporter) throws IOException {
		for (InputPanel panel : panels) {
			panel.saveFiles(dir, reporter);
		}
	}

	public String buildXml(Document xml) {
		Element gamedata = xml.getRootElement();
		String type = preBuildXml(gamedata);

		Element row = XmlHelper.getChildElement(XmlHelper.getChildElement(gamedata, "Units"), "Row"); 
		
		for (InputPanel panel : panels) {
			panel.buildXml(gamedata, row, type);
		}
		
		postBuildXml(gamedata, row, type);
		
		return XmlOutputHelper.prettyOutput(xml, "UTF-8");
	}
	
	protected String preBuildXml(Element gamedata) {
		String type = "UNKNOWN";
		
		for (InputPanel panel : panels) {
			type = panel.preBuildXml(gamedata, type);
		}
		
		return type;
	}

	protected void postBuildXml(Element gamedata, Element row, String type) {
		for (InputPanel panel : panels) {
			panel.postBuildXml(gamedata, row, type);
		}
	}

	public void addValidityListener(ValidityListener listener) {
		listeners.add(listener);
	}

	@Override
	public void validityUpdate(boolean valid) {
		verify(loaded);
	}
}
