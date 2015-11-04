package civ.improvement.exporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import civ.Exporter;

public abstract class ImprovementsExporter extends Exporter {
	public ImprovementsExporter(String jdbcDataConnection, String jdbcTextConnection) {
		super(jdbcDataConnection, jdbcTextConnection);
	}

	@Override
	public void export(File baseDir, boolean includeData, boolean includeArt) throws IOException, SQLException {
		Connection dbData = null;
		Connection dbtext = null;

		try {
			File outputDir = getOutputDir(baseDir);
			outputDir.mkdirs();

			dbData = open(jdbcDataConnection);
			dbtext = open(jdbcTextConnection);

			ImprovementExporter exporter = getImprovementExporter(dbData, dbtext);

			PreparedStatement ps = dbData.prepareStatement("SELECT Type FROM Improvements");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String improvementType = rs.getString(1);
				PrintStream out = new PrintStream(getOutputFile(outputDir, improvementType), "UTF-8");
				exporter.export(out, improvementType, includeData, includeArt);
				out.close();
			}
		} finally {
			if (dbData != null) {
				dbData.close();
			}
		}
	}

	protected abstract ImprovementExporter getImprovementExporter(Connection dbData, Connection dbText);
}
