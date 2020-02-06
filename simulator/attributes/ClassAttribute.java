package sporemodder.file.simulator.attributes;

import java.lang.reflect.Constructor;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.file.simulator.SimulatorClass;

public class ClassAttribute<T extends SimulatorClass> implements SimulatorAttribute {
	private final Class<T> clazz;
	public T value;
	
	public ClassAttribute(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public void read(StreamReader stream, int size) throws Exception {
		Constructor<T> ctor = clazz.getConstructor();
		value = ctor.newInstance();
		
		value.read(stream);
	}

	@Override
	public void write(StreamWriter stream) throws Exception {
		value.write(stream);
	}

	@Override
	public int getSize() {
		return value.calculateSize();
	}
	
	@Override
	public String toString(String tabulation) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		
		value.print(sb, tabulation + "\t");
		
		sb.append(tabulation);
		sb.append('}');
		
		return sb.toString();
	}

	@Override
	public String toXmlString(String tabulation) {
		StringBuilder sb = new StringBuilder();
		sb.append('\n');
		value.printXML(sb, tabulation + "\t");
		sb.append('\n');
		sb.append(tabulation);
		return sb.toString();
	}
}
