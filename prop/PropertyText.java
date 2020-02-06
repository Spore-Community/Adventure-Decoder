package sporemodder.file.prop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import emord.filestructures.Stream.StringEncoding;
import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;
import sporemodder.file.DocumentException;
import sporemodder.file.LocalizedText;
import sporemodder.file.argscript.ArgScriptArguments;
import sporemodder.file.argscript.ArgScriptLine;
import sporemodder.file.argscript.ArgScriptParser;
import sporemodder.file.argscript.ArgScriptSpecialBlock;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.file.argscript.WordSplitLexer;
import sporemodder.file.effects.ResourceID;

public class PropertyText extends BaseProperty {
	
	public static final int TYPE_CODE = 0x0022;
	public static final String KEYWORD = "text";
	public static final int ARRAY_SIZE = 520;

	private LocalizedText[] values;
	
	public PropertyText() {
		super(TYPE_CODE, 0);
	}
	
	public PropertyText(LocalizedText value) {
		// Text properties are only supported as arrays
		this(new LocalizedText[] {value});
	}
	
	public PropertyText(LocalizedText ... values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.length);
		this.values = values;
	}
	
	public PropertyText(List<LocalizedText> values) {
		super(TYPE_CODE, 0, ARRAY_SIZE, values.size());
		this.values = values.toArray(new LocalizedText[values.size()]);
	}
	
	public LocalizedText[] getValues() {
		return values;
	}
	
	@Override
	public void read(StreamReader stream, int itemCount) throws IOException {
		values = new LocalizedText[itemCount];
		for (int i = 0; i < itemCount; i++) {
			LocalizedText value = new LocalizedText();
			
			value.setTableID(stream.readLEInt());
			value.setInstanceID(stream.readLEInt());
			
			long position = stream.getFilePointer();
			value.setText(stream.readCString(StringEncoding.UTF16LE));
			stream.seek(position + 512);
			
			values[i] = value;
		}
	}
	
	@Override
	public void write(StreamWriter stream) throws IOException {
		for (LocalizedText value : values) {
			stream.writeLEInt(value.getTableID());
			stream.writeLEInt(value.getInstanceID());
			stream.writeCString(value.getText(), StringEncoding.UTF16LE);
		}
	}
	
	public static void fastConvertXML(StreamWriter stream, Attributes attributes, String text, List<String> autoLocaleStrings, String autoLocaleName) throws IOException {
		int instanceID = 0;
		int tableID = 0;
		
		HashManager hasher = HashManager.get();
		
		String value = attributes.getValue("tableid");
		if (value == null) attributes.getValue("tableID");
		if (value != null && value.length() > 0) tableID = hasher.getFileHash(value);
		
		value = attributes.getValue("instanceid");
		if (value == null) attributes.getValue("instanceID");
		if (value != null && value.length() > 0) instanceID = hasher.getFileHash(value);
		
		// add autolocales if necessary
		if (autoLocaleStrings != null && instanceID == 0 && tableID == 0) {
			
			tableID = hasher.fnvHash(autoLocaleName);
			instanceID = 1 + autoLocaleStrings.size();
			
			autoLocaleStrings.add(text);
		}
		
		stream.writeLEInt(tableID);
		stream.writeLEInt(instanceID);
		byte[] arr = text.getBytes("UTF-16LE");
		stream.write(arr);
		if (arr.length < 512) {
			stream.writePadding(512 - arr.length);
		}
		
	}
	
	@Override
	public void writeArgScript(String propertyName, ArgScriptWriter writer) {if (isArray) {
			writer.command(KEYWORD + "s").arguments(propertyName);
			writer.startBlock();
			for (LocalizedText value : values) {
				writer.indentNewline();
				value.write(writer);
			}
			writer.endBlock();
			writer.command("end");
		} 
		else {
			writer.command(KEYWORD).arguments(propertyName);
			values[0].write(writer);
		}
	}
	
	public static void addParser(ArgScriptStream<PropertyList> stream) {
		final ArgScriptArguments args = new ArgScriptArguments();
		
		stream.addParser(KEYWORD, ArgScriptParser.create((parser, line) -> {
			line.createError("Text properties are only available in array format.");
		}));
		
		stream.addParser(KEYWORD + "s", new ArgScriptSpecialBlock<PropertyList>() {
			String propertyName;
			final ArrayList<LocalizedText> values = new ArrayList<LocalizedText>();
			
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
				try {
					LocalizedText value = new LocalizedText();
					WordSplitLexer lexer = new WordSplitLexer(text);
					
					String word1 = lexer.nextWord();
					String word2 = lexer.nextWord();
					
					if (word2 == null) {
						// No localization
						value.setText(word1);
					}
					else {
						ResourceID resource = new ResourceID();
						resource.parse(word1);
						value.setTableID(resource.getGroupID());
						value.setInstanceID(resource.getInstanceID());
						
						value.setText(word2);
					}
					values.add(value);
				} 
				catch (DocumentException e) {
					stream.addError(e.getError());
				}
				
				return true;
			}
			
			@Override
			public void onBlockEnd() {
				stream.getData().add(propertyName, new PropertyText(values));
				stream.endSpecialBlock();
			}
		});
	}
}
