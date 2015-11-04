package civ.unit.exporter.sql;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.sql.SqlPrimaryTableExporter;
import civ.unit.exporter.UnitArtExporter;
import civ.unit.exporter.UnitExporter;

public class SqlUnitExporter extends UnitExporter {
	public SqlUnitExporter(Connection dbData, Connection dbText, boolean includeOrbital) {
		super(dbData, dbText, includeOrbital);
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
	
	@Override
	protected UnitArtExporter getUnitArtExporter(PrintStream out) {
		return new SqlUnitArtExporter(out, dbData);
	}
}
