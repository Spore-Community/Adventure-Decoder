package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;

public class IntAttribute implements SimulatorAttribute {
	public int value;
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		value = stream.readInt();
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writeInt(value);
	}

	@Override
	public int getSize() {
		return 4;
	}
	
	@Override
	public String toString(String tabulation) {
		return HashManager.get().formatInt32(value);
	}

	@Override
	public String toXmlString(String tabulation) {
		return toString(tabulation);
	}
}
