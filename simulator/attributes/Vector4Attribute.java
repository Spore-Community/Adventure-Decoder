package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.util.Vector4;

public class Vector4Attribute implements SimulatorAttribute {
	public final Vector4 value = new Vector4();
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		value.readBE(stream);
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		value.writeBE(stream);
	}

	@Override
	public int getSize() {
		return 4 * 4;
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