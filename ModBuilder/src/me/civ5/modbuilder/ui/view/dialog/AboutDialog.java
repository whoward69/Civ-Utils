package me.civ5.modbuilder.ui.view.dialog;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;

import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.view.dialog.ModDialog;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class AboutDialog extends ModDialog {
	public AboutDialog(ModFrame frame, Action action, Element language) {
		super(frame, XpathHelper.getString(language, "./ui/about/title"), true, action);

		// And this is why I hate Swing Layout Managers with a passion!
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(Box.createVerticalGlue());
		verticalBox.add(createLine(XpathHelper.getString(language, "./ui/about/desc")));
		verticalBox.add(Box.createVerticalStrut(10));
		verticalBox.add(createLine("CopyRight (c) 2013 - William Howard"));
		verticalBox.add(Box.createVerticalStrut(10));
		verticalBox.add(createLine("See http://www.picknmixmods.com/ for fair usage policy"));
		verticalBox.add(Box.createVerticalGlue());
		
		getContentPane().add(verticalBox, BorderLayout.CENTER);
		
		addCloseButton();
		show(340, 160);
	}
	
	private JComponent createLine(String text) {
		Box line = Box.createHorizontalBox();
		line.add(Box.createHorizontalGlue());
		line.add(new JLabel(text));
		line.add(Box.createHorizontalGlue());
		
		return line;
	}
}
