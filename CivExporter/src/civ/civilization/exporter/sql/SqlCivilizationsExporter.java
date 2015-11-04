package civ.civilization.exporter.sql;

import java.io.File;
import java.sql.Connection;

import civ.civilization.exporter.CivilizationExporter;
import civ.civilization.exporter.CivilizationsExporter;

public class SqlCivilizationsExporter extends CivilizationsExporter {
	public SqlCivilizationsExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeUniques) {
		super(jdbcDataConnection, jdbcTextConnection, includeUniques);
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
	protected CivilizationExporter getCivilizationExporter(Connection dbData, Connection dbText) {
		return new SqlCivilizationExporter(dbData, dbText);
	}
}
