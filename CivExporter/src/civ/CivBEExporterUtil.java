package civ;


public abstract class CivBEExporterUtil {
	protected static final String myGamesDir = "C:/Users/William/Documents/My Games";
	protected static final String tempDir ="C:/temp/civBE";
	
	private static final String civCache = myGamesDir + "/Sid Meier's Civilization Beyond Earth/cache";
	
	private static final String dbData = civCache + "/CivBEDebugDatabase.db";
	private static final String dbText = civCache + "/Localization-Merged.db";

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace(System.err);
		}
	}
	
	protected static final String jdbcDataConnection = "jdbc:sqlite:" + dbData;
	protected static final String jdbcTextConnection = "jdbc:sqlite:" + dbText;
}
