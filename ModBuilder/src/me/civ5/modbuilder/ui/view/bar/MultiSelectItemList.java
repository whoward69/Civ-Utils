package me.civ5.modbuilder.ui.view.bar;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ToolTipManager;

import me.civ5.modbuilder.ui.model.SelectEntry;

public class MultiSelectItemList extends JList<SelectEntry> {
	public MultiSelectItemList(ListModel<SelectEntry> model) {
		super(model);

		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	public void selectKeys(List<String> keys) {
		for (String key : keys) {
			selectKey(key);
		}
	}
	
	public void selectKey(String key) {
		ListModel<SelectEntry> model = getModel();
		
		for (int i = 0; i < model.getSize(); ++i ) {
			if (((SelectEntry) model.getElementAt(i)).getKey().equals(key)) {
				addSelectionInterval(i, i);
				break;
			}
		}
	}

	public void deselectKey(String key) {
		ListModel<SelectEntry> model = getModel();
		
		for (int i = 0; i < model.getSize(); ++i ) {
			if (((SelectEntry) model.getElementAt(i)).getKey().equals(key)) {
				removeSelectionInterval(i, i);
				break;
			}
		}
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		int index = locationToIndex(e.getPoint());
		if ( index >= 0 ) {
			return ((SelectEntry) getModel().getElementAt(index)).getTip();
		}
		
		return null;
	}
}
