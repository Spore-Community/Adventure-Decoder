package sporemodder.file.prop;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;

/**
 * The class that represents a PROP file, which is just a map of properties and their IDs.
 */
public class PropertyList {

	private static final Comparator<Integer> DESCENDING_COMPARATOR = new Comparator<Integer>() {
		@Override
        public int compare(Integer o1, Integer o2) {
			return Integer.compareUnsigned(o1, o2);
        }
    };
    
	/** This maps each property to its ID. It's a TreeMap, meaning that the entries will be ordered
	 * according to the ID. We don't respect insertion order because Spore requires the properties to be ordered.
	 * We use long because we don't want the hashes to be signed.
	 */
	private final TreeMap<Integer, BaseProperty> properties = new TreeMap<Integer, BaseProperty>(DESCENDING_COMPARATOR);
	
	public void read(StreamReader stream) throws IOException {

		int count = stream.readInt();
		
		for (int i = 0; i < count; i++) {
			int id = stream.readInt();
			int type = stream.readShort();
			int flags = stream.readShort();
			
			BaseProperty property = create(type);
			property.flags = flags;
			
			if ((flags & 0x30) == 0) {
				property.isArray = false;
				property.read(stream, 1);
			} 
			else if ((flags & 0x40) == 0) {
				// Array property
				property.arrayItemCount = stream.readInt();
				property.arrayItemSize = stream.readInt();
				property.isArray = true;
				property.read(stream, property.arrayItemCount);
			}
			// The else case should never happen
			
			properties.put(id, property);
		}
	}
	
	public void write(StreamWriter stream) throws IOException {
		
		stream.writeInt(properties.size());
		
		for (Map.Entry<Integer, BaseProperty> entry : properties.entrySet()) {
			
			BaseProperty property = entry.getValue();
			stream.writeInt(entry.getKey().intValue());
			stream.writeShort(property == null ? 0 : property.type);
			stream.writeShort(property == null ? 0 : property.flags);
			
			if (property == null) {
				stream.writePadding(16);
			}
			else {
				if (property.isArray) {
					stream.writeInt(property.arrayItemCount);
					stream.writeInt(property.arrayItemSize);
				}
				
				property.write(stream);
			}
		}
	}
	
	public String toArgScript() {
		ArgScriptWriter writer = new ArgScriptWriter();
		HashManager hasher = HashManager.get();
		
		for (Map.Entry<Integer, BaseProperty> entry : properties.entrySet()) {
			
			if (entry.getValue() != null) {
				entry.getValue().writeArgScript(hasher.getPropName(entry.getKey()), writer);
			}
		}
		
		return writer.toString();
	}
	
	public BaseProperty get(int id) {
		return properties.get(id);
	}
	
	public BaseProperty get(String name) {
		return properties.get(HashManager.get().getPropHash(name));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BaseProperty> T get(int id, Class<T> type) {
		
		BaseProperty property = properties.get(id);
		if (property == null || property.getClass() != type) return null;
		
		return (T) property;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BaseProperty> T getOrAdd(int id, T defaultValue) {
		
		BaseProperty property = properties.get(id);
		if (property == null) {
			properties.put(id, defaultValue);
			return defaultValue;
		}
		else if (property.getClass() != defaultValue.getClass()) {
			return null;
		}
		else {
			return (T) property;
		}
	}
	
	public <T extends BaseProperty> T get(String name, Class<T> type) {
		return get(HashManager.get().getPropHash(name), type);
	}
	
	public <T extends BaseProperty> T getOrAdd(String name, T defaultValue) {
		return getOrAdd(HashManager.get().getPropHash(name), defaultValue);
	}
	
	public void add(int id, BaseProperty property) {
		properties.put(id, property);
	}
	
	public void add(String name, BaseProperty property) {
		properties.put(HashManager.get().getPropHash(name), property);
	}
	
	public void remove(int id) {
		properties.remove(id);
	}
	
	public void remove(String name) {
		properties.remove(HashManager.get().getPropHash(name));
	}
	
	public TreeMap<Integer, BaseProperty> getProperties() {
		return properties;
	}
	
	public void addList(PropertyList other) {
		properties.putAll(other.properties);
	}
	
	public void addListProperties(PropertyList other, String ... names) {
		HashManager hasher = HashManager.get();
		
		for (String name : names) {
			int hash = hasher.getPropHash(name);
			BaseProperty property = other.properties.get(hash);
			if (property != null) {
				properties.put(hash, property);
			}
		}
	}
	
	private static BaseProperty create(int type) {
		switch (type) {
		case PropertyUnknown.TYPE_CODE: return new PropertyUnknown();
		case PropertyBool.TYPE_CODE: return new PropertyBool();
		case PropertyChar.TYPE_CODE: return new PropertyChar();
		case PropertyWChar.TYPE_CODE: return new PropertyWChar();
		case PropertyInt8.TYPE_CODE: return new PropertyInt8();
		case PropertyUInt8.TYPE_CODE: return new PropertyUInt8();
		case PropertyInt16.TYPE_CODE: return new PropertyInt16();
		case PropertyUInt16.TYPE_CODE: return new PropertyUInt16();
		case PropertyInt32.TYPE_CODE: return new PropertyInt32();
		case PropertyUInt32.TYPE_CODE: return new PropertyUInt32();
		case PropertyInt64.TYPE_CODE: return new PropertyInt64();
		case PropertyUInt64.TYPE_CODE: return new PropertyUInt64();
		case PropertyFloat.TYPE_CODE: return new PropertyFloat();
		case PropertyDouble.TYPE_CODE: return new PropertyDouble();
		// 0x0F: tPtrType
		// 0x10: tVoidType
		// 0x11: tIUnknownRCType
		case PropertyString8.TYPE_CODE: return new PropertyString8();
		case PropertyString16.TYPE_CODE: return new PropertyString16();
		case PropertyKey.TYPE_CODE: return new PropertyKey();
		// 0x21: tFlags
		case PropertyText.TYPE_CODE: return new PropertyText();
		case PropertyVector2.TYPE_CODE: return new PropertyVector2();
		case PropertyVector3.TYPE_CODE: return new PropertyVector3();
		case PropertyColorRGB.TYPE_CODE: return new PropertyColorRGB();
		case PropertyVector4.TYPE_CODE: return new PropertyVector4();
		case PropertyColorRGBA.TYPE_CODE: return new PropertyColorRGBA();
		// 0x35: tmatrix2Type
		// 0x36: tmatrix3Type
		// 0x37: tmatrix4Type
		case PropertyTransform.TYPE_CODE: return new PropertyTransform();
		case PropertyBBox.TYPE_CODE: return new PropertyBBox();
		default: return null;
		}
	}
	
	private static void addStreamParsers(ArgScriptStream<PropertyList> stream) {
		PropertyUnknown.addParser(stream);
		PropertyBool.addParser(stream);
		PropertyChar.addParser(stream);
		PropertyWChar.addParser(stream);
		PropertyInt8.addParser(stream);
		PropertyUInt8.addParser(stream);
		PropertyInt16.addParser(stream);
		PropertyUInt16.addParser(stream);
		PropertyInt32.addParser(stream);
		PropertyUInt32.addParser(stream);
		PropertyInt64.addParser(stream);
		PropertyUInt64.addParser(stream);
		PropertyFloat.addParser(stream);
		PropertyDouble.addParser(stream);
		PropertyString8.addParser(stream);
		PropertyString16.addParser(stream);
		PropertyKey.addParser(stream);
		PropertyText.addParser(stream);
		PropertyVector2.addParser(stream);
		PropertyVector3.addParser(stream);
		PropertyColorRGB.addParser(stream);
		PropertyVector4.addParser(stream);
		PropertyColorRGBA.addParser(stream);
		PropertyTransform.addParser(stream);
		PropertyBBox.addParser(stream);
	}
	
	/**
	 * Generates the ArgScript stream used to parse PROP files.
	 * @return
	 */
	public ArgScriptStream<PropertyList> generateStream() {

		ArgScriptStream<PropertyList> stream = new ArgScriptStream<PropertyList>();
		stream.setData(this);
		stream.addDefaultParsers();
		
		addStreamParsers(stream);
		
		return stream;
	}
}
