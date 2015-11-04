package me.civ5.modtools.mod.modinfo;

import java.io.PrintStream;

import me.civ5.modutils.log.ModReporter;


public class References extends Relateds {
	public static final String NAME = "References";
	
	public References(ModReporter reporter) {
		super(reporter, NAME);
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		writeAsProject(out, indent, "ModReferences");
	}
}
