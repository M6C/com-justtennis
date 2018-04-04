package com.cameleon.common.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.os.Environment;

public class FileTool {

	private static FileTool instance = null;

	private FileTool() {

	}

	public static FileTool getInstance() {
		if (instance == null) {
			instance = new FileTool();
		}
		return instance;
	}

	public synchronized File backupToSdcard(String sourcePath, String destinationDirectory) throws IOException {
		File ret = null;
		File sd = Environment.getExternalStorageDirectory();

        if (sd.canWrite()) {
        	int idx = sourcePath.lastIndexOf("/");
        	if (idx > 0) {
            	String name = sourcePath.substring(idx+1);
        		
	    		String backupPath = "/" + destinationDirectory + "/" + name;
	
	            File dir = new File(sd, destinationDirectory);
	        	if (!dir.exists() && !dir.mkdirs()) {
	        		throw new IOException("Dir '"+destinationDirectory+"' creation error on sd");
	        	}
	
		        File source = new File(sourcePath);
		        File destination = new File(sd, backupPath);
		
		        copy(source, destination);
		        ret = destination;
        	}
        }
        else {
        	System.out.println("sdcard directory is not writable");
        }
		return ret;
	}

	public synchronized void copy(File sourceFilename, File destinationFilename) throws IOException {
        FileChannel src = null, dst = null;
        try {
            src = new FileInputStream(sourceFilename).getChannel();
            dst = new FileOutputStream(destinationFilename).getChannel();
            dst.transferFrom(src, 0, src.size());
        }
        finally {
        	if (src!=null)
        		src.close();
        	if (dst!=null)
        		dst.close();
        }

        System.out.println("copy From:" + sourceFilename.toString() + " To:" + destinationFilename.toString());
	}
}