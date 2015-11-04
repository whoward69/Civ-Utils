package me.civ5.modbuilder.ui.control.select;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class ReferenceMultiSelectItem extends MultiSelectItem {
	private String refTable;
	private String refCol;
	
	public ReferenceMultiSelectItem(String id, String tag, String refTable, String refCol, ModDb db, String lookupTable, String keyColumn, String valueColumn, String whereClause) {
		super(id, tag, db, lookupTable, keyColumn, valueColumn, whereClause);
		
		this.refTable = refTable;
		this.refCol = refCol;
	}

	@Override
	public String getText() {
		throw new RuntimeException("Can't call getText() for a multi-select item!");
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		// <Table name="{refTable}">
		//   <Column name="UnitType" type="text" reference="Units(Type)"/>
		//   <Column name="{refCol}" type="text" reference="Builds(Type)"/>
		// </Table>
		if (!isEmpty()) {
			Element rTable = XmlHelper.getChildElement(gamedata, refTable);
			
			for (SelectEntry entry : list.getSelectedValuesList()) {
				Element rRow = XmlHelper.newElement(rTable, "Row");
				XmlHelper.newTextElement(rRow, "UnitType", type);
				XmlHelper.newTextElement(rRow, refCol, entry.getKey());
			}
		}
	}
}
