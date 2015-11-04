package civ.building.exporter.xml;

import java.io.File;
import java.sql.Connection;

import civ.building.exporter.BuildingExporter;
import civ.building.exporter.BuildingsExporter;

public class XmlBuildingsExporter extends BuildingsExporter {
	public XmlBuildingsExporter(String jdbcDataConnection, String jdbcTextConnection) {
		super(jdbcDataConnection, jdbcTextConnection);
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
	protected BuildingExporter getBuildingExporter(Connection dbData, Connection dbText) {
		return new XmlBuildingExporter(dbData, dbText);
	}
}
