package me.civ5.modtools.ui.view.dialog;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.view.dialog.ModDialog;
import me.civ5.modutils.utils.ModUtilsOptions;

public class OptionsDialog extends ModDialog {
	private ModUtilsOptions options;
	
	private GroupLayout layout;
	private SequentialGroup rows;
	private ParallelGroup labelStack;
	private ParallelGroup controlStack;
	
	private JTextField textHomeDir;
	private JTextField textModsDir;
	private JTextField textModBuddyDir;
	private JCheckBox checkFixErrors;
	private JCheckBox checkRemoveFiles;

	public OptionsDialog(ModFrame frame, Action action, ModUtilsOptions options) {
		super(frame, "ModTools Preferences", true, action);
		this.options = options;

		textHomeDir = new JTextField(options.getUserDir().getAbsolutePath(), 30);
		textModsDir = new JTextField(options.getModDir().getAbsolutePath(), 30);
		textModBuddyDir = new JTextField(options.getProjectDir().getAbsolutePath(), 30);
		checkFixErrors = new JCheckBox("", options.isFixMinorErrors());
		checkRemoveFiles = new JCheckBox("", options.isRemoveRedundantFiles());
		
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
		
		addControl("User home:", textHomeDir);
		addControl("MODS path:", textModsDir);
		addControl("ModBuddy path:", textModBuddyDir);
		addControl("Fix minor errors:", checkFixErrors);
		addControl("Remove redundant files:", checkRemoveFiles);

		getContentPane().add(prefsPanel, BorderLayout.CENTER);
		
		addCloseButton();
		show(600, 220);
	}
	
	@Override
	public void close() {
		options.setUserDir(textHomeDir.getText().trim());
		options.setModDir(textModsDir.getText().trim());
		options.setProjectDir(textModBuddyDir.getText().trim());
		
		options.setFixMinorErrors(checkFixErrors.isSelected());
		options.setRemoveRedundantFiles(checkRemoveFiles.isSelected());
		
		super.close();
	}
	
	private void addControl(String text, JComponent control) {
		JLabel label = new JLabel(text);
		
		labelStack.addComponent(label);
		controlStack.addComponent(control);
		
		rows.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label).addComponent(control));
	}
}
