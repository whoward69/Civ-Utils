package me.civ5.modbuilder.ui.panel;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import me.civ5.modbuilder.ui.ValidityListener;
import me.civ5.modbuilder.ui.control.ModBuilderControl;
import me.civ5.modutils.log.ModReporter;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class ItemPanel extends JPanel implements ValidityListener {
	private String id;
	private Element language;
	
	private boolean loaded = false;

	private GroupLayout layout;
	private SequentialGroup rows;
	private ParallelGroup labelStack;
	private ParallelGroup controlStack;
	
	private List<ModBuilderControl> controls = new ArrayList<ModBuilderControl>();
	private List<ValidityListener> listeners = new ArrayList<ValidityListener>();
	
	private Border validBorder;
	private Border invalidBorder;
	
	public ItemPanel(String id) {
		super();
		this.id = id;
		
		// And this is why I hate Swing Layout Managers with a passion!
		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		rows = layout.createSequentialGroup();
		labelStack = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		controlStack = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(labelStack).addGroup(controlStack));
		layout.setVerticalGroup(rows);
	}
	
	public void setLanguage(Element parentLanguage) {
		language = XpathHelper.getElement(parentLanguage, id);
		
		String title = XpathHelper.getString(language, "./@name");

		validBorder = BorderFactory.createTitledBorder(title);
		invalidBorder = BorderFactory.createTitledBorder(null, title, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, Color.RED);

		setBorder(validBorder);
		// setToolTipText(tip);

		for (ModBuilderControl control : controls) {
			control.setLanguage(language);
		}
	}

	public ModBuilderControl addControl(ModBuilderControl control) {
		control.setLanguage(language);
		
		controls.add(control);
		control.addValidityListener(this);
		
		labelStack.addComponent(control.getLabel());
		controlStack.addComponent(control.getControl());
		
		rows.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(control.getLabel()).addComponent(control.getControl()));
		
		verify(loaded);
		
		return control;
				
	}
	
	public void addValidityListener(ValidityListener l) {
		listeners.add(l);
	}
	
	public void serialise(Element container) {
		Element me = XmlHelper.newIdElement(container, "group", id);
		
		for (ModBuilderControl control : controls) {
			control.serialise(me);
		}
	}
	
	public void deserialise(Element container) {
		Element me = XpathHelper.getElement(container, "./group[@id='" + id + "']");

		for (ModBuilderControl control : controls) {
			control.deserialise(me);
		}
	}
	
	public boolean verify(boolean loaded) {
		boolean ok = true;
		this.loaded = loaded;
		
		for (ModBuilderControl control : controls) {
			ok = control.verify(loaded) && ok;
		}
		
		setBorder(ok ? validBorder : invalidBorder);

		return ok;
	}
	
	public void saveFiles(File dir, ModReporter reporter) throws IOException {
		for (ModBuilderControl control : controls) {
			control.saveFiles(dir, reporter);
		}
	}

	public void buildXml(Element gamedata, Element row, String type) {
		for (ModBuilderControl control : controls) {
			control.buildXml(gamedata, row, type);
		}
	}

	public String preBuildXml(Element gamedata, String type) {
		for (ModBuilderControl control : controls) {
			type = control.preBuildXml(gamedata, type);
		}
		
		return type;
	}
	
	public void postBuildXml(Element gamedata, Element row, String type) {
		for (ModBuilderControl control : controls) {
			control.postBuildXml(gamedata, row, type);
		}
	}

	@Override
	public void validityUpdate(boolean valid) {
		verify(loaded);
		
		for (ValidityListener l : listeners) {
			l.validityUpdate(valid);
		}
	}
}
