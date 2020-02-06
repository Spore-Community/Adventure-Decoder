package sporemodder.file.prop;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.file.argscript.ArgScriptWriter;

abstract class BaseProperty {
	protected boolean isArray;
	protected int arrayItemSize;
	protected int arrayItemCount;
	protected int flags;
	protected int type;
	
	protected BaseProperty(int type, int flags) {
		this.isArray = false;
		this.arrayItemSize = 0;
		this.arrayItemCount = 0;
		this.flags = flags;
		this.type = type;
	}
	
	protected BaseProperty(int type, int flags, int arrayItemSize, int arrayItemCount) {
		this.isArray = true;
		this.arrayItemSize = arrayItemSize;
		this.arrayItemCount = arrayItemCount;
		this.flags = flags | 0x30;  // Ensure it is an array
		this.type = type;
	}
	
	public abstract void read(StreamReader stream, int itemCount) throws IOException;
	public abstract void write(StreamWriter stream) throws IOException;
	
	public abstract void writeArgScript(String propertyName, ArgScriptWriter writer);
}
