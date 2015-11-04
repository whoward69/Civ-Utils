package civ.improvement;

import java.io.File;

import civ.Civ5ExporterUtil;
import civ.improvement.exporter.sql.SqlImprovementsExporter;
import civ.improvement.exporter.xml.XmlImprovementsExporter;


public class Civ5ImprovementsExporterUtil extends Civ5ExporterUtil {
	public static void main(String[] args) throws Exception {
		File improvementsDataDir = new File(tempDir, "improvements/data");

		new XmlImprovementsExporter(jdbcDataConnection, jdbcTextConnection).export(improvementsDataDir, true, false);
		new SqlImprovementsExporter(jdbcDataConnection, jdbcTextConnection).export(improvementsDataDir, true, false);
	}
}
