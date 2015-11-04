package me.civ5.modbuilder.ui.control.select;

import javax.swing.JComboBox;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.control.SimpleControl;
import me.civ5.modbuilder.ui.model.SelectEntry;
import me.civ5.xml.XmlHelper;

import org.jdom.Element;

public class ReferenceSelectItem extends EmptySelectItem {
	private String refTable;
	private String refCol;
	
	public ReferenceSelectItem(String id, String tag, String refTable, String refCol, ModDb db, String lookupTable, String keyColumn, String valueColumn) {
		super(id, tag, db, lookupTable, keyColumn, valueColumn);
		
		this.refTable = refTable;
		this.refCol = refCol;
	}

	@Override
	public void buildXmlImpl(Element gamedata, Element row, String type) {
		// <Table name="{refTable}">
		//   <Column name="UnitType" type="text" reference="Units(Type)"/>
		//   <Column name="{refCol}" type="text" reference="Builds(Type)"/>
		//   <Column name="{linkedControl}" type="integer"/>
		// </Table>

		JComboBox<SelectEntry> list = getComboControl();
		
		if (!isEmpty()) {
			Element rTable = XmlHelper.getChildElement(gamedata, refTable);
			Element rRow = XmlHelper.newElement(rTable, "Row");
			XmlHelper.newTextElement(rRow, "UnitType", type);
			XmlHelper.newTextElement(rRow, refCol, ((SelectEntry) list.getSelectedItem()).getKey());
			
			if (linkedControl != null && linkedControl instanceof SimpleControl) {
				if (linkedTag != null) {
					XmlHelper.newTextElement(rRow, linkedTag, linkedControl.getText());
				} else {
					linkedControl.buildXml(gamedata, rRow, type);
				}
			}
		}
	}
}
