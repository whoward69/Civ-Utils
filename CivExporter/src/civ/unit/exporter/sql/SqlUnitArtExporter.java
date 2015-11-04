package civ.unit.exporter.sql;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import civ.unit.exporter.UnitArtExporter;

public class SqlUnitArtExporter extends UnitArtExporter {
	public SqlUnitArtExporter(PrintStream out, Connection conn) {
		super(out, conn);
	}
	
	@Override
	public void outputComment(String comment) {
		out.println("\n-- " + comment);
	}

	@Override
	public void outputQuery(String table, String query, String fkValue) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, fkValue);
	      
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();

		if ( rs.next() ) {
			do {
				out.print("INSERT INTO " + table);
				String prefix = "(";
				
				for ( int i = 1; i <= rsmd.getColumnCount(); ++i ) {
					String val = rs.getString(i);
					
					if ( val != null && val.trim().length() > 0 ) {
						String col = rsmd.getColumnName(i);
						
						if ( "index".equalsIgnoreCase(col) ) {
							col = '"' + col + '"';
						}
						
						out.print(prefix + col);
						prefix = ", ";
					}
				}

				out.print(")\n  VALUES ");
				prefix = "(";

				for ( int i = 1; i <= rsmd.getColumnCount(); ++i ) {
					String val = rs.getString(i);
					
					if ( val != null && val.trim().length() > 0 ) {
						int ct = rsmd.getColumnType(i);
						if ( ct == Types.VARCHAR || ct == Types.CHAR ) {
							out.print(prefix + "'" + val + "'");
						} else {
							out.print(prefix + val);
						}
						prefix = ", ";
					}
				}

				out.println(");");
			} while ( rs.next() );
		}
	}
}
