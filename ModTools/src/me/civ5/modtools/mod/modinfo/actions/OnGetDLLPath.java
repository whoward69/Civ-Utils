package me.civ5.modtools.mod.modinfo.actions;

import java.io.PrintStream;

import me.civ5.modtools.mod.modinfo.Section;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class OnGetDLLPath extends Section {
	public static final String NAME = "OnGetDLLPath";
	
	private ModReporter reporter;

	private SetDllPath dllPath = null;
	private SetDllPath tempDll = null;

	public OnGetDLLPath(ModReporter reporter) {
		super(NAME);
		
		this.reporter = reporter;
	}
	
	public SetDllPath getDll() {
		return dllPath;
	}
	
	@Override
	public boolean isEmpty() {
		return (dllPath != null);
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
		if ( "SetDllPath".equals(localName) ) {
			tempDll = new SetDllPath(null);
		} else {
			reporter.log(new LogError(getName() + ": Unknown child " + localName));
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ( tempDll != null ) {
			tempDll.setPath(tempDll.getPath() + new String(ch, start, length));
		}
	}

	@Override
	public boolean endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if ( super.endElement(namespaceURI, localName, qualifiedName) ) {
			return true;
		}

		if ( tempDll != null ) {
			dllPath = tempDll;
				
			tempDll = null;
		}
		
		return false;
	}

	@Override
	public boolean verify(ModReporter reporter) {
		// DLL imports are verified by usage in the ModInfo class
		return true;
	}

	@Override
	public void merge(ModReporter reporter, Section section) {
		if (getName().equals(section.getName()) ) {
			OnGetDLLPath onGetDLLPath = (OnGetDLLPath) section;

			if (dllPath == null) {
				dllPath = onGetDLLPath.getDll();
			}
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge " + getName() + " with " + section.getName()));
		}
	}

	@Override
	public void writeAsMod(PrintStream out, String indent) {
		if (dllPath != null) {
			startSection(out, indent);
	
			dllPath.writeAsMod(out, indent + "  ");
			
			endSection(out, indent);
		}
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
		if (dllPath != null) {
			out.println(indent + "<Action>");
			out.println(indent + "  <Set>" + "OnGetDLLPath" + "</Set>");
			out.println(indent + "  <Type>" + "SetDllPath" + "</Type>");
			out.println(indent + "  <FileName>" + getDll().getPath() + "</FileName>");
			out.println(indent + "</Action>");
		}
	}
}
