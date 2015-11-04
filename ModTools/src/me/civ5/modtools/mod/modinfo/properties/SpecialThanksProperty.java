package me.civ5.modtools.mod.modinfo.properties;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

public class SpecialThanksProperty extends Property {
	private Set<String> values = new LinkedHashSet<String>();
	
	public SpecialThanksProperty() {
		super("SpecialThanks");
	}

	public SpecialThanksProperty(String value) {
		this();
		addValue(value);
	}
	
	public void addValue(String value) {
		StringTokenizer st = new StringTokenizer(value, ",");
		while ( st.hasMoreTokens() ) {
			values.add(st.nextToken().trim());
		}
	}

	@Override
	public String getValue() {
		if ( values.size() > 0 ) {
			StringBuilder sb = new StringBuilder();
			
			for ( String value : values ) {
				sb.append(", ").append(value);
			}
			
			return sb.toString().substring(2);
		} else {
			return "";
		}
	}

	@Override
	public void setValue(String value) {
		values.clear();
		addValue(value);
	}

	@Override
	public void merge(ModReporter reporter, Property property) {
		if ( property instanceof SpecialThanksProperty ) {
			values.addAll(((SpecialThanksProperty) property).values);
		} else if ( property instanceof StringProperty ) {
			addValue(property.getValue());
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge StringProperty with " + property.getClass().getSimpleName()));
		}
	}
}
