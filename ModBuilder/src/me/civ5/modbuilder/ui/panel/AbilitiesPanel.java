package me.civ5.modbuilder.ui.panel;

import java.sql.SQLException;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.ModBuilderControl;
import me.civ5.modbuilder.ui.control.bool.FalseBooleanItem;
import me.civ5.modbuilder.ui.control.num.PositiveIntegerItem;
import me.civ5.modbuilder.ui.control.select.EmptySelectItem;
import me.civ5.modbuilder.ui.control.select.ReferenceMultiSelectItem;
import me.civ5.modbuilder.ui.control.select.ReferenceSelectItem;

public class AbilitiesPanel extends InputPanel {
	public AbilitiesPanel(ModDb modDb) throws SQLException {
		super("abilities");

		ItemPanel abilitiesPanel = getItemPanel("abilities");
		abilitiesPanel.addControl(new FalseBooleanItem("found", "Found"));
		abilitiesPanel.addControl(new FalseBooleanItem("abroad", "FoundAbroad"));
		ModBuilderControl pillage = abilitiesPanel.addControl(new FalseBooleanItem("pillage", "Pillage", true));
		ModBuilderControl pillageTech = abilitiesPanel.addControl(new EmptySelectItem("pillagetech", "PillagePrereqTech", modDb, "Technologies", "Type", "Description"));
		pillage.setLinkedControl(pillageTech);
		abilitiesPanel.addControl(new FalseBooleanItem("rival", "RivalTerritory"));
		abilitiesPanel.addControl(new FalseBooleanItem("nobad", "NoBadGoodies"));

		ItemPanel workPanel = getItemPanel("work");
		ModBuilderControl workRate = workPanel.addControl(new PositiveIntegerItem("rate", "WorkRate"));
		ModBuilderControl builds = workPanel.addControl(new ReferenceMultiSelectItem("builds", "Type", "Unit_Builds", "BuildType", modDb, "Builds", "Type", "Description", "Kill != 1"));
		workRate.setLinkedControl(builds);
	
		ItemPanel yieldPanel = getItemPanel("yields");
		ModBuilderControl killYield = yieldPanel.addControl(new ReferenceSelectItem("yield", "Type", "Unit_YieldFromKills", "YieldType", modDb, "Yields", "Type", "Description"));
		ModBuilderControl killPercent = yieldPanel.addControl(new PositiveIntegerItem("percent", null));
		killYield.setLinkedControl(killPercent, "Yield");
	}
}
