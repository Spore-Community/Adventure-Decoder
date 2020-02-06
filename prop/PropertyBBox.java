package sporemodder.file.prop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.file.BoundingBox;
import sporemodder.file.argscript.ArgScriptArguments;
import sporemodder.file.argscript.ArgScriptLine;
import sporemodder.file.argscript.ArgScriptParser;
import sporemodder.file.argscript.ArgScriptSpecialBlock;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.util.Vector3;

public class PropertyBBox extends BaseProperty {
	
	public static final int TYPE_CODE = 0x0039;
	public static final String KEYWORD = "bbox";
	public static final int ARRAY_SIZE = 24;

	private BoundingBox[] values;
	
	public PropertyBBox() {
		super(TYPE_CODE, 0);
	}
	
	public PropertyBBox(BoundingBox value) {
		// BoundingBox properties are only supported as arrays
		this(new BoundingBox[] {value});
	}
	
	public PropertyBBox(BoundingBox ... values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.length);
		this.values = values;
	}
	
	public PropertyBBox(List<BoundingBox> values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.size());
		this.values = values.toArray(new BoundingBox[values.size()]);
	}
	
	public BoundingBox[] getValues() {
		return values;
	}
	
	@Override
	public void read(StreamReader stream, int itemCount) throws IOException {
		values = new BoundingBox[itemCount];
		for (int i = 0; i < itemCount; i++) {
			BoundingBox value = new BoundingBox();
			value.read(stream);
			values[i] = value;
		}
	}
	
	@Override
	public void write(StreamWriter stream) throws IOException {
		for (BoundingBox value : values) {
			value.write(stream);
		}
	}
	
	@Override
	public void writeArgScript(String propertyName, ArgScriptWriter writer) {
		if (isArray) {
			writer.command(KEYWORD + "s").arguments(propertyName);
			writer.startBlock();
			for (BoundingBox value : values) {
				writer.indentNewline();
				writer.arguments(value.getMin().toString(), value.getMax().toString());
			}
			writer.endBlock();
			writer.commandEND();
		} 
		else {
			writer.command(KEYWORD).arguments(propertyName, values[0].getMin().toString(), values[0].getMax().toString());
		}
	}
	
	public static void addParser(ArgScriptStream<PropertyList> stream) {
		stream.addParser(KEYWORD, ArgScriptParser.create((parser, line) -> {
			line.createError("BoundingBox properties are only available in array format.");
		}));
		
		stream.addParser(KEYWORD + "s", new ArgScriptSpecialBlock<PropertyList>() {
			String propertyName;
			final ArrayList<BoundingBox> values = new ArrayList<BoundingBox>();
			final ArgScriptLine line = new ArgScriptLine(stream);
			final ArgScriptArguments args = new ArgScriptArguments();
			
			@Override
			public void parse(ArgScriptLine line) {
				values.clear();
				stream.startSpecialBlock(this, "end");
				
				if (line.getArguments(args, 1)) {
					propertyName = args.get(0);
				}
			}
			
			@Override
			public boolean processLine(String text) {
				line.fromLine(text, null);
				line.getSplitsAsArguments(args);
				float[] min = new float[3];
				float[] max = new float[3];
				
				stream.parseVector3(args, 0, min);
				stream.parseVector3(args, 1, max);
				values.add(new BoundingBox(new Vector3(min), new Vector3(max)));
				
				return true;
			}
			
			@Override
			public void onBlockEnd() {
				stream.getData().add(propertyName, new PropertyBBox(values));
				stream.endSpecialBlock();
			}
		});
	}
}
