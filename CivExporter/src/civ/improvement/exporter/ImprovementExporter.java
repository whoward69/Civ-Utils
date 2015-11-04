package civ.improvement.exporter;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.TableExporter;

public abstract class ImprovementExporter extends TableExporter {
	protected ImprovementExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
	}
	
	protected abstract void start(PrintStream out) throws IOException;
	protected abstract void end(PrintStream out) throws IOException;
	
	public void export(PrintStream out, String improvementType, boolean includeData, boolean includeArt) throws IOException, SQLException {
		start(out);
		if (includeData) exportData(out, improvementType);
		end(out);
	}
	
	protected void exportData(PrintStream out, String improvementType) throws SQLException {
		PrimaryTableExporter tableExporter = getPrimaryTableExporter();

		tableExporter.export(out, "Improvements", improvementType, "Improvement_%");

		tableExporter.outputComment("Text Entries");
		tableExporter.exportTextKeys();
	}
}
