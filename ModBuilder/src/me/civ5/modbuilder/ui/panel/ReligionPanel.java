package me.civ5.modbuilder.ui.panel;

import java.sql.SQLException;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.bool.FalseBooleanItem;
import me.civ5.modbuilder.ui.control.num.IntegerItem;

public class ReligionPanel extends InputPanel {
	public ReligionPanel(ModDb modDb) throws SQLException {
		super("religion");

		ItemPanel religionPanel = getItemPanel("religion");
		religionPanel.addControl(new FalseBooleanItem("spread", "SpreadReligion"));
		religionPanel.addControl(new FalseBooleanItem("remove", "RemoveHeresy"));
		religionPanel.addControl(new IntegerItem("count", "ReligionSpreads"));
		religionPanel.addControl(new IntegerItem("strength", "ReligiousStrength"));
		religionPanel.addControl(new FalseBooleanItem("found", "FoundReligion"));
		religionPanel.addControl(new FalseBooleanItem("enhanced", "RequiresEnhancedReligion"));
		religionPanel.addControl(new FalseBooleanItem("prohibits", "ProhibitsSpread"));
	}
}
