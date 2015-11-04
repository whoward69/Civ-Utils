package civ.unit;

import java.io.File;

import civ.Civ5ExporterUtil;
import civ.unit.exporter.sql.SqlUnitsExporter;
import civ.unit.exporter.xml.XmlUnitsExporter;


public class Civ5UnitsArtExporterUtil extends Civ5ExporterUtil {
	public static void main(String[] args) throws Exception {
		File unitsArtDir = new File(tempDir, "units/art");

		new XmlUnitsExporter(jdbcDataConnection, jdbcTextConnection, false).export(unitsArtDir, false, true);
		new SqlUnitsExporter(jdbcDataConnection, jdbcTextConnection, false).export(unitsArtDir, false, true);
	}
}
