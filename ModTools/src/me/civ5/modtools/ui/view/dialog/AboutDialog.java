package me.civ5.modtools.ui.view.dialog;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;

import me.civ5.modtools.ModTools;
import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.view.dialog.ModDialog;

public class AboutDialog extends ModDialog {
	public AboutDialog(ModFrame frame, Action action) {
		super(frame, "About ModTools", true, action);

		// And this is why I hate Swing Layout Managers with a passion!
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(Box.createVerticalGlue());
		verticalBox.add(createLine(ModTools.ABOUT_TITLE));
		verticalBox.add(Box.createVerticalStrut(10));
		verticalBox.add(createLine(ModTools.ABOUT_VERSION));
		verticalBox.add(Box.createVerticalStrut(10));
		verticalBox.add(createLine(ModTools.ABOUT_COPY));
		verticalBox.add(Box.createVerticalStrut(10));
		verticalBox.add(createLine(ModTools.ABOUT_LINK));
		verticalBox.add(Box.createVerticalGlue());
		
		getContentPane().add(verticalBox, BorderLayout.CENTER);
		
		addCloseButton();
		show(350, 190);
	}
	
	private JComponent createLine(String text) {
		Box line = Box.createHorizontalBox();
		line.add(Box.createHorizontalGlue());
		line.add(new JLabel(text));
		line.add(Box.createHorizontalGlue());
		
		return line;
	}
}
