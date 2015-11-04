package me.civ5.modddsconverter.ui.model.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import me.civ5.modddsconverter.ui.panel.IconsPanel;

public class AtlasCellModelFactory {
	private static List<AtlasCellModel> store = new ArrayList<AtlasCellModel>();
	
	private static Vector<AtlasCellData> items = new Vector<AtlasCellData>();
	
	static {
		items.add(new AtlasCellData(null));
	}
	
	public static AtlasCellModel getAtlasCellModel() {
		AtlasCellModel model = new AtlasCellModel(items);
		store.add(model);
		
		return model;
	}
	
	public static void addPanel(IconsPanel panel) {
		AtlasCellData item = new AtlasCellData(panel);
		if (items.add(item)) {
			for (AtlasCellModel model : store) {
				model.elementAdded(items.size()-1);
			}
		}
	}
	
	public static void removePanel(IconsPanel panel) {
		for (AtlasCellData item : items) {
			if (item.isPanel(panel)) {
				int index = items.indexOf(item);

				if (items.remove(item)) {
					for (AtlasCellModel model : store) {
						model.elementRemoved(index);
					}
				}
				
				break;
			}
		}
	}
	
	public static void updatePanel(IconsPanel panel) {
		for (AtlasCellData item : items) {
			if (item.isPanel(panel)) {
				int index = items.indexOf(item);

				for (AtlasCellModel model : store) {
					model.elementChanged(index);
				}
				
				break;
			}
		}
	}
	
	// Static access only please
	private AtlasCellModelFactory() {
	}
}
