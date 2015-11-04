package civ.building;

import java.io.File;

import civ.CivBEExporterUtil;
import civ.building.exporter.xml.XmlBuildingsExporter;


public class CivBEBuildingsExporterUtil extends CivBEExporterUtil {
	public static void main(String[] args) throws Exception {
		File buildingsDataDir = new File(tempDir, "buildings/data");

		new XmlBuildingsExporter(jdbcDataConnection, jdbcTextConnection).export(buildingsDataDir, true, false);
		// new SqlBuildingsExporter(jdbcDataConnection, jdbcTextConnection).export(buildingsDataDir, true, false);
	}
}
