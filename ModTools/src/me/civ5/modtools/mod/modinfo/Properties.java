package me.civ5.modtools.mod.modinfo;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.civ5.modtools.mod.modinfo.properties.BooleanProperty;
import me.civ5.modtools.mod.modinfo.properties.CustomProperty;
import me.civ5.modtools.mod.modinfo.properties.FalseBiasedProperty;
import me.civ5.modtools.mod.modinfo.properties.Property;
import me.civ5.modtools.mod.modinfo.properties.SpecialThanksProperty;
import me.civ5.modtools.mod.modinfo.properties.StringProperty;
import me.civ5.modtools.mod.modinfo.properties.TrueBiasedProperty;
import me.civ5.modutils.log.ModReporter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class Properties extends Section {
	public static final String NAME = "Properties";
	
	private Map<String, Property> propertyList = new LinkedHashMap<String, Property>();
	private Property tempProperty = null;
	
	public Properties() {
		super(NAME);
	}

	@Override
	public boolean isEmpty() {
		return propertyList.isEmpty();
	}
	
	public void add(Property property) {
		propertyList.put(property.getName(), property);
	}
	
	public void setName(String name) {
		propertyList.get("Name").setValue(name);
	}
	
	public String getValue(String key, String defValue) {
		Property prop = propertyList.get(key);
		
		if (prop == null) {
			return defValue;
		} else {
			return prop.getValue();
		}
	}
	
	public String getVsValue(String key) {
		return getVsValue(key, "");
	}
	
	public String getVsValue(String key, String defValue) {
		Property property = propertyList.get(key); 
		return ((property != null) ? property.getVsValue() : defValue);
	}
	
	public List<Property> getVsPropertyList() {
		List<Property> propList = new ArrayList<Property>();
		
		for (Property property : propertyList.values()) {
			if ( property instanceof CustomProperty ) {
				propList.add(property);
			}
		}
		
		return propList;
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attrs) throws SAXException {
		if ( "Name".equals(localName) || "Teaser".equals(localName) || "Description".equals(localName) || "Authors".equals(localName) ) {
			tempProperty = new StringProperty(localName);
		} else if ( "SpecialThanks".equals(localName) ) {
			tempProperty = new SpecialThanksProperty();
		} else if ( "Stability".equals(localName) ) {
			tempProperty = new StringProperty(localName);
		} else if ( "Homepage".equals(localName) ) {
			tempProperty = new StringProperty(localName);
		} else if ( "MinCompatibleSaveVersion".equals(localName) ) {
			tempProperty = new StringProperty(localName);
		} else if ( "AffectsSavedGames".equals(localName) ) {
			tempProperty = new TrueBiasedProperty(localName);
		} else if ( "SupportsSinglePlayer".equals(localName) || "SupportsMultiplayer".equals(localName) || "SupportsHotSeat".equals(localName) || "SupportsMac".equals(localName) ) {
			tempProperty = new FalseBiasedProperty(localName);
		} else if ( "HideSetupGame".equals(localName) ) {
			tempProperty = new FalseBiasedProperty(localName);
		} else if ( "ReloadAudioSystem".equals(localName) || "ReloadLandmarkSystem".equals(localName) || "ReloadStrategicViewSystem".equals(localName) || "ReloadUnitSystem".equals(localName) ) {
			tempProperty = new FalseBiasedProperty(localName);
		} else {
			tempProperty = new CustomProperty(localName);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ( tempProperty != null ) {
			if ( tempProperty instanceof BooleanProperty ) {
				tempProperty.setValue(new String(ch, start, length));
			} else {
				tempProperty.setValue(tempProperty.getValue() + new String(ch, start, length));
			}
		}
	}

	@Override
	public boolean endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
		if ( super.endElement(namespaceURI, localName, qualifiedName) ) {
			return true;
		}

		if ( tempProperty != null ) {
			propertyList.put(tempProperty.getName(), tempProperty);
				
			tempProperty = null;
		}
		
		return false;
	}

	@Override
	public boolean verify(ModReporter reporter) {
		boolean bOK = true;
		
		for ( Property property : propertyList.values() ) {
			bOK = property.verify(reporter) && bOK;
		}

		return bOK;
	}

	@Override
	public void merge(ModReporter reporter, Section section) {
		if (getName().equals(section.getName()) ) {
			Properties properties = (Properties) section;

			for (Entry<String, Property> property : properties.propertyList.entrySet() ) {
				String name = property.getKey();
				
				if ( "Name".equals(name) ) {
					Property desc = propertyList.get("Description");
					desc.setValue(desc.getValue() + "\r\n  " + property.getValue().getValue() + ",");
				} else if ( "Authors".equals(name) ) {
					// Authors are appended to the SpecialThanks property
					Property thanks = propertyList.get("SpecialThanks");
					
					if ( thanks == null ) {
						thanks = new SpecialThanksProperty();
						propertyList.put(thanks.getName(), thanks);
					}
					
					thanks.merge(reporter, property.getValue());
				} else if ( "Teaser".equals(name) || "Description".equals(name) || "Homepage".equals(name) ) {
					// These are never merged as they only really apply to the mod being merged in
				} else if ( "MinCompatibleSaveVersion".equals(name) || "Stability".equals(name) ) {
					// Ignore these as they only apply to the mod being merged in, and not the new meta-mod
				} else {
					if ( propertyList.containsKey(property.getKey()) ) {
						propertyList.get(property.getKey()).merge(reporter, property.getValue());
					} else {
						propertyList.put(property.getKey(), property.getValue());
					}
				}
			}
		}
	}
	
	@Override
	public void writeAsMod(PrintStream out, String indent) {
		startSection(out, indent);
		
		for (Property property : propertyList.values()) {
			property.writeAsMod(out, indent + "  ");
		}
		
		endSection(out, indent);
	}

	@Override
	public void writeAsProject(PrintStream out, String indent) {
	}
}
