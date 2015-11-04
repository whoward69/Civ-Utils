package me.civ5.modtools.mod.modinfo.related;

import java.io.PrintStream;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

public class GameRelated extends Related {
	private String minVersion;
	private String maxVersion;

	public GameRelated(ModReporter reporter, String id, String minVersion, String maxVersion) {
		super(reporter, id, minVersion, maxVersion);
		
		this.minVersion = minVersion;
		this.maxVersion = maxVersion;
	}

	@Override
	public void merge(Related related) {
		if ( related instanceof GameRelated ) {
			GameRelated gameRelated = (GameRelated) related;
			
			// This doesn't work (as 1.0.3.78 > 1.0.3.124), but I don't think anybody but me ever used it anyway!
			if ( gameRelated.minVersion.compareTo(this.minVersion) < 0 ) {
				this.minVersion = gameRelated.minVersion;
			}

			if ( gameRelated.maxVersion.compareTo(this.maxVersion) > 0 ) {
				this.maxVersion = gameRelated.maxVersion;
			}
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge different related types!!!"));
		}
	}
	
	@Override
	public void writeAsMod(PrintStream out, String indent) {
		out.print(indent);
		
		out.print("<");
		out.print("Game");
		out.print(" minversion=\"");
		out.print(minVersion);
		out.print("\" maxversion=\"");
		out.print(maxVersion);
		out.println("\" />");
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		out.println(indent + "<Association>");
		out.println(indent + "  <Type>" + "Game" + "</Type>");
		out.println(indent + "  <Name />");
		out.println(indent + "  <Id />");
		out.println(indent + "  <MinVersion>" + minVersion + "</MinVersion>");
		out.println(indent + "  <MaxVersion>" + maxVersion + "</MaxVersion>");
		out.println(indent + "</Association>");
	}
}
