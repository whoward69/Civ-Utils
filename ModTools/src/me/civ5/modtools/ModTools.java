package me.civ5.modtools;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import me.civ5.modtools.mod.Mod;
import me.civ5.modtools.ui.ModToolsFrame;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogInfo;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.utils.ModUtilsOptions;
import me.civ5.modutils.utils.ModUtilsReporter;

public class ModTools {
	public static final String NAME = "ModTools by whoward69";
	
	public static final String ABOUT_TITLE = "ModTools - Utilities for Civ5 Mods";
	public static final String ABOUT_VERSION = "Version 2015-08-09";
	public static final String ABOUT_COPY = "CopyRight (c) 2013-2015 - William Howard";
	public static final String ABOUT_LINK = "See http://www.picknmixmods.com/ for fair usage policy";
	
	private ModReporter reporter;
	private ModUtilsOptions options;
	
	public ModTools(String args[]) {
		reporter = new ModUtilsReporter();
		options = new ModUtilsOptions(reporter, args);
		
		if (options.isHelp()) {
			showHelp();
		}
		
		if (options.isConsole()) {
			System.exit(process());
		} else {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						new ModToolsFrame(reporter, options);
					} catch (Exception e) {
						reporter.log(new LogError(e.getMessage()));
					}
				}
			});
		}
	}
	
	public void showHelp() {
		System.out.println("java -jar ModTools.jar");
		System.out.println(options.getHelpText());
		System.exit(0);
	}

	public int process() {
		if (options.isSaveAsMod()) {
			try {
				Mod newMod = combineMods(options.getSaveAsMod(), "1", "Comprising the following mods:");

				newMod.saveAsMod(options.getSaveAsMod(), "1");
			} catch (Exception e) {
				reporter.log(e);
			}
		} else if (options.isSaveAsProject()) {
			try {
				Mod newMod = combineMods(options.getSaveAsProject(), "1", "Comprising the following mods:");
				
				newMod.saveAsProject(options.getProjectDir().getAbsolutePath(), options.getSaveAsProject());
			} catch (Exception e) {
				reporter.log(e);
			}
		} else if (options.isVerify()) {
			for (File modDir : options.getModDirs()) {
				try {
					Mod mod = new Mod(reporter, modDir);
					reporter.log(new LogInfo("Mod: " + mod.getNameWithVersion()));
					mod.verify(options.isFixMinorErrors(), options.isRemoveRedundantFiles());
				} catch (Exception e) {
					reporter.log(e);
				}
			}
		} else {
			showHelp();
		}
		
		return 0;
	}
	
	private Mod combineMods(String modName, String modVersion, String modDesc) throws IOException, SAXException {
		Mod newMod = null;
		
		if (options.isMergeMods()) {
			newMod = new Mod(reporter, options.getModDir().getAbsolutePath(), modName, modVersion, modDesc);
		
			for (File modDir : options.getModDirs()) {
				newMod.merge(new Mod(reporter, modDir));
			}
		} else {
			newMod = new Mod(reporter, options.getModDirs().get(0));
			newMod.generateNewGUID();
			newMod.setNameAndVersion(modName, modVersion);
		}
		
		if (options.isVerify()) {
			newMod.verify(options.isFixMinorErrors(), options.isRemoveRedundantFiles());
		}
		
		return newMod;
	}
	
	public static void main(String args[]) {
		new ModTools(args);
	}
}
