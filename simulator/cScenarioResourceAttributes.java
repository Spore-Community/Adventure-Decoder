package sporemodder.file.simulator;

import sporemodder.file.simulator.attributes.BooleanAttribute;
import sporemodder.file.simulator.attributes.ClassArrayAttribute;
import sporemodder.file.simulator.attributes.ClassAttribute;
import sporemodder.file.simulator.attributes.DefaultAttribute;
import sporemodder.file.simulator.attributes.FloatAttribute;
import sporemodder.file.simulator.attributes.Int64Attribute;
import sporemodder.file.simulator.attributes.IntArrayAttribute;
import sporemodder.file.simulator.attributes.IntAttribute;
import sporemodder.file.simulator.attributes.ResourceKeyArrayAttribute;
import sporemodder.file.simulator.attributes.ResourceKeyAttribute;
import sporemodder.file.simulator.attributes.SimulatorAttribute;
import sporemodder.file.simulator.attributes.Vector3Attribute;
import sporemodder.file.simulator.attributes.Vector4Attribute;

public class cScenarioResourceAttributes extends SimulatorClass {
	
	public static final int CLASS_ID = 0x01A80D26;
	
	public cScenarioResourceAttributes() {
		super(CLASS_ID);
	}
	
	@Override public SimulatorAttribute createAttribute(String name) {
		switch (name) {
		case "avatarPosition":	return new Vector3Attribute();
		case "avatarHealthMultiplier":	return new FloatAttribute();
		case "avatarIsInvulnerable":	return new BooleanAttribute();
		case "avatarOrientation":	return new Vector4Attribute();
		case "avatarScale":	return new FloatAttribute();
		case "bAvatarLocked":	return new BooleanAttribute();
		case "initialPosseMembers":	return new ClassArrayAttribute<>(cScenarioPosseMember.class);
		case "numAllowedPosseMembers":	return new IntAttribute();
		case "classes":	return new ClassArrayAttribute<>(cScenarioClass.class, true);  // it's indexed
		case "acts":	return new ClassArrayAttribute<>(cScenarioAct.class);
		case "markers":	return new ClassArrayAttribute<>(cScenarioMarker.class);
		case "winText":	return new ClassAttribute<>(cScenarioString.class);
		case "loseText":	return new ClassAttribute<>(cScenarioString.class);
		case "introText":	return new ClassAttribute<>(cScenarioString.class);
		case "type":	return new IntAttribute();
		case "bIsMission":	return new BooleanAttribute();
		case "classIDCounter":	return new IntAttribute();
		case "mScreenshotTypes":	return new IntArrayAttribute();
		case "mAvatarAsset":	return new ClassAttribute<>(cScenarioAsset.class);
		case "markerPositioningVersion":	return new IntAttribute();
		case "usedAppPackIds":	return new IntArrayAttribute();
		case "cameraTarget":	return new Vector3Attribute();
		case "cameraOrientation":	return new Vector4Attribute();
		case "cameraDistance":	return new FloatAttribute();
		case "avatarAssetKeyDEPRECATED":	return new ResourceKeyAttribute();
		case "avatarServerIDDEPRECATED":	return new Int64Attribute();
		case "atmosphereScoreDEPRECATED":	return new FloatAttribute();
		case "temperatureScoreDEPRECATED":	return new FloatAttribute();
		//TODO classesOld (or not)
		case "initialPosseMemberKeysDEPRECATED":	return new ResourceKeyArrayAttribute();
		case "waterScoreDEPRECATED":	return new FloatAttribute();
		case "mbIsTimeLockedDEPRECATED":	return new BooleanAttribute();
		case "mTimeElapsedDEPRECATED":	return new FloatAttribute();
		case "mbCustomScreenshotThumbnailDEPRECATED":	return new BooleanAttribute();
		
		default:
			return new DefaultAttribute();
		}
	}
}
