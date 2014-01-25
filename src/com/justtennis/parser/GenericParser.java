package com.justtennis.parser;

import com.justtennis.ApplicationConfig;
import com.justtennis.tool.ParserTool;

public class GenericParser {

	protected Long parseLong(String text) {
		logParser("parseLong text:", text);
		return ParserTool.getInstance().parseLong(text);
	}

	protected String parseString(String text) {
		logParser("parseString text:", text);
		return ParserTool.getInstance().parseString(text);
	}

	private void logMe(String message) {
		System.out.println(message);
	}

	private void logParser(String title, String data) {
		if (ApplicationConfig.SHOW_LOG_TRACE_PARSER) {
			logMe("SHOW_LOG_TRACE_PARSER "+title+data);
		}
	}
}