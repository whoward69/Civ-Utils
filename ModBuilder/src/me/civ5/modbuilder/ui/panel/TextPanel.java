package me.civ5.modbuilder.ui.panel;

import java.sql.SQLException;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.bool.TrueBooleanItem;
import me.civ5.modbuilder.ui.control.text.TextAreaItem;

public class TextPanel extends InputPanel {
	public TextPanel(ModDb modDb) throws SQLException {
		super("text");

		ItemPanel helpPanel = getItemPanel("help");
		helpPanel.addControl(new TextAreaItem("strategy", "Strategy", "_STRATEGY"));
		helpPanel.addControl(new TextAreaItem("help", "Help", "_HELP"));

		ItemPanel pediaPanel = getItemPanel("pedia");
		pediaPanel.addControl(new TextAreaItem("pedia", "Civilopedia", "_PEDIA"));
		pediaPanel.addControl(new TrueBooleanItem("show", "ShowInPedia"));
	}
}
