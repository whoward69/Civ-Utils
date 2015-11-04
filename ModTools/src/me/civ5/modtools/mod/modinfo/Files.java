package me.civ5.modtools.mod.modinfo;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import me.civ5.modtools.mod.modinfo.files.LuaFile;
import me.civ5.modtools.mod.modinfo.files.ModFile;
import me.civ5.modtools.mod.modinfo.files.SqlFile;
import me.civ5.modtools.mod.modinfo.files.VfsFile;
import me.civ5.modtools.mod.modinfo.files.XmlFile;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogWarn;
import me.civ5.modutils.log.ModReporter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class Files extends Section {
	public static final String NAME = "Files";
	
	private ModReporter reporter;
	
	private File modDir = null;
	
	private Map<String, ModFile> fileList = new LinkedHashMap<String, ModFile>();
	private String lastFileMD5 = null;
	private String lastImportVFS = null;
	private String lastFilePath = "";
	
	public Files(ModReporter reporter, File modDir) {
		super(NAME);
		this.reporter = reporter;
		
		this.modDir = modDir;
	}

	public Map<String, ModFile> getFiles() {
		return fileList;
	}
	
	@Override
	public boolean isEmpty() {
		return fileList.isEmpty();
	}
	
	private List<String> getVsDirList() {
		Set<String> dirList = new TreeSet<String>();
		
		for ( ModFile file : fileList.values() ) {
			File path = new File(file.getPath());
				
			while ( path.getParentFile() != null ) {
				path = path.getParentFile();
				dirList.add(path.getPath());
			}
		}

		return new ArrayList<String>(dirList);
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
		if ( "File".equals(localName) ) {
			lastFileMD5 = attrs.getValue("md5");
			lastImportVFS = attrs.getValue("import");
		} else {
			reporter.log(new LogError(getName() + ": Unknown child " + localName));
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		lastFilePath = lastFilePath + new String(ch, start, length);
	}

	@Override
	public boolean endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if ( super.endElement(namespaceURI, localName, qualifiedName) ) {
			return true;
		}
		
		lastFilePath = lastFilePath.trim();
		String tempPath = lastFilePath.toLowerCase();

		if ( tempPath.endsWith(".sql") ) {
			fileList.put(lastFilePath, new SqlFile(modDir, lastFilePath, lastFileMD5, lastImportVFS));
		} else if ( tempPath.endsWith(".xml") ) {
			fileList.put(lastFilePath, new XmlFile(modDir, lastFilePath, lastFileMD5, lastImportVFS));
		} else if ( tempPath.endsWith(".lua") ) {
			fileList.put(lastFilePath, new LuaFile(modDir, lastFilePath, lastFileMD5, lastImportVFS));
		} else if ( tempPath.endsWith(".dds") ) {
			fileList.put(lastFilePath, new VfsFile(modDir, lastFilePath, lastFileMD5, lastImportVFS));
		} else {
			fileList.put(lastFilePath, new VfsFile(modDir, lastFilePath, lastFileMD5, lastImportVFS));
		}
		
		lastFilePath = "";

		return false;
	}

	@Override
	public boolean verify(ModReporter reporter) {
		for (ModFile file : fileList.values()) {
			try {
				String md5 = DigestUtils.md5Hex(FileUtils.readFileToByteArray(file.getFullPath())).toUpperCase();
				if (!md5.equalsIgnoreCase(file.getMd5())) {
					reporter.log(new LogWarn("MD5 for " + file.getPath() + " is " + file.getMd5() + " (expecting " + md5 + ")"));
				}
			} catch (IOException e) {
				reporter.log(new LogError(e.getMessage()));
			}
		}

		return true;
	}

	@Override
	public void merge(ModReporter reporter, Section section) {
		if (getName().equals(section.getName()) ) {
			Files files = (Files) section;

			for (Entry<String, ModFile> file : files.fileList.entrySet() ) {
				if ( fileList.containsKey(file.getKey()) ) {
					ModFile thisFile = fileList.get(file.getKey());
					ModFile thatFile = file.getValue();
					
					if ( thisFile.getMd5().equals(thatFile.getMd5()) ) {
						// The paths and the md5 sigs are the same, assume this is the same file
					} else {
						reporter.log(new LogError(thatFile.getPath() + " is a duplicate, but has a different md5 sig!"));
					}
				} else {
					fileList.put(file.getKey(), file.getValue());
				}
			}
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge " + getName() + " with " + section.getName()));
		}
	}

	@Override
	public void writeAsMod(PrintStream out, String indent) {
		if ( isEmpty() ) {
			emptySection(out, indent);
		} else {
			startSection(out, indent);
			
			for (ModFile file : fileList.values()) {
				file.writeAsMod(out, indent + "  ");
			}
			
			endSection(out, indent);
		}
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		out.println(indent + "<ItemGroup>");
		for ( String dir : getVsDirList() ) {
			out.println(indent + "  <Folder Include=\"" + dir + "\" />");
		}
		out.println(indent + "</ItemGroup>");
		
		out.println(indent + "<ItemGroup>");
		for ( ModFile file : fileList.values() ) {
			out.println(indent + "  <Content Include=\"" + file.getVsPath() + "\">");
			out.println(indent + "    <SubType>Lua</SubType>"); // Weird, but they really are all subtype Lua
			out.println(indent + "    <ImportIntoVFS>" + file.getVsImportVFS() + "</ImportIntoVFS>");
			out.println(indent + "  </Content>");
		}
		out.println(indent + "</ItemGroup>");
	}
}
