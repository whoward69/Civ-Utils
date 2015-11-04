package civ.table.exporter;

import java.sql.Connection;

public abstract class TableExporter {
	protected Connection dbData;
	protected Connection dbText;
	
	protected TableExporter(Connection dbData, Connection dbText) {
		this.dbData = dbData;
		this.dbText = dbText;
	}
	
	protected abstract PrimaryTableExporter getPrimaryTableExporter();
}
