package me.civ5.modutils.ui.view.bar;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import me.civ5.modutils.log.LogMessage;
import me.civ5.modutils.log.ModReporter;

public class LogLevelMenu extends JMenu {
	private ModReporter reporter;
	
	public LogLevelMenu(ModReporter reporter) {
		super("Log Level");
		this.reporter = reporter;
		
		ButtonGroup group = new ButtonGroup();

		add(new LogLevelMenuItem(group, "Error", LogMessage.ERROR));
		add(new LogLevelMenuItem(group, "Warn", LogMessage.WARN));
		add(new LogLevelMenuItem(group, "Info", LogMessage.INFO));
		add(new LogLevelMenuItem(group, "Debug", LogMessage.DEBUG));
	}

	private class LogLevelMenuItem extends JRadioButtonMenuItem {
		public LogLevelMenuItem(ButtonGroup group, String label, int level) {
			super(new LogLevelAction(label, level));
			
			group.add(this);
			setSelected(true);
		}
	}
	
	private class LogLevelAction extends AbstractAction {
		private int level;
		
		public LogLevelAction(String name, int level) {
			super(name);
			this.level = level;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			reporter.setLevel(level);
		}
	}
}