package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.BooleanAttribute;
import sporemodder.file.simulator.attributes.ClassAttribute;
import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.Int64Attribute;
import sporemodder.file.simulator.attributes.ResourceKeyAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;

public class cScenarioPosseMember extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioPosseMember() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "mAsset":	return new ClassAttribute<>(cScenarioAsset.class);
		case "isLocked":	return new BooleanAttribute();
		case "assetKeyDEPRECATED":	return new ResourceKeyAttribute();
		case "serverIDDEPRECATED":	return new Int64Attribute();
		default:
			return new DefaultAttribute();
		}
	}
}
