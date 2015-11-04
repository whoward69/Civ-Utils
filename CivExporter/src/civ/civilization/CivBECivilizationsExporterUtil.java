package civ.civilization;

import java.io.File;

import civ.CivBEExporterUtil;
import civ.civilization.exporter.xml.XmlCivilizationsExporter;

public class CivBECivilizationsExporterUtil extends CivBEExporterUtil {
	public static void main(String[] args) throws Exception {
		File civsDir = new File(tempDir, "civs");

		new XmlCivilizationsExporter(jdbcDataConnection, jdbcTextConnection, false).export(civsDir, true, false);
//		new SqlCivilizationsExporter(jdbcDataConnection, jdbcTextConnection, false).export(civsDir, true, false);
	}
}
