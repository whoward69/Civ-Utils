package me.civ5.modtools.mod.modinfo.properties;

import java.util.HashSet;
import java.util.Set;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogWarn;
import me.civ5.modutils.log.ModReporter;

public class StringProperty extends Property {
	private Set<String> alternateValues = new HashSet<String>();
	
	public StringProperty(String name) {
		super(name);
	}

	public StringProperty(String name, String value) {
		this(name);
		setValue(value);
	}
	
	@Override
	public boolean verify(ModReporter reporter) {
		for (String altValue : alternateValues) {
			reporter.log(new LogWarn("Property " + getName() + " has an alternate value of " + altValue));
		}
		
		return (alternateValues.size() == 0);
	}

	@Override
	public void merge(ModReporter reporter, Property property) {
		if ( property instanceof StringProperty ) {
			if ( getValue().equals(property.getValue()) ) {
				// The values are the same, so we can safely ignore this property
			} else {
				// This assumes that we never merge A into B and X into Y and then B into Y
				alternateValues.add(property.getValue());
			}
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge StringProperty with " + property.getClass().getSimpleName()));
		}
	}
}
