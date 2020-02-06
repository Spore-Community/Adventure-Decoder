package sporemodder.file.prop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.file.argscript.ArgScriptArguments;
import sporemodder.file.argscript.ArgScriptLine;
import sporemodder.file.argscript.ArgScriptParser;
import sporemodder.file.argscript.ArgScriptSpecialBlock;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;

public class PropertyWChar extends BaseProperty {
	
	public static final int TYPE_CODE = 0x0003;
	public static final String KEYWORD = "wchar";
	public static final int ARRAY_SIZE = 2;

	private char[] values;
	
	public PropertyWChar() {
		super(TYPE_CODE, 0);
	}
	
	public PropertyWChar(char value) {
		super(TYPE_CODE, 0);
		this.values = new char[] {value};
	}
	
	public PropertyWChar(char ... values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.length);
		this.values = values;
	}
	
	public PropertyWChar(List<Character> values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.size());
		this.values = new char[values.size()];
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = values.get(i);
		}
	}
	
	public char[] getValues() {
		return values;
	}
	
	@Override
	public void read(StreamReader stream, int itemCount) throws IOException {
		values = new char[itemCount];
		for (int i = 0; i < itemCount; i++) {
			values[i] = Character.toChars(stream.readUShort())[0];
		}
	}
	
	@Override
	public void write(StreamWriter stream) throws IOException {
		for (char value : values) {
			stream.writeUShort(Character.getNumericValue(value));
		}
	}
	
	public static void fastConvertXML(StreamWriter stream, Attributes attributes, String text) throws IOException {
		stream.writeShort((int)text.charAt(0));
	}
	
	@Override
	public void writeArgScript(String propertyName, ArgScriptWriter writer) {
		if (isArray) {
			writer.command(KEYWORD + "s").arguments(propertyName);
			writer.startBlock();
			for (char value : values) {
				writer.indentNewline();
				writer.arguments(Character.toString(value));
			}
			writer.endBlock();
			writer.commandEND();
		} 
		else {
			writer.command(KEYWORD).arguments(propertyName, Character.toString(values[0]));
		}
	}
	
	public static void addParser(ArgScriptStream<PropertyList> stream) {
		final ArgScriptArguments args = new ArgScriptArguments();
		
		stream.addParser(KEYWORD, ArgScriptParser.create((parser, line) -> {
			if (line.getArguments(args, 2)) {
				parser.getData().add(args.get(0), new PropertyWChar(args.get(1).charAt(0)));
			}
		}));
		
		stream.addParser(KEYWORD + "s", new ArgScriptSpecialBlock<PropertyList>() {
			String propertyName;
			final ArrayList<Character> values = new ArrayList<Character>();
			
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
				values.add(line.trim().charAt(0));
				
				return true;
			}
			
			@Override
			public void onBlockEnd() {
				stream.getData().add(propertyName, new PropertyWChar(values));
				stream.endSpecialBlock();
			}
		});
	}
}
