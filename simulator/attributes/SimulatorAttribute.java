package sporemodder.file.simulator.attributes;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;

public interface SimulatorAttribute {

	public void read(StreamReader stream, int size) throws Exception;
	public void write(StreamWriter stream) throws Exception;
	public int getSize();
	
	public String toString(String tabulation);
	public String toXmlString(String tabulation);
	
	
}
