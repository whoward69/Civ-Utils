package civ.unit;

import java.io.File;

import civ.CivBEExporterUtil;
import civ.unit.exporter.xml.XmlUnitsExporter;


public class CivBEUnitsExporterUtil extends CivBEExporterUtil {
	public static void main(String[] args) throws Exception {
		File unitsDataDir = new File(tempDir, "units/data");

		new XmlUnitsExporter(jdbcDataConnection, jdbcTextConnection, true).export(unitsDataDir, true, false);
		// new SqlUnitsExporter(jdbcDataConnection, jdbcTextConnection, true).export(unitsDataDir, true, false);
	}
}
