package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;
import sporemodder.file.simulator.attributes.StringAttribute;

public class cScenarioString extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioString() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "mNonLocalizedString":	return new StringAttribute();
		case "mLocalizedStringTableID":	return new IntAttribute();
		case "mLocalizedStringInstanceID":	return new IntAttribute();
		case "mComments":	return new StringAttribute();
		default:
			return new DefaultAttribute();
		}
	}
}
