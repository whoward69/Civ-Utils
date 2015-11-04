package me.civ5.modtools.test;

import java.io.File;

import me.civ5.modtools.mod.Mod;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogInfo;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.utils.ModUtilsOptions;
import me.civ5.modutils.utils.ModUtilsReporter;

public class ModToolsTest {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ModReporter reporter = new ModUtilsReporter();
		ModUtilsOptions options = new ModUtilsOptions(reporter, args);
		options.parse(args);
		
		if (options.isHelp()) {
			System.out.println("java -jar ModTools.jar");
			System.out.println(options.getHelpText());
			System.exit(0);
		}
		
		File[] modList = options.getModDir().listFiles();
		// modList = new File[] {new File(options.getModDir(), "UI - City Expansion (v 5)")};
		
		for ( File modDir : modList ) {
			String modName = modDir.getName();
			reporter.log(new LogInfo(modName));

			int pos = modName.lastIndexOf('(');
			String name = modName.substring(0, pos).trim();
			String version = modName.substring(pos+3, modName.length()-1);
			
			try {
				Mod newMod = new Mod(reporter, modDir);
				newMod.verify(false, false);
				// newMod.saveAsMod("../ModToolsTest", name, version);
				// newMod.saveAsProject("../ModToolsTest", options.getProjectDir().getAbsolutePath(), name);
			} catch (Exception e) {
				reporter.log(new LogError(e.getMessage()));
			}
		}
		
		reporter.log(new LogInfo("Done"));
	}
}
