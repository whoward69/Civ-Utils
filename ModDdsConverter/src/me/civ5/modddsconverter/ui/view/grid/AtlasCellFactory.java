package me.civ5.modddsconverter.ui.view.grid;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import me.civ5.modddsconverter.ui.model.grid.AtlasCellModelFactory;

public class AtlasCellFactory {
	private static List<AtlasCell> store = new ArrayList<AtlasCell>();
	private static List<AtlasCell> free = new ArrayList<AtlasCell>();
	
	public static AtlasCell getAtlasCell() {
		AtlasCell cell = null;
		
		if (free.size() > 0) {
			cell = free.remove(0);
		} else {
			cell = new AtlasCell(AtlasCellModelFactory.getAtlasCellModel());
			store.add(cell);
		}
		
		return cell;
	}
	
	public static void returnAtlasCell(AtlasCell cell) {
		free.add(cell);
	}
	
	public static Dimension getPreferredSize() {
		return store.get(0).getPreferredSize();
	}
	
	// Static access only please
	private AtlasCellFactory() {
		// Make sure there is always something in the store to use to get the preferred size
		returnAtlasCell(getAtlasCell());
	}
}
