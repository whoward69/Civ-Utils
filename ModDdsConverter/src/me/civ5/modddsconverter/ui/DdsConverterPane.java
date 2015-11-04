package me.civ5.modddsconverter.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import me.civ5.modddsconverter.ui.model.grid.AtlasCellModelFactory;
import me.civ5.modddsconverter.ui.panel.AbstractPanel;
import me.civ5.modddsconverter.ui.panel.AtlasPanel;
import me.civ5.modddsconverter.ui.panel.IconsPanel;
import me.civ5.modddsconverter.ui.panel.ImagesPanel;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class DdsConverterPane extends JTabbedPane {
	private List<AbstractPanel> panels = new ArrayList<AbstractPanel>();
	private int imageHWM = 0;
	private int iconsHWM = 0;
	
	private DdsConverterFrame owner;

	private String id;
	private Element language, configs;
	
	public DdsConverterPane(DdsConverterFrame owner, String id, Element configs) {
		this.owner = owner;
		this.id = id;
		this.configs = configs;
	}
	
	public void setLanguage(Element parentLanguage) {
		language = XpathHelper.getElement(parentLanguage, id);

		for (AbstractPanel panel : panels) {
			panel.setLanguage(language);
		}
	}
	
	public Element serialise(Element container) {
		Element me = XmlHelper.newIdElement(container, "pane", id);
		me.setAttribute("selected", Integer.toString(getSelectedIndex()));
		
		int i = 0;
		for (AbstractPanel panel : panels) {
			panel.setId(Integer.toString(i++));
			panel.serialise(me);
		}
		
		return me;
	}
	
	public void deserialise(Element container) {
		Element me = XpathHelper.getElement(container, "./pane[@id='" + id + "']");

		for (Element p : XpathHelper.getElements(me, "./panel[@canClose='true']")) {
			String type = XpathHelper.getString(p, "./@type");
			
			if ("images".equals(type)) {
				add(new ImagesPanel(owner, XpathHelper.getElement(configs, "./config[@type='images']"), true)); 
			} else if ("icons".equals(type)) {
				add(new IconsPanel(owner, XpathHelper.getElement(configs, "./config[@type='icons']"), true)); 
			} else if ("atlas".equals(type)) {
				add(new AtlasPanel(owner, XpathHelper.getElement(configs, "./config[@type='atlas']"), true)); 
			}
		}

		int i = 0;
		for (AbstractPanel panel : panels) {
			panel.setId(Integer.toString(i++));
			panel.deserialise(me);
		}
	}
	
	public AbstractPanel add(AbstractPanel panel) {
		int index;
		
		panel.setLanguage(language);

		if (panel instanceof ImagesPanel) {
			index = imageHWM;
			insertTab(panel.getTitle(), null, panel, panel.getTip(), index);
			panels.add(imageHWM, panel);
		
			++imageHWM;
		} else if (panel instanceof IconsPanel) {
			index = imageHWM + iconsHWM;
			insertTab(panel.getTitle(), null, panel, panel.getTip(), index);
			panels.add(imageHWM + iconsHWM, panel);
			
			++iconsHWM;

			AtlasCellModelFactory.addPanel((IconsPanel) panel);
		} else {
			index = getTabCount();
			addTab(panel.getTitle(), null, panel, panel.getTip());
			panels.add(panel);
		}
		
		setSelectedIndex(index);
		
		return panel;
	}
	
	public void remove(AbstractPanel panel) {
		int index = getSelectedIndex();
		
		removeTabAt(index);
		panels.remove(index);

		if (panel instanceof ImagesPanel) {
			imageHWM--;
		} else if (panel instanceof IconsPanel) {
			iconsHWM--;
			
			if (iconsHWM <= 1) {
				int i = imageHWM + iconsHWM;
				while (i < panels.size()) {
					removeTabAt(i);
					panels.remove(i);
				}
			}
		}
	}
}
