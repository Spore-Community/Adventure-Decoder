package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;

public class Int64Attribute implements SimulatorAttribute {
	public long value;
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		value = stream.readLong();
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writeLong(value);
	}

	@Override
	public int getSize() {
		return 8;
	}
	
	@Override
	public String toString(String tabulation) {
		return HashManager.get().formatInt64(value);
	}

	@Override
	public String toXmlString(String tabulation) {
		return toString(tabulation);
	}
}
