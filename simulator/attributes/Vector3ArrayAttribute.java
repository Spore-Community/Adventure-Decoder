package sporemodder.file.simulator.attributes;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.util.Vector3;

public class Vector3ArrayAttribute implements SimulatorAttribute {
	public Vector3[] value;
	
	@Override
	public void read(StreamReader stream, int size) throws IOException {
		value = new Vector3[stream.readInt()];
		
		for (int i = 0; i < value.length; i++) {
			Vector3 object = new Vector3();
			object.readBE(stream);
			value[i] = object;
		}
	}

	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writeInt(value.length);
		for (Vector3 object : value) {
			object.writeBE(stream);
		}
	}

	@Override
	public int getSize() {
		return 4 + 12 * value.length;
	}
	
	@Override
	public String toString(String tabulation) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < value.length; i++) {
			sb.append(value[i].toString());
			
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
			sb.append(value[i].toString());
			
			if (i + 1 < value.length) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}