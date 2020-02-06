package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.BooleanArrayAttribute;
import sporemodder.file.simulator.attributes.ClassArrayAttribute;
import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.FloatArrayAttribute;
import sporemodder.file.simulator.attributes.FloatAttribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;
import sporemodder.file.simulator.attributes.Vector3Attribute;
import sporemodder.file.simulator.attributes.Vector4Attribute;

public class cScenarioMarker extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioMarker() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "classIndex":	return new IntAttribute();
		case "position":	return new Vector3Attribute();
		case "orientation":	return new Vector4Attribute();
		case "elevation":	return new FloatAttribute();
		case "mFlags":	return new BooleanArrayAttribute();
		case "scale":	return new FloatAttribute();
		case "relativeScaleToDefault":	return new FloatAttribute();
		case "pitch":	return new FloatAttribute();
		case "gain":	return new FloatAttribute();
		case "teleportPosition":	return new Vector3Attribute();
		case "teleportOrientation":	return new Vector4Attribute();
		case "teleportScale":	return new FloatAttribute();
		case "oldDistances":	return new FloatArrayAttribute();
		case "acts":	return new ClassArrayAttribute<>(cScenarioMarkerAct.class);
		default:
			return new DefaultAttribute();
		}
	}
}
