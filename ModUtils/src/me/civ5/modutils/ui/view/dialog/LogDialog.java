package me.civ5.modutils.ui.view.dialog;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import me.civ5.modutils.log.LogMessage;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.model.LogDocument;
import me.civ5.modutils.ui.view.bar.LogLevelMenu;

public class LogDialog extends ModDialog implements ModReporter {
	private LogDocument log;
	
	public LogDialog(ModFrame frame, String title, int level, Action action) {
		super(frame, title, false, action);

		log = new LogDocument();
		log.setLevel(level);

		setJMenuBar(new VerifyMenuBar());

		JTextPane textPane = new JTextPane(log);
		textPane.setFont(new Font("Serif", Font.PLAIN, 14));
		textPane.setEditable(false);

		getContentPane().add(new JScrollPane(textPane), BorderLayout.CENTER);

		addCloseButton();
		show(600, 400);
	}
	
	@Override
	public int getLevel() {
		return log.getLevel();
	}
	
	@Override
	public void setLevel(int level) {
		log.setLevel(level);
	}
	
	@Override
	public void log(Exception e) {
		log.log(e);
	}

	@Override
	public void log(LogMessage msg) {
		log.log(msg);
	}
	
	private class VerifyMenuBar extends JMenuBar {
		public VerifyMenuBar() {
			add(new LogLevelMenu(log));
		}
	}
}