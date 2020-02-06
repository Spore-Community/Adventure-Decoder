package sporemodder.file.prop;

import java.io.IOException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.file.argscript.ArgScriptArguments;
import sporemodder.file.argscript.ArgScriptParser;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;

public class PropertyUnknown extends BaseProperty {
	
	public static final int TYPE_CODE = 0x0000;
	public static final String KEYWORD = "unknown";
	
	public PropertyUnknown() {
		super(TYPE_CODE, 0);
	}
	
	@Override
	public void read(StreamReader stream, int itemCount) throws IOException {
		stream.skip(16);
	}
	
	@Override
	public void write(StreamWriter stream) throws IOException {
		stream.writePadding(16);
	}

	@Override
	public void writeArgScript(String propertyName, ArgScriptWriter writer) {
		writer.command(KEYWORD).arguments(propertyName);
	}
	
	public static void addParser(ArgScriptStream<PropertyList> stream) {
		final ArgScriptArguments args = new ArgScriptArguments();
		
		stream.addParser(KEYWORD, ArgScriptParser.create((parser, line) -> {
			if (line.getArguments(args, 1)) {
				parser.getData().add(args.get(0), new PropertyUnknown());
			}
		}));
		
	}
}
