package civ.table.exporter.sql;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import civ.table.exporter.PrimaryTableExporter;

public class SqlPrimaryTableExporter extends PrimaryTableExporter {
	public SqlPrimaryTableExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
	}

	// TODO - write all SQL

	@Override
	public void outputComment(String comment) {
		out.println("\n-- " + comment);
	}

	@Override
	public void exportTextKeys() throws SQLException {
	}

	@Override
	public void exportTable(String table, String pkCol, String pkValue) throws SQLException {
	}

	@Override
	protected void exportCityNamesTable(String pkValue) throws SQLException {
	}

	@Override
	public void exportDiplomacyResponsesTable(PrintStream out, String pkValue) throws SQLException {
	}
}
