package org.gdocument.gtracergps;


public class GpsConstant {

	public static final String VERSION_REPORT = "V 0.0.1";
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME_FORMAT = "HH:mm:ss";

	/**
	 * FILEUTIL
	 */
	public static final String DATETIME_FORMAT_DATEDFILENAME = "yyyyMMdd-HHmmss";
	public static final String DATE_TAG = "[DATE]";

	/**
	 * LOGGER
	 */
	public static final String DATETIME_FORMAT_LOG = "yyyyMMdd-HHmmss";
	public static final String FILENAME_LOG = "log_GTracerGps_"+GpsConstant.DATE_TAG;
	public static boolean LOG_WRITE = true;
	public static boolean LOG_WRITE_SYSOUT = false;
	public static boolean LOG_WRITE_SD = false;//true;
}
