package me.civ5.modbuilder.ui.panel;

import java.sql.SQLException;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.bool.FalseBooleanItem;
import me.civ5.modbuilder.ui.control.num.IntegerItem;
import me.civ5.modbuilder.ui.control.select.EmptySelectItem;
import me.civ5.modbuilder.ui.control.select.NukeDamageSelectItem;
import me.civ5.modbuilder.ui.control.select.PromotionMultiSelectItem;

public class CombatPanel extends InputPanel {
	public CombatPanel(ModDb modDb) throws SQLException {
		super("combat");
		
		ItemPanel meleePanel = getItemPanel("melee");
		meleePanel.addControl(new IntegerItem("combat", "Combat"));
		meleePanel.addControl(PromotionMultiSelectItem.getBooleanItem("defensive", "PROMOTION_ONLY_DEFENSIVE"));
		
		ItemPanel rangedPanel = getItemPanel("ranged");
		rangedPanel.addControl(new IntegerItem("combat", "RangedCombat"));
		rangedPanel.addControl(new IntegerItem("range", "Range"));
		rangedPanel.addControl(PromotionMultiSelectItem.getBooleanItem("setup", "PROMOTION_MUST_SET_UP"));
		rangedPanel.addControl(new FalseBooleanItem("domain", "RangeAttackOnlyInDomain"));
		rangedPanel.addControl(new FalseBooleanItem("los", "RangeAttackIgnoreLOS"));
		rangedPanel.addControl(new FalseBooleanItem("defense", "IgnoreBuildingDefense"));
		
		ItemPanel movesPanel = getItemPanel("moves");
		movesPanel.addControl(new IntegerItem("moves", "Moves", "2"));
		movesPanel.addControl(PromotionMultiSelectItem.getBooleanItem("ignore", "PROMOTION_IGNORE_TERRAIN_COST"));
		movesPanel.addControl(PromotionMultiSelectItem.getBooleanItem("followup", "PROMOTION_CAN_MOVE_AFTER_ATTACKING"));
		// TODO - immobile - anything which is domain air is immobile
		movesPanel.addControl(new FalseBooleanItem("immobile", "Immobile"));
		movesPanel.addControl(new FalseBooleanItem("suicide", "Suicide"));

		ItemPanel airPanel = getItemPanel("aircraft");
		airPanel.addControl(new IntegerItem("range", "AirInterceptRange"));
		airPanel.addControl(PromotionMultiSelectItem.getBooleanItem("recon", "PROMOTION_AIR_RECON"));
		airPanel.addControl(new NukeDamageSelectItem("nuke", "NukeDamageLevel"));
		// TODO - special cargo - not really an "aircraft" group item
		airPanel.addControl(new EmptySelectItem("cargo", "SpecialCargo", modDb, "SpecialUnits", "Type", "Description"));
		airPanel.addControl(new EmptySelectItem("special", "Special", modDb, "SpecialUnits", "Type", "Description"));
	}
}
