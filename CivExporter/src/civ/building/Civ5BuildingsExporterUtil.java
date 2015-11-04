package civ.building;

import java.io.File;

import civ.Civ5ExporterUtil;
import civ.building.exporter.sql.SqlBuildingsExporter;
import civ.building.exporter.xml.XmlBuildingsExporter;


public class Civ5BuildingsExporterUtil extends Civ5ExporterUtil {
	public static void main(String[] args) throws Exception {
		File buildingsDataDir = new File(tempDir, "buildings/data");

		new XmlBuildingsExporter(jdbcDataConnection, jdbcTextConnection).export(buildingsDataDir, true, false);
		new SqlBuildingsExporter(jdbcDataConnection, jdbcTextConnection).export(buildingsDataDir, true, false);
	}
}
