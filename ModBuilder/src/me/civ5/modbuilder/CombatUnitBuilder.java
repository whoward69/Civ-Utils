package me.civ5.modbuilder;

import java.awt.EventQueue;

import me.civ5.modbuilder.ui.CombatUnitBuilderFrame;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.utils.ModUtilsOptions;
import me.civ5.modutils.utils.ModUtilsReporter;
import me.civ5.xml.XmlBuilder;
import me.civ5.xpath.XpathHelper;

import org.jdom.Document;

public class CombatUnitBuilder {
	private ModReporter reporter;
	private ModUtilsOptions options;
	
	public CombatUnitBuilder(String args[]) {
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
						Document language = XmlBuilder.parse(getClass().getResourceAsStream("/me/civ5/modbuilder/languages/ModBuilder_en_GB.xml"));
						new CombatUnitBuilderFrame(reporter, options, XpathHelper.getElement(language, "/languages/language"));
					} catch (Exception e) {
						reporter.log(new LogError(e.getMessage()));
					}
				}
			});
		}
	}
	
	public void showHelp() {
		System.out.println("java -jar ModBuilder.jar");
		System.out.println(options.getHelpText());
		System.exit(0);
	}

	public int process() {
		return 0;
	}

	public static void main(String args[]) {
		new CombatUnitBuilder(args);
	}
}
