package me.civ5.modtools.mod.modinfo;

import java.io.PrintStream;

import me.civ5.modtools.mod.modinfo.actions.OnGetDLLPath;
import me.civ5.modtools.mod.modinfo.actions.OnModActivated;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class Actions extends Section {
	public static final String NAME = "Actions";
	
	private ModReporter reporter;
	
	private OnGetDLLPath onGetDLLPath;
	private OnModActivated onModActivated;
	private Section activeSection = null;
	
	public Actions(ModReporter reporter) {
		super(NAME);
		this.reporter = reporter;
		
		onGetDLLPath = new OnGetDLLPath(reporter);
		onModActivated = new OnModActivated(reporter);
	}

	public OnGetDLLPath getOnGetDLLPath() {
		return onGetDLLPath;
	}
	
	public OnModActivated getOnModActivated() {
		return onModActivated;
	}
	
	@Override
	public boolean isEmpty() {
		return (onGetDLLPath.isEmpty() && onModActivated.isEmpty());
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
		if ( OnGetDLLPath.NAME.equals(localName) ) {
			activeSection = onGetDLLPath;
		} else if ( OnModActivated.NAME.equals(localName) ) {
			activeSection = onModActivated;
    	} else if ( activeSection != null ) {
    		activeSection.startElement(namespaceURI, localName, qualifiedName, attrs);
    	} else {
			reporter.log(new LogError("Unknown Actions sub-section: " + localName));
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ( activeSection != null ) {
			activeSection.characters(ch, start, length);
		}
	}

	@Override
	public boolean endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if (super.endElement(namespaceURI, localName, qualifiedName)) {
			return true;
		}
		
    	if ( activeSection != null ) {
    		if ( activeSection.endElement(namespaceURI, localName, qualifiedName) ) {
    	    	activeSection = null;
    		}
    	}
		
		return false;
	}
	
	@Override
	public boolean verify(ModReporter reporter) {
		boolean bOK = true;
		
		bOK = (onGetDLLPath.verify(reporter) & onModActivated.verify(reporter)) && bOK;

		return bOK;
	}

	@Override
	public void merge(ModReporter reporter, Section section) {
		if (getName().equals(section.getName()) ) {
			Actions actions = (Actions) section;

			onGetDLLPath.merge(reporter, actions.onGetDLLPath);
			onModActivated.merge(reporter, actions.onModActivated);
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge " + getName() + " with " + section.getName()));
		}
	}

	@Override
	public void writeAsMod(PrintStream out, String indent) {
		if ( !isEmpty() ) {
			startSection(out, indent);
	
			onGetDLLPath.writeAsMod(out, indent + "  ");
			onModActivated.writeAsMod(out, indent + "  ");
			
			endSection(out, indent);
		}
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		if (isEmpty()) {
			out.println(indent + "<ModActions />");
		} else {
			out.println(indent + "<ModActions>");
			
			if (!onGetDLLPath.isEmpty()) {
				onGetDLLPath.writeAsProject(out, indent + "  ");
			}
			
			if (!onModActivated.isEmpty()) {
				onModActivated.writeAsProject(out, indent + "  ");
			}
			
			out.println(indent + "</ModActions>");
		}
	}
}
