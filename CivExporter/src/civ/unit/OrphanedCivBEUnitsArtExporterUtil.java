package civ.unit;

import java.io.File;

import civ.CivBEExporterUtil;
import civ.unit.exporter.sql.SqlOrphanedUnitArtExporter;
import civ.unit.exporter.xml.XmlOrphanedUnitArtExporter;


public class OrphanedCivBEUnitsArtExporterUtil extends CivBEExporterUtil {
	public static void main(String[] args) throws Exception {
		File unitsArtDir = new File(tempDir, "orphans/unitart");

		new XmlOrphanedUnitArtExporter(jdbcDataConnection, jdbcTextConnection, true).export(unitsArtDir, false, true);
		new SqlOrphanedUnitArtExporter(jdbcDataConnection, jdbcTextConnection, true).export(unitsArtDir, false, true);
	}
}
