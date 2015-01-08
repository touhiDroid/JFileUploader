/**
 * 
 */
package com.touhiDroid.JFileUploader.Utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Touhid
 * 
 */
public class Utils {
	public static final int DEFAULT_PORT = 5018;
	public static final String DEFAULT_SERVER = "localhost";
	public static final int BUFFER_SIZE = 512;
	public static final String DEFAULT_DESTINATION = "C:\\";

	public static final String KEY_DIRECTORY = "Directory";
	public static final String KEY_FILE_OK = "OK";
	public static final String KEY_FILE_ERROR = "ERROR";

	public static final String KEY_DIR_STOPPER = "__TOUHID__STOP__DIR__RECEPTION__";

	public static final int MODE_FILE_FOLDER = 10001;
	public static final int MODE_MULTIPLE_FILES = 10002;
	public static final int MODE_SINGLE_FILE = 10003;
	

	public static void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "ErrOrrrrrrrr",
				JOptionPane.ERROR_MESSAGE);
	}

	public static void showWarning(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Warning",
				JOptionPane.WARNING_MESSAGE);
	}
}
