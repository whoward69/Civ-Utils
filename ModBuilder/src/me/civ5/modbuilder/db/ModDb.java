package me.civ5.modbuilder.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.utils.ModUtilsOptions;

public class ModDb {
	private static final String jdbcPrefix = "jdbc:sqlite:";
	private static final String dbData = "/Civ5DebugDatabase.db";
	private static final String dbText = "/Localization-Merged.db";
	
	private ModReporter reporter;
	private ModUtilsOptions options;

	private Connection dataConn;
	private Connection textConn;
	
	private PreparedStatement psTxtKey;
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace(System.err);
		}
	}
	
	public ModDb(ModReporter reporter, ModUtilsOptions options) throws SQLException {
		this.reporter = reporter;
		this.options = options;
		
		dataConn = open(getJdbcDataConnection());
		textConn = open(getJdbcTextConnection());
		
		psTxtKey = textConn.prepareStatement("SELECT Text FROM Language_en_US WHERE Tag=?");
	}
	
	private String getJdbcDataConnection() {
		return jdbcPrefix + options.getSqlDir().getAbsolutePath() + dbData;
	}
	
	private String getJdbcTextConnection() {
		return jdbcPrefix + options.getSqlDir().getAbsolutePath() + dbText;
	}
	
	public boolean availableType(String table, String type) {
		boolean available = true;
		
		try {
			String query = "SELECT 1 FROM " + table + " WHERE Type=?";
			PreparedStatement ps = dataConn.prepareStatement(query);
			ps.setString(1, type);
			ResultSet rs = ps.executeQuery();
			
			available = !rs.next();
		} catch (SQLException e) {
			available = false;
			reporter.log(e);
		}
		
		return available;
	}
	
	public Object[] executeQuery(String query, Object[] params, int selected) {
		Object[] results = new Object[selected];
		
		try {
			PreparedStatement ps = dataConn.prepareStatement(query);
			
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					ps.setObject(i+1, params[i]);
				}
			}
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				for (int i = 0; i < selected; ++i) {
					results[i] = rs.getObject(i+1);
				}
			}
		} catch (SQLException e) {
			reporter.log(e);
			return null;
		}
		
		return results;
	}
	
	public List<Object[]> executeQuery(String query, Object[] params, int selected, int limit) {
		List<Object[]> results = new ArrayList<Object[]>();
		
		try {
			PreparedStatement ps = dataConn.prepareStatement(query);
			
			if (params != null) {
				for (int i = 0; i < params.length; ++i) {
					ps.setObject(i+1, params[i]);
				}
			}
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next() && limit-- > 0) {
				Object[] set = new Object[selected];
				
				for (int i = 0; i < selected; ++i) {
					set[i] = rs.getObject(i+1);
				}
				
				results.add(set);
			}
		} catch (SQLException e) {
			reporter.log(e);
			return null;
		}
		
		return results;
	}
	
	public Set<SelectEntry> getSelectItems(String lookupTable, String keyColumn, String valueColumn, String whereClause) {
		Set<SelectEntry> items = new TreeSet<SelectEntry>();
		
		try {
			String query = "SELECT " + keyColumn + ", " + valueColumn + " FROM " + lookupTable;
			if (whereClause != null) {
				query = query + " WHERE " + whereClause;
			}
			
			PreparedStatement ps = dataConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			if ( rs.next() ) {
				do {
					items.add(new SelectEntry(rs.getString(1), translate(rs.getString(2)), rs.getString(1)));
				} while ( rs.next() );
			}
		} catch (SQLException e) {
			items.clear();
			reporter.log(e);
		}

		return items;
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

	private String translate(String txtKey) throws SQLException {
		String text = txtKey;
		
		if (txtKey.startsWith("TXT_KEY")) {
			psTxtKey.setString(1, txtKey);
			ResultSet rs = psTxtKey.executeQuery();
	
			if (rs.next()) {
				text = rs.getString(1);
			}
		}
		
		int start = text.indexOf('[');
		while (start != -1) {
			int end = text.indexOf(']', start);
			if (end != -1) {
				text = text.substring(0, start) + text.substring(end+1);
			}
			
			start = text.indexOf('[', start);
		}
		
		return text;
	}
}
