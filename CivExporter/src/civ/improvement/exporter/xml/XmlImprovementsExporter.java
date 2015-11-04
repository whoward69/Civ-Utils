package civ.improvement.exporter.xml;

import java.io.File;
import java.sql.Connection;

import civ.improvement.exporter.ImprovementExporter;
import civ.improvement.exporter.ImprovementsExporter;

public class XmlImprovementsExporter extends ImprovementsExporter {
	public XmlImprovementsExporter(String jdbcDataConnection, String jdbcTextConnection) {
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
	protected ImprovementExporter getImprovementExporter(Connection dbData, Connection dbText) {
		return new XmlImprovementExporter(dbData, dbText);
	}
}
