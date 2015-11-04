package me.civ5.xml;

import java.util.List;
import java.util.Map;

import me.civ5.xpath.XpathHelper;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;

public class XmlHelper {

	public static Element getChildElement(Element parent, String name) {
		Element element = XpathHelper.getElement(parent, "./" + name);

		if ( element == null ) {
			element = newElement(parent, name, null);
		}
		
		return element;
	}
	
	public static Element getIdChildElement(Element parent, String name, String attrId) {
		return getAttributedChildElement(parent, name, "id", attrId);
	}
	
	public static Element getTypedChildElement(Element parent, String name, String attrType) {
		return getAttributedChildElement(parent, name, "type", attrType);
	}
	
	public static Element getNamedChildElement(Element parent, String name, String attrName) {
		return getAttributedChildElement(parent, name, "name", attrName);
	}
	
	private static Element getAttributedChildElement(Element parent, String name, String attrName, String attrValue) {
		Element element = XpathHelper.getElement(parent, "./" + name + "[@" + attrName + "='" + attrValue + "']");

		if ( element == null ) {
			element = newAttributedElement(parent, name, null, attrName, attrValue);
		}
		
		return element;
	}
	
	
	public static Element newElement(Document parent, String name) {
		return newElement(parent, name, null);
	}
	
	public static Element newElement(Document parent, String name, Namespace ns) {
		Element element = (ns != null) ? new Element(name, ns) : new Element(name);
		
		parent.setRootElement(element);
		
		return element;
	}
	
	public static Element newElement(Element parent, String name) {
		return newElement(parent, name, null);
	}
	
	public static Element newElement(Element parent, String name, Namespace ns) {
		Element element = (ns != null) ? new Element(name, ns) : new Element(name);
		
		if ( parent != null ) {
			parent.addContent(element);
		}
		
		return element;
	}
	
	
	public static Element newIdElement(Document parent, String name, String attrId) {
		return newIdElement(parent, name, null, attrId);
	}
	
	public static Element newIdElement(Document parent, String name, Namespace ns, String attrId) {
		return newAttributedElement(parent, name, ns, "id", attrId);
	}
	
	public static Element newIdElement(Element parent, String name, String attrId) {
		return newIdElement(parent, name, null, attrId);
	}
	
	public static Element newIdElement(Element parent, String name, Namespace ns, String attrId) {
		return newAttributedElement(parent, name, ns, "id", attrId);
	}
	
	
	public static Element newTypedElement(Document parent, String name, String attrType) {
		return newTypedElement(parent, name, null, attrType);
	}
	
	public static Element newTypedElement(Document parent, String name, Namespace ns, String attrType) {
		return newAttributedElement(parent, name, ns, "type", attrType);
	}
	
	public static Element newTypedElement(Element parent, String name, String attrType) {
		return newTypedElement(parent, name, null, attrType);
	}
	
	public static Element newTypedElement(Element parent, String name, Namespace ns, String attrType) {
		return newAttributedElement(parent, name, ns, "type", attrType);
	}
	
	
	public static Element newNamedElement(Document parent, String name, String attrName) {
		return newNamedElement(parent, name, null, attrName);
	}
	
	public static Element newNamedElement(Document parent, String name, Namespace ns, String attrName) {
		return newAttributedElement(parent, name, ns, "name", attrName);
	}
	
	public static Element newNamedElement(Element parent, String name, String attrName) {
		return newNamedElement(parent, name, null, attrName);
	}
	
	public static Element newNamedElement(Element parent, String name, Namespace ns, String attrName) {
		return newAttributedElement(parent, name, ns, "name", attrName);
	}

	
	private static Element newAttributedElement(Document parent, String name, Namespace ns, String attrName, String attrValue) {
		Element ele = newElement(parent, name, ns);
		
		setAttribute(ele, attrName, attrValue);
		
		return ele;
	}

	private static Element newAttributedElement(Element parent, String name, Namespace ns, String attrName, String attrValue) {
		Element ele = newElement(parent, name, ns);
		
		setAttribute(ele, attrName, attrValue);
		
		return ele;
	}

	
	public static Element newNVElement(Element parent, String name, String attrName, String attrValue) {
		return newNVElement(parent, name, null, attrName, attrValue);
	}
	
	public static Element newNVElement(Element parent, String name, Namespace ns, String attrName, String attrValue) {
		Element ele = newElement(parent, name, ns);
		
		setAttribute(ele, "name",  attrName);
		setAttribute(ele, "value", attrValue);
		
		return ele;
	}
	
	public static Element newTextElement(Element parent, String name, String text) {
		return newTextElement(parent, name, null, text);
	}
	
	public static Element newTextElement(Element parent, String name, Namespace ns, String text) {
		Element element = newElement(parent, name, ns);
		
		if ( text != null ) {
			element.setText(text);
		}
		
		return element;
	}
	
	
	public static ProcessingInstruction newProcessingInstruction(Element parent, String target, String data) {
		ProcessingInstruction pi = new ProcessingInstruction(target, data);
		
		parent.addContent(pi);
		
		return pi;
	}

	public static ProcessingInstruction newProcessingInstruction(Element parent, String target, Map<?, ?> data) {
		ProcessingInstruction pi = new ProcessingInstruction(target, data);
		
		parent.addContent(pi);
		
		return pi;
	}

	
	public static void setAttributes(Element element, List<Attribute> attrs) {
		if (element != null ) {
			if ( attrs != null ) {
 				// Do NOT use element.setAttributes() here - it does a "clear and add" not just an "add"
				for ( Attribute attr : attrs ) {
					element.setAttribute(attr);
				}
			}
		}
	}
	
	public static void setAttribute(Element element, Attribute attr) {
		if (element != null ) {
			if ( attr != null ) {
				element.setAttribute(attr);
			}
		}
	}
	
	public static void setAttribute(Element element, String name, int value) {
		setAttribute(element, name, Integer.toString(value));
	}
	
	public static void setAttribute(Element element, String name, boolean value) {
		setAttribute(element, name, Boolean.toString(value));
	}
	
	public static void setAttribute(Element element, String name, String value) {
		if (element != null ) {
			if ( name != null ) {
				if ( value != null ) {
					element.setAttribute(name, value);
				} else {
					element.removeAttribute(name);
				}
			}
		}
	}
	
	public static void setTrimmedAttribute(Element element, String name, String value) {
		if ( value.trim().length() == 0 ) {
			value = null;
		}

		setAttribute(element, name, value);
	}

	
	public static Element detachElement(Document parent, String xPath) {
    	Element ele = XpathHelper.getElement(parent, xPath);
    	
    	if ( ele != null ) {
    		ele.detach();
    	}
    	
    	return ele;
    }

	public static Element detachElement(Element parent, String xPath) {
    	Element ele = XpathHelper.getElement(parent, xPath);
    	
    	if ( ele != null ) {
    		ele.detach();
    	}
    	
    	return ele;
    }

    public static List<Element> detachElements(Element parent, String xPath) {
    	List<Element> eles = XpathHelper.getElements(parent, xPath);
    	
    	for ( Element ele : eles ) {
    		ele.detach();
    	}
    	
    	return eles;
    }
    
    @SuppressWarnings("unchecked")
	public static String getConcatText(Element element) {
    	StringBuilder sb = new StringBuilder();
    	
    	for ( Content child : ((List<Content>) element.getContent()) ) {
    		if ( child instanceof Text ) {
    			sb.append(((Text) child).getText());
    		} else if ( child instanceof Element ) {
    			sb.append(getConcatText((Element) child));
    		}
    	}
    	
    	return sb.toString();
    }
}
