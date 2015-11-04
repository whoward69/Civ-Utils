package civ.unit.exporter.xml;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import civ.unit.exporter.UnitArtExporter;

public class XmlUnitArtExporter extends UnitArtExporter {
	public XmlUnitArtExporter(PrintStream out, Connection conn) {
		super(out, conn);
	}

	@Override
	public void outputComment(String comment) {
		out.println("\n  <!-- " + comment + " -->");
	}

	@Override
	public void outputQuery(String table, String query, String fkValue) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, fkValue);
	      
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();

		if ( rs.next() ) {
			out.println("  <" + table + ">");

			do {
				out.println("    <Row>");
				for ( int i = 1; i <= rsmd.getColumnCount(); ++i ) {
					String val = rs.getString(i);
					
					if ( val != null && val.trim().length() > 0 ) {
						String col = rsmd.getColumnName(i);
						out.println("      <" + col + ">" + val + "</" + col + ">");
					}
				}
				out.println("    </Row>");
			} while ( rs.next() );

			out.println("  </" + table + ">");
		}
	}
}
