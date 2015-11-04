package civ.unit;

import java.io.File;

import civ.Civ5ExporterUtil;
import civ.unit.exporter.sql.SqlUnitsExporter;
import civ.unit.exporter.xml.XmlUnitsExporter;


public class Civ5UnitsExporterUtil extends Civ5ExporterUtil {
	public static void main(String[] args) throws Exception {
		File unitsDataDir = new File(tempDir, "units/data");

		new XmlUnitsExporter(jdbcDataConnection, jdbcTextConnection, false).export(unitsDataDir, true, false);
		new SqlUnitsExporter(jdbcDataConnection, jdbcTextConnection, false).export(unitsDataDir, true, false);
	}
}
