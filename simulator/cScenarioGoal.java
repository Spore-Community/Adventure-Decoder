package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.BooleanArrayAttribute;
import sporemodder.file.simulator.attributes.BooleanAttribute;
import sporemodder.file.simulator.attributes.ClassArrayAttribute;
import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;

public class cScenarioGoal extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioGoal() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "type":	return new IntAttribute();
		case "targetClassIndex":	return new IntAttribute();
		case "targetClass2Index":	return new IntAttribute();
		case "requiredCount":	return new IntAttribute();
		case "visibility":	return new BooleanAttribute();
		case "dialog":	return new ClassArrayAttribute<>(cScenarioDialog.class);
		case "flags":	return new BooleanArrayAttribute();
		default:
			return new DefaultAttribute();
		}
	}
}
