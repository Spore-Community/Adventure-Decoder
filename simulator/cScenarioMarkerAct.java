package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.FloatAttribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;
import sporemodder.file.simulator.attributes.Vector3ArrayAttribute;
import sporemodder.file.simulator.attributes.Vector3Attribute;

public class cScenarioMarkerAct extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioMarkerAct() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "wanderRadius":	return new FloatAttribute();
		case "patrolPositionDEPRECATED":	return new Vector3Attribute();
		case "teleportPositionDEPRECATED":	return new Vector3Attribute();
		case "patrolPath":	return new Vector3ArrayAttribute();
		case "patrolStyle":	return new IntAttribute();
		default:
			return new DefaultAttribute();
		}
	}
}
