package civ;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Exporter {
	protected String jdbcDataConnection;
	protected String jdbcTextConnection;
	
	public Exporter(String jdbcDataConnection, String jdbcTextConnection) {
		this.jdbcDataConnection = jdbcDataConnection;
		this.jdbcTextConnection = jdbcTextConnection;
	}
	
	protected Connection open(String jdbcConnection) throws SQLException {
		Connection db = DriverManager.getConnection(jdbcConnection);
		Statement statement = db.createStatement();
		statement.setQueryTimeout(30);
		
		return db;
	}
	
	protected void close(Connection db) {
    	if ( db != null ) {
    		try {
				db.close();
			} catch (SQLException e) {}
    	}
	}

	protected String getName(String type) {
		int pos = type.indexOf('_');
		String name = type.substring(pos+1);
		name = name.substring(0, 1) + name.substring(1).toLowerCase();
		
		pos = name.indexOf('_');
		while ( pos != -1 ) {
			name = name.substring(0, pos) + name.substring(pos+1, pos+2).toUpperCase() + name.substring(pos+2);
			pos = name.indexOf('_');
		}
		
		return name;
	}
	
	public abstract void export(File baseDir, boolean includeData, boolean includeArt) throws IOException, SQLException;
	
	protected abstract File getOutputDir(File baseDir);
	protected abstract File getOutputFile(File outputDir, String type);
}
