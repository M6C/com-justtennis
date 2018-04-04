package com.cameleon.common.tool;

import java.io.IOException;

import android.content.Context;

public class StringTool {

	private static StringTool instance = null;

	private StringTool() {
	}

	public static StringTool getInstance() {
		if (instance == null) {
			instance = new StringTool();
		}
		return instance;
	}

	@Deprecated
	public static StringTool getInstance(Context context) {
		if (instance == null) {
			instance = new StringTool();
		}
		return instance;
	}

	public boolean equals(String str1, String str2) throws IOException {
		if (str1 == null && str2 == null) {
			return true;
		} else if (str1 != null && str2 != null) {
			return str1.equals(str2);
		} else {
			return false;
		}
	}
	
	public boolean equalsIgnoreCase(String str1, String str2) throws IOException {
		if (str1 == null && str2 == null) {
			return true;
		} else if (str1 != null && str2 != null) {
			return str1.equalsIgnoreCase(str2);
		} else {
			return false;
		}
	}

	public boolean isEmpty(String str) {
		return str == null || "".equals(str);
	}
}