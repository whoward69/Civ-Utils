package civ.building.exporter.sql;

import java.io.File;
import java.sql.Connection;

import civ.building.exporter.BuildingExporter;
import civ.building.exporter.BuildingsExporter;

public class SqlBuildingsExporter extends BuildingsExporter {
	public SqlBuildingsExporter(String jdbcDataConnection, String jdbcTextConnection) {
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
	protected BuildingExporter getBuildingExporter(Connection dbData, Connection dbText) {
		return new SqlBuildingExporter(dbData, dbText);
	}
}
