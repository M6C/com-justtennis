package com.justtennis.tool;


public class CryptoTool {

	private final static boolean ENABLE_CRYPTO = true; 
	private final static boolean ENABLE_CRYPTO_NUMERIC = true;

	private static CryptoTool instance = null;
	
	private CryptoTool() {
		
	}
	
	public static CryptoTool getInstance() {
		if (instance==null) {
			instance = new CryptoTool();
		}
		return instance;
	}

	public String crypte(String text) {
		if (ENABLE_CRYPTO) {
			String ret = null;
			if (text!=null) {
				char[] cTab = text.toCharArray();
				int size = cTab.length;
				char[] cTabDest = new char[size+1];
				int i=0,j=0;
				for (i=0 ; i<(size+1) ; i++) {
					cTabDest[i]=(char)-1;
				}
				for(i=0 ; i<size ; i++) {
					if ((i%2)==0) {
						j=i+1;
					}
					else {
						j=i-1;
					}
					cTabDest[j]=cTab[i];
				}
				StringBuilder sb = new StringBuilder();
				for (i=0 ; i<(size+1) ; i++) {
					char c = cTabDest[i];
					if (c!=(char)-1)
						sb.append(c);
				}
				ret = sb.toString();
			}
			return ret;
		} else {
			return text;
		}
	}

	public String decrypte(String text) {
		return crypte(text);
	}

	public String crypteNumeric(long number) {
		if (ENABLE_CRYPTO_NUMERIC) {
			return crypteNumeric(Long.toString(number));
		} else {
			return Long.toString(number);
		}
	}

	public String crypteNumeric(String number) {
		if (ENABLE_CRYPTO_NUMERIC) {
			return cryptoNumeric(number, +17);
		} else {
			return number;
		}
	}

	public String decrypteNumeric(String number) {
		if (ENABLE_CRYPTO_NUMERIC) {
			return cryptoNumeric(number, -17);
		} else {
			return number;
		}
	}

	private String cryptoNumeric(String number, int increment) {
		StringBuilder ret = new StringBuilder();
		char[] cTab = number.toCharArray();
		char c;
		int size = cTab.length;
		for (int i=0 ; i<size ; i++) {
			c = cTab[i];
			ret.append((char)(c+increment));
		}
		return ret.toString();
	}
}