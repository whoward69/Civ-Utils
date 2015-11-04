package civ.civilization.exporter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import civ.Exporter;

public abstract class CivilizationsExporter extends Exporter {
	private boolean includeUniques = true;
	
	public CivilizationsExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeUniques) {
		super(jdbcDataConnection, jdbcTextConnection);
		
		this.includeUniques = includeUniques;
	}
	
	@Override
	public void export(File baseDir, boolean includeData, boolean includeArt) throws IOException, SQLException {
		Connection dbData = null;
		Connection dbText = null;

		try {
			File outputDir = getOutputDir(baseDir);
			outputDir.mkdirs();
			
			dbData = open(jdbcDataConnection);
			dbText = open(jdbcTextConnection);

			CivilizationExporter exporter = getCivilizationExporter(dbData, dbText);

			PreparedStatement ps = dbData.prepareStatement("SELECT Type FROM Civilizations WHERE Playable=1 OR AIPlayable=1");
			ResultSet rs = ps.executeQuery();

			while ( rs.next() ) {
				String civType = rs.getString(1);
				
				File outputCivDir = new File(outputDir, getName(civType));
				outputCivDir.mkdirs();
				
				exporter.export(outputCivDir, civType, includeUniques);
			}
	    } finally {
	    	close(dbData);
	    	close(dbText);
	    }
	}
	
	protected abstract CivilizationExporter getCivilizationExporter(Connection dbData, Connection dbText);
}
