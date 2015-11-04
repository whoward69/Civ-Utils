package me.civ5.modtools.mod.modinfo.files;

import java.io.File;

import me.civ5.modutils.log.ModReporter;

public class SqlFile extends ModFile {

	public SqlFile(File modDir, String path, String md5, String importVFS) {
		super(modDir, path, md5, importVFS);
	}
	
	@Override
	public boolean verify(ModReporter reporter) {
		// I suppose we could find a Java SQL parser and validate the syntax ... nah!
		return true;
	}
}
