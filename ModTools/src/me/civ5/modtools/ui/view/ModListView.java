package me.civ5.modtools.ui.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;

import me.civ5.modtools.mod.Mod;
import me.civ5.modtools.ui.model.ModListModel;

public class ModListView extends JList<Object> {
	private JScrollPane pane;
	
	public ModListView(String title, ModListModel listModel, TransferHandler handler) {
		super(listModel);

		getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setDropMode(DropMode.INSERT);
		setDragEnabled(true);
		setTransferHandler(handler);

		AbstractAction dummy = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {}
		};
		ActionMap map = getActionMap();
		map.put(TransferHandler.getCutAction().getValue(Action.NAME), dummy);
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME), dummy);
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME), dummy);
		
		ToolTipManager.sharedInstance().registerComponent(this);
		
		pane = new JScrollPane(this);
		pane.setBorder(BorderFactory.createTitledBorder(title));
	}
	
	public Component getComponent() {
		return pane;
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		int maxTipLength = 150;
		
		int index = locationToIndex(e.getPoint());
		if ( index >= 0 ) {
			String desc = ((Mod) getModel().getElementAt(index)).getDescription();
			return ((desc.length() < maxTipLength) ? desc : (desc.substring(0, maxTipLength) + "..."));
		}
		
		return null;
	}
}
