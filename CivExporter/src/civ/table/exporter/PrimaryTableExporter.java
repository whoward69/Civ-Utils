package civ.table.exporter;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PrimaryTableExporter {
	protected PrintStream out;
	protected Connection dbData;
	protected Connection dbText;
	
	protected Set<TextKey> textTags;
	
	public PrimaryTableExporter(Connection dbData, Connection dbText) {
		this.dbData = dbData;
		this.dbText = dbText;
		
		this.textTags = new LinkedHashSet<TextKey>();
	}
	
	public void export(PrintStream out, String pTable, String pkValue, String secondaryPattern) throws SQLException {
		this.out = out;
		
		exportTable(pTable, "Type", pkValue);
		
		if ( secondaryPattern != null ) {
			out.println("");
		
			for ( String table : getAllTables(dbData, secondaryPattern) ) {
				if ( !isPrimaryTable(dbData, table) ) {
					String col = references(dbData, table, pTable);
					if ( col != null ) {
						if ( table.equalsIgnoreCase("Civilization_CityNames") ) {
							exportCityNamesTable(pkValue);
						} else {
							exportTable(table, col, pkValue);
						}
					}
				}
			}
		}
	}
	
	public abstract void outputComment(String comment) throws SQLException;
	public abstract void exportTextKeys() throws SQLException;
	public abstract void exportTable(String table, String pkCol, String pkValue) throws SQLException;
	protected abstract void exportCityNamesTable(String pkValue) throws SQLException;
	public abstract void exportDiplomacyResponsesTable(PrintStream out, String pkValue) throws SQLException;

	// Get all tables in the database that match a pattern, typically "primarytable_%"
	protected static List<String> getAllTables(Connection dbData, String tablePattern) throws SQLException {
		List<String> tables = new ArrayList<String>();
		
		ResultSet rs = dbData.getMetaData().getTables(null, null, tablePattern, new String[] {"TABLE"});
		while (rs.next()) {
			tables.add(rs.getString(3));
		}
		
		return tables;
	}

	// Does any column of table contain a foreign key reference to foreignTable.Type?
	protected static String references(Connection dbData, String table, String foreignTable) throws SQLException {
	    DatabaseMetaData metaData = dbData.getMetaData();
	    ResultSet foreignKeys = metaData.getImportedKeys(dbData.getCatalog(), null, table);
	    while ( foreignKeys.next() ) {
	    	if ( foreignKeys.getString("PKCOLUMN_NAME").equalsIgnoreCase("Type") && foreignKeys.getString("PKTABLE_NAME").equalsIgnoreCase(foreignTable) ) {
	    		return foreignKeys.getString("FKCOLUMN_NAME");
	    	}
	    }
	    
	    return null;
	}
	
	private Map<String, Boolean> primaryTables = new HashMap<String, Boolean>();
	
	protected boolean isPrimaryTable(Connection dbData, String table) throws SQLException {
		if ( !primaryTables.containsKey(table) ) {
			boolean isPrimary = false;
			
			if ( !table.contains("_") ) {
				boolean bID = false;
				boolean bType = false;
				
				DatabaseMetaData meta = dbData.getMetaData();
				ResultSet rs = meta.getColumns(dbData.getCatalog(), meta.getUserName(), table, "%");
				
				while ( rs.next() ) {
					String col = rs.getString(4);
					
					if ( col.equalsIgnoreCase("ID") ) {
						bID = true;
					} else if ( col.equalsIgnoreCase("Type") ) {
						bType = true;
					}
				}
				
				isPrimary = bID && bType;
			}
			
			primaryTables.put(table, isPrimary);
		}
		
		return primaryTables.get(table);
	}
	
	protected class TextKey implements Comparable<TextKey> {
		String key;
		String altKey = null;
		
		boolean placeHolder = false;
		String placeHolderText;
		
		protected TextKey(String key, boolean placeHolder) {
			this.key = key;
			this.placeHolder = placeHolder;
		}
		
		public TextKey(String key, String altKey) {
			this.key = key;
			this.altKey = altKey;
		}
		
		public TextKey(String text) {
			this(text, false);
		}

		public String getKey() {
			return key;
		}
		
		public String getAltKey() {
			return ((altKey != null) ? altKey : key);
		}
		
		public boolean isMultiple() {
			return getAltKey().endsWith("%");
		}

		public boolean isPlaceHolder() {
			return placeHolder;
		}

		public String getPlaceHolderText() {
			return placeHolderText;
		}

		@Override
		public int compareTo(TextKey that) {
			return this.key.compareTo(that.key);
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if ( obj instanceof TextKey ) {
				return key.equals(((TextKey) obj).key);
			}
			
			return false;
		}
	}
	
	protected class CivilopediaTextKey extends TextKey {
		public CivilopediaTextKey(String key) {
			super(key, true);
			placeHolderText = "<!-- Add Civilopedia text here -->";
		}
	}
	
	protected class DiplomacyTextKey extends TextKey {
		public DiplomacyTextKey(String key, String text) {
			super(key, true);
			placeHolderText = (text != null) ? text : "<!-- Add Diplomacy text here -->";
		}
	}
}
