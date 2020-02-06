package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;

public class BooleanAttribute implements SimulatorAttribute {
	public boolean value;
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		value = stream.readBoolean();
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writeBoolean(value);
	}

	@Override
	public int getSize() {
		return 1;
	}
	
	@Override
	public String toString(String tabulation) {
		return Boolean.toString(value);
	}

	@Override
	public String toXmlString(String tabulation) {
		return toString(tabulation);
	}
}