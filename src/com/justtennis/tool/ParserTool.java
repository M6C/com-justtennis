package com.justtennis.tool;


public class ParserTool {

	private static ParserTool instance = null;
	
	private ParserTool() {
		
	}
	
	public static ParserTool getInstance() {
		if (instance==null) {
			instance = new ParserTool();
		}
		return instance;
	}

	public Long parseLong(String text) {
		return "null".equals(text) ? null : Long.parseLong(text);
	}

	public String parseString (String text) {
		return "null".equals(text) ? null : text;
	}
}
