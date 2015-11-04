package me.civ5.modtools.mod.modinfo.related;

import java.io.PrintStream;

import me.civ5.modutils.log.ModReporter;

public class ModRelated extends Related {
	public ModRelated(ModReporter reporter, String id, String minVersion, String maxVersion) {
		super(reporter, id, minVersion, maxVersion);
	}

	@Override
	public void writeAsMod(PrintStream out, String indent) {
		super.writeAsMod(out, indent, "Mod");
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		super.writeAsProject(out, indent, "Mod");
	}
}
