package sporemodder.file.dbpf;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import emord.filestructures.FileStream;
import emord.filestructures.MemoryStream;
import emord.filestructures.StreamWriter;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import sporemodder.FormatManager;
import sporemodder.HashManager;
import sporemodder.file.Converter;
import sporemodder.util.Project;

public class DBPFPackingTask extends Task<Void> {
	
	/** The estimated progress (in %) that writing the index takes. */ 
	private static final int INDEX_PROGRESS = 10;
	
	/** The progress bar (if any) that we are updating. */
	private ProgressBar progressBar;
	
	/** The folder with the contents that are being packed. */
	private File inputFolder;
	
	private File outputFile;
	
	/** The output stream where the DBPF file will be written. */
	private StreamWriter stream;
	
	
//	/** If our output stream is a file on the system, we can use Java NIO for better performance. */
//	private FileChannel outputChannel;
	
	private byte[] currentInputData;
	private int nCurrentInputLength;
	
	/** The fast memory stream used to write the DBPF index. */
	private MemoryStream indexStream;
	
	/** The total progress (in %). */
	private float progress = 0;
	
	/** A map with all the files that couldn't be packed. */
	private final HashMap<File, Exception> failedFiles = new HashMap<File, Exception>();
	
	/** An object that holds information to be used by the ModAPI; it is optional. */
	private DebugInformation debugInfo;
	
	// Temporary things
	private String currentFolderName;
	private String currentFileName;
	private String currentExtension;
	
	private int currentGroupID;
	private int currentInstanceID;
	private int currentTypeID;
	
	/** The total amount of items that have been written. */
	private int nItemsCount = 0;
	
	/** If a file is bigger (in bytes) than this number, it will get compressed. If the value is -1, it is ignored. */
	private int nCompressThreshold = -1;
	
	private final RefPackCompression.CompressorOutput compressOut = new RefPackCompression.CompressorOutput();
	
	public DBPFPackingTask(Project project, ProgressBar progressBar, boolean storeDebugInformation) {
		this.inputFolder = project.getFolder();
		this.progressBar = progressBar;
		this.outputFile = project.getOutputPackage();
		
		if (storeDebugInformation) {
			debugInfo = new DebugInformation(project.getName(), inputFolder.getAbsolutePath());
		}
	}

	@Override
	protected Void call() throws Exception {
		
		try (StreamWriter stream = new FileStream(outputFile, "rw");
				MemoryStream indexStream = new MemoryStream()) {
			this.stream = stream;
			this.indexStream = indexStream;
			//TODO support DBBF maybe?
			
			// DOesn't really make sense to let the user disable converters.
			List<Converter> converters = FormatManager.get().getConverters();
			
			File[] folders = inputFolder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File arg0) {
					return arg0.isDirectory();
				}
				
			});
			
			// The header will be written after, now we just write padding
			stream.writePadding(96);
			
			// Write the default index information
			indexStream.writeLEInt(4);
			indexStream.writeInt(0);
			
			
			DatabasePackedFile header = new DatabasePackedFile();
			
			/** How much we increment the progress (in %) after every folder is completed. */
			float inc = (100.0f - INDEX_PROGRESS) / folders.length;
			
			//TODO name registry
			
			final DBPFItem item = new DBPFItem();
			final HashManager hasher = HashManager.get();
			
			for (File folder : folders) {
				
				currentFolderName = folder.getName();
				currentGroupID = hasher.getFileHash(currentFolderName);
				
				File[] files = folder.listFiles();
				
				for (File file : files) {
					
					//TODO auto-locale files
					
					boolean bUsesConverter = false;
					
					String name = file.getName();
					file = getNestedFile(file, name);
					
					// Skip if there was a problem
					if (file == null) continue;
					
					String[] splits = name.split("\\.", 2);
					currentFileName = splits[0];
					currentExtension = splits.length > 1 ? splits[1] : "";
					
					currentInstanceID = hasher.getFileHash(currentFileName);
					
					for (Converter converter : converters) {
						if (converter.isEncoder(file)) {
							MemoryStream conversionStream = new MemoryStream();
							
							if (converter.encode(file, conversionStream)) {
								
								currentTypeID = converter.getOriginalTypeID(currentExtension); 
								
								currentInputData = conversionStream.getRawData();
								nCurrentInputLength = (int) conversionStream.length();
										
								bUsesConverter = true;
							}
							
							conversionStream.close();
						}
					}
					
					if (!bUsesConverter) {
						
						currentTypeID = hasher.getTypeHash(currentExtension);
						
						currentInputData = Files.readAllBytes(file.toPath());
						nCurrentInputLength = currentInputData.length;
					}
					
					item.name.setGroupID(currentGroupID);
					item.name.setInstanceID(currentInstanceID);
					item.name.setTypeID(currentTypeID);
					
					
					writeFile(item, currentInputData, nCurrentInputLength);
					addFile(item);
					
					// Add debug information
					if (debugInfo != null 
							&& !bUsesConverter) {  // We cannot get the files from disk in Spore if they needed to be converted
						debugInfo.addFile(currentFolderName, name, currentGroupID, currentInstanceID, currentTypeID);
					}
				}
				
				//TODO embed editorPackages
				
				incProgress(inc);
			}
			
			//TODO Write names list
			//TODO Write editorPackages
			
			// Save debug information
			if (debugInfo != null) {
				debugInfo.saveInformation(this);
			}
			
			// Write header and index
			// First ensure we have no offset base
			stream.setBaseOffset(0);
			header.indexOffset = stream.getFilePointer();
			header.indexCount = nItemsCount;
			header.indexSize = (int) indexStream.length();
			
			indexStream.writeInto(stream);
			stream.write(indexStream.toByteArray());
			
			incProgress(INDEX_PROGRESS);
			
			// Go back and write header
			stream.seek(0);
			header.writeHeader(stream);
		}
		catch (Exception e) {
			e.printStackTrace();
			
			//TODO error handling
		}
		
		return null;
	}

	private void incProgress(float increment) {
		progress += increment;

		if (progressBar != null) {
			progressBar.setProgress(progress / 100.0f);
		}
	}
	
	public void addFile(DBPFItem item) throws IOException {
		item.write(indexStream, false, true, true);
		nItemsCount++;
	}
	
	public boolean writeFile(DBPFItem item, byte[] data, int length) throws IOException {
		
		item.chunkOffset = stream.getFilePointer();
		
		if (nCompressThreshold != -1 && length > nCompressThreshold) {
			
			RefPackCompression.compress(data, length, compressOut);

			stream.write(compressOut.data, 0, compressOut.lengthInBytes);
			item.isCompressed = true;
			item.memSize = length;
			item.compressedSize = compressOut.lengthInBytes;
		}
		else {
			stream.write(data, 0, length);
			item.isCompressed = false;
			item.memSize = length;
			item.compressedSize = item.memSize;
		}
		

		return item.isCompressed;
	}
	
	//TODO consider changing how nested files work
	private File getNestedFile(File file, String name) {
		if (!file.isFile()) {
			if (name.contains(".") && !name.endsWith(".effdir.unpacked")) {
				File newFile = new File(file, name);
				if (!newFile.exists()) {
					failedFiles.put(file, new UnsupportedOperationException("Couldn't find file " + name + " inside subfolder " + name));
					return null;
				}
				file = newFile;
			}
			else if (!name.endsWith(".effdir.unpacked")) {
				failedFiles.put(file, new UnsupportedOperationException("Nested subfolders are not supported. File: " + name));
				return null;
			}
		}
		
		return file;
	}
}
