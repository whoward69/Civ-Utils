package me.civ5.modtools.mod.modinfo.properties;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

public class TrueBiasedProperty extends BooleanProperty {
	public TrueBiasedProperty(String name) {
		super(name, false);
	}

	@Override
	public int getValueAsInt() {
		try {
			return Integer.parseInt(getValue());
		} catch (NumberFormatException e) {
			return 1;
		}
	}
	
	@Override
	// Merge the two properties, favouring true values
	public void merge(ModReporter reporter, Property property) {
		if ( property instanceof BooleanProperty ) {
			setValue(isTrue() || ((BooleanProperty) property).isTrue());
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge TrueBiasedProperty with " + property.getClass().getSimpleName()));
		}
	}
}
