package civ.building.exporter.sql;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

import civ.building.exporter.BuildingExporter;
import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.sql.SqlPrimaryTableExporter;

public class SqlBuildingExporter extends BuildingExporter {
	public SqlBuildingExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
	}

	@Override
	protected void start(PrintStream out) throws IOException {
	}

	@Override
	protected void end(PrintStream out) throws IOException {
	}

	@Override
	protected PrimaryTableExporter getPrimaryTableExporter() {
		return new SqlPrimaryTableExporter(dbData, dbText);
	}
}
