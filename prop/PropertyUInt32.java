package sporemodder.file.prop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;
import sporemodder.file.DocumentError;
import sporemodder.file.DocumentException;
import sporemodder.file.argscript.ArgScriptArguments;
import sporemodder.file.argscript.ArgScriptLexer;
import sporemodder.file.argscript.ArgScriptLine;
import sporemodder.file.argscript.ArgScriptParser;
import sporemodder.file.argscript.ArgScriptSpecialBlock;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;

public final class PropertyUInt32 extends BaseProperty {
	
	public static final int TYPE_CODE = 0x000A;
	public static final String KEYWORD = "uint32";
	public static final int ARRAY_SIZE = 4;
	
	public static final long MIN_VALUE = 0;
	public static final long MAX_VALUE = (long) Math.pow(2, 32);

	private long[] values;
	
	public PropertyUInt32() {
		super(TYPE_CODE, 0);
	}
	
	public PropertyUInt32(long value) {
		super(TYPE_CODE, 0);
		this.values = new long[] {value};
	}
	
	public PropertyUInt32(long ... values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.length);
		this.values = values;
	}
	
	public PropertyUInt32(List<Long> values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.size());
		this.values = new long[values.size()];
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = values.get(i);
		}
	}
	
	public long[] getValues() {
		return values;
	}
	
	@Override
	public void read(StreamReader stream, int itemCount) throws IOException {
		values = new long[itemCount];
		stream.readUInts(values);
	}
	
	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writeUInts(values);
	}
	
	public static void fastConvertXML(StreamWriter stream, Attributes attributes, String text) throws IOException {
		stream.writeUInt(HashManager.get().uint32(text));
	}
	
	@Override
	public void writeArgScript(String propertyName, ArgScriptWriter writer) {
		if (isArray) {
			writer.command(KEYWORD + "s").arguments(propertyName);
			writer.startBlock();
			HashManager hasher = HashManager.get();
			for (long value : values) {
				writer.indentNewline();
				writer.arguments(hasher.formatUInt32(value));
			}
			writer.endBlock();
			writer.commandEND();
		} 
		else {
			writer.command(KEYWORD).arguments(propertyName, HashManager.get().formatUInt32(values[0]));
		}
	}
	
	public static void addParser(ArgScriptStream<PropertyList> stream) {
		final ArgScriptArguments args = new ArgScriptArguments();
		
		stream.addParser(KEYWORD, ArgScriptParser.create((parser, line) -> {
			Number value = null;
			
			if (line.getArguments(args, 2) && (value = stream.parseUInt(args, 1)) != null) {
				parser.getData().add(args.get(0), new PropertyUInt32(value.longValue()));
			}
		}));
		
		stream.addParser(KEYWORD + "s", new ArgScriptSpecialBlock<PropertyList>() {
			String propertyName;
			final ArrayList<Long> values = new ArrayList<Long>();
			final ArgScriptLexer lexer = new ArgScriptLexer();
			
			@Override
			public void parse(ArgScriptLine line) {
				values.clear();
				stream.startSpecialBlock(this, "end");
				
				// We want the "hash" function, basically
				lexer.removeFunctions();
				lexer.addDefaultFunctions(stream);
				
				if (line.getArguments(args, 1)) {
					propertyName = args.get(0);
				}
			}
			
			@Override
			public boolean processLine(String line) {
				lexer.setText(line.trim());
				
				try {
					long value = lexer.parseInteger();
					
					if (value > MAX_VALUE || value < MIN_VALUE) {
						stream.addError(new DocumentError(String.format("32-bit unsigned integer out of the range (%d, %d).", MIN_VALUE, MAX_VALUE), 0, line.length()));
					}
					else {
						values.add(value);
					}
				}
				catch (DocumentException e) {
					stream.addError(e.getError());
				}
				
				return true;
			}
			
			@Override
			public void onBlockEnd() {
				stream.getData().add(propertyName, new PropertyUInt32(values));
				stream.endSpecialBlock();
			}
		});
	}
}
