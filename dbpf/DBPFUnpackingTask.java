package sporemodder.file.dbpf;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import emord.filestructures.FileStream;
import emord.filestructures.MemoryStream;
import emord.filestructures.StreamReader;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import sporemodder.FormatManager;
import sporemodder.HashManager;
import sporemodder.UIManager;
import sporemodder.file.Converter;
import sporemodder.util.Project;

public class DBPFUnpackingTask extends Task<Void> {
	
	/** The estimated progress (in %) that reading the index takes. */ 
	private static final int INDEX_PROGRESS = 15;
	
	/** The progress bar (if any) that we are updating. */
	private ProgressBar progressBar;
	
	/** The input DBPF file. */
	private File inputFile;
	
	/** The folder where all the contents will be written. */
	private File outputFolder;
	
	/** The current progress, in %. */
	private float progress;
	
	/** We will keep all files that couldn't be converted here, so that we can keep unpacking the DBPF. */
	private final HashMap<DBPFItem, Exception> exceptions = new HashMap<DBPFItem, Exception>();
	
	/** All the converters used .*/
	private final List<Converter> converters;
	
	/** How much time the operation took, in milliseconds. */
	private long ellapsedTime;
	
	private final AtomicBoolean running = new AtomicBoolean(true);
	
	public DBPFUnpackingTask(File inputFile, Project outputProject) {
		//TODO get the converters from the project?
		this(inputFile, outputProject.getFolder(), FormatManager.get().getConverters());
	}
	
	public DBPFUnpackingTask(File inputFile, File outputFolder, List<Converter> converters) {
		this.inputFile = inputFile;
		this.outputFolder = outputFolder;
		this.converters = converters;
	}
	
	/**
	 * Sets the progress bar that will be updated every time a file is unpacked, in order to show
	 * the progress of the process.
	 * @param progressBar
	 */
	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	@Override
	protected Void call() throws Exception {
		
		long initialTime = System.currentTimeMillis();
		
		try (StreamReader packageStream = new FileStream(inputFile, "r"))  {
			
			DatabasePackedFile header = new DatabasePackedFile();
			header.readHeader(packageStream);
			header.readIndex(packageStream);
			
			DBPFIndex index = header.index;
			index.readItems(packageStream, header.indexCount, header.isDBBF);
			
			incProgress(INDEX_PROGRESS);
			
			float inc = (100.0f - INDEX_PROGRESS) / header.indexCount;
			
			//TODO First search sporemaster/names.txt, and use it if it exists
			//findNamesFile(index.items, dbpf.getSource());
			
			HashManager hasher = HashManager.get();
			
			for (DBPFItem item : index.items) {
				
				if (!running.get()) {
					synchronized (running) {
						while (!running.get()) {
							running.wait();
						}
					}
				}
				
				String fileName = hasher.getFileName(item.name.getInstanceID()) ;
				
				// skip autolocale files
				if (item.name.getGroupID() == 0x02FABF01 && fileName.startsWith("auto_")) {
					continue;
				}
				
				
				File folder = new File(outputFolder, hasher.getFileName(item.name.getGroupID()));
				folder.mkdir();
				
				try (MemoryStream dataStream = item.processFile(packageStream)) {
					
					// Has the file been converted?
					boolean isConverted = false;
					
					for (Converter converter : converters) {
						if (converter.isDecoder(item.name)) {
							
							if (converter.decode(dataStream, folder, item.name)) {
								isConverted = true;
								break;
							}
							else {
								// throw new IOException("File could not be converted.");
								// We could throw an error here, but it is not appropriate:
								// some files cannot be converted but did not necessarily have an error,
								// for example trying to convert a non-texture rw4. So we jsut keep searching
								// for another converter or write the raw file.s
								continue;
							}
						}
					}
					
					if (!isConverted) {
						// If it hasn't been converted, just write the file straight away.
						
						String name = hasher.getFileName(item.name.getInstanceID()) + "." + hasher.getTypeName(item.name.getTypeID());
						
						dataStream.writeToFile(new File(folder, name));
					}
				}
				catch (Exception e) {
					exceptions.put(item, e);
				}
				
				incProgress(inc);
			}
			
			//TODO // disable extra names
			//sHasher.UsedNames = null;
		}
		
		ellapsedTime = System.currentTimeMillis() - initialTime;
		
		// Ensure the taskbar progress is over
		setProgress(100);
		
		return null;
	}
	
	/**
	 * Returns a Map with all the items that could not be unpacked/converted, mapped to the exception that caused that error.
	 * @return
	 */
	public HashMap<DBPFItem, Exception> getExceptions() {
		return exceptions;
	}
	
	/**
	 * Returns how much time the operation took, in milliseconds.
	 * @return
	 */
	public long getEllapsedTime() {
		return ellapsedTime;
	}

	private void incProgress(float increment) {
		setProgress(progress + increment);
	}
	
	private void setProgress(float progress) {
		this.progress = progress;
		
		if (progressBar != null) {
			Platform.runLater(() -> {
				progressBar.setProgress(progress / 100.0f);
				
				UIManager.get().setProgramProgress(progress / 100.0f);
			});
		}
	}
	
	public void pause() {
		running.set(false);
	}
	
	public void resume() {
		running.set(true);
		
		synchronized(running) {
			running.notify();
		}
	}
}
