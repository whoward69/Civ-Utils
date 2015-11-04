package me.civ5.modtools.mod.modinfo.actions;

public class Action {
	private String set;
	private String type;
	private String filename;
	
	public Action(String set, String type, String filename) {
		this.set = set;
		this.type = type;
		this.filename = filename;
	}

	public String getSet() {
		return set;
	}

	public String getType() {
		return type;
	}

	public String getFilename() {
		return filename;
	}
}
