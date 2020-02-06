package sporemodder.file.prop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;
import sporemodder.file.DocumentException;
import sporemodder.file.argscript.ArgScriptArguments;
import sporemodder.file.argscript.ArgScriptLexer;
import sporemodder.file.argscript.ArgScriptLine;
import sporemodder.file.argscript.ArgScriptParser;
import sporemodder.file.argscript.ArgScriptSpecialBlock;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;

public class PropertyInt64 extends BaseProperty {
	
	public static final int TYPE_CODE = 0x000B;
	public static final String KEYWORD = "int64";
	public static final int ARRAY_SIZE = 8;
	
	public static final long MIN_VALUE = Long.MIN_VALUE;
	public static final long MAX_VALUE = Long.MAX_VALUE;

	private long[] values;
	
	public PropertyInt64() {
		super(TYPE_CODE, 0);
	}
	
	public PropertyInt64(long value) {
		super(TYPE_CODE, 0);
		this.values = new long[] {value};
	}
	
	public PropertyInt64(long ... values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.length);
		this.values = values;
	}
	
	public PropertyInt64(List<Long> values) {
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
		stream.readLongs(values);
	}
	
	@Override
	public void write(StreamWriter stream) throws IOException {
		for (long value : values) {
			stream.writeLong(value);
		}
	}
	
	public static void fastConvertXML(StreamWriter stream, Attributes attributes, String text) throws IOException {
		stream.writeLong(HashManager.get().int64(text));
	}
	
	@Override
	public void writeArgScript(String propertyName, ArgScriptWriter writer) {
		if (isArray) {
			writer.command(KEYWORD + "s").arguments(propertyName);
			writer.startBlock();
			for (long value : values) {
				writer.indentNewline();
				writer.arguments(Long.toString(value));
			}
			writer.endBlock();
			writer.commandEND();
		} 
		else {
			writer.command(KEYWORD).arguments(propertyName, Long.toString(values[0]));
		}
	}
	
	public static void addParser(ArgScriptStream<PropertyList> stream) {
		final ArgScriptArguments args = new ArgScriptArguments();
		
		stream.addParser(KEYWORD, ArgScriptParser.create((parser, line) -> {
			Number value = null;
			
			if (line.getArguments(args, 2) && (value = stream.parseLong(args, 1)) != null) {
				parser.getData().add(args.get(0), new PropertyInt64(value.longValue()));
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
				
				if (line.getArguments(args, 1)) {
					propertyName = args.get(0);
				}
			}
			
			@Override
			public boolean processLine(String line) {
				lexer.setText(line.trim());
				
				try {
					values.add(lexer.parseInteger());
				}
				catch (DocumentException e) {
					stream.addError(e.getError());
				}
				
				return true;
			}
			
			@Override
			public void onBlockEnd() {
				stream.getData().add(propertyName, new PropertyInt64(values));
				stream.endSpecialBlock();
			}
		});
	}
}
