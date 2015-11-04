package civ.unit.exporter;

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

public abstract class UnitExporter extends TableExporter {
	protected boolean includeOrbital = false;
	
	protected UnitExporter(Connection dbData, Connection dbText, boolean includeOrbital) {
		super(dbData, dbText);

		this.includeOrbital = includeOrbital;
	}
	
	protected abstract void start(PrintStream out) throws IOException;
	protected abstract void end(PrintStream out) throws IOException;
	
	public void export(PrintStream out, String unitType, boolean includeData, boolean includeArt) throws IOException, SQLException {
		start(out);
		if (includeData) exportData(out, unitType);
		if (includeArt) exportArt(out, unitType);
		end(out);
	}
	
	protected void exportData(PrintStream out, String unitType) throws SQLException {
		PrimaryTableExporter tableExporter = getPrimaryTableExporter();

		tableExporter.export(out, "Units", unitType, "Unit%");
		if ( includeOrbital ) {
			// Civ:BE upgrades and perks
			PreparedStatement ps = dbData.prepareStatement("SELECT Type FROM UnitUpgrades WHERE UnitType=? ORDER BY Type");
			ps.setString(1, unitType);
		      
			ResultSet rs = ps.executeQuery();
			
			if ( rs.isBeforeFirst() ) {
				tableExporter.outputComment("Upgrades and Perks");
			}
			
			while ( rs.next() ) {
				String unitUpgrade = rs.getString(1);
				tableExporter.exportTable("UnitUpgrades", "Type", unitUpgrade);
				tableExporter.exportTable("UnitUpgradePerkChoices", "UpgradeType", unitUpgrade);
			}
		}

		if ( includeOrbital && isOrbital(unitType) ) {
			tableExporter.outputComment("Orbital Layer");
			tableExporter.export(out, "OrbitalUnits", getOrbitalType(unitType), "OrbitalUnit%");
		}

		tableExporter.outputComment("Text Entries");
		tableExporter.exportTextKeys();
	}
	
	protected void exportArt(PrintStream out, String unitType) throws SQLException {
		List<String> unitArtTypes = getUnitArtTypes(unitType);
		
		if ( unitArtTypes != null ) {
			UnitArtExporter exporter = getUnitArtExporter(out);
			
			for ( String unitArtType : unitArtTypes ) {
				exporter.outputQuery("ArtDefine_UnitInfos", 
						"SELECT * FROM ArtDefine_UnitInfos WHERE Type=?", unitArtType);
				exporter.outputQuery("ArtDefine_UnitInfoMemberInfos", 
						"SELECT * FROM ArtDefine_UnitInfoMemberInfos WHERE UnitInfoType=?", unitArtType);
				exporter.outputQuery("ArtDefine_UnitMemberInfos", 
						"SELECT m.* FROM ArtDefine_UnitMemberInfos m, ArtDefine_UnitInfoMemberInfos u WHERE u.UnitInfoType=? AND u.UnitMemberInfoType=m.Type", unitArtType);
				exporter.outputQuery("ArtDefine_UnitMemberCombats", 
						"SELECT m.* FROM ArtDefine_UnitMemberCombats m, ArtDefine_UnitInfoMemberInfos u WHERE u.UnitInfoType=? AND u.UnitMemberInfoType=m.UnitMemberType", unitArtType);
				exporter.outputQuery("ArtDefine_UnitMemberCombatWeapons", 
						"SELECT m.* FROM ArtDefine_UnitMemberCombatWeapons m, ArtDefine_UnitInfoMemberInfos u WHERE u.UnitInfoType=? AND u.UnitMemberInfoType=m.UnitMemberType ORDER BY \"m.Index\", m.SubIndex", unitArtType);
		
				exporter.outputQuery("ArtDefine_StrategicView", 
						"SELECT * FROM ArtDefine_StrategicView WHERE StrategicViewType=?", unitArtType);
			}
			
			if ( includeOrbital && isOrbital(unitType) ) {
				exporter.outputComment("Orbital Layer");
				exportArt(out, getOrbitalArtType(unitType));
			}
		}
	}
	
	protected List<String> getUnitArtTypes(String type) throws SQLException {
		List<String> unitArtTypes = null;
		
		if ( type.startsWith("UNIT_") ) {
			PreparedStatement ps = dbData.prepareStatement("SELECT UnitArtInfo FROM Units WHERE Type=?");
			ps.setString(1, type);
		      
			ResultSet rs = ps.executeQuery();
	
			if ( rs.next() ) {
				unitArtTypes = getUnitArtTypes(rs.getString(1));
			}
		} else if ( type.startsWith("ART_DEF_UNIT_") ) {
			PreparedStatement ps = dbData.prepareStatement("SELECT Type FROM ArtDefine_UnitInfos WHERE Type=?");
			ps.setString(1, type);
		      
			ResultSet rs = ps.executeQuery();
	
			if ( rs.next() ) {
				unitArtTypes = new ArrayList<String>();
				unitArtTypes.add(rs.getString(1));
			} else {
				// Civ:BE level/affinity variations
				ps = dbData.prepareStatement("SELECT Type FROM ArtDefine_UnitInfos WHERE Type LIKE ? ORDER BY Type");
				ps.setString(1, type + "0%");
			      
				rs = ps.executeQuery();
				
				while ( rs.next() ) {
					if ( unitArtTypes == null ) {
						unitArtTypes = new ArrayList<String>();
					}

					unitArtTypes.add(rs.getString(1));
				}
			}
		}
		
		return unitArtTypes;
	}
	
	protected boolean isOrbital(String type) throws SQLException {
		return (getOrbitalType(type) != null);
	}
	
	protected String getOrbitalType(String type) throws SQLException {
		if ( type.startsWith("UNIT_") ) {
			PreparedStatement ps = dbData.prepareStatement("SELECT Orbital FROM Units WHERE Orbital IS NOT NULL AND Type=?");
			ps.setString(1, type);
		      
			ResultSet rs = ps.executeQuery();
	
			if ( rs.next() ) {
				return rs.getString(1);
			}
		}
		
		return null;
	}
	
	protected String getOrbitalArtType(String type) throws SQLException {
		if ( type.startsWith("UNIT_") ) {
			PreparedStatement ps = dbData.prepareStatement("SELECT o.InOrbitUnitArtInfo FROM Units u, OrbitalUnits o WHERE u.Orbital=o.Type AND u.Type=?");
			ps.setString(1, type);
		      
			ResultSet rs = ps.executeQuery();
	
			if ( rs.next() ) {
				return rs.getString(1);
			}
		}
		
		return null;
	}
	
	protected abstract UnitArtExporter getUnitArtExporter(PrintStream out);
}
