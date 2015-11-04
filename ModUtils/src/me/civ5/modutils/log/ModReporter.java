package me.civ5.modutils.log;

public interface ModReporter {
	public int getLevel();
	public void setLevel(int level);
	
	public void log(LogMessage msg);
	public void log(Exception e);
}
