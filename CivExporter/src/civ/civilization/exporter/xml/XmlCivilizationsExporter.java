package civ.civilization.exporter.xml;

import java.io.File;
import java.sql.Connection;

import civ.civilization.exporter.CivilizationExporter;
import civ.civilization.exporter.CivilizationsExporter;

public class XmlCivilizationsExporter extends CivilizationsExporter {
	public XmlCivilizationsExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeUniques) {
		super(jdbcDataConnection, jdbcTextConnection, includeUniques);
	}

	@Override
	protected File getOutputDir(File baseDir) {
		return new File(baseDir, "xml");
	}

	@Override
	protected File getOutputFile(File outputDir, String type) {
		return new File(outputDir, getName(type) + ".xml");
	}

	@Override
	protected CivilizationExporter getCivilizationExporter(Connection dbData, Connection dbText) {
		return new XmlCivilizationExporter(dbData, dbText);
	}
}
