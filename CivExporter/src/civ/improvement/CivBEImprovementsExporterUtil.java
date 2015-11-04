package civ.improvement;

import java.io.File;

import civ.CivBEExporterUtil;
import civ.improvement.exporter.xml.XmlImprovementsExporter;


public class CivBEImprovementsExporterUtil extends CivBEExporterUtil {
	public static void main(String[] args) throws Exception {
		File improvementsDataDir = new File(tempDir, "improvements/data");

		new XmlImprovementsExporter(jdbcDataConnection, jdbcTextConnection).export(improvementsDataDir, true, false);
		// new SqlImprovementsExporter(jdbcDataConnection, jdbcTextConnection).export(improvementsDataDir, true, false);
	}
}
