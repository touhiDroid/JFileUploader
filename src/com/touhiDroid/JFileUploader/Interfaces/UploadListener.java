/**
 * 
 */
package com.touhiDroid.JFileUploader.Interfaces;

import java.io.File;

/**
 * @author Touhid
 *
 */
public interface UploadListener {
	
	public void startUpload(File f, String serverAddress, int serverPort);

}
