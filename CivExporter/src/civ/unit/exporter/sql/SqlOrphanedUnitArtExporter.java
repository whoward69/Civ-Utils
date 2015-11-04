package civ.unit.exporter.sql;

import java.io.File;
import java.sql.Connection;

import civ.unit.exporter.OrphanedUnitArtExporter;
import civ.unit.exporter.UnitExporter;

public class SqlOrphanedUnitArtExporter extends OrphanedUnitArtExporter {
	public SqlOrphanedUnitArtExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeOrbital) {
		super(jdbcDataConnection, jdbcTextConnection, includeOrbital);
	}

	@Override
	protected File getOutputDir(File baseDir) {
		return new File(baseDir, "sql");
	}

	@Override
	protected File getOutputFile(File outputDir, String type) {
		return new File(outputDir, getName(type) + ".sql");
	}

	@Override
	protected UnitExporter getUnitExporter(Connection dbData, Connection dbText) {
		return new SqlUnitExporter(dbData, dbText, includeOrbital);
	}
}
