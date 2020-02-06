package sporemodder.file.prop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import emord.filestructures.StreamReader;
import emord.filestructures.StreamWriter;
import sporemodder.HashManager;
import sporemodder.file.Converter;
import sporemodder.file.ResourceKey;

public class PropConverter implements Converter {
	
	private static String extension = null;
	private static String soundExtension = null;

	@Override
	public boolean decode(StreamReader stream, File outputFolder, ResourceKey key) throws IOException {
		PropertyList list = new PropertyList();
		list.read(stream);
		
		try (PrintWriter out = new PrintWriter(Converter.getOutputFile(key, outputFolder, "prop_t"))) {
		    out.println(list.toArgScript());
		}
		
		return true;
	}

	@Override
	public boolean encode(File input, StreamWriter output) throws IOException, ParserConfigurationException, SAXException {
		String name = input.getName();
		if (name.endsWith(".prop.xml")) {
			//TODO consider the auto-locale thing
			try (InputStream in = new FileInputStream(input)) {
				XmlPropParser.xmlToProp(in, output, null, null);
				
				return true;
			}
		}
		else {
			PropertyList list = new PropertyList();
			list.generateStream().process(input);
			list.write(output);
			return true;
			
		}
	}

	@Override
	public boolean isDecoder(ResourceKey key) {
		// There are two extensions for PROP: the standard and the sound one
		return key.getTypeID() == 0x00B1B104 || key.getTypeID() == 0x02B9F662;
	}

	@Override
	public boolean isEncoder(File file) {
		if (extension == null) {
			extension = "." + HashManager.get().getTypeName(0x00B1B104);
			soundExtension = "." + HashManager.get().getTypeName(0x02B9F662);
		}
		return file.isFile() && (
				file.getName().endsWith(extension + ".xml") || 
				file.getName().endsWith(extension + ".prop_t") ||
				file.getName().endsWith(soundExtension + ".xml") || 
				file.getName().endsWith(soundExtension + ".prop_t"));
	}

	@Override
	public String getName() {
		return "Properties File (." + HashManager.get().getTypeName(0x00B1B104) + ", ." + HashManager.get().getTypeName(0x02B9F662) + ")";
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public int getOriginalTypeID(String extension) {
		return extension.startsWith(soundExtension) ? 0x02B9F662 : 0x00B1B104;
	}
}
