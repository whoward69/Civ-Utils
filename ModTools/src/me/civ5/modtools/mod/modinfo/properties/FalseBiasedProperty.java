package me.civ5.modtools.mod.modinfo.properties;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

public class FalseBiasedProperty extends BooleanProperty {
	public FalseBiasedProperty(String name) {
		super(name, true);
	}

	@Override
	public int getValueAsInt() {
		try {
			return Integer.parseInt(getValue());
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	@Override
	// Merge the two properties, favouring false values
	public void merge(ModReporter reporter, Property property) {
		if ( property instanceof BooleanProperty ) {
			setValue(isTrue() && ((BooleanProperty) property).isTrue());
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge FalseBiasedProperty with " + property.getClass().getSimpleName()));
		}
	}
}
