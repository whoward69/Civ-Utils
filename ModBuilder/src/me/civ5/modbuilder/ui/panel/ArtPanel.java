package me.civ5.modbuilder.ui.panel;

import java.sql.SQLException;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.ModBuilderControl;
import me.civ5.modbuilder.ui.control.art.AtlasIconItem;
import me.civ5.modbuilder.ui.control.art.FlagIconItem;
import me.civ5.modbuilder.ui.control.art.ModelItem;
import me.civ5.modbuilder.ui.control.art.SvIconItem;
import me.civ5.modbuilder.ui.control.select.FlagSelectItem;
import me.civ5.modbuilder.ui.control.select.SvSelectItem;

public class ArtPanel extends InputPanel {
	public ArtPanel(ModDb modDb) throws SQLException {
		super("art");
		
		ItemPanel modelPanel = getItemPanel("model");
		modelPanel.addControl(new ModelItem("model", modDb));

		ItemPanel iconPanel = getItemPanel("icons");
		iconPanel.addControl(new AtlasIconItem("icon", new int[][] {new int[] {256, 176}, new int[] {128, 88}, new int[] {80, 56}, new int[] {64, 44}, new int[] {45, 32}}));

		ItemPanel flagPanel = getItemPanel("flag");
		ModBuilderControl flagUnit = flagPanel.addControl(new FlagSelectItem("flag", modDb));
		ModBuilderControl flagIcon = flagPanel.addControl(new FlagIconItem("icon"));
		flagUnit.setInvertedLinkedControl(flagIcon);
		
		ItemPanel svPanel = getItemPanel("sv");
		ModBuilderControl svUnit = svPanel.addControl(new SvSelectItem("sv", modDb));
		ModBuilderControl svIcon = svPanel.addControl(new SvIconItem("icon"));
		svUnit.setInvertedLinkedControl(svIcon);
		
		flagUnit.setLinkedControl(svUnit);
	}
}
