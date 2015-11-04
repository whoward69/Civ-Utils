package civ.unit;

import java.io.File;

import civ.Civ5ExporterUtil;
import civ.unit.exporter.sql.SqlOrphanedUnitArtExporter;
import civ.unit.exporter.xml.XmlOrphanedUnitArtExporter;


public class OrphanedCiv5UnitsArtExporterUtil extends Civ5ExporterUtil {
	public static void main(String[] args) throws Exception {
		File unitsArtDir = new File(tempDir, "orphans/unitart");

		new XmlOrphanedUnitArtExporter(jdbcDataConnection, jdbcTextConnection, false).export(unitsArtDir, false, true);
		new SqlOrphanedUnitArtExporter(jdbcDataConnection, jdbcTextConnection, false).export(unitsArtDir, false, true);
	}
}
