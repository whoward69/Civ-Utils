package civ.unit.exporter.xml;

import java.io.File;
import java.sql.Connection;

import civ.unit.exporter.OrphanedUnitArtExporter;
import civ.unit.exporter.UnitExporter;

public class XmlOrphanedUnitArtExporter extends OrphanedUnitArtExporter {
	public XmlOrphanedUnitArtExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeOrbital) {
		super(jdbcDataConnection, jdbcTextConnection, includeOrbital);
	}

	@Override
	protected File getOutputDir(File baseDir) {
		return new File(baseDir, "xml");
	}

	@Override
	protected File getOutputFile(File outputDir, String type) {
		return new File(outputDir, getName(type) + ".xml");
	}

	@Override
	protected UnitExporter getUnitExporter(Connection dbData, Connection dbText) {
		return new XmlUnitExporter(dbData, dbText, includeOrbital);
	}
}
