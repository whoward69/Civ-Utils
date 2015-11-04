package me.civ5.modutils.ui.worker;

import java.util.List;

import javax.swing.SwingWorker;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogMessage;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.ui.view.dialog.LogDialog;

public abstract class ModWorker extends SwingWorker<Void, LogMessage> implements ModReporter {
	protected LogDialog logDialog;
	
	public final void doWork(LogDialog logDialog) {
		this.logDialog = logDialog;

		execute();
	}
	
	@Override
	protected void process(List<LogMessage> msgs) {
		if (logDialog != null) {
			for (LogMessage msg : msgs) {
				logDialog.log(msg);
			}
		}
	}

	@Override
	public void done() {
		if (logDialog != null) {
			logDialog.setCloseEnabled(true);
		}
	}
	
	@Override
	public int getLevel() {
		return (logDialog != null) ? logDialog.getLevel() : LogMessage.ERROR;
	}

	@Override
	public void setLevel(int level) {
		if (logDialog != null) {
			logDialog.setLevel(level);
		}
	}
	
	@Override
	public void log(LogMessage msg) {
		publish(msg);
	}

	@Override
	public void log(Exception e) {
		log(new LogError(e.getMessage()));
	}
}
