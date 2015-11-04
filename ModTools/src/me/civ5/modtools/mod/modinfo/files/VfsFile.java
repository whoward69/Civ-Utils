package me.civ5.modtools.mod.modinfo.files;

import java.io.File;

import me.civ5.modutils.log.ModReporter;

public class VfsFile extends ModFile {

	public VfsFile(File modDir, String path, String md5, String importVFS) {
		super(modDir, path, md5, importVFS);
	}
	
	@Override
	public boolean verify(ModReporter reporter) {
		// We could check the extension to see if we can be in the VFS, but that is already handled by the ModInfo verifyUsage() method
		return true;
	}
}
