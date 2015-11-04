package civ.improvement.exporter.xml;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

import civ.improvement.exporter.ImprovementExporter;
import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.xml.XmlPrimaryTableExporter;

public class XmlImprovementExporter extends ImprovementExporter {
	public XmlImprovementExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
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
}
