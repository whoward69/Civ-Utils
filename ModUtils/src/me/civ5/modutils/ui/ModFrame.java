package me.civ5.modutils.ui;

import javax.swing.Action;
import javax.swing.JFrame;

import me.civ5.modutils.log.LogMessage;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.utils.ModUtilsOptions;

public abstract class ModFrame extends JFrame implements ModReporter {
	protected ModReporter sysReporter;
	protected ModUtilsOptions options;
	
	public ModFrame(String title, ModReporter sysReporter, ModUtilsOptions options) {
		super(title);
		
		this.sysReporter = sysReporter;
		this.options = options;
	}

	public abstract Action getPreferencesAction();
	public abstract Action getAboutAction();
	public abstract Action getExitAction();

	public abstract void enableListActions(boolean enabled);

	public int getLevel() {
		return sysReporter.getLevel();
	}

	public void setLevel(int level) {
		sysReporter.setLevel(level);
	}

	public void log(LogMessage msg) {
		sysReporter.log(msg);
	}

	public void log(Exception e) {
		sysReporter.log(e);
	}
}
