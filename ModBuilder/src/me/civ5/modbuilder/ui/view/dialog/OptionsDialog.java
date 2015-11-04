package me.civ5.modbuilder.ui.view.dialog;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.view.dialog.ModDialog;
import me.civ5.modutils.utils.ModUtilsOptions;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class OptionsDialog extends ModDialog {
	private ModUtilsOptions options;
	
	private GroupLayout layout;
	private SequentialGroup rows;
	private ParallelGroup labelStack;
	private ParallelGroup controlStack;
	
	private JTextField textHomeDir;
	private JTextField textDbcacheDir;

	public OptionsDialog(ModFrame frame, Action action, Element language, ModUtilsOptions options) {
		super(frame, XpathHelper.getString(language, "./ui/options/title"), true, action);
		this.options = options;

		textHomeDir = new JTextField(options.getUserDir().getAbsolutePath(), 30);
		textDbcacheDir = new JTextField(options.getSqlDir().getAbsolutePath(), 30);
		
		// And this is why I hate Swing Layout Managers with a passion!
		JPanel prefsPanel = new JPanel();
		layout = new GroupLayout(prefsPanel);
		prefsPanel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		rows = layout.createSequentialGroup();
		labelStack = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		controlStack = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(labelStack).addGroup(controlStack));
		layout.setVerticalGroup(rows);
		
		addControl(XpathHelper.getString(language, "./ui/options/homedir"), textHomeDir);
		addControl(XpathHelper.getString(language, "./ui/options/dbdir"), textDbcacheDir);

		getContentPane().add(prefsPanel, BorderLayout.CENTER);
		
		addCloseButton();
		show(600, 180);
	}
	
	@Override
	public void close() {
		options.setUserDir(textHomeDir.getText().trim());
		options.setModDir(textDbcacheDir.getText().trim());
		
		super.close();
	}
	
	private void addControl(String text, JComponent control) {
		JLabel label = new JLabel(text);
		
		labelStack.addComponent(label);
		controlStack.addComponent(control);
		
		rows.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label).addComponent(control));
	}
}
