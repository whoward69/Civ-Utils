package me.civ5.modbuilder.ui.panel;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import me.civ5.modbuilder.db.ModDb;

public class XmlPanel extends DisplayPanel {
	private JTextPane xml;
	
	public XmlPanel(ModDb modDb) throws SQLException {
		super(new BorderLayout(), "xml");
		
		xml = new JTextPane();
		
		add(new JScrollPane(xml), BorderLayout.CENTER);
	}

	@Override
	public void display(String gamedata) {
		xml.setText(gamedata);
	}
	
	@Override
	public String getText() {
		return xml.getText();
	}
}
