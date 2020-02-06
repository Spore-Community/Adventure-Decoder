package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.BooleanAttribute;
import sporemodder.file.simulator.attributes.ClassArrayAttribute;
import sporemodder.file.simulator.attributes.ClassAttribute;
import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;

public class cScenarioAct extends SimulatorClass {
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioAct() {
		super(CLASS_ID);
	}

	@Override public SimulatorAttribute createAttribute(String name) {
		switch(name) {
		case "name":	return new ClassAttribute<>(cScenarioString.class);
		case "description":	return new ClassAttribute<>(cScenarioString.class);
		case "timeLimitSecs":	return new IntAttribute();
		case "bTimeVisible":	return new BooleanAttribute();
		case "actMusicId":	return new IntAttribute();
		case "goals":	return new ClassArrayAttribute<>(cScenarioGoal.class);
		default:
			return new DefaultAttribute();
		}
	}
}
