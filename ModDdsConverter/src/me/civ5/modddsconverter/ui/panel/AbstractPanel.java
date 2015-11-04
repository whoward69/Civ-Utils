package me.civ5.modddsconverter.ui.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import me.civ5.modddsconverter.ui.DdsConverterFrame;
import me.civ5.modutils.ui.action.ModAction;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public abstract class AbstractPanel extends JPanel {
	protected DdsConverterFrame owner;
	
	protected String id;
	protected String type;
	protected Element config;
	protected Element language;
	
	private String title;
	private String tip;
	
	protected ModAction openAction, saveAction;
	protected JButton closeButton;

	private JTextField description = new JTextField(1) {
		@Override
		public Dimension getMaximumSize() {
			Dimension max = super.getMaximumSize();
			max.height = getPreferredSize().height;
			return max;
		}
	};

	private JTextArea comments = new JTextArea(6, 1);
	private JScrollPane commentsScroller = new JScrollPane(comments);

	public AbstractPanel(DdsConverterFrame owner, LayoutManager layout, String type, Element config) {
		super(layout);
		
		this.owner = owner;

		this.id = type;
		this.type = type;
		this.config = config;

		description.setEditable(false);
		description.setAlignmentX(Component.LEFT_ALIGNMENT);

		comments.setEditable(false);
		comments.setAlignmentX(Component.LEFT_ALIGNMENT);
		commentsScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setLanguage(Element parentLanguage) {
		language = XpathHelper.getElement(parentLanguage, type);
		
		title = getLangStr("./@name");
		tip = getLangStr("./@tip", new Object[] {title});
	}
	
	public Element serialise(Element container) {
		Element me = XmlHelper.newTypedElement(container, "panel", type);
		me.setAttribute("id", id);
		me.setAttribute("canClose", Boolean.toString(closeButton.isVisible()));
		
		return me;
	}
	
	public Element deserialise(Element container) {
		Element me = XpathHelper.getElement(container, "./panel[@id='" + id + "']");
		
		return me;
	}
	
	protected String getLangStr(String xpath) {
		return XpathHelper.getString(language, xpath, xpath);
	}
	
	protected String getLangStr(String xpath, Object[] params) {
		StringBuilder sb = new StringBuilder();
		String format = getLangStr(xpath);

		int start = 0;
		int next = format.indexOf('{', start);
		
		while (next != -1) {
			sb.append(format.substring(start, next));
		      
			int end = format.indexOf('}', next);
			int index = -1;
			
			try {
				index = Integer.parseInt(format.substring(next+1, end));
			} catch (NumberFormatException e) {}
				
			if (index > -1 && index < params.length) {
				sb.append(params[index].toString());
			} else {
				sb.append(format.substring(next, end+1));
			}
			
			start = end + 1;
			next = format.indexOf('{', start);
		}

		return sb.toString();
	}

	public String getTitle() {
		return title;
	}
	
	public String getTip() {
		return tip;
	}
	
	protected JTextField getDescription() {
		return description;
	}
	
	protected void setDescription(String text) {
		description.setText(text);
	}
	
	protected JScrollPane getCommentsScroller() {
		return commentsScroller;
	}
	
	protected void clearComments() {
		comments.setText("");
	}

	protected void appendComment(String text) {
		comments.append(text);
	}
	
	public void appendComment(String id, Object[] params) {
		StringBuilder sb = new StringBuilder();
		String format = getLangStr("./comments/comment[@id='" + id + "']");

		int start = 0;
		int next = format.indexOf('{', start);
		
		while (next != -1) {
			sb.append(format.substring(start, next));
		      
			int end = format.indexOf('}', next);
			int index = -1;
			
			try {
				index = Integer.parseInt(format.substring(next+1, end));
			} catch (NumberFormatException e) {}
				
			if (index > -1 && index < params.length) {
				sb.append(params[index].toString());
			} else {
				sb.append(format.substring(next, end+1));
			}
			
			start = end + 1;
			next = format.indexOf('{', start);
		}
		
		sb.append(format.substring(start));
		sb.append("\n");
      
		appendComment(sb.toString());
	}
	
	public abstract void open();
}
