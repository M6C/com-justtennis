package org.gdocument.gtracergps.launcher.util;

import java.util.HashMap;
import java.util.Map;

public class HTMLEncoder {
	   private static Map<Character, String> mapChar2HTMLEntity;
	private final static char [] characters = {
			'Ü', 'Ä', 'Ö', 'Ë', 'Ç', 'Æ', 'Å', 'Ø',
			'ü', 'ä', 'ö', 'ë', 'ç', 'å', 'ø', '`', 'à', 'è', 'ì', 'ò', 'ù',
			'&', 'ß', ' ', '>', '<',
			'©', '¢', '£', '«', '»', '®', '-', '\'',
			'á','ú','ó','é','í','ñ','§','è','î','ô','â','û','ê',
			'æ','¡','\"','ª','×','°','?','¦'
	};
	private final static String[] entities = {
	       "&Uuml;","&Auml;","&Ouml;","&Euml;","&Ccedil;","&AElig;","&Aring;","&Oslash;",     "&uuml;","&auml;","&ouml;","&euml;","&ccedil;","&aring;","&oslash;","&grave;","&agrave;","&egrave;","&igrave;","&ograve;","&ugrave;",
	       "&amp;","&szlig;","&nbsp;","&gt;","&lt;",
	       "&copy;","&cent;","&pound;","&laquo;","&raquo;","&reg;","&middot;","&acute;",     "&aacute;","&uacute;","&oacute;","&eacute;","&iacute;","&ntilde;","&sect;","&egrave;","&icirc;","&ocirc;","&acirc;","&ucirc;","&ecirc;",
	       "&aelig;","&iexcl;","&quot;","&ordf;","&times;","&deg;","&euro;","&brvbar;"
	  };  
	  static {
	     mapChar2HTMLEntity= new HashMap<Character, String>();
	     int longueur = characters.length;
	     for (int i = 0; i < longueur; i++) mapChar2HTMLEntity.put(new Character(characters[i]), entities[i]);
	  }	  
	   public static String encode(String s) {
	     int longueur = s.length();
	     final StringBuffer sb = new StringBuffer(longueur * 2);
	     char ch;
	     for (int i =0; i < longueur ; ++i) {
	       ch = s.charAt(i);
	       if ((ch >= 63 && ch <= 90) || (ch >= 97 && ch <= 122)) sb.append(ch);
	       else {
		 String ss = (String)mapChar2HTMLEntity.get(new Character(ch));
		 if(ss==null) sb.append(ch); else sb.append(ss);
	       }
	     }
	     return sb.toString();
	   }
}