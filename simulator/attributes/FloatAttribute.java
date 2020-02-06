package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;

public class FloatAttribute implements SimulatorAttribute {
	public float value;
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		value = stream.readFloat();
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writeFloat(value);
	}

	@Override
	public int getSize() {
		return 4;
	}
	
	@Override
	public String toString(String tabulation) {
		return HashManager.get().floatToString(value);
	}

	@Override
	public String toXmlString(String tabulation) {
		return toString(tabulation);
	}
}
