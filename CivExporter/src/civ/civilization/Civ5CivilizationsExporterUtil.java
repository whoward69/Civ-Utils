package civ.civilization;

import java.io.File;

import civ.Civ5ExporterUtil;
import civ.civilization.exporter.xml.XmlCivilizationsExporter;

public class Civ5CivilizationsExporterUtil extends Civ5ExporterUtil {
	public static void main(String[] args) throws Exception {
		File civsDir = new File(tempDir, "civs");

		new XmlCivilizationsExporter(jdbcDataConnection, jdbcTextConnection, true).export(civsDir, true, false);
//		new SqlCivilizationsExporter(jdbcDataConnection, jdbcTextConnection, true).export(civsDir, true, false);
	}
}
