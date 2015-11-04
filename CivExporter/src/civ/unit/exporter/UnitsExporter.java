package civ.unit.exporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import civ.Exporter;

public abstract class UnitsExporter extends Exporter {
	protected boolean includeOrbital = false;
	
	public UnitsExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeOrbital) {
		super(jdbcDataConnection, jdbcTextConnection);
		
		this.includeOrbital = includeOrbital;
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

			UnitExporter exporter = getUnitExporter(dbData, dbtext);

			PreparedStatement ps = dbData.prepareStatement("SELECT Type FROM Units");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String unitType = rs.getString(1);
				PrintStream out = new PrintStream(getOutputFile(outputDir, unitType), "UTF-8");
				exporter.export(out, unitType, includeData, includeArt);
				out.close();
			}
		} finally {
			if (dbData != null) {
				dbData.close();
			}
		}
	}

	protected abstract UnitExporter getUnitExporter(Connection dbData, Connection dbText);
}
