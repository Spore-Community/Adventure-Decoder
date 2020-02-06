package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.ClassAttribute;
import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;

public class cScenarioDialog extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioDialog() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "bubbleType":	return new IntAttribute();
		case "text":	return new ClassAttribute<cScenarioString>(cScenarioString.class);
		case "animType":	return new IntAttribute();
		default:
			return new DefaultAttribute();
		}
	}
}
