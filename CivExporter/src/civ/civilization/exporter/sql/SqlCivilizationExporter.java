package civ.civilization.exporter.sql;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

import civ.civilization.exporter.CivilizationExporter;
import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.sql.SqlPrimaryTableExporter;

public class SqlCivilizationExporter extends CivilizationExporter {
	public SqlCivilizationExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
	}

	@Override
	protected File getFile(File dir, String name) throws IOException {
		return new File(dir, name + ".sql");
	}

	@Override
	protected PrintStream start(File file) throws IOException {
		return super.start(file);
	}

	@Override
	protected void end(PrintStream out) throws IOException {
		super.end(out);
	}

	@Override
	protected PrimaryTableExporter getPrimaryTableExporter() {
		return new SqlPrimaryTableExporter(dbData, dbText);
	}
}
