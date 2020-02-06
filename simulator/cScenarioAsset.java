package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.Int64Attribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.ResourceKeyAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;

public class cScenarioAsset extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioAsset() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "mKey":	return new ResourceKeyAttribute();
		case "mMachineId":	return new IntAttribute();
		case "mServerId":	return new Int64Attribute();
		case "mModelType":	return new IntAttribute();
		default:
			return new DefaultAttribute();
		}
	}
}
