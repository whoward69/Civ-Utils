package civ.improvement.exporter.sql;

import java.io.File;
import java.sql.Connection;

import civ.improvement.exporter.ImprovementExporter;
import civ.improvement.exporter.ImprovementsExporter;

public class SqlImprovementsExporter extends ImprovementsExporter {
	public SqlImprovementsExporter(String jdbcDataConnection, String jdbcTextConnection) {
		super(jdbcDataConnection, jdbcTextConnection);
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
	protected ImprovementExporter getImprovementExporter(Connection dbData, Connection dbText) {
		return new SqlImprovementExporter(dbData, dbText);
	}
}
