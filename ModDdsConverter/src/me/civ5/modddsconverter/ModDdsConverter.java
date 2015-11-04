package me.civ5.modddsconverter;

import java.awt.EventQueue;

import javax.swing.JFrame;

import me.civ5.modddsconverter.ui.DdsConverterFrame;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.utils.ModUtilsOptions;
import me.civ5.modutils.utils.ModUtilsReporter;
import me.civ5.xml.XmlBuilder;
import me.civ5.xpath.XpathHelper;

import org.jdom.Document;

public class ModDdsConverter extends JFrame {
	private ModReporter reporter;
	private ModUtilsOptions options;
	
	public ModDdsConverter(String args[]) {
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
						Document config = XmlBuilder.parse(getClass().getResourceAsStream("/me/civ5/modddsconverter/config/ModDdsConverter.xml"));
						Document language = XmlBuilder.parse(getClass().getResourceAsStream("/me/civ5/modddsconverter/languages/ModDdsConverter_en_GB.xml"));
						new DdsConverterFrame(reporter, options, XpathHelper.getElement(config, "/configs"), XpathHelper.getElement(language, "/languages/language"));
					} catch (Exception e) {
						e.printStackTrace(System.err);
						reporter.log(new LogError(e.getMessage()));
					}
				}
			});
		}
	}
	
	public void showHelp() {
		System.out.println("java -jar DdsConverter.jar");
		System.out.println(options.getHelpText());
		System.exit(0);
	}

	public int process() {
		return 0;
	}

	public static void main(String args[]) {
		new ModDdsConverter(args);
	}
}
