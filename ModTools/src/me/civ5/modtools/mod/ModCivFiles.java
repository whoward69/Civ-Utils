package me.civ5.modtools.mod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.civ5.modtools.mod.modfiles.CivFile;
import me.civ5.modutils.log.LogDebug;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

public class ModCivFiles {
	protected ModReporter reporter;
	private File modDir;
	
	private Map<String, CivFile> fileList = new LinkedHashMap<String, CivFile>();
	
	public ModCivFiles(ModReporter reporter, File modDir) {
		this.reporter = reporter;
		this.modDir = modDir;
	}
	
	public ModCivFiles(ModReporter reporter, File modDir, ModInfo modInfo) {
		this(reporter, modDir);
		
		for (me.civ5.modtools.mod.modinfo.files.ModFile file : modInfo.getFileList() ) {
			add(file.getPath());
		}
	}
	
	public void setReporter(ModReporter reporter) {
		this.reporter = reporter;
	}
	
	public void add(String modFile) {
		fileList.put(modFile, new CivFile(modDir, modFile));
	}

	public Map<String, CivFile> getFileList() {
		return fileList;
	}

	// Verify that every referenced file exists - we are not concerned with file type or usage here
	public boolean verify() {
		boolean bFatal = false;
		
		for ( CivFile modFile : fileList.values() ) {
			File path = modFile.getLocation();
			
			if ( !path.exists() ) {
				// A missing file is fatal
				reporter.log(new LogError(modFile.getLocation().getAbsolutePath() + " cannot be found on the file system"));
				bFatal = true;
			} else if (!(path.isFile() && path.canRead())) {
				// As is a file we can't read
				reporter.log(new LogError(modFile.getLocation().getAbsolutePath() + " is not a readable file"));
				bFatal = true;
			}
		}
		
		return !bFatal;
	}
	
	public void merge(ModCivFiles mergeModFiles) {
		for ( Entry<String, CivFile> entry : mergeModFiles.fileList.entrySet() ) {
			if ( !fileList.containsKey(entry.getKey()) ) {
				fileList.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void copyTo(File dir) {
		for ( CivFile modFile : fileList.values() ) {
			File fromFile = modFile.getLocation();
			File toFile = new File(dir, modFile.getPath());
			
			if ( toFile.exists() ) {
				reporter.log(new LogError("File copy error: " + toFile.getAbsolutePath() + " already exists!"));
			} else {
				File toPath = toFile.getParentFile();
				if ( !toPath.exists() ) {
					toPath.mkdirs();
				}
				
				reporter.log(new LogDebug("Copy " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath()));
				try {
					if ( toFile.createNewFile() ) {
						InputStream is = null;
						OutputStream os = null;
						
						try {
							is = new FileInputStream(fromFile);
							try {
								os = new FileOutputStream(toFile);

								byte[] b = new byte[20000];
								int len;
								while ( (len = is.read(b, 0, 20000)) != -1 ) {
									os.write(b, 0, len);
								}
							} catch (FileNotFoundException e) {
								reporter.log(new LogError("File copy error: " + toFile.getAbsolutePath() + " unable to open"));
							} catch (IOException e) {
								reporter.log(new LogError("File copy error: " + toFile.getAbsolutePath() + " unable to copy"));
							} finally {
								if ( os != null ) {
									try {
										os.close();
									} catch (IOException e) {
										reporter.log(new LogError("File copy error: " + toFile.getAbsolutePath() + " unable to close"));
									}
								}
							}
						} catch (FileNotFoundException e) {
							reporter.log(new LogError("File copy error: " + fromFile.getAbsolutePath() + " unable to open"));
						} finally {
							if ( is != null ) {
								try {
									is.close();
								} catch (IOException e) {
									reporter.log(new LogError("File copy error: " + fromFile.getAbsolutePath() + " unable to close"));
								}
							}
						}
					} else {
						reporter.log(new LogError("File copy error: " + toFile.getAbsolutePath() + " not created!"));
					}
				} catch (IOException e) {
					reporter.log(new LogError("File copy error: " + fromFile.getAbsolutePath() + " unable to create"));
				}
			}
		}
	}
}
