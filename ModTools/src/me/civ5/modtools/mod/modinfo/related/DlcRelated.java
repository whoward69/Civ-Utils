package me.civ5.modtools.mod.modinfo.related;

import java.io.PrintStream;

import me.civ5.modutils.log.ModReporter;

public class DlcRelated extends Related {
	public DlcRelated(ModReporter reporter, String id, String minVersion, String maxVersion) {
		super(reporter, id, minVersion, maxVersion);
	}

	@Override
	public void writeAsMod(PrintStream out, String indent) {
		super.writeAsMod(out, indent, "Dlc");
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		super.writeAsProject(out, indent, "Dlc");
	}
}
