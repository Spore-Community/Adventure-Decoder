package sporemodder.file.prop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;
import sporemodder.file.ResourceKey;
import sporemodder.file.argscript.ArgScriptArguments;
import sporemodder.file.argscript.ArgScriptLine;
import sporemodder.file.argscript.ArgScriptParser;
import sporemodder.file.argscript.ArgScriptSpecialBlock;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;

public class PropertyKey extends BaseProperty {
	
	public static final int TYPE_CODE = 0x0020;
	public static final String KEYWORD = "key";
	public static final int ARRAY_SIZE = 12;

	private ResourceKey[] values;
	
	public PropertyKey() {
		super(TYPE_CODE, 0);
	}
	
	public PropertyKey(ResourceKey value) {
		super(TYPE_CODE, 0);
		this.values = new ResourceKey[] {value};
	}
	
	public PropertyKey(ResourceKey ... values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.length);
		this.values = values;
	}
	
	public PropertyKey(List<ResourceKey> values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.size());
		this.values = values.toArray(new ResourceKey[values.size()]);
	}
	
	public ResourceKey[] getValues() {
		return values;
	}
	
	@Override
	public void read(StreamReader stream, int itemCount) throws IOException {
		values = new ResourceKey[itemCount];
		
		for (int i = 0; i < itemCount; i++) {
			ResourceKey value = new ResourceKey();
			value.readLE(stream);
			values[i] = value;
			
			if (!isArray) stream.skip(4);
		}
	}
	
	@Override
	public void write(StreamWriter stream) throws IOException {
		for (ResourceKey value : values) {
			value.writeLE(stream);
			
			if (!isArray) stream.writePadding(4);
		}
	}
	
	public static void fastConvertXML(StreamWriter stream, Attributes attributes, String text, boolean bArray) throws IOException {
	
		HashManager hasher = HashManager.get();
		
		int[] values = new int[3];
		
		String str = attributes.getValue("groupid");
		if (str == null) str = attributes.getValue("groupID");
		if (str != null && str.length() > 0) {
			values[0] = hasher.getFileHash(str);
		}
		
		str = attributes.getValue("instanceid");
		if (str == null) str = attributes.getValue("instanceID");
		if (str != null && str.length() > 0) {
			values[1] = hasher.getFileHash(str);
		}
		
		str = attributes.getValue("typeid");
		if (str == null) str = attributes.getValue("typeID");
		if (str != null && str.length() > 0) {
			values[2] = hasher.getTypeHash(str);
		}
		
		stream.writeLEInt(values[1]);
		stream.writeLEInt(values[2]);
		stream.writeLEInt(values[0]);
		if (!bArray) {
			stream.writeLEInt(0);
		}
	}
	
	@Override
	public void writeArgScript(String propertyName, ArgScriptWriter writer) {
		if (isArray) {
			writer.command(KEYWORD + "s").arguments(propertyName);
			writer.startBlock();
			for (ResourceKey value : values) {
				writer.indentNewline();
				writer.arguments(value.toString());
			}
			writer.endBlock();
			writer.commandEND();
		} 
		else {
			writer.command(KEYWORD).arguments(propertyName, values[0].toString());
		}
	}
	
	public static void addParser(ArgScriptStream<PropertyList> stream) {
		final ArgScriptArguments args = new ArgScriptArguments();
		
		stream.addParser(KEYWORD, ArgScriptParser.create((parser, line) -> {
			if (line.getArguments(args, 2)) {
				ResourceKey key = new ResourceKey();
				key.parse(args, 1);
				parser.getData().add(args.get(0), new PropertyKey(key));
			}
		}));
		
		stream.addParser(KEYWORD + "s", new ArgScriptSpecialBlock<PropertyList>() {
			String propertyName;
			final ArrayList<ResourceKey> values = new ArrayList<ResourceKey>();
			
			@Override
			public void parse(ArgScriptLine line) {
				values.clear();
				stream.startSpecialBlock(this, "end");
				
				if (line.getArguments(args, 1)) {
					propertyName = args.get(0);
				}
			}
			
			@Override
			public boolean processLine(String line) {
				ResourceKey key = new ResourceKey();
				key.parse(line.trim());
				values.add(key);
				return true;
			}
			
			@Override
			public void onBlockEnd() {
				stream.getData().add(propertyName, new PropertyKey(values));
				stream.endSpecialBlock();
			}
		});
	}
}
