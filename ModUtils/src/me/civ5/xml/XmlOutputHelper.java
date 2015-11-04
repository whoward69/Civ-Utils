package me.civ5.xml;

import java.io.UnsupportedEncodingException;

import me.civ5.exception.XmlException;

import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlOutputHelper {
    public static byte[] rawOutputBytes(Document document, String encoding) throws XmlException {
    	try {
    		return rawOutput(document, encoding).getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new XmlException("Encoding error", e);
		}
    }

    public static byte[] rawOutputBytes(Element element, String encoding) throws XmlException {
    	try {
    		return rawOutput(element, encoding).getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new XmlException("Encoding error", e);
		}
    }
    
    public static byte[] prettyOutputBytes(Document document, String encoding) throws XmlException {
    	try {
    		return prettyOutput(document, encoding).getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new XmlException("Encoding error", e);
		}
    }

    public static byte[] prettyOutputBytes(Element element, String encoding) throws XmlException {
    	try {
    		return prettyOutput(element, encoding).getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new XmlException("Encoding error", e);
		}
    }
    
    public static String rawOutput(Document document, String encoding) {
    	if ( document != null ) {
    		return getRawOutputter(encoding).outputString(document);
    	}
    	
    	return "Document is null";
    }

    public static String rawOutput(Element element, String encoding) {
    	if ( element != null ) {
    		return getRawOutputter(encoding).outputString(element);
    	}
    	
    	return "Element is null";
    }
    
    public static String rawOutputContent(Content content, String encoding) {
    	if ( content != null ) {
    		if ( content instanceof Text ) {
    			return getRawOutputter(encoding).outputString((Text) content);
    		} else if ( content instanceof ProcessingInstruction ) {
    			return getRawOutputter(encoding).outputString((ProcessingInstruction) content);
    		} else if ( content instanceof Comment ) {
    			return getRawOutputter(encoding).outputString((Comment) content);
    		} else if ( content instanceof CDATA ) {
    			return getRawOutputter(encoding).outputString((CDATA) content);
    		} else if ( content instanceof DocType ) {
    			return getRawOutputter(encoding).outputString((DocType) content);
    		} else if ( content instanceof EntityRef ) {
    			return getRawOutputter(encoding).outputString((EntityRef) content);
    		} else if ( content instanceof Element ) {
    			return getRawOutputter(encoding).outputString((Element) content);
    		}
    	}
    	
    	return "Content is null";
    }
    
    public static String prettyOutput(Document document, String encoding) {
    	if ( document != null ) {
    		return getPrettyOutputter(encoding).outputString(document);
    	}
    	
    	return "Document is null";
    }

    public static String prettyOutput(Element element, String encoding) {
    	if ( element != null ) {
    		return getPrettyOutputter(encoding).outputString(element);
    	}
    	
    	return "Element is null";
    }
    
    public static String prettyOutputContent(Content content, String encoding) {
    	if ( content != null ) {
    		if ( content instanceof Text ) {
    			return getPrettyOutputter(encoding).outputString((Text) content);
    		} else if ( content instanceof ProcessingInstruction ) {
    			return getPrettyOutputter(encoding).outputString((ProcessingInstruction) content);
    		} else if ( content instanceof Comment ) {
    			return getPrettyOutputter(encoding).outputString((Comment) content);
    		} else if ( content instanceof CDATA ) {
    			return getPrettyOutputter(encoding).outputString((CDATA) content);
    		} else if ( content instanceof DocType ) {
    			return getPrettyOutputter(encoding).outputString((DocType) content);
    		} else if ( content instanceof EntityRef ) {
    			return getPrettyOutputter(encoding).outputString((EntityRef) content);
    		} else if ( content instanceof Element ) {
    			return getPrettyOutputter(encoding).outputString((Element) content);
    		}
    	}
    	
    	return "Content is null";
    }
    
    private static XMLOutputter getRawOutputter(String encoding) {
    	Format compactFormat = Format.getRawFormat();
    	compactFormat.setEncoding(encoding);
    	
        return new XMLOutputter(compactFormat);
    }

    private static XMLOutputter getPrettyOutputter(String encoding) {
    	Format prettyFormat = Format.getPrettyFormat();
    	prettyFormat.setEncoding(encoding);
    	
        return new XMLOutputter(prettyFormat);
    }
}
