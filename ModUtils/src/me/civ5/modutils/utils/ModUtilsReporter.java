package me.civ5.modutils.utils;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogMessage;
import me.civ5.modutils.log.ModReporter;

public class ModUtilsReporter implements ModReporter {
	private int level = LogMessage.INFO;
	
	@Override
	public int getLevel() {
		return level;
	}
	
	@Override
	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
	public void log(LogMessage msg) {
		String text = msg.getText(level);
		if (text != null) {
			System.out.println(msg.getLevelAsString() + ": "+ text);
		}
	}

	@Override
	public void log(Exception e) {
		log(new LogError(e.getMessage()));
	}
}
