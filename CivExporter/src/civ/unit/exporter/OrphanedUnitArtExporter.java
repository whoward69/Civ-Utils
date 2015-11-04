package civ.unit.exporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import civ.Exporter;

public abstract class OrphanedUnitArtExporter extends Exporter {
	protected boolean includeOrbital = false;
	
	public OrphanedUnitArtExporter(String jdbcDataConnection, String jdbcTextConnection, boolean includeOrbital) {
		super(jdbcDataConnection, jdbcTextConnection);

		this.includeOrbital = includeOrbital;
	}

	@Override
	public void export(File baseDir, boolean includeData, boolean includeArt) throws IOException, SQLException {
		Connection dbData = null;
		Connection dbText = null;

		try {
			File outputDir = getOutputDir(baseDir);
			outputDir.mkdirs();

			dbData = open(jdbcDataConnection);
			dbText = open(jdbcTextConnection);

			UnitExporter exporter = getUnitExporter(dbData, dbText);

			PreparedStatement ps = dbData.prepareStatement("SELECT Type FROM ArtDefine_UnitInfos WHERE Type NOT IN (SELECT UnitArtInfo FROM Units)");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String unitArtType = rs.getString(1);
				PrintStream out = new PrintStream(getOutputFile(outputDir, unitArtType), "UTF-8");
				exporter.export(out, unitArtType, includeData, includeArt);
				out.close();
			}
		} finally {
			if (dbData != null) {
				dbData.close();
			}
		}
	}
	
	@Override
	protected String getName(String type) {
		if ( type.startsWith("ART_DEF_UNIT_") ) {
			type = type.substring("ART_DEF_".length());
		}

		return super.getName(type);
	}

	protected abstract UnitExporter getUnitExporter(Connection dbData, Connection dbText);
}
