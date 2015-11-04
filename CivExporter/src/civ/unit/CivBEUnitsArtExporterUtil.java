package civ.unit;

import java.io.File;

import civ.CivBEExporterUtil;
import civ.unit.exporter.sql.SqlUnitsExporter;
import civ.unit.exporter.xml.XmlUnitsExporter;


public class CivBEUnitsArtExporterUtil extends CivBEExporterUtil {
	public static void main(String[] args) throws Exception {
		File unitsArtDir = new File(tempDir, "units/art");

		new XmlUnitsExporter(jdbcDataConnection, jdbcTextConnection, true).export(unitsArtDir, false, true);
		new SqlUnitsExporter(jdbcDataConnection, jdbcTextConnection, true).export(unitsArtDir, false, true);
	}
}
