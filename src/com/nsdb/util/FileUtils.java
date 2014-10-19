package com.nsdb.util;

import java.io.File;

public class FileUtils {
	
	public static boolean removeDirectory(File f) {
		if(f.isDirectory()) {
			for(File c : f.listFiles()) {
				removeDirectory(c);
			}
		}
		return f.delete();
	}
}
