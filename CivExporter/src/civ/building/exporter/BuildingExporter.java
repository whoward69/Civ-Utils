package civ.building.exporter;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.TableExporter;

public abstract class BuildingExporter extends TableExporter {
	protected BuildingExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
	}
	
	protected abstract void start(PrintStream out) throws IOException;
	protected abstract void end(PrintStream out) throws IOException;
	
	public void export(PrintStream out, String buildingType, boolean includeData, boolean includeArt) throws IOException, SQLException {
		start(out);
		if (includeData) exportData(out, buildingType);
		end(out);
	}
	
	protected void exportData(PrintStream out, String buildingType) throws SQLException {
		PrimaryTableExporter tableExporter = getPrimaryTableExporter();

		tableExporter.export(out, "Buildings", buildingType, "Building_%");

		tableExporter.outputComment("Text Entries");
		tableExporter.exportTextKeys();
	}
}
