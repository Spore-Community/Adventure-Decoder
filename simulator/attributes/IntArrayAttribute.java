package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;

public class IntArrayAttribute implements SimulatorAttribute {
	public int[] value;
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		value = new int[stream.readInt()];
		stream.readInts(value);
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writeInt(value.length);
		stream.writeInts(value);
	}

	@Override
	public int getSize() {
		return 4 + 4 * value.length;
	}
	
	@Override
	public String toString(String tabulation) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < value.length; i++) {
			sb.append(HashManager.get().formatInt32(value[i]));
			
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
			sb.append(HashManager.get().formatInt32(value[i]));
			
			if (i + 1 < value.length) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}