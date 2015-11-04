package me.civ5.modbuilder.ui.panel;

import java.sql.SQLException;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.bool.FalseBooleanItem;
import me.civ5.modbuilder.ui.control.select.EmptySelectItem;
import me.civ5.modbuilder.ui.control.select.ReferenceMultiSelectItem;

public class RequirementsPanel extends InputPanel {
	public RequirementsPanel(ModDb modDb) throws SQLException {
		super("requirements");
		
		ItemPanel techsPanel = getItemPanel("technologies");
		techsPanel.addControl(new EmptySelectItem("prereq", "PrereqTech", modDb, "Technologies", "Type", "Description"));
		techsPanel.addControl(new EmptySelectItem("obsolete", "ObsoleteTech", modDb, "Technologies", "Type", "Description"));

		ItemPanel resourcesPanel = getItemPanel("resources");
		resourcesPanel.addControl(new ReferenceMultiSelectItem("resources", "Type", "Unit_ResourceQuantityRequirements", "ResourceType", modDb, "Resources", "Type", "Description", "ResourceClassType NOT IN ('RESOURCECLASS_BONUS', 'RESOURCECLASS_LUXURY')"));
		resourcesPanel.addControl(new FalseBooleanItem("prereq", "PrereqResources"));
	}
}
