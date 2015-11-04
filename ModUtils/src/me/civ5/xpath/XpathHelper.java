package me.civ5.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import me.civ5.exception.XpathException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.xpath.XPath;

public class XpathHelper {
	static {
		try {
			String xPathClass = "XPath Engine: " + (newInstance(null, ".").getClass().getPackage().getName());
			System.out.println(xPathClass);
		} catch (Exception e) {
			System.err.println("Unable to determine XPath Engine");
		}
	}

	public static String getString(Document parent, String xPath, String defValue) {
		return getString(null, parent, xPath, defValue);
	}

	public static String getString(Namespace[] ns, Document parent, String xPath, String defValue) {
		String value = getString(ns, parent, xPath);
		return (value == null) ? defValue : value;
	}

	public static String getString(Element parent, String xPath, String defValue) {
		return getString(null, parent, xPath, defValue);
	}

	public static String getString(Namespace[] ns, Element parent, String xPath, String defValue) {
		String value = getString(ns, parent, xPath);
		return (value == null) ? defValue : value;
	}

	public static String getString(Document parent, String xPath) {
		return getString(null, parent, xPath);
	}

	public static String getString(Namespace[] ns, Document parent, String xPath) {
		if (parent == null) {
			return null;
		}

		return getString(ns, parent.getRootElement(), xPath);
	}

	public static String getString(Element parent, String xPath) {
		return getString(null, parent, xPath);
	}

	public static String getString(Namespace[] ns, Element parent, String xPath) {
		String string = null;

		if (parent != null) {
			try {
				XPath xp = newInstance(ns, xPath);

				Object object = xp.selectSingleNode(parent);
				if (object != null) {
					if (object instanceof Element) {
						string = ((Element) object).getText();
					} else if (object instanceof Attribute) {
						string = ((Attribute) object).getValue();
					} else if (object instanceof Text) {
						string = ((Text) object).getValue();
					} else if (object instanceof String) {
						string = ((String) object);
					}
				}
			} catch (JDOMException e) {
				System.err.println("Error locating xPath " + xPath);
			}
		}

		return string;
	}

	public static List<String> getStrings(Document parent, String xPath) throws XpathException {
		return getStrings(null, parent, xPath);
	}

	public static List<String> getStrings(Namespace[] ns, Document parent, String xPath) throws XpathException {
		if (parent == null) {
			return new ArrayList<String>(0);
		}

		return getStrings(ns, parent.getRootElement(), xPath);
	}

	public static List<String> getStrings(Element parent, String xPath) throws XpathException {
		return getStrings(null, parent, xPath);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getStrings(Namespace[] ns, Element parent, String xPath) throws XpathException {
		if (parent == null) {
			return new ArrayList<String>(0);
		}

		try {
			XPath xp = newInstance(ns, xPath);

			List<Object> objects = (List<Object>) xp.selectNodes(parent);
			if (objects == null) {
				return new ArrayList<String>(0);
			} else {
				ArrayList<String> strings = new ArrayList<String>(objects.size());

				for (Object object : objects) {
					if (object instanceof Element) {
						strings.add(((Element) object).getText());
					} else if (object instanceof Attribute) {
						strings.add(((Attribute) object).getValue());
					} else if (object instanceof Text) {
						strings.add(((Text) object).getValue());
					} else if (object instanceof String) {
						strings.add(((String) object));
					}
				}

				return strings;
			}
		} catch (JDOMException e) {
			System.err.println("Error locating xPath " + xPath);
			throw new XpathException("Error locating xPath " + xPath, e);
		}
	}

	public static Element getElement(Document parent, String xPath) {
		return getElement(null, parent, xPath);
	}

	public static Element getElement(Namespace[] ns, Document parent, String xPath) {
		if (parent == null) {
			return null;
		}

		return getElement(ns, parent.getRootElement(), xPath);
	}

	public static Element getElement(Element parent, String xPath) {
		return getElement(null, parent, xPath);
	}

	public static Element getElement(Namespace[] ns, Element parent, String xPath) {
		Element element = null;

		if (parent != null) {
			try {
				XPath xp = newInstance(ns, xPath);

				Object object = xp.selectSingleNode(parent);
				if (object != null && object instanceof Element) {
					element = (Element) object;
				}
			} catch (JDOMException e) {
				System.err.println("Error locating xPath " + xPath);
			}
		}

		return element;
	}

	public static List<Element> getElements(Document parent, String xPath) {
		return getElements(null, parent, xPath);
	}

	public static List<Element> getElements(Namespace[] ns, Document parent, String xPath) {
		if (parent == null) {
			return new ArrayList<Element>(0);
		}

		return getElements(ns, parent.getRootElement(), xPath);
	}

	public static List<Element> getElements(Element parent, String xPath) {
		return getElements(null, parent, xPath);
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getElements(Namespace[] ns, Element parent, String xPath) {
		if (parent == null) {
			return new ArrayList<Element>(0);
		}

		try {
			XPath xp = newInstance(ns, xPath);

			List<Object> objects = (List<Object>) xp.selectNodes(parent);
			if (objects == null) {
				return new ArrayList<Element>(0);
			} else {
				ArrayList<Element> elements = new ArrayList<Element>(objects.size());

				for (Object object : objects) {
					if (object instanceof Element) {
						elements.add((Element) object);
					}
				}

				return elements;
			}
		} catch (JDOMException e) {
			System.err.println("Error locating xPath " + xPath);
			return new ArrayList<Element>(0);
		}
	}

	public static ProcessingInstruction getPI(Document parent, String xPath) {
		return getPI(null, parent, xPath);
	}

	public static ProcessingInstruction getPI(Namespace[] ns, Document parent, String xPath) {
		if (parent == null) {
			return null;
		}

		return getPI(ns, parent.getRootElement(), xPath);
	}

	public static ProcessingInstruction getPI(Element parent, String xPath) {
		return getPI(null, parent, xPath);
	}

	public static ProcessingInstruction getPI(Namespace[] ns, Element parent, String xPath) {
		ProcessingInstruction pi = null;

		if (parent != null) {
			try {
				XPath xp = newInstance(ns, xPath);

				Object object = xp.selectSingleNode(parent);
				if (object != null && object instanceof ProcessingInstruction) {
					pi = (ProcessingInstruction) object;
				}
			} catch (JDOMException e) {
				System.err.println("Error locating xPath " + xPath);
			}
		}

		return pi;
	}

	public static List<ProcessingInstruction> getPIs(Document parent, String xPath) {
		return getPIs(null, parent, xPath);
	}

	public static List<ProcessingInstruction> getPIs(Namespace[] ns, Document parent, String xPath) {
		if (parent == null) {
			return new ArrayList<ProcessingInstruction>(0);
		}

		return getPIs(ns, parent.getRootElement(), xPath);
	}

	public static List<ProcessingInstruction> getPIs(Element parent, String xPath) {
		return getPIs(null, parent, xPath);
	}

	@SuppressWarnings("unchecked")
	public static List<ProcessingInstruction> getPIs(Namespace[] ns, Element parent, String xPath) {
		if (parent == null) {
			return new ArrayList<ProcessingInstruction>(0);
		}

		try {
			XPath xp = newInstance(ns, xPath);

			List<Object> objects = (List<Object>) xp.selectNodes(parent);
			if (objects == null) {
				return new ArrayList<ProcessingInstruction>(0);
			} else {
				ArrayList<ProcessingInstruction> pis = new ArrayList<ProcessingInstruction>(objects.size());

				for (Object object : objects) {
					if (object instanceof ProcessingInstruction) {
						pis.add((ProcessingInstruction) object);
					}
				}

				return pis;
			}
		} catch (JDOMException e) {
			System.err.println("Error locating xPath " + xPath);
			return new ArrayList<ProcessingInstruction>(0);
		}
	}

	public static int getInt(Document parent, String xPath, int defValue) {
		return getInt(null, parent, xPath, defValue);
	}

	public static int getInt(Namespace[] ns, Document parent, String xPath, int defValue) {
		if (parent == null) {
			return defValue;
		}

		return getInt(ns, parent.getRootElement(), xPath, defValue);
	}

	public static int getInt(Element parent, String xPath, int defValue) {
		return getInt(null, parent, xPath, defValue);
	}

	public static int getInt(Namespace[] ns, Element parent, String xPath, int defValue) {
		int value = defValue;

		if (parent != null) {
			String strValue = getString(ns, parent, xPath);
			if (strValue != null) {
				try {
					value = Integer.parseInt(strValue);
				} catch (NumberFormatException e) {
				}
			}
		}

		return value;
	}

	public static float getFloat(Document parent, String xPath, float defValue) {
		return getFloat(null, parent, xPath, defValue);
	}

	public static float getFloat(Namespace[] ns, Document parent, String xPath, float defValue) {
		if (parent == null) {
			return defValue;
		}

		return getFloat(ns, parent.getRootElement(), xPath, defValue);
	}

	public static float getFloat(Element parent, String xPath, float defValue) {
		return getFloat(null, parent, xPath, defValue);
	}

	public static float getFloat(Namespace[] ns, Element parent, String xPath, float defValue) {
		float value = defValue;

		if (parent != null) {
			String strValue = getString(ns, parent, xPath);
			if (strValue != null) {
				try {
					value = Float.parseFloat(strValue);
				} catch (NumberFormatException e) {
				}
			}
		}

		return value;
	}

	public static double getDouble(Document parent, String xPath, double defValue) {
		return getDouble(null, parent, xPath, defValue);
	}

	public static double getDouble(Namespace[] ns, Document parent, String xPath, double defValue) {
		if (parent == null) {
			return defValue;
		}

		return getDouble(ns, parent.getRootElement(), xPath, defValue);
	}

	public static double getDouble(Element parent, String xPath, double defValue) {
		return getDouble(null, parent, xPath, defValue);
	}

	public static double getDouble(Namespace[] ns, Element parent, String xPath, double defValue) {
		double value = defValue;

		if (parent != null) {
			String strValue = getString(ns, parent, xPath);
			if (strValue != null) {
				try {
					value = Double.parseDouble(strValue);
				} catch (NumberFormatException e) {
				}
			}
		}

		return value;
	}

	public static boolean getBoolean(Document parent, String xPath, boolean defValue) {
		return getBoolean(null, parent, xPath, defValue);
	}

	public static boolean getBoolean(Namespace[] ns, Document parent, String xPath, boolean defValue) {
		if (parent == null) {
			return defValue;
		}

		return getBoolean(ns, parent.getRootElement(), xPath, defValue);
	}

	public static boolean getBoolean(Element parent, String xPath, boolean defValue) {
		return getBoolean(null, parent, xPath, defValue);
	}

	public static boolean getBoolean(Namespace[] ns, Element parent, String xPath, boolean defValue) {
		boolean value = defValue;

		if (parent != null) {
			String strValue = getString(ns, parent, xPath);
			if (strValue != null) {
				value = strValue.equalsIgnoreCase("true");
			}
		}

		return value;
	}
	
	public static String quote(String value) {
		if ( value != null ) {
			if ( value.contains("'") ) {
				if ( value.contains("\"") ) {
					// We've got something perverse like [["It's back!"]] (ie a mix of single and double quotes!)
					StringBuilder sb = new StringBuilder("concat(");

					StringTokenizer st = new StringTokenizer(value, "'\"", true);
					while ( st.hasMoreTokens() ) {
						sb.append(quote(st.nextToken()));
						
						if ( st.hasMoreTokens() ) {
							sb.append(", ");
						}
					}
					
					sb.append(")");
					
					return sb.toString();
				} else {
					return '"' + value + '"';
				}
				
			} else {
				return "'" + value + "'";
			}
		}
		
		return value;
	}

	protected static XPath newInstance(Namespace[] ns, String xPath) throws JDOMException {
		XPath xp = XPath.newInstance(xPath);

		if (ns != null) {
			for (Namespace n : ns) {
				xp.addNamespace(n);
			}
		}

		return xp;
	}
}
