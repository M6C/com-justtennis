package org.gdocument.gtracergps.launcher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gdocument.gtracergps.GpsConstant;
import org.gdocument.gtracergps.launcher.log.Logger;

public class FileUtil {
	public static final String TAG = "FileUtil";

	public static String initDatedFilename(String filename) {
		String dateTime = new SimpleDateFormat(GpsConstant.DATETIME_FORMAT_DATEDFILENAME).format(new Date());
		return filename.replace(GpsConstant.DATE_TAG, dateTime);
	}

	public static void createFile(String filename) {
        try {
			new File(filename).createNewFile();
		} catch (IOException ex) {
			logMe(TAG, ex);
		}
	}

	public static void logMe(String tag, String msg) {
		com.crashlytics.android.Crashlytics.log(msg);
		Logger.logMe(tag, msg);
    }

	public static void logMe(String tag, Exception ex) {
		com.crashlytics.android.Crashlytics.logException(ex);
		Logger.logMe(tag, ex);
   }

	// write on SD card file text
	public static void writeMeSD(String filename, String text) {
		FileOutputStream fOut = null;
		OutputStreamWriter myOutWriter = null;
		String path = filename;
		try {
			File myFile = new File(path);
			fOut = new FileOutputStream(myFile, true);
			myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(text);
		} catch (Exception e) {
			e.printStackTrace();
			File myFile = new File(path);
			if (!myFile.exists()) {
				// Create file and retry write in file
				try {
					myFile.createNewFile();
					fOut = new FileOutputStream(myFile, true);
					myOutWriter = new OutputStreamWriter(fOut);
					myOutWriter.append(text);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
        }
		finally {
			if (myOutWriter!=null) {
				try {
					myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fOut!=null) {
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }

	// write on SD card file text
	public static void writeMeSD(String filename, List<String> listText, String rowSeparator) {
		FileOutputStream fOut = null;
		OutputStreamWriter myOutWriter = null;
		String path = filename;
		try {
			File myFile = new File(path);
			fOut = new FileOutputStream(myFile, true);
			myOutWriter = new OutputStreamWriter(fOut);
			writeInStream(listText, rowSeparator, myOutWriter);
		} catch (Exception e) {
			e.printStackTrace();
			File myFile = new File(path);
			if (!myFile.exists()) {
				// Create file and retry write in file
				try {
					myFile.createNewFile();
					fOut = new FileOutputStream(myFile, true);
					myOutWriter = new OutputStreamWriter(fOut);
					writeInStream(listText, rowSeparator, myOutWriter);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
        }
		finally {
			if (myOutWriter!=null) {
				try {
					myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fOut!=null) {
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }

	// write on SD card file text
	public static void writeMeSD(String filename, Exception ex) {
		FileOutputStream fOut = null;
		OutputStreamWriter myOutWriter = null;
		PrintStream ps = null;
		try {
			writeMeSD(filename, ex.getMessage());

			String path = filename;
			File myFile = new File(path);
//			if (!myFile.exists())
//			myFile.createNewFile();
			fOut = new FileOutputStream(myFile, true);
			myOutWriter = new OutputStreamWriter(fOut);

			ps = new PrintStream(fOut);
			ex.printStackTrace(ps);
		} catch (Exception e) {
			e.printStackTrace();
        }
		finally {
			if (myOutWriter!=null) {
				try {
					myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ps!=null) {
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fOut!=null) {
				ps.close();
			}
		}
    }


	// write on SD card file text
	public static List<String> readMeSD(String filename) {
		List<String> ret = null;
		FileInputStream fIn = null;
		InputStreamReader myInReader = null;
		BufferedReader myInBufReader = null;
		try {
			String path = filename;
			File myFile = new File(path);
			if (!myFile.exists()) {
				ret = new ArrayList<String>();
				fIn = new FileInputStream(myFile);
				myInReader = new InputStreamReader(fIn);
				myInBufReader = new BufferedReader(myInReader);
				String text = null;
				do {
					text = myInBufReader.readLine();
					if (text!=null)
						ret.add(text);
				}
				while(text!=null);
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
		finally {
			if (myInBufReader!=null) {
				try {
					myInBufReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (myInReader!=null) {
				try {
					myInReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fIn!=null) {
				try {
					fIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
    }

	private static void writeInStream(List<String> listText, String rowSeparator, OutputStreamWriter myOutWriter) throws IOException {
		for(int i=0 ; i<listText.size() ; i++) {
			if (i>0)
				myOutWriter.append(rowSeparator);
			myOutWriter.append(listText.get(i));
		}
	}
}
