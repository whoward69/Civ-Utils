package me.civ5.modtools.mod.modinfo;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.civ5.modtools.mod.modinfo.entrypoints.EntryPoint;
import me.civ5.modtools.mod.modinfo.files.ModFile;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class EntryPoints extends Section {
	public static final String NAME = "EntryPoints";
	
	private ModReporter reporter;
	
	private Map<String, EntryPoint> entryList = new LinkedHashMap<String, EntryPoint>();
	private EntryPoint tempEntry = null;
	
	public EntryPoints(ModReporter reporter) {
		super(NAME);
		this.reporter = reporter;
	}

	@Override
	public boolean isEmpty() {
		return entryList.isEmpty();
	}
	
	public Map<String, EntryPoint> getEntryPoints() {
		return entryList;
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
		if ( "EntryPoint".equals(localName) ) {
			tempEntry = new EntryPoint(attrs.getValue("type"), attrs.getValue("file"), null, null);
		} else if ( tempEntry != null ) {
			tempEntry.startElement(namespaceURI, localName, qualifiedName, attrs);
		} else {
			reporter.log(new LogError(getName() + ": Unknown child " + localName));
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ( tempEntry != null ) {
			tempEntry.characters(ch, start, length);
		}
	}

	@Override
	public boolean endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if ( super.endElement(namespaceURI, localName, qualifiedName) ) {
			return true;
		}

		if ( tempEntry != null ) {
			if ( tempEntry.endElement(namespaceURI, localName, qualifiedName) ) {
				entryList.put(tempEntry.getPath(), tempEntry);
				
				tempEntry = null;
			}
		}
		
		return false;
	}
	
	public boolean isUiAddin(ModFile file) {
		String path = file.getPath();
		
		if (entryList.containsKey(path)) {
			return entryList.get(path).isUiAddin();
		}
		
		// It doesn't seem to be an addin ... but ... it could be the other half of a UI xml/lua pair
		String uiPair = null;
		if (path.endsWith(".xml")) {
			uiPair = path.substring(0, path.length() - 4) + ".lua";
		} else if (path.endsWith(".lua")) {
			uiPair = path.substring(0, path.length() - 4) + ".xml";
		}

		if (uiPair != null && entryList.containsKey(uiPair)) {
			return entryList.get(uiPair).isUiAddin();
		}

		if (path.endsWith("_small.xml")) {
			uiPair = path.substring(0, path.length() - 10) + ".xml";
			
			if (entryList.containsKey(uiPair)) {
				return entryList.get(uiPair).isUiAddin();
			} else {
				uiPair = path.substring(0, path.length() - 10) + ".lua";
				if (entryList.containsKey(uiPair)) {
					return entryList.get(uiPair).isUiAddin();
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean verify(ModReporter reporter) {
		boolean bOK = true;
		
		boolean seenCustom = false;
		
		// There can only be one Custom entry point
		for (EntryPoint entry : entryList.values()) {
			if (entry.getType().equals("Custom")) {
				if (seenCustom) {
					reporter.log(new LogError("Duplicate Custom entry point found " + entry.getName()));
					bOK = false;
				}
				
				seenCustom = true;
			}
		}

		// Entry points are verified by usage in the ModInfo class
		return bOK;
	}

	@Override
	public void merge(ModReporter reporter, Section section) {
		if (getName().equals(section.getName()) ) {
			EntryPoints entryPoints = (EntryPoints) section;

			for (Entry<String, EntryPoint> entry : entryPoints.entryList.entrySet() ) {
				if ( entryList.containsKey(entry.getKey()) ) {
					EntryPoint thisEntry = entryList.get(entry.getKey());
					EntryPoint thatEntry = entry.getValue();

					if ( !thisEntry.getType().equals(thatEntry.getType()) ) {
						entryList.put(entry.getKey(), entry.getValue());
					}
				} else {
					entryList.put(entry.getKey(), entry.getValue());
				}
			}
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge " + getName() + " with " + section.getName()));
		}
	}

	@Override
	public void writeAsMod(PrintStream out, String indent) {
		if ( !isEmpty() ) {
			startSection(out, indent);
			
			for (EntryPoint entry : entryList.values()) {
				entry.writeAsMod(out, indent + "  ");
			}
			
			endSection(out, indent);
		}
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		if (entryList.isEmpty()) {
			out.println(indent + "<ModContent />");
		} else {
			out.println(indent + "<ModContent>");
			
			for ( EntryPoint content : entryList.values() ) {
				out.println(indent + "  <Content>");
				out.println(indent + "    <Type>" + content.getType() + "</Type>");
				out.println(indent + "    <Name>" + content.getName() + "</Name>");
				if (content.getDescription().trim().length() == 0) {
					out.println(indent + "    <Description />");
				} else {
					out.println(indent + "    <Description>" + content.getDescription().trim() + "</Description>");
				}
				out.println(indent + "    <FileName>" + content.getPath() + "</FileName>");
				out.println(indent + "  </Content>");
			}
	
			out.println(indent + "</ModContent>");
		}
	}
}
