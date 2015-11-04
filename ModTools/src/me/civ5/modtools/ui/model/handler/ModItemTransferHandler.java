package me.civ5.modtools.ui.model.handler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import me.civ5.modtools.mod.Mod;
import me.civ5.modtools.ui.model.ModListModel;
import me.civ5.modtools.ui.model.SortedModListModel;

public class ModItemTransferHandler extends TransferHandler {
	private final DataFlavor localObjectFlavor;
	private Object[] transferedObjects = null;

	private JList<?> source = null;

	private int[] indices = null;
	private int addIndex = -1;
	private int addCount = 0;

	public ModItemTransferHandler() {
		localObjectFlavor = new ActivationDataFlavor(Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Transferable createTransferable(JComponent c) {
		source = (JList<?>) c;
		indices = source.getSelectedIndices();
		transferedObjects = source.getSelectedValues();

		return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
	}

	@Override
	public boolean canImport(TransferSupport info) {
		// Bail out early if this isn't a drap-and-drop operation (eg a paste from the clipboard)
		if (!info.isDrop()) return false;
		
		// Don't allow drag-and-drop within a JList backed by a SortedModListModel
		if (source.getModel() instanceof SortedModListModel && source.equals(info.getComponent())) {
			return false;
		}
		
		return info.isDataFlavorSupported(localObjectFlavor);
	}

	@Override
	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	@Override
	public boolean importData(TransferSupport info) {
		if (!canImport(info)) {
			return false;
		}

		JList<?> target = (JList<?>) info.getComponent();
		JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
		ModListModel listModel = (ModListModel) target.getModel();
		int index = dl.getIndex();
		int max = listModel.getSize();
		if (index < 0 || index > max) {
			index = max;
		}
		addIndex = index;

		try {
			Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
			for (int i = 0; i < values.length; i++) {
				if (values[i] instanceof Mod) {
					int idx = index++;
					listModel.addElement(idx, values[i]);
					target.addSelectionInterval(idx, idx);
				}
			}
			addCount = (target == source) ? values.length : 0;
			target.clearSelection();
			return true;
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace(System.err);
		} catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
		return false;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		cleanup(c, action == MOVE);
	}

	private void cleanup(JComponent c, boolean remove) {
		if (remove && indices != null) {
			JList<?> source = (JList<?>) c;
			ModListModel model = (ModListModel) source.getModel();
			// If we are moving items around in the same list, we
			// need to adjust the indices accordingly, since those
			// after the insertion point have moved.
			if (addCount > 0) {
				for (int i = 0; i < indices.length; i++) {
					if (indices[i] >= addIndex) {
						indices[i] += addCount;
					}
				}
			}
			for (int i = indices.length - 1; i >= 0; i--) {
				model.removeElement(indices[i]);
			}
		}

		indices = null;
		addCount = 0;
		addIndex = -1;
	}
}