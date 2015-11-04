package me.civ5.modbuilder.ui.panel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;

import me.civ5.modbuilder.ui.ValidityListener;
import me.civ5.modutils.log.ModReporter;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public abstract class InputPanel extends AbstractPanel implements ValidityListener {
	private List<ItemPanel> panels = new ArrayList<ItemPanel>();
	private List<ValidityListener> listeners = new ArrayList<ValidityListener>();
	
	private Box panelStack = Box.createVerticalBox();
	
	public InputPanel(String id) {
		super(id);
		
		panelStack = Box.createVerticalBox();
		panelStack.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		// And this is why I hate Swing Layout Managers with a passion!
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(panelStack);
		verticalBox.add(Box.createVerticalGlue());
		
		add(verticalBox);
	}
	
	@Override
	public void setLanguage(Element parentLanguage) {
		super.setLanguage(parentLanguage);
		
		for (ItemPanel panel : panels) {
			panel.setLanguage(language);
		}
	}
	
	public ItemPanel getItemPanel(String id) {
		ItemPanel panel = new ItemPanel(id);
		panel.setLanguage(language);
		addPanel(panel);
		
		return panel;
	}
	
	public void addPanel(ItemPanel panel) {
		panels.add(panel);
		panel.addValidityListener(this);
		
		panel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		
		panelStack.add(Box.createVerticalStrut(10));
		panelStack.add(panel);
	}
	
	public void serialise(Element container) {
		Element me = XmlHelper.newIdElement(container, "panel", id);
		
		for (ItemPanel panel : panels) {
			panel.serialise(me);
		}
	}
	
	public void deserialise(Element container) {
		Element me = XpathHelper.getElement(container, "./panel[@id='" + id + "']");

		for (ItemPanel panel : panels) {
			panel.deserialise(me);
		}
	}
	
	public boolean verify(boolean loaded) {
		boolean ok = true;
		
		for (ItemPanel panel : panels) {
			ok = panel.verify(loaded) && ok;
		}
		
		return ok;
	}
	
	public void saveFiles(File dir, ModReporter reporter) throws IOException {
		for (ItemPanel panel : panels) {
			panel.saveFiles(dir, reporter);
		}
	}

	public void buildXml(Element gamedata, Element row, String type) {
		for (ItemPanel panel : panels) {
			panel.buildXml(gamedata, row, type);
		}
	}

	public String preBuildXml(Element gamedata, String type) {
		for (ItemPanel panel : panels) {
			type = panel.preBuildXml(gamedata, type);
		}
		
		return type;
	}
	
	public void postBuildXml(Element gamedata, Element row, String type) {
		for (ItemPanel panel : panels) {
			panel.postBuildXml(gamedata, row, type);
		}
	}

	public void addValidityListener(ValidityListener listener) {
		listeners.add(listener);
	}

	@Override
	public void validityUpdate(boolean valid) {
		for (ValidityListener listener : listeners) {
			listener.validityUpdate(valid);
		}
	}
}
