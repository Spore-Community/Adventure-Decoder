package sporemodder.file.simulator.attributes;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import emord.filestructures.Stream.StringEncoding;

public class StringAttribute implements SimulatorAttribute {
	public String value;
	
	@Override
	public void read(StreamReader stream, int size) throws Exception {
		value = stream.readString(StringEncoding.UTF16BE, stream.readInt());
	}

	@Override
	public void write(StreamWriter stream) throws Exception {
		stream.writeInt(value.length());
		stream.writeString(value, StringEncoding.UTF16BE);
	}

	@Override
	public int getSize() {
		return 4 + 2*value.length();
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
