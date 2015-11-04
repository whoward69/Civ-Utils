package civ.unit.exporter.xml;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.xml.XmlPrimaryTableExporter;
import civ.unit.exporter.UnitArtExporter;
import civ.unit.exporter.UnitExporter;

public class XmlUnitExporter extends UnitExporter {
	public XmlUnitExporter(Connection dbData, Connection dbText, boolean includeOrbital) {
		super(dbData, dbText, includeOrbital);
	}

	@Override
	protected void start(PrintStream out) throws IOException {
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	    out.println("<GameData>");
	}

	@Override
	protected void end(PrintStream out) throws IOException {
		out.println("</GameData>");
	}

	@Override
	protected PrimaryTableExporter getPrimaryTableExporter() {
		return new XmlPrimaryTableExporter(dbData, dbText);
	}
	
	@Override
	protected UnitArtExporter getUnitArtExporter(PrintStream out) {
		return new XmlUnitArtExporter(out, dbData);
	}
}
