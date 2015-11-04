package civ.civilization.exporter.xml;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;

import civ.civilization.exporter.CivilizationExporter;
import civ.table.exporter.PrimaryTableExporter;
import civ.table.exporter.xml.XmlPrimaryTableExporter;

public class XmlCivilizationExporter extends CivilizationExporter {
	public XmlCivilizationExporter(Connection dbData, Connection dbText) {
		super(dbData, dbText);
	}
	
	@Override
	protected File getFile(File dir, String name) throws IOException {
		return new File(dir, name + ".xml");
	}

	@Override
	protected PrintStream start(File file) throws IOException {
		PrintStream out = super.start(file);
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	    out.println("<GameData>");
	    
	    return out;
	}

	@Override
	protected void end(PrintStream out) throws IOException {
		out.println("</GameData>");
		
		super.end(out);
	}

	@Override
	protected PrimaryTableExporter getPrimaryTableExporter() {
		return new XmlPrimaryTableExporter(dbData, dbText);
	}
}
