package me.civ5.modtools.mod.modinfo;

import java.io.PrintStream;

import me.civ5.modutils.log.ModReporter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class Section {
	private String name;
	
	public Section(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract boolean isEmpty();
	
	public abstract void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException;
	public abstract void characters(char[] ch, int start, int length) throws SAXException;

	public boolean endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if ( name.equals(localName) ) {
			return true;
		}
		
		return false;
	}

	public abstract boolean verify(ModReporter reporter);
	public abstract void merge(ModReporter reporter, Section section);
	
	public abstract void writeAsMod(PrintStream out, String indent);
	public abstract void writeAsProject(PrintStream out, String indent);
	
	protected void startSection(PrintStream out, String indent) {
		out.print(indent);
		
		out.print("<");
		out.print(name);
		out.println(">");
	}
	
	protected void emptySection(PrintStream out, String indent) {
		out.print(indent);
		
		out.print("<");
		out.print(name);
		out.println(" />");
	}
	
	protected void endSection(PrintStream out, String indent) {
		out.print(indent);
		
		out.print("</");
		out.print(name);
		out.println(">");
	}
}
