package me.civ5.modtools.mod.modinfo.actions;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.civ5.modtools.mod.modinfo.Section;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class OnModActivated extends Section {
	public static final String NAME = "OnModActivated";
	
	private ModReporter reporter;

	private Map<String, UpdateDatabase> updateList = new LinkedHashMap<String, UpdateDatabase>();
	private UpdateDatabase tempUpdate = null;

	public OnModActivated(ModReporter reporter) {
		super(NAME);
		
		this.reporter = reporter;
	}
	
	public Map<String, UpdateDatabase> getUpdates() {
		return updateList;
	}
	
	@Override
	public boolean isEmpty() {
		return updateList.isEmpty();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
		if ( "UpdateDatabase".equals(localName) ) {
			tempUpdate = new UpdateDatabase(null);
		} else {
			reporter.log(new LogError(getName() + ": Unknown child " + localName));
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ( tempUpdate != null ) {
			tempUpdate.setPath(tempUpdate.getPath() + new String(ch, start, length));
		}
	}

	@Override
	public boolean endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if ( super.endElement(namespaceURI, localName, qualifiedName) ) {
			return true;
		}

		if ( tempUpdate != null ) {
			updateList.put(tempUpdate.getPath(), tempUpdate);
				
			tempUpdate = null;
		}
		
		return false;
	}

	@Override
	public boolean verify(ModReporter reporter) {
		// Database updates are verified by usage in the ModInfo class
		return true;
	}

	@Override
	public void merge(ModReporter reporter, Section section) {
		if (getName().equals(section.getName()) ) {
			OnModActivated onModActivated = (OnModActivated) section;

			for (Entry<String, UpdateDatabase> update : onModActivated.updateList.entrySet() ) {
				if ( !updateList.containsKey(update.getKey()) ) {
					updateList.put(update.getKey(), update.getValue());
				}
			}
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge " + getName() + " with " + section.getName()));
		}
	}

	@Override
	public void writeAsMod(PrintStream out, String indent) {
		startSection(out, indent);

		for (UpdateDatabase update : updateList.values()) {
			update.writeAsMod(out, indent + "  ");
		}
		
		endSection(out, indent);
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		for ( UpdateDatabase update : getUpdates().values() ) {
			out.println(indent + "<Action>");
			out.println(indent + "  <Set>" + "OnModActivated" + "</Set>");
			out.println(indent + "  <Type>" + "UpdateDatabase" + "</Type>");
			out.println(indent + "  <FileName>" + update.getPath() + "</FileName>");
			out.println(indent + "</Action>");
		}
	}
}
