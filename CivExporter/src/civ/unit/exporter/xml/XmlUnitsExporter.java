package civ.unit.exporter.xml;

import java.io.File;
import java.sql.Connection;

import civ.unit.exporter.UnitExporter;
import civ.unit.exporter.UnitsExporter;

public class XmlUnitsExporter extends UnitsExporter {
	public XmlUnitsExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeOrbital) {
		super(jdbcDataConnection, jdbcTextConnection, includeOrbital);
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
	protected UnitExporter getUnitExporter(Connection dbData, Connection dbText) {
		return new XmlUnitExporter(dbData, dbText, includeOrbital);
	}
}
