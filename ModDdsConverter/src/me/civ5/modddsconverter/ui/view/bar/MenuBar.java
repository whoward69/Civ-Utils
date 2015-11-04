package me.civ5.modddsconverter.ui.view.bar;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import me.civ5.modddsconverter.ui.DdsConverterFrame;

public class MenuBar extends JMenuBar {
	public MenuBar(DdsConverterFrame owner, boolean webStart) {
		JMenu fileMenu = new JMenu("File");
		add(fileMenu);
		
		fileMenu.add(new JMenuItem(owner.getAddImageAction()));
		fileMenu.add(new JMenuItem(owner.getAddIconAction()));
		fileMenu.add(new JMenuItem(owner.getAddAtlasAction()));
		fileMenu.addSeparator();

		fileMenu.add(new JMenuItem(owner.getAboutAction()));
		fileMenu.addSeparator();
		
		fileMenu.add(new JMenuItem(owner.getExitAction()));

		if (!webStart) {
			JMenu sessionMenu = new JMenu("Session");
			add(sessionMenu);
			
			sessionMenu.add(new JMenuItem(owner.getSaveSessionAction()));
			sessionMenu.add(new JMenuItem(owner.getLoadSessionAction()));
			sessionMenu.addSeparator();
	
			sessionMenu.add(new JMenuItem(owner.getPreferencesAction()));
		}
	}
}
