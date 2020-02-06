package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;

public class DefaultAttribute implements SimulatorAttribute {
	public byte[] data;
	public long position;
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		data = new byte[size];
		position = stream.getFilePointer();
		stream.read(data);
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		if (data != null) {
			stream.write(data);
		}
	}
	
	@Override
	public String toString(String tabulation) {
		return "DEFAULT IMPLEMENTATION (" + data.length + ", " + position + ")";
	}

	@Override
	public int getSize() {
		return data == null ? 0 : data.length;
	}

	@Override
	public String toXmlString(String tabulation) {
		return toString(tabulation);
	}
}
