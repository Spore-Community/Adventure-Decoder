package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;

public class cBehaviorEntry extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cBehaviorEntry() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "condition.conditionType":	return new IntAttribute();
		case "condition.target.type":	return new IntAttribute();
		case "condition.target.value":	return new IntAttribute();
		case "action.actionType":	return new IntAttribute();
		case "action.target.type":	return new IntAttribute();
		case "action.target.value":	return new IntAttribute();
		case "action.secondaryTarget.type":	return new IntAttribute();
		case "action.secondaryTarget.value":	return new IntAttribute();
		default:
			return new DefaultAttribute();
		}
	}
}
