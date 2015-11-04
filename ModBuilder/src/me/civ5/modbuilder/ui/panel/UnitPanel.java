package me.civ5.modbuilder.ui.panel;

import java.sql.SQLException;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.ModBuilderControl;
import me.civ5.modbuilder.ui.control.bool.FalseBooleanItem;
import me.civ5.modbuilder.ui.control.num.IntegerItem;
import me.civ5.modbuilder.ui.control.num.PositiveIntegerItem;
import me.civ5.modbuilder.ui.control.select.SelectItem;
import me.civ5.modbuilder.ui.control.text.StaticItem;
import me.civ5.modbuilder.ui.control.unit.CivilizationItem;
import me.civ5.modbuilder.ui.control.unit.CombatClassItem;
import me.civ5.modbuilder.ui.control.unit.PrimaryRoleItem;
import me.civ5.modbuilder.ui.control.unit.SecondaryRoleItem;
import me.civ5.modbuilder.ui.control.unit.UnitItem;


public class UnitPanel extends InputPanel {
	private UnitItem unit;
	
	public UnitPanel(ModDb modDb) throws SQLException {
		super("unit");

		ItemPanel unitPanel = getItemPanel("unit");
		unit = (UnitItem) unitPanel.addControl(new UnitItem("unit", "Type", modDb, "Units", "UNIT_"));
		ModBuilderControl type = unitPanel.addControl(new StaticItem("type"));
		unit.setLinkedControl(type);
		unitPanel.addControl(new CombatClassItem("combat", "CombatClass", modDb, "UnitCombatInfos", "Type", "Description", null));
		unitPanel.addControl(new PrimaryRoleItem("primary", "DefaultUnitAI"));
		unitPanel.addControl(new SecondaryRoleItem("secondary", "Type"));

		ItemPanel civPanel = getItemPanel("civ");
		ModBuilderControl civList = civPanel.addControl(new CivilizationItem("civ", "Class", modDb));
		ModBuilderControl unitList = civPanel.addControl(new SelectItem("replaces", null, modDb, "UnitClasses", "Type", "Description"));
		civList.setLinkedControl(unitList);
		
		ItemPanel costPanel = getItemPanel("cost");
		costPanel.addControl(new IntegerItem("cost", "Cost"));
		costPanel.addControl(new PositiveIntegerItem("hurry", "HurryCostModifier"));
		costPanel.addControl(new PositiveIntegerItem("advanced", "AdvancedStartCost"));
		costPanel.addControl(new PositiveIntegerItem("faith", "FaithCost"));
		costPanel.addControl(new FalseBooleanItem("requiresfaith", "RequiresFaithPurchaseEnabled"));
		costPanel.addControl(new FalseBooleanItem("maintenance", "NoMaintenance"));
	}
	
	public String getUnitName() {
		return unit.getText();
	}
}
