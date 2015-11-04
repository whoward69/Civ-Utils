package civ.table.exporter.xml;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import civ.table.exporter.PrimaryTableExporter;

public class XmlPrimaryTableExporter extends PrimaryTableExporter {
	public XmlPrimaryTableExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
	}

	@Override
	public void outputComment(String comment) {
		out.println("\n  <!-- " + comment + " -->");
	}

	@Override
	public void exportTable(String table, String pkCol, String pkValue) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement("SELECT * FROM " + table + " WHERE " + pkCol + "=?");
		ps.setString(1, pkValue); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();

		if ( rs.next() ) {
			out.println("  <" + table + ">");

			do {
				out.println("    <Row>");
				for ( int i = 1; i <= rsmd.getColumnCount(); ++i ) {
					if ( !rsmd.isAutoIncrement(i) ) {
						String val = rs.getString(i);
						String col = rsmd.getColumnName(i);
						
						if ( col.equalsIgnoreCase("PackageID") ) {
							continue;
						}
						
						out.println("      <" + col + ">" + ((val == null) ? "" : val.trim()) + "</" + col + ">");
							
						if ( col.equalsIgnoreCase("CivilopediaTag") ) {
							// Special case for Civilizations and Leaders tables, take all keys that match val_%
							PreparedStatement ps2 = dbText.prepareStatement("SELECT Tag FROM Language_en_US WHERE Tag LIKE '" + val + "_%'");
							ResultSet rs2 = ps2.executeQuery();
					
							while ( rs2.next() ) {
								String txtKey = rs2.getString(1);
								if ( txtKey.contains("_TEXT") ) {
									textTags.add(new CivilopediaTextKey(txtKey));
								} else {
									textTags.add(new TextKey(txtKey));
								}
							}
						} else if ( val != null && val.trim().startsWith("TXT_KEY_") ) {
							if ( col.equalsIgnoreCase("Civilopedia") ) {
								textTags.add(new CivilopediaTextKey(val.trim()));
							} else {
								textTags.add(new TextKey(val.trim()));
							}
						}
					}
				}
				out.println("    </Row>");
			} while ( rs.next() );

			out.println("  </" + table + ">");
		} else {
			out.println("  <!-- " + table + " -->");
		}
	}
	
	@Override
	protected void exportCityNamesTable(String pkValue) throws SQLException {
		PreparedStatement ps = dbData.prepareStatement("SELECT * FROM Civilization_CityNames WHERE CivilizationType=?");
		ps.setString(1, pkValue); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();

		if ( rs.next() ) {
			out.println("  <Civilization_CityNames>");
			String civ = pkValue.substring(pkValue.indexOf('_')+1);
			int index = 0;

			do {
				out.println("    <Row>");
				for ( int i = 1; i <= rsmd.getColumnCount(); ++i ) {
					String val = rs.getString(i);
					String col = rsmd.getColumnName(i);
					
					if ( col.equalsIgnoreCase("CityName") ) {
						String txtKey = "TXT_KEY_CITY_NAME_" + civ + "_" + (index++);
						out.println("      <" + col + ">" + txtKey + "</" + col + ">");
						textTags.add(new TextKey(val.trim(), txtKey));
					} else {
						out.println("      <" + col + ">" + val.trim() + "</" + col + ">");
					}
				}
				out.println("    </Row>");
			} while ( rs.next() );

			out.println("  </Civilization_CityNames>");
		} else {
			out.println("  <!-- Civilization_CityNames -->");
		}
	}
	
	@Override
	public void exportDiplomacyResponsesTable(PrintStream out, String pkValue) throws SQLException {
		this.out = out;
		
		PreparedStatement ps = dbData.prepareStatement("SELECT * FROM Diplomacy_Responses WHERE LeaderType=?");
		ps.setString(1, pkValue); // Bind this as it deals with quoting issues
	      
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();

		if ( rs.next() ) {
			out.println("  <Diplomacy_Responses>");
			String leader = pkValue.substring(pkValue.indexOf('_')+1);

			do {
				out.println("    <Row>");
				for ( int i = 1; i <= rsmd.getColumnCount(); ++i ) {
					String val = rs.getString(i);
					String col = rsmd.getColumnName(i);
					
					if ( val == null ) {
						out.println("      <" + col + "></" + col + ">");
					} else {
						out.println("      <" + col + ">" + val.trim() + "</" + col + ">");
					}

					if ( col.equalsIgnoreCase("Response") ) {
						PreparedStatement ps3 = dbText.prepareStatement("SELECT Text FROM Language_en_US WHERE Tag=?");
						PreparedStatement ps2 = dbText.prepareStatement("SELECT Tag FROM Language_en_US WHERE Tag LIKE '" + val + "'");
						ResultSet rs2 = ps2.executeQuery();
				
						while ( rs2.next() ) {
							String txtKey = rs2.getString(1);
							String altTxtKey = txtKey.replace("_LEADER_", "_").replace(leader, "GENERIC");
							
							ps3.setString(1, altTxtKey);
							ResultSet rs3 = ps3.executeQuery();
							if ( rs3.next() ) {
								textTags.add(new DiplomacyTextKey(txtKey, rs3.getString(1)));
							} else {
								ps3.setString(1, altTxtKey.replace("_GENERIC_", "_"));
								rs3 = ps3.executeQuery();
								if ( rs3.next() ) {
									textTags.add(new DiplomacyTextKey(txtKey, rs3.getString(1)));
								} else {
									textTags.add(new TextKey(txtKey));
								}
							}
						}
					}
				}
				out.println("    </Row>");
			} while ( rs.next() );

			out.println("  </Diplomacy_Responses>");
		} else {
			out.println("  <!-- Diplomacy_Responses -->");
		}
	}

	@Override
	public void exportTextKeys() throws SQLException {
		if ( textTags.size() > 0 ) {
			PreparedStatement psSingle = dbText.prepareStatement("SELECT Text, Gender, Plurality FROM Language_en_US WHERE Tag=?");
			PreparedStatement psMultiple = dbText.prepareStatement("SELECT Tag FROM Language_en_US WHERE Tag LIKE ?");
			
			out.println("  <Language_en_US>");
	
			for ( TextKey tag : textTags ) {
				if ( tag.isMultiple() ) {
					psMultiple.setString(1, tag.getKey()); // Bind this as it deals with quoting issues
					ResultSet rs = psMultiple.executeQuery();

					while ( rs.next() ) {
						TextKey tempTag = new TextKey(rs.getString(1));
						exportTextKey(psSingle, tempTag);
					}
				} else {
					exportTextKey(psSingle, tag);
				}
			}
			
			out.println("  </Language_en_US>");
			
			// Empty the list now we've processed the tags
			textTags.clear();
		}
	}
	
	private void exportTextKey(PreparedStatement ps, TextKey tag) throws SQLException {
		out.println("    <Row Tag=\"" + tag.getAltKey() + "\">");
		
		if ( tag.isPlaceHolder() ) {
			out.println("      <Text>" + tag.getPlaceHolderText() + "</Text>");
		} else {
			ps.setString(1, tag.getKey()); // Bind this as it deals with quoting issues
			ResultSet rs = ps.executeQuery();

			if ( rs.next() ) {
				String text = rs.getString(1);
				String gender = rs.getString(2);
				String plurality = rs.getString(3);
				
				text = text.replaceAll("&", "&amp;");
				text = text.replaceAll("<", "&lt;");
				text = text.replaceAll(">", "&gt;");
				text = text.replaceAll("\"", "&quot;");
				out.println("      <Text>" + ((text != null) ? text.trim() : "") + "</Text>");
	
				if ( gender != null && gender.trim().length() > 0 ) {
					out.println("      <Gender>" + gender.trim() + "</Gender>");
				}
	
				if ( plurality != null && plurality.trim().length() > 0 ) {
					out.println("      <Plurality>" + plurality.trim() + "</Plurality>");
				}
			} else {
				out.println("      <Text></Text>");
			}
		}
		
		out.println("    </Row>");
	}
}
