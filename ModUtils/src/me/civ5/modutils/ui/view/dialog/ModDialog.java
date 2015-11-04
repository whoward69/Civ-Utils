package me.civ5.modutils.ui.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import me.civ5.modutils.ui.ModFrame;

public abstract class ModDialog extends JDialog implements ActionListener {
	protected JButton buttonClose = new JButton("Close");

	private ModFrame owner;
	private Action action;
	
	public ModDialog(ModFrame owner, String title, boolean modal, Action action) {
		super(owner, title, modal);
		this.owner = owner;
		this.action = action;

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				close();
			}
		});
	}
	
	protected void addCloseButton() {
		JPanel panel = new JPanel();
		panel.add(buttonClose);
		buttonClose.setEnabled(true);
		buttonClose.addActionListener(this);

		getContentPane().add(panel, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(buttonClose);
	}
	
	public void setCloseEnabled(boolean enabled) {
		buttonClose.setEnabled(enabled);
	}

	protected void show(int x, int y) {
		pack();
		
		Dimension dimFrame = owner.getSize();
		Dimension dimDialog = new Dimension(x, y);

		setSize(dimDialog);
		Point p = owner.getLocationOnScreen();
		setLocation(p.x + ((dimFrame.width - dimDialog.width) / 2), p.y + ((dimFrame.height - dimDialog.height) / 2));

		owner.enableListActions(false);

		if (action != null) {
			action.setEnabled(false);
		}
		
		// Must be the last thing we do here!
		setVisible(true);
	}
	
	public void close() {
		dispose();
		
		owner.enableListActions(true);

		if (action != null) {
			action.setEnabled(true);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		close();
	}
}
