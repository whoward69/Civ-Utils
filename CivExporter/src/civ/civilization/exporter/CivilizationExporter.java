package civ.civilization.exporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.TableExporter;

public abstract class CivilizationExporter extends TableExporter {
	protected CivilizationExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
	}
	
	protected abstract File getFile(File dir, String name) throws IOException;
	
	protected PrintStream start(File file) throws IOException {
		return new PrintStream(file, "UTF-8");
	}
	
	protected void end(PrintStream out) throws IOException {
		out.close();
	}
	
	public void export(File civDir, String civType, boolean includeUniques) throws IOException, SQLException {
		PrintStream out; 

		if ( civType != null ) {
			PrimaryTableExporter tableExporter = getPrimaryTableExporter();
			
			// Civilization
			out = start(getFile(civDir, "Civilization"));
			tableExporter.export(out, "Civilizations", civType, "Civilization_%");

			out.println();
			tableExporter.export(out, "PlayerColors", getPlayerColour(civType), null);
			tableExporter.export(out, "Colors", getPlayerPrimaryColour(civType), null);
			tableExporter.export(out, "Colors", getPlayerSecondaryColour(civType), null);

			tableExporter.exportTextKeys();
			end(out);
			
			// Leader
			out = start(getFile(civDir, "Leader"));
			tableExporter.export(out, "Leaders", getLeader(civType), "Leader_%");
			tableExporter.exportTextKeys();
			end(out);
			
			if ( !includeUniques ) {
				// Personality
				out = start(getFile(civDir, "Personality"));
				tableExporter.export(out, "Personalities", getPersonality(civType), null);
				// tableExporter.exportTextKeys();
				end(out);
			}

			// Diplomacy Responses
			out = start(getFile(civDir, "DiplomacyResponses"));
			tableExporter.exportDiplomacyResponsesTable(out, getLeader(civType));
			tableExporter.exportTextKeys();
			end(out);

			// Trait
			out = start(getFile(civDir, "Trait"));
			tableExporter.export(out, "Traits", getLeaderTrait(civType), "Trait_%");
			tableExporter.exportTextKeys();
			end(out);

			if ( includeUniques ) {
				boolean bNext;
				
				// Unique Units
				List<String> units = getUniqueUnits(civType);
				if ( units.size() > 0 ) {
					out = start(getFile(civDir, "Units"));
					bNext = false;
	
					for ( String unit : units ) {
						if ( bNext ) out.println();
						tableExporter.export(out, "Units", unit, "Unit%");
						tableExporter.exportTextKeys();
						bNext = true;
					}
					
					end(out);
				}
	
				// Unique Buildings
				List<String> buildings = getUniqueBuildings(civType);
				if ( buildings.size() > 0 ) {
					out = start(getFile(civDir, "Buildings"));
					bNext = false;
	
					for ( String building : buildings ) {
						if ( bNext ) out.println();
						tableExporter.export(out, "Buildings", building, "Building_%");
						tableExporter.exportTextKeys();
						bNext = true;
					}
					
					end(out);
				}
	
				// Unique Improvements
				List<String> improvements = getUniqueImprovements(civType);
				if ( improvements.size() > 0 ) {
					out = start(getFile(civDir, "Improvements"));
					bNext = false;
	
					for ( String improvement : improvements ) {
						if ( bNext ) out.println();
						tableExporter.export(out, "Improvements", improvement, "%");
						tableExporter.exportTextKeys();
						bNext = true;
					}
					
					end(out);
				}
			}
		}
	}

	private String getPlayerColour(String civType) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement("SELECT DefaultPlayerColor FROM Civilizations WHERE Type=?");
		ps.setString(1, civType); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();

		if ( rs.next() ) {
			return rs.getString(1);
		}
		
		throw new SQLException("No PlayerColour found for Civilizations.Type='" + civType + "'");
	}

	private String getPlayerPrimaryColour(String civType) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement("SELECT pc.PrimaryColor FROM PlayerColors pc, Civilizations c WHERE c.DefaultPlayerColor=pc.Type AND c.Type=?");
		ps.setString(1, civType); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();

		if ( rs.next() ) {
			return rs.getString(1);
		}
		
		throw new SQLException("No PlayerColour found for Civilizations.Type='" + civType + "'");
	}

	private String getPlayerSecondaryColour(String civType) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement("SELECT pc.SecondaryColor FROM PlayerColors pc, Civilizations c WHERE c.DefaultPlayerColor=pc.Type AND c.Type=?");
		ps.setString(1, civType); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();

		if ( rs.next() ) {
			return rs.getString(1);
		}
		
		throw new SQLException("No PlayerColour found for Civilizations.Type='" + civType + "'");
	}

	private String getLeader(String civType) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement("SELECT LeaderheadType FROM Civilization_Leaders WHERE CivilizationType=?");
		ps.setString(1, civType); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();

		if ( rs.next() ) {
			return rs.getString(1);
		}
		
		throw new SQLException("No Leader found for Civilizations.Type='" + civType + "'");
	}

	private String getLeaderTrait(String civType) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement("SELECT t.TraitType FROM Civilization_Leaders l, Leader_Traits t WHERE l.CivilizationType=? AND l.LeaderheadType=t.LeaderType");
		ps.setString(1, civType); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();

		if ( rs.next() ) {
			return rs.getString(1);
		}
		
		throw new SQLException("No Trait found for Civilizations.Type='" + civType + "'");
	}

	private String getPersonality(String civType) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement("SELECT Personality FROM Civilizations WHERE Type=?");
		ps.setString(1, civType); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();

		if ( rs.next() ) {
			return rs.getString(1);
		}
		
		throw new SQLException("No Personality found for Civilizations.Type='" + civType + "'");
	}

	private List<String> getUniqueUnits(String civType) throws SQLException {
		return getUniques("SELECT UnitType FROM Civilization_UnitClassOverrides WHERE CivilizationType=?", civType);
	}

	private List<String> getUniqueBuildings(String civType) throws SQLException {
		return getUniques("SELECT BuildingType FROM Civilization_BuildingClassOverrides WHERE CivilizationType=?", civType);
	}

	private List<String> getUniqueImprovements(String civType) throws SQLException {
		return getUniques("SELECT Type FROM Improvements WHERE SpecificCivRequired=1 AND CivilizationType=?", civType);
	}

	private List<String> getUniques(String query, String civType) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement(query);
		ps.setString(1, civType); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();
		
		List<String> uniques = new ArrayList<String>();

		if ( rs.next() ) {
			do {
				uniques.add(rs.getString(1));
			} while ( rs.next() );
		}
		
		return uniques;
	}
}
