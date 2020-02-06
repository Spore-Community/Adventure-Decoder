package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.BooleanAttribute;
import sporemodder.file.simulator.attributes.ClassArrayAttribute;
import sporemodder.file.simulator.attributes.ClassAttribute;
import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.Int64Attribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.ResourceKeyAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;

public class cScenarioClass extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioClass() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "acts":	return new ClassArrayAttribute<>(cScenarioClassAct.class);
		case "castName":	return new ClassAttribute<>(cScenarioString.class);
		case "mAsset":	return new ClassAttribute<>(cScenarioAsset.class);
		case "gameplayObjectGfxOverrideAsset":	return new ClassAttribute<>(cScenarioAsset.class);
		case "gameplayObjectGfxOverrideAsset_Secondary":	return new ClassAttribute<>(cScenarioAsset.class);
		case "gfxOverrideType":	return new IntAttribute();
		case "gfxOverrideTypeSecondary":	return new IntAttribute();
		
		case "isDeepEditEnabled":	return new BooleanAttribute();
		case "modelTypeDEPRECATED":	return new IntAttribute();
		case "serverIDDEPRECATED":	return new Int64Attribute();
		case "assetKeyDEPRECATED":	return new ResourceKeyAttribute();
		default:
			return new DefaultAttribute();
		}
	}
}
