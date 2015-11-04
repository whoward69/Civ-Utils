package me.civ5.modutils.log;

public class LogMessage {
	public static final int BLANK = 0;
	public static final int DEBUG = 1;
	public static final int INFO  = 2;
	public static final int WARN  = 3;
	public static final int ERROR = 4;
	
	private String[] levels = {"", "DEBUG", " INFO", " WARN", "ERROR"};
	
	private int level;
	private String text;
	
	public LogMessage(int level, String text) {
		this.level = level;
		this.text = text;
	}
	
	public boolean isError() {
		return (level == ERROR);
	}

	public boolean isWarn() {
		return (level == WARN);
	}

	public boolean isInfo() {
		return (level == INFO);
	}

	public boolean isDebug() {
		return (level == DEBUG);
	}
	
	public boolean isBlank() {
		return (level == BLANK);
	}
	
	public String getLevelAsString() {
		return levels[level];
	}

	public String getText() {
		return text;
	}
	
	public String getText(int level) {
		if (isBlank() || this.level >= level) {
			return text;
		}
		
		return null;
	}
}
