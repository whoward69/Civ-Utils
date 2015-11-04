package me.civ5.modtools.mod.modinfo.entrypoints;

import java.io.PrintStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EntryPoint {
	private String type;
	private String path;
	
	private String name;
	private String description;
	
	private boolean receivingName = false;
	private boolean receivingDesc = false;
	
	public EntryPoint(String type, String path, String name, String description) {
		this.type = type;
		this.path = path.replaceAll("\\\\", "/");
		
		this.name = (name == null ? "" : name);
		this.description = (description == null ? "" : description);
	}

	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isUiAddin() {
		return (type.equals("InGameUIAddin") || type.equals("CityViewUIAddin") || type.equals("DiplomacyUIAddin") || type.equals("Custom"));
	}

	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
		if ( "Name".equals(localName) ) {
			receivingName = true;
		} else if ( "Description".equals(localName) ) {
			receivingDesc = true;
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if ( receivingName ) {
			name = name + new String(ch, start, length);
		} else if ( receivingDesc ) {
			description = description + new String(ch, start, length);
		}
	}

	public boolean endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if ( "EntryPoint".equals(localName) ) {
			return true; 
		} else {
			receivingName = false;
			receivingDesc = false;
		}

		return false;
	}
	
	public void writeAsMod(PrintStream out, String indent) {
		out.print(indent);
		out.print("<EntryPoint type=\"");
		out.print(type);
		out.print("\" file=\"");
		out.print(path);
		out.println("\">");
		
		out.print(indent + "  ");
		out.print("<Name>");
		out.print(name);
		out.println("</Name>");
		
		out.print(indent + "  ");
		out.print("<Description>");
		out.print(description);
		out.println("</Description>");
		
		out.print(indent);
		out.println("</EntryPoint>");
	}
}
