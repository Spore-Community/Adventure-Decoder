package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;

public class BooleanArrayAttribute implements SimulatorAttribute {
	public boolean[] value;
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		value = new boolean[stream.readInt()];
		stream.readBooleans(value);
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writeInt(value.length);
		stream.writeBooleans(value);
	}

	@Override
	public int getSize() {
		return 4 + value.length;
	}
	
	@Override
	public String toString(String tabulation) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < value.length; i++) {
			sb.append(Boolean.toString(value[i]));
			
			if (i + 1 < value.length) {
				sb.append(", ");
			}
		}
		sb.append(']');
		
		return sb.toString();
	}

	@Override
	public String toXmlString(String tabulation) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length; i++) {
			sb.append(Boolean.toString(value[i]));
			
			if (i + 1 < value.length) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}