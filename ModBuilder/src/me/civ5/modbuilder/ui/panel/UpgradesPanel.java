package me.civ5.modbuilder.ui.panel;

import java.sql.SQLException;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.ModBuilderControl;
import me.civ5.modbuilder.ui.control.select.EmptySelectItem;
import me.civ5.modbuilder.ui.control.select.PromotionMultiSelectItem;
import me.civ5.modbuilder.ui.control.select.ReferenceSelectItem;

public class UpgradesPanel extends InputPanel {
	public UpgradesPanel(ModDb modDb) throws SQLException {
		super("upgrades");
		
		ItemPanel upgradesPanel = getItemPanel("upgrades");
		ModBuilderControl normalUpgrade = upgradesPanel.addControl(new ReferenceSelectItem("normal", "Type", "Unit_ClassUpgrades", "UnitClassType", modDb, "UnitClasses", "Type", "Description"));
		ModBuilderControl goodyUpgrade = upgradesPanel.addControl(new EmptySelectItem("goody", "GoodyHutUpgradeUnitClass", modDb, "UnitClasses", "Type", "Description"));
		normalUpgrade.setLinkedControl(goodyUpgrade);

		ItemPanel promotionsPanel = getItemPanel("promotions");
		promotionsPanel.addControl(PromotionMultiSelectItem.getInstance("free", modDb));
	}
}
