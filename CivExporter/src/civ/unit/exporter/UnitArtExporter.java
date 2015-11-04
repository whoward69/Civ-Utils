package civ.unit.exporter;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class UnitArtExporter {
	protected PrintStream out;
	protected Connection conn;
	
	public UnitArtExporter(PrintStream out, Connection conn) {
		this.out = out;
		this.conn = conn;
	}
	
	public abstract void outputQuery(String table, String query, String fkValue) throws SQLException;
	public abstract void outputComment(String comment) throws SQLException;
}
