package me.civ5.modtools.mod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import me.civ5.modtools.mod.modinfo.Actions;
import me.civ5.modtools.mod.modinfo.Blocks;
import me.civ5.modtools.mod.modinfo.Dependencies;
import me.civ5.modtools.mod.modinfo.EntryPoints;
import me.civ5.modtools.mod.modinfo.Files;
import me.civ5.modtools.mod.modinfo.Properties;
import me.civ5.modtools.mod.modinfo.References;
import me.civ5.modtools.mod.modinfo.Section;
import me.civ5.modtools.mod.modinfo.actions.SetDllPath;
import me.civ5.modtools.mod.modinfo.actions.UpdateDatabase;
import me.civ5.modtools.mod.modinfo.entrypoints.EntryPoint;
import me.civ5.modtools.mod.modinfo.files.ModFile;
import me.civ5.modtools.mod.modinfo.files.XmlFile;
import me.civ5.modtools.mod.modinfo.properties.FalseBiasedProperty;
import me.civ5.modtools.mod.modinfo.properties.Property;
import me.civ5.modtools.mod.modinfo.properties.StringProperty;
import me.civ5.modtools.mod.modinfo.properties.TrueBiasedProperty;
import me.civ5.modtools.mod.modinfo.related.Related;
import me.civ5.modutils.log.LogDebug;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogInfo;
import me.civ5.modutils.log.LogWarn;
import me.civ5.modutils.log.ModReporter;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ModInfo {
	private ModReporter reporter;

	private File modinfoPath;

	private String guid;
	private String version;

	private Map<String, Section> sections = new LinkedHashMap<String, Section>();

	public ModInfo(ModReporter reporter, File modDir, String modName, String modVersion, String modDescription) {
		init(reporter, modDir);

		generateNewGUID();
		this.version = modVersion;

		Properties properties = getProperties();

		properties.add(new StringProperty("Name", modName));
		properties.add(new StringProperty("Teaser", modName));
		properties.add(new StringProperty("Description", modDescription));

		properties.add(new StringProperty("Stability", "Beta"));
		properties.add(new StringProperty("Authors", "ModMerge Utility"));

		properties.add(new TrueBiasedProperty("HideSetupGame"));
		properties.add(new TrueBiasedProperty("AffectsSavedGames"));
		properties.add(new StringProperty("MinCompatibleSaveVersion", "0"));

		properties.add(new FalseBiasedProperty("SupportsSinglePlayer"));
		properties.add(new FalseBiasedProperty("SupportsMultiplayer"));
		properties.add(new FalseBiasedProperty("SupportsHotSeat"));
		properties.add(new FalseBiasedProperty("SupportsMac"));

		properties.add(new TrueBiasedProperty("ReloadAudioSystem"));
		properties.add(new TrueBiasedProperty("ReloadLandmarkSystem"));
		properties.add(new TrueBiasedProperty("ReloadStrategicViewSystem"));
		properties.add(new TrueBiasedProperty("ReloadUnitSystem"));
	}

	public ModInfo(ModReporter reporter, File modDir) throws SAXException, IOException {
		init(reporter, modDir);
		parse();
	}

	private void init(ModReporter reporter, File modDir) {
		this.reporter = reporter;

		sections.put(Properties.NAME, new Properties());

		sections.put(Dependencies.NAME, new Dependencies(reporter));
		sections.put(References.NAME, new References(reporter));
		sections.put(Blocks.NAME, new Blocks(reporter));

		sections.put(Files.NAME, new Files(reporter, modDir));
		sections.put(Actions.NAME, new Actions(reporter));
		sections.put(EntryPoints.NAME, new EntryPoints(reporter));

		this.modinfoPath = new File(modDir, modDir.getName() + ".modinfo");
	}
	
	public void setReporter(ModReporter reporter) {
		this.reporter = reporter;
	}

	public void generateNewGUID() {
		this.guid = UUID.randomUUID().toString();
	}

	public void setNameAndVersion(String name, String version) {
		getProperties().setName(name);
		this.version = version;
	}
	
	public String getName() {
		return getProperties().getValue("Name", null); 
	}

	public String getVersion() {
		return version; 
	}

	public String getDescription() {
		return getProperties().getValue("Description", ""); 
	}

	private Properties getProperties() {
		return ((Properties) sections.get(Properties.NAME));
	}

	private Map<String, ModFile> getFiles() {
		return ((Files) sections.get(Files.NAME)).getFiles();
	}
	
	private SetDllPath getDll() {
		return ((Actions) sections.get(Actions.NAME)).getOnGetDLLPath().getDll();
	}

	private Map<String, UpdateDatabase> getUpdates() {
		return ((Actions) sections.get(Actions.NAME)).getOnModActivated().getUpdates();
	}

	private Map<String, EntryPoint> getEntryPoints() {
		return ((EntryPoints) sections.get(EntryPoints.NAME)).getEntryPoints();
	}

	public Collection<ModFile> getFileList() {
		return getFiles().values();
	}

	public List<String> getVsDirList() {
		Set<String> dirList = new TreeSet<String>();

		for (ModFile modFile : getFileList()) {
			File path = new File(modFile.getPath());

			while (path.getParentFile() != null) {
				path = path.getParentFile();
				dirList.add(path.getPath());
			}
		}

		return new ArrayList<String>(dirList);
	}

	public List<ModFile> getVsFileList() {
		Set<ModFile> fileList = new TreeSet<ModFile>();

		for (ModFile modFile : getFileList()) {
			fileList.add(modFile);
		}

		return new ArrayList<ModFile>(fileList);
	}

	private void parse() throws SAXException, IOException {
		XMLReader parser = XMLReaderFactory.createXMLReader();

		ModInfoHandler handler = new ModInfoHandler();
		parser.setContentHandler(handler);

		parser.parse(new InputSource(new FileInputStream(modinfoPath)));
	}

	public boolean verify(boolean fixImports, boolean removeFiles) {
		boolean bOK = true;

		// Generally verify all sections
		for (Section section : sections.values()) {
			bOK = section.verify(reporter) && bOK;
		}

		// Verify the files first, as this adds the Context, GameData, etc info
		bOK = verifyFiles() && bOK;

		// Then perform some knowledgeable verifications
		bOK = verifyUsage(fixImports) && bOK;
		bOK = verifyDLL(fixImports) && bOK;
		bOK = verifyUpdates(fixImports) && bOK;
		bOK = verifyAssociations() && bOK;
		bOK = verifyImports(removeFiles) && bOK;
		bOK = verifyCustomScenario() && bOK;

		return bOK;
	}

	// Verify the usage of each file by extension
	private boolean verifyUsage(boolean fixImports) {
		boolean bFatal = false;

		for (ModFile file : getFileList()) {
			String extn = file.getExtn();

			if (extn.equals(".civ5map")) {
				if (file.isImportVFS()) {
					if (!getEntryPoints().containsKey(file.getPath())) {
						reporter.log(new LogError("Map file " + file.getPath() + " needs a Map content entry"));
					} else {
						EntryPoint entry = getEntryPoints().get(file.getPath());
						if (!entry.getType().equals("Map")) {
							reporter.log(new LogError("Map file " + file.getPath() + " needs a Map content entry (found '" + entry.getType() + "')"));
						}
					}
				} else {
					// Not an error, provided the map is loaded as part of a
					// custom scenario
					if (!getProperties().getValue("HideSetupGame", "0").equals("1")) {
						reporter.log(new LogError("Map file " + file.getPath() + " is not part of a custom scenario"));
					}
				}
			} else if (extn.equals(".civ5mod")) {
				// Mod archive, VFS=false (shouldn't really be here)
				if (file.isImportVFS()) {
					String msg = file.getPath() + " shouldn't be imported into the VFS";
					if (fixImports) {
						file.setImportVFS(0);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}

					reporter.log(new LogWarn(file.getPath() + " is not a valid file type for a mod!"));
				}
			} else if (extn.equals(".dds")) {
				// Image assets, VFS = true
				if (!file.isImportVFS()) {
					String msg = "Image asset " + file.getPath() + " should be imported into the VFS";
					if (fixImports) {
						file.setImportVFS(1);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}
				}

				byte[] b = new byte[88];
				InputStream is = null;
				try {
					is = new FileInputStream(file.getFullPath());
					if (is.read(b, 0, 88) == 88) {
						String format = new String(b, 0, 4);
						if (format.equals("DDS ")) {
							int width = getDWORD(b, 4);
							int height = getDWORD(b, 3);
							String compression = new String(b, 84, 4);
							reporter.log(new LogDebug("DDS file: " + file.getPath() + " " + width + "x" + height + " " + (compression.startsWith("D") ? compression : "")));

							if ((width % 4) != 0 && (height % 4) != 0) {
								if (compression.startsWith("DXT")) {
									reporter.log(new LogError(file.getPath() + " uses " + compression + " compression but its dimensions are not a multiple of 4"));
								}
							}
						} else {
							reporter.log(new LogError(file.getPath() + " doesn't appear to be a DDS file (invalid signature)"));
						}
					} else {
						reporter.log(new LogError("Unable to read the header from the DDS file " + file.getPath()));
					}
				} catch (IOException e) {
					reporter.log(new LogError("Unable to read the header from the DDS file " + file.getPath()));
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
						}
					}
				}
			} else if (extn.equals(".dll")) {
				SetDllPath dllPath = getDll();
				if (dllPath != null && dllPath.getPath().equals(file.getPath())) {
					// DLL, OnGetDLLPath->SetDllPath
					if (file.isImportVFS()) {
						String msg = file.getPath() + " should NOT be imported into the VFS";
						if (fixImports) {
							file.setImportVFS(0);
							reporter.log(new LogWarn(msg + " ... fixing"));
						} else {
							reporter.log(new LogError(msg + "!"));
						}
					}
				} else {
					// DLL, VFS = true
					if (file.getPath().equals("CvGameCore_Expansion1.dll") || file.getPath().equals("CvGameCore_Expansion2.dll")) {
						if (!file.isImportVFS()) {
							String msg = file.getPath() + " should be imported into the VFS";
							if (fixImports) {
								file.setImportVFS(1);
								reporter.log(new LogWarn(msg + " ... fixing"));
							} else {
								reporter.log(new LogError(msg + "!"));
							}
						}
					} else {
						reporter.log(new LogError("DLL files MUST be called CvGameCore_Expansion1.dll and be in the root of the mod"));
					}
				}
			} else if (extn.equals(".dge") || extn.equals(".fsmxml") || extn.equals(".ftsxml") || extn.equals(".fxsxml") || extn.equals(".gr2")) {
				// Animation assets, VFS = true
				if (!file.isImportVFS()) {
					String msg = "Animation asset " + file.getPath() + " should be imported into the VFS";
					if (fixImports) {
						file.setImportVFS(1);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}
				}
			} else if (extn.equals(".fpk")) {
				// Asset archive, VFS=false (shouldn't really be here)
				if (file.isImportVFS()) {
					String msg = file.getPath() + " shouldn't be imported into the VFS";
					if (fixImports) {
						file.setImportVFS(0);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}

					reporter.log(new LogWarn(file.getPath() + " is not a valid file type for a mod!"));
				}
			} else if (extn.equals(".ggxml")) {
				// Font Icon map, VFS=true
				if (!file.isImportVFS()) {
					String msg = "Font Icon map file " + file.getPath() + " should be imported into the VFS";
					if (fixImports) {
						file.setImportVFS(1);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}
				}
			} else if (extn.equals(".ltp")) {
				// LiveTuner Panel plug-in, VFS=false
				if (file.isImportVFS()) {
					String msg = file.getPath() + " shouldn't be imported into the VFS";
					if (fixImports) {
						file.setImportVFS(0);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}
				}
			} else if (extn.equals(".lua")) {
				EntryPoints addins = ((EntryPoints) sections.get(EntryPoints.NAME));
				
				if (file.isImportVFS()) {
					if (addins.isUiAddin(file)) {
						String msg = "Lua file " + file.getPath() + " UI Addin must NOT have VFS=true";
						if (fixImports) {
							file.setImportVFS(0);
							reporter.log(new LogWarn(msg + " ... fixing"));
						} else {
							reporter.log(new LogError(msg + "!"));
						}
					}
				} else {
					if (!addins.isUiAddin(file)) {
						reporter.log(new LogError("Lua file " + file.getPath() + " is neither VFS=true nor a UI Addin"));
					}
				}
			} else if (extn.equals(".mp3") || extn.equals(".ogg") || extn.equals(".wav")) {
				// Sound assets, VFS = true
				if (!file.isImportVFS()) {
					String msg = "Sound asset " + file.getPath() + " should be imported into the VFS";
					if (fixImports) {
						file.setImportVFS(1);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}
				}
			} else if (extn.equals(".sql")) {
				// SQL, VFS=false
				if (file.isImportVFS()) {
					String msg = file.getPath() + " shouldn't be imported into the VFS";
					if (fixImports) {
						file.setImportVFS(0);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}
				}

				if (!getUpdates().containsKey(file.getPath())) {
					reporter.log(new LogError("SQL file " + file.getPath() + " needs an OnModActivate->UpdateDatabase entry"));
				}
			} else if (extn.equals(".xml")) {
				EntryPoints addins = ((EntryPoints) sections.get(EntryPoints.NAME));
				
				if (file.isImportVFS()) {
					if (addins.isUiAddin(file)) {
						String msg = "XML file " + file.getPath() + " UI Addin must NOT have VFS=true";
						if (fixImports) {
							file.setImportVFS(0);
							reporter.log(new LogWarn(msg + " ... fixing"));
						} else {
							reporter.log(new LogError(msg + "!"));
						}
					} else {
						if (!(file instanceof XmlFile && (((XmlFile) file).isContext() || ((XmlFile) file).isLeaderScene()))) {
							reporter.log(new LogError("XML file " + file.getPath() + " in the VFS is not a <Context> or <LeaderScene>"));
						}
					}
				} else {
					if (addins.isUiAddin(file)) {
						if (!(file instanceof XmlFile && ((XmlFile) file).isContext())) {
							reporter.log(new LogError("XML file " + file.getPath() + " UI addin file does not start with <Context>"));
						}
					} else {
						if (!(file instanceof XmlFile && ((XmlFile) file).isGameData())) {
							reporter.log(new LogError("XML file " + file.getPath() + " database file does not start with <GameData>"));
						}
					}
				}
			} else {
				// Unknown file types will be caught by verifyImports()
			}
		}

		return !bFatal;
	}

	private boolean verifyDLL(boolean fixImports) {
		boolean bFatal = false;
		
		Map<String, ModFile> fileList = getFiles();
		SetDllPath dllPath = getDll();
		if (dllPath != null) {
			ModFile file = fileList.get(dllPath.getPath());
			if (file == null) {
				// A missing file is fatal
				reporter.log(new LogError(dllPath.getPath() + " cannot be found in the file list"));
				bFatal = true;
			} else {
				if (file.getImportVFS() == 1) {
					// Incorrectly importing into the VFS is not fatal
					String msg = dllPath.getPath() + " is incorrectly imported into the VFS";
					if (fixImports) {
						file.setImportVFS(0);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}
				}
			}
		}

		return !bFatal;
	}

	private boolean verifyUpdates(boolean fixImports) {
		boolean bFatal = false;

		Map<String, ModFile> fileList = getFiles();
		for (UpdateDatabase update : getUpdates().values()) {
			ModFile file = fileList.get(update.getPath());
			if (file == null) {
				// A missing file is fatal
				reporter.log(new LogError(update.getPath() + " cannot be found in the file list"));
				bFatal = true;
			} else {
				if (file.getImportVFS() == 1) {
					// Incorrectly importing into the VFS is not fatal
					String msg = update.getPath() + " is incorrectly imported into the VFS";
					if (fixImports) {
						file.setImportVFS(0);
						reporter.log(new LogWarn(msg + " ... fixing"));
					} else {
						reporter.log(new LogError(msg + "!"));
					}
				}
			}
		}

		return !bFatal;
	}

	private boolean verifyFiles() {
		boolean bFatal = false;

		for (ModFile file : getFiles().values()) {
			file.verify(reporter);
		}

		return !bFatal;
	}

	private boolean verifyAssociations() {
		boolean bFatal = false;

		Dependencies dependencies = ((Dependencies) sections.get(Dependencies.NAME));
		References references = ((References) sections.get(References.NAME));

		Blocks blocks = ((Blocks) sections.get(Blocks.NAME));
		for (Related related : blocks.getList()) {
			if (dependencies.contains(related)) {
				reporter.log(new LogError("Mod/Dlc with id " + related.getId() + " is both required and blocked"));
				bFatal = true;
			}
		}

		for (Related related : blocks.getList()) {
			if (references.contains(related)) {
				reporter.log(new LogError("Mod/Dlc with id " + related.getId() + " is both referenced and blocked"));
			}
		}

		return !bFatal;
	}

	// Each file not imported should be used to update the database, be part of an InGameUIAddin or a retrieved DLL
	private boolean verifyImports(boolean removeFiles) {
		boolean bFatal = false;

		EntryPoints addins = ((EntryPoints) sections.get(EntryPoints.NAME));
		Map<String, UpdateDatabase> updateList = getUpdates();
		SetDllPath dllPath = getDll();

		Iterator<ModFile> itrFileList = getFileList().iterator();
		while (itrFileList.hasNext()) {
			ModFile file = itrFileList.next();

			if (file.getImportVFS() == 0) {
				String path = file.getPath();

				if (updateList.containsKey(path)) {
					// It's used to update the database, the check for extn type is in verifyUpdates()
				} else if (addins.isUiAddin(file)) {
					// It's an addin
				} else if (dllPath != null && path.equals(dllPath.getPath())) {
					// It's a retrieved DLL
				} else if (path.endsWith(".ltp")) {
					// It's a LiveTuner panel
					reporter.log(new LogInfo("The LiveTuner plug-in '" + path + "' file should be manually copied to the main Debug sub-directory"));
				} else {
					// An additional file is not fatal - for example, it could be a ReadMe.txt file
					reporter.log(new LogWarn(file.getPath() + " appears to be unused"));

					if (removeFiles) {
						itrFileList.remove();
					}
				}
			}
		}

		return !bFatal;
	}

	private boolean verifyCustomScenario() {
		boolean bFatal = false;

		// If the mod hides the setup screen there must be a Custom entry point
		if (getProperties().getValue("HideSetupGame", "0").equals("1")) {
			EntryPoint custom = null;

			for (EntryPoint entry : getEntryPoints().values()) {
				if (entry.getType().equals("Custom")) {
					custom = entry;
					break;
				}
			}

			if (custom == null) {
				reporter.log(new LogError("HideSetupGame is 1 but no Custom entry point has been defined"));
				bFatal = true;
			} else {
				if (getProperties().getValue("Custom_Background_" + custom.getName(), null) == null) {
					reporter.log(new LogWarn("Custom_Background property is missing for custom scenario"));
				}
			}
		}

		return !bFatal;
	}

	private int getDWORD(byte[] b, int offset) {
		int val = 0;

		offset = offset * 4;

		val = val +  (b[offset + 0] & 0xff);
		val = val + ((b[offset + 1] & 0xff) << 8);
		val = val + ((b[offset + 2] & 0xff) << 16);
		val = val + ((b[offset + 3] & 0xff) << 24);

		return val;
	}

	public void merge(ModInfo mergeModInfo) {
		for (Entry<String, Section> entry : sections.entrySet()) {
			entry.getValue().merge(reporter, mergeModInfo.sections.get(entry.getKey()));
		}
	}

	public void saveAsMod(File newModPath) {
		File newModInfoPath = new File(newModPath, newModPath.getName() + ".modinfo");

		if (newModInfoPath.exists()) {
			newModInfoPath.delete();
		} else if (!newModInfoPath.getParentFile().exists()) {
			newModInfoPath.getParentFile().mkdirs();
		}

		try {
			if (newModInfoPath.createNewFile()) {
				reporter.log(new LogDebug("Created " + newModInfoPath.getAbsolutePath()));
				PrintStream out = null;

				try {
					out = new PrintStream(newModInfoPath);
					writeAsMod(out);
				} catch (FileNotFoundException e) {
					reporter.log(new LogError("ModInfo Error: " + newModInfoPath.getAbsolutePath() + " unable to open"));
				} finally {
					if (out != null) {
						out.close();
					}
				}

			} else {
				reporter.log(new LogError("ModInfo Error: " + newModInfoPath.getAbsolutePath() + " unable to create"));
			}
		} catch (IOException e) {
			reporter.log(new LogError("ModInfo Error: " + newModInfoPath.getAbsolutePath() + " unable to create"));
		}
	}

	public void writeAsMod(PrintStream out) {
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

		out.print("<Mod id=\"");
		out.print(guid);
		out.print("\" version=\"");
		out.print(version);
		out.println("\">");

		for (Section section : sections.values()) {
			section.writeAsMod(out, "  ");
		}

		out.println("</Mod>");
	}

	public void writeVsProjectProperties(PrintStream out, String indent) {
		Properties properties = getProperties();

		out.println(indent + "<Name>" + properties.getVsValue("Name") + "</Name>");
		out.println(indent + "<Guid>" + guid + "</Guid>");
		out.println(indent + "<ModVersion>" + version + "</ModVersion>");
		out.println(indent + "<Stability>" + properties.getVsValue("Stability", "Stable") + "</Stability>");
		out.println(indent + "<Teaser>" + properties.getVsValue("Teaser") + "</Teaser>");
		out.println(indent + "<Description>" + properties.getVsValue("Description") + "</Description>");
		out.println(indent + "<Authors>" + properties.getVsValue("Authors") + "</Authors>");
		out.println(indent + "<SpecialThanks>" + properties.getVsValue("SpecialThanks") + "</SpecialThanks>");
		out.println(indent + "<Categories>$Categories$</Categories>");
		out.println(indent + "<AffectsSavedGames>" + properties.getVsValue("AffectsSavedGames") + "</AffectsSavedGames>");
		out.println(indent + "<MinCompatibleSaveVersion>" + properties.getVsValue("MinCompatibleSaveVersion", "0") + "</MinCompatibleSaveVersion>");
		out.println(indent + "<SupportsSinglePlayer>" + properties.getVsValue("SupportsSinglePlayer") + "</SupportsSinglePlayer>");
		out.println(indent + "<SupportsMultiplayer>" + properties.getVsValue("SupportsMultiplayer") + "</SupportsMultiplayer>");
		out.println(indent + "<SupportsHotSeat>" + properties.getVsValue("SupportsHotSeat") + "</SupportsHotSeat>");
		out.println(indent + "<SupportsMac>" + properties.getVsValue("SupportsMac") + "</SupportsMac>");
		out.println(indent + "<ReloadAudioSystem>" + properties.getVsValue("ReloadAudioSystem") + "</ReloadAudioSystem>");
		out.println(indent + "<ReloadLandmarkSystem>" + properties.getVsValue("ReloadLandmarkSystem") + "</ReloadLandmarkSystem>");
		out.println(indent + "<ReloadStrategicViewSystem>" + properties.getVsValue("ReloadStrategicViewSystem") + "</ReloadStrategicViewSystem>");
		out.println(indent + "<ReloadUnitSystem>" + properties.getVsValue("ReloadUnitSystem") + "</ReloadUnitSystem>");
		out.println(indent + "<HideSetupGame>" + properties.getVsValue("HideSetupGame") + "</HideSetupGame>");
		out.println(indent + "<Homepage>" + properties.getVsValue("Homepage") + "</Homepage>");
		out.println(indent + "<AssemblyName>" + properties.getVsValue("Name") + "</AssemblyName>");
		out.println(indent + "<RootNamespace>" + properties.getVsValue("Name") + "</RootNamespace>");

		// Write out any custom properties
		out.println(indent + "<ModProperties>");
		for (Property property : properties.getVsPropertyList()) {
			out.println(indent + "  <Property>");
			out.println(indent + "    <Name>" + property.getName() + "</Name>");
			out.println(indent + "    <Description>" + property.getVsValue() + "</Description>");
			out.println(indent + "  </Property>");
		}
		out.println(indent + "</ModProperties>");

	}

	public void writeVsProjectAssociations(PrintStream out, String indent) {
		sections.get(Dependencies.NAME).writeAsProject(out, indent);
		sections.get(References.NAME).writeAsProject(out, indent);
		sections.get(Blocks.NAME).writeAsProject(out, indent);
	}

	public void writeVsProjectActions(PrintStream out, String indent) {
		sections.get(Actions.NAME).writeAsProject(out, indent);
	}

	public void writeVsProjectContent(PrintStream out, String indent) {
		sections.get(EntryPoints.NAME).writeAsProject(out, indent);
	}

	public void writeVsProjectDirsAndFiles(PrintStream out, String indent) {
		sections.get(Files.NAME).writeAsProject(out, indent);
	}

	private class ModInfoHandler extends DefaultHandler {
		private Section activeSection = null;

		@Override
		public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
			if (activeSection != null) {
				activeSection.startElement(namespaceURI, localName, qualifiedName, attrs);
			} else if (sections.containsKey(localName)) {
				activeSection = sections.get(localName);
			} else if ("Mod".equals(localName)) {
				guid = attrs.getValue("id");
				version = attrs.getValue("version");
			} else {
				reporter.log(new LogError("Start: " + localName + " is unknown!"));
			}
		}

		@Override
		public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
			if (activeSection != null) {
				if (activeSection.endElement(namespaceURI, localName, qualifiedName)) {
					activeSection = null;
				}
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (activeSection != null) {
				activeSection.characters(ch, start, length);
			}
		}
	}
}
