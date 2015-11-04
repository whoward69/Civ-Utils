package civ.unit.exporter.sql;

import java.io.File;
import java.sql.Connection;

import civ.unit.exporter.UnitExporter;
import civ.unit.exporter.UnitsExporter;

public class SqlUnitsExporter extends UnitsExporter {
	public SqlUnitsExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeOrbital) {
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
