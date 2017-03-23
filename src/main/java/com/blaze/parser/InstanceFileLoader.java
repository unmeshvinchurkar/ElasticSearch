package com.blaze.parser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.blaze.BlazeConstants;

public class InstanceFileLoader {

	private String rootDir = null;
	private File rootDirectory = null;
	private List<File> currentFiles = new ArrayList<File>();;

	public InstanceFileLoader(String rootDir) {
		this.rootDir = rootDir;
		rootDirectory = new File(this.rootDir);

		if (rootDirectory.exists()) {
			currentFiles.add(rootDirectory);
		}
	}

	public boolean hasMoreFiles() {
		if (currentFiles == null || currentFiles.size() == 0) {
			return false;
		}
		return true;
	}

	public File getNextFile() {

		if (currentFiles.size() == 0) {
			return null;
		}

		File file = currentFiles.get(currentFiles.size() - 1);

		currentFiles.remove(currentFiles.size() - 1);

		if (file.isDirectory()) {
			
			File[] subFiles = file.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.indexOf(".") == -1;
				}
			});

			if (subFiles != null) {
				for (File f : subFiles) {
					if (new File(f.getAbsolutePath() + BlazeConstants.INNOVATOR_ATTRIB_FILE_POST_FIX).exists()) {
						currentFiles.add(f);
					}
				}
			}

			return getNextFile();
		}

		return file;

	}

}
