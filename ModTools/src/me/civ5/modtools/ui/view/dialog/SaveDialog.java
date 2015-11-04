package me.civ5.modtools.ui.view.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.view.dialog.ModDialog;

public class SaveDialog extends ModDialog implements CaretListener {
	private JButton buttonYes = new JButton("OK");
	private JButton buttonNo = new JButton("Cancel");
	
	private JTextField modNameField;
	private String modName = null;
	
	public SaveDialog(ModFrame frame, String prompt, String modName, Action action) {
		super(frame, "Save as ...", true, action);

		JPanel promptPanel = new JPanel();
		promptPanel.add(new JLabel(prompt));
		modNameField = new JTextField(40);
		modNameField.setFont(new Font("Serif", Font.PLAIN, 14));
		modNameField.addCaretListener(this);
		promptPanel.add(modNameField);
		getContentPane().add(promptPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(buttonYes);
		buttonYes.addActionListener(this);
		buttonYes.setEnabled(false);
		buttonPanel.add(buttonNo);
		buttonNo.addActionListener(this);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(buttonYes);

		if (modName != null) {
			modNameField.setText(modName);
		}
		
		show(580, 140);
	}
	
	public String getModName() {
		return modName;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonYes) {
			modName = modNameField.getText().trim();
			
			if (modName.length() == 0) {
				modName = null;
			}
		} else {
			modName = null;
		}
		
		super.actionPerformed(e);
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		buttonYes.setEnabled(modNameField.getText().trim().length() != 0);
	}
}
