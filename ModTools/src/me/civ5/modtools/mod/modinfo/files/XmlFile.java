package me.civ5.modtools.mod.modinfo.files;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogWarn;
import me.civ5.modutils.log.ModReporter;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class XmlFile extends ModFile {
	private ModReporter reporter;
	
	private String root = null;

	public XmlFile(File modDir, String path, String md5, String importVFS) {
		super(modDir, path, md5, importVFS);
	}
	
	public boolean isContext() {
		return "Context".equals(root);
	}
	
	public boolean isGameData() {
		return "GameData".equals(root);
	}
	
	public boolean isLeaderScene() {
		return "LeaderScene".equals(root);
	}
	
	@Override
	public boolean verify(ModReporter reporter) {
		this.reporter = reporter;
		
        try {
        	Document xml = (new SAXBuilder("org.apache.xerces.parsers.SAXParser")).build(getFullPath());
        	
        	root = xml.getRootElement().getName();
        	
        	if (isGameData()) {
        		return verifyDbXml(xml);
        	} else if (isContext()) {
        		return verifyUiXml(xml);
        	} else if (isLeaderScene()) {
        		return verifySceneXml(xml);
        	} else {
        		reporter.log(new LogError("Unknown XML content in " + getPath()));
        	}
		} catch (Exception e) {
			reporter.log(new LogError("XML error for " + getPath() + ": " + e.getMessage()));
		}
        
        return false;
	}
	
	@SuppressWarnings("unchecked")
	private boolean verifyDbXml(Document xml) {
		boolean bOK = true;
		
		for ( Element child : ((List<Element>) xml.getRootElement().getChildren()) ) {
			if (child.getName().equals("Table")) {
				bOK = verifyDbTableCreate(child) && bOK;
			} else if (child.getName().equals("Index")) {
				bOK = verifyDbIndexCreate(child) && bOK;
			} else if (child.getName().equals("DeleteMissingReferences")) {
				bOK = verifyDbDeleteMissing(child) && bOK;
			} else {
				bOK = verifyDbTableUpdate(child) && bOK;
			}
		}
		
		return bOK;
	}
	
	@SuppressWarnings("unchecked")
	private boolean verifyDbTableCreate(Element xml) {
		// <Table name="CustomModOptions">
		//   <Column name="Name" type="text" primarykey="true"/>
		//   <Column name="Value" type="integer" default="1"/>
		//   <Column name="Class" type="integer" default="5"/>
		// </Table>
		boolean bOK = verifyDbTag(xml, new String[] {"name"}, new String[] {});

		for ( Element child : ((List<Element>) xml.getChildren()) ) {
			if (child.getName().equals("Column")) {
				bOK = verifyDbTag(child, new String[] {"name", "type"}, new String[] {"default", "reference", "notnull", "primarykey", "autoincrement", "unique"}) && bOK;
			} else {
				reporter.log(new LogError("Unknown tag <" + child.getName() + "> in <Table>"));
				bOK = false;
			}
		}

		return bOK;
	}
	
	@SuppressWarnings("unchecked")
	private boolean verifyDbIndexCreate(Element xml) {
		// <Index name="Diplomacy_ResponsesIndex" table="Diplomacy_Responses">
		//   <Column name="LeaderType" sort="ASC" />
		//   <Column name="ResponseType" sort="ASC" />
		// </Index>
		boolean bOK = verifyDbTag(xml, new String[] {"name", "table"}, new String[] {});

		for ( Element child : ((List<Element>) xml.getChildren()) ) {
			if (child.getName().equals("Column")) {
				bOK = verifyDbTag(child, new String[] {"name"}, new String[] {"sort"}) && bOK;
			} else {
				reporter.log(new LogError("Unknown tag <" + child.getName() + "> in <Index>"));
				bOK = false;
			}
		}

		return bOK;
	}
	
	private boolean verifyDbDeleteMissing(Element xml) {
		// <DeleteMissingReferences table="Units" column="Type"/>

		return verifyDbTag(xml, new String[] {"table", "column"}, new String[] {});
	}
	
	@SuppressWarnings("unchecked")
	private boolean verifyDbTableUpdate(Element xml) {
		boolean bOK = true;
		String table = xml.getName();
		
		for ( Element child : ((List<Element>) xml.getChildren()) ) {
			if (child.getName().equals("Row")) {
				bOK = verifyDbRow(table, child) && bOK;
			} else if (child.getName().equals("Replace")) {
				bOK = verifyDbRow(table, child) && bOK;
			} else if (child.getName().equals("InsertOrAbort")) {
				bOK = verifyDbRow(table, child) && bOK;
			} else if (child.getName().equals("Update")) {
				bOK = verifyDbUpdate(table, child) && bOK;
			} else if (child.getName().equals("Delete")) {
				bOK = verifyDbDelete(table, child) && bOK;
			} else {
				reporter.log(new LogError("Unknown tag <" + child.getName() + "> in <" + table + ">"));
				bOK = false;
			}
		}
		
		return bOK;
	}
	
	private boolean verifyDbRow(String table, Element xml) {
		// <Row>
		//   <Type>BUILDINGCLASS_GOLDSMITH</Type>
		//   <DefaultBuilding>BUILDING_GOLDSMITH</DefaultBuilding>
		//   <Description>TXT_KEY_BUILDING_GOLDSMITH</Description>
		// </Row>
		//
		// <Replace Tag="TXT_KEY_PREVIOUS_ITEM_TT">
		//   <Text>Centre map on previous item</Text>
		// </Replace>
		//
		// <InsertOrAbort>
		//   <Type>COLOR_PLAYER_SPAIN_BACKGROUND</Type>
		//   <Red>0.329</Red>
		//   <Green>0.102</Green>
		//   <Blue>0.102</Blue>
		//   <Alpha>1</Alpha>
		// </InsertOrAbort>

		// Without checking column names in the db, any attribute names or child tags are (potentially) valid
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean verifyDbUpdate(String table, Element xml) {
		// <Update>
		//   <Where Type="BUILDING_LIGHTHOUSE"/>
		//   <Set Water="0"/>
		// </Update>
		//
		// <Update>
		//   <Where Name="UNIT_MAINTENANCE_GAME_MULTIPLIER"/>
		//   <Set>
		//	     <Value>0</Value>
		//   </Set>
		// </Update>
		boolean bOK = true;

		boolean seenWhere = false;
		boolean seenSet = false;
		
		for ( Element child : ((List<Element>) xml.getChildren()) ) {
			if (child.getName().equals("Where")) {
				if (seenWhere) {
					reporter.log(new LogError("Duplicate <Where> tag found in <Update> for <" + table + ">"));
					bOK = false;
				}
				
				for ( Element grandchild : ((List<Element>) child.getChildren()) ) {
					reporter.log(new LogError("Unexpected <" + grandchild.getName() + "> tag found in <Where> for <" + table + ">"));
					bOK = false;
				}
				
				seenWhere = true;
			} else if (child.getName().equals("Set")) {
				if (seenSet) {
					reporter.log(new LogError("Duplicate <Set> tag found in <Update> for <" + table + ">"));
					bOK = false;
				}
				
				seenSet = true;
			} else {
				reporter.log(new LogError("Unknown tag <" + child.getName() + "> in <Update> for <" + table + ">"));
				bOK = false;
			}
		}
		
		if (!seenWhere) {
			reporter.log(new LogWarn("No <Where> tag in <Update> for <" + table + ">"));
		}
		
		if (!seenSet) {
			reporter.log(new LogError("Missing <Set> tag in <Update> for <" + table + ">"));
			bOK = false;
		}
		
		return bOK;
	}

	@SuppressWarnings("unchecked")
	private boolean verifyDbDelete(String table, Element xml) {
		// <Delete GoodyType="GOODY_MAP"/>
		boolean bOK = true;
		
		if (xml.getAttributes().size() == 0) {
			reporter.log(new LogWarn("No conditions on <Delete> tag for <" + table + ">"));
		}

		for ( Element child : ((List<Element>) xml.getChildren()) ) {
			reporter.log(new LogError("Unexpected <" + child.getName() + "> tag found in <Delete> for <" + table + ">"));
			bOK = false;
		}

		return bOK;
	}
	
	@SuppressWarnings("unchecked")
	private boolean verifyDbTag(Element xml, String[] requiredAttrs, String[] optionalAttrs) {
		boolean bOK = true;

		Set<String> attrs = new HashSet<String>();
		
		for (String attr : requiredAttrs) {
			attrs.add(attr);
			
			if (xml.getAttributeValue(attr) == null) {
				reporter.log(new LogError("Missing " + attr + " on <" + xml.getName() + "> tag"));
				bOK = false;
			}
		}
		
		for (String attr : optionalAttrs) {
			attrs.add(attr);
		}
		
		for (Attribute attr : ((List<Attribute>) xml.getAttributes())) {
			if (!attrs.contains(attr.getName())) {
				reporter.log(new LogWarn(attr.getName() + " on <" + xml.getName() + "> tag is redundant"));
			}
		}
		
		return bOK;
	}

	
	private boolean verifyUiXml(Document xml) {
    	// This is too much effort for little gain
		return true;
	}

	private boolean verifySceneXml(Document xml) {
    	// This is too much effort for little gain
		return true;
	}
}
