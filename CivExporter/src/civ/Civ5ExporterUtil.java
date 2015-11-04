package civ;


public abstract class Civ5ExporterUtil {
	protected static final String myGamesDir = "C:/Users/William/Documents/My Games";
	protected static final String tempDir ="C:/temp/civ5";
	
	private static final String civCache = myGamesDir + "/Sid Meier's Civilization 5/cache";
	
	private static final String dbData = civCache + "/Civ5DebugDatabase.db";
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
