package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.file.ResourceKey;

public class ResourceKeyAttribute implements SimulatorAttribute {
	public final ResourceKey value = new ResourceKey();
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		// Uses different order
		value.setGroupID(stream.readInt());
		value.setTypeID(stream.readInt());
		value.setInstanceID(stream.readInt());
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		// Uses different order
		stream.writeInt(value.getGroupID());
		stream.writeInt(value.getTypeID());
		stream.writeInt(value.getInstanceID());
	}

	@Override
	public int getSize() {
		return 3 * 4;
	}
	
	@Override
	public String toString(String tabulation) {
		return value.toString();
	}

	@Override
	public String toXmlString(String tabulation) {
		return toString(tabulation);
	}
}
