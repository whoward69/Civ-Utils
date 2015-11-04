package me.civ5.modtools.ui.view.bar;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import me.civ5.modtools.ui.ModToolsFrame;

public class MenuBar extends JMenuBar {
	public MenuBar(ModToolsFrame owner) {
		JMenu fileMenu = new JMenu("File");
		add(fileMenu);
		
		fileMenu.add(new JMenuItem(owner.getSaveModAction()));
		fileMenu.add(new JMenuItem(owner.getSaveProjectAction()));
		fileMenu.addSeparator();
		
		fileMenu.add(new JMenuItem(owner.getAboutAction()));
		fileMenu.addSeparator();
		
		fileMenu.add(new JMenuItem(owner.getExitAction()));

		JMenu sessionMenu = new JMenu("Session");
		add(sessionMenu);
		
		sessionMenu.add(new JMenuItem(owner.getLoadSessionAction()));
		sessionMenu.add(new JMenuItem(owner.getSaveSessionAction()));
		sessionMenu.addSeparator();
		
		sessionMenu.add(new JMenuItem(owner.getPreferencesAction()));
	}
}
