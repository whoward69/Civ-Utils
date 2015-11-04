package me.civ5.modtools.mod.modinfo;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.civ5.modtools.mod.modinfo.related.DlcRelated;
import me.civ5.modtools.mod.modinfo.related.GameRelated;
import me.civ5.modtools.mod.modinfo.related.ModRelated;
import me.civ5.modtools.mod.modinfo.related.Related;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class Relateds extends Section {
	protected ModReporter reporter;
	
	private Map<String, Related> relatedList = new LinkedHashMap<String, Related>();

	public Relateds(ModReporter reporter, String name) {
		super(name);
		this.reporter = reporter;
	}

	@Override
	public boolean isEmpty() {
		return relatedList.isEmpty();
	}
	
	public Collection<Related> getList() {
		return relatedList.values();
	}
	
	public boolean contains(Related related) {
		return relatedList.containsKey(related.getId());
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
		if ( "Game".equals(localName) ) {
			relatedList.put("game", new GameRelated(reporter, null, attrs.getValue("minversion"), attrs.getValue("maxversion")));
		} else if ( "Dlc".equals(localName) ) {
			String id = attrs.getValue("id");
			relatedList.put(id, new DlcRelated(reporter, id, attrs.getValue("minversion"), attrs.getValue("maxversion")));
		} else if ( "Mod".equals(localName) ) {
			String id = attrs.getValue("id");
			relatedList.put(id, new ModRelated(reporter, id, attrs.getValue("minversion"), attrs.getValue("maxversion")));
		} else {
			reporter.log(new LogError(getName() + ": Unknown child " + localName));
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// Just ignore them
	}

	@Override
	public boolean verify(ModReporter reporter) {
		// Nothing to do, but in the ModInfo verification we check that we're not both blocking and dependant on the same mod/dlc
		return true;
	}

	@Override
	public void merge(ModReporter reporter, Section section) {
		if (getName().equals(section.getName()) ) {
			Relateds relateds = (Relateds) section;

			for (Entry<String, Related> related : relateds.relatedList.entrySet() ) {
				if ( relatedList.containsKey(related.getKey()) ) {
					relatedList.get(related.getKey()).merge(related.getValue());
				} else {
					relatedList.put(related.getKey(), related.getValue());
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
			
			for (Related related : relatedList.values()) {
				related.writeAsMod(out, indent + "  ");
			}
			
			endSection(out, indent);
		}
	}
	
	protected void writeAsProject(PrintStream out, String indent, String name) {
		if ( isEmpty() ) {
			out.print(indent);
			out.print("<");
			out.print(name);
			out.println(" />");
		} else {
			out.print(indent);
			out.print("<");
			out.print(name);
			out.println(">");
			
			for (Related related : relatedList.values()) {
				related.writeAsProject(out, indent + "  ");
			}
			
			out.print(indent);
			out.print("</");
			out.print(name);
			out.println(">");
		}
	}
}
