package org.gdocument.gtracergps.launcher.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gdocument.gtracergps.GpsConstant;
import org.gdocument.gtracergps.launcher.util.FileUtil;

import android.os.Environment;
import android.util.Log;

public class Logger {
	public static final String TAG = "GTracerGpsActivity";
	private static String logFilename = GpsConstant.FILENAME_LOG+".txt";

	public static void initLogSD() {
        if (GpsConstant.LOG_WRITE_SD) {
        	initLogFilename();
        	FileUtil.createFile(getLogFilename());
        }
	}

	public static void logMe(String tag, String msg) {
		if(GpsConstant.LOG_WRITE && msg!=null)
			Log.i(tag, msg);

		if(GpsConstant.LOG_WRITE_SYSOUT)
			System.out.println(tag+" "+msg);

        if (GpsConstant.LOG_WRITE_SD)
        	writeMeSD(tag+" "+msg);
    }

	public static void logMe(String tag, Exception ex) {
		ex.printStackTrace();

		if (GpsConstant.LOG_WRITE_SD)
			writeMeSD(ex);
    }

	public static void logWa(String tag, String msg) {
		if(GpsConstant.LOG_WRITE_SYSOUT)
			System.out.println(tag+" [WARNING] "+msg);

		Log.w(tag, msg);

		if (GpsConstant.LOG_WRITE_SD)
			writeMeSD(tag+" [WARNING] "+msg);
    }

	public static void logEr(String tag, String msg) {
		if(GpsConstant.LOG_WRITE_SYSOUT)
			System.err.println(tag+" [ERROR] "+msg);

		Log.e(tag, msg);

		if (GpsConstant.LOG_WRITE_SD)
			writeMeSD(tag+" [ERROR] "+msg);
    }

	private static void initLogFilename() {
		String root = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		String path = logFilename;
		if (!logFilename.startsWith(root))
			path = root + logFilename;
		logFilename = FileUtil.initDatedFilename(path);
//		String dateTime = new SimpleDateFormat(DATETIME_FORMAT).format(new Date());
//		logFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FILENAME+"_"+dateTime+".txt";
	}

	private static String getLogFilename() {
		return logFilename;
	}

	// write on SD card file text
	private static void writeMeSD(String text) {
		String path = getLogFilename();
		String date = new SimpleDateFormat(GpsConstant.DATETIME_FORMAT_LOG).format(new Date());
		text = "\r\n"+date+"-"+text;
		
		FileUtil.writeMeSD(path, text);
    }

	// write on SD card file text
	private static void writeMeSD(Exception ex) {
		String path = getLogFilename();
		
		FileUtil.writeMeSD(path, ex);
    }

}
