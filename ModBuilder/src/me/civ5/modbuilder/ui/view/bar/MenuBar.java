package me.civ5.modbuilder.ui.view.bar;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import me.civ5.modbuilder.ui.CombatUnitBuilderFrame;

public class MenuBar extends JMenuBar {
	public MenuBar(CombatUnitBuilderFrame owner) {
		JMenu fileMenu = new JMenu("File");
		add(fileMenu);
		
		fileMenu.add(new JMenuItem(owner.getSaveAction()));
		fileMenu.addSeparator();
		
		fileMenu.add(new JMenuItem(owner.getAboutAction()));
		fileMenu.addSeparator();
		
		fileMenu.add(new JMenuItem(owner.getExitAction()));

		JMenu sessionMenu = new JMenu("Session");
		add(sessionMenu);
		
		sessionMenu.add(new JMenuItem(owner.getSaveSessionAction()));
		sessionMenu.add(new JMenuItem(owner.getLoadSessionAction()));
		sessionMenu.addSeparator();

		sessionMenu.add(new JMenuItem(owner.getPreferencesAction()));
	}
}
