package me.civ5.modutils.ui.view.dialog;

import javax.swing.Action;

import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.ui.ModFrame;

public class ProgressDialog extends LogDialog implements ModReporter {
	public ProgressDialog(ModFrame frame, String title, int level, Action action) {
		super(frame, title, level, action);

		buttonClose.setText("Cancel");
	}
}