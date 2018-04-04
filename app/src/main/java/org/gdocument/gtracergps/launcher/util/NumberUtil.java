package org.gdocument.gtracergps.launcher.util;

public class NumberUtil {

	public static final int FORMAT_NB_DECIMAL_DEFAULT = 2;

	public static final double formatDouble(double value) {
		return formatDouble(value, FORMAT_NB_DECIMAL_DEFAULT);
	}

	public static final double formatDouble(double value, int nbDecimal) {
		int dec = (int)Math.pow(10, nbDecimal);
		
		return ((double)((int)(value * dec)) / dec);
	}

	public static final float formatFloat(float value) {
		return formatFloat(value, FORMAT_NB_DECIMAL_DEFAULT);
	}

	public static final float formatFloat(float value, int nbDecimal) {
		int dec = (int)Math.pow(10, nbDecimal);
		
		return ((float)((int)(value * dec)) / dec);
	}
}
