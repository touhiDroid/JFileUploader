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

	public void connectAndGetServerConstraints(String serverAddress,
			int serverPort);

	public void startUpload(File[] f, String serverAddress, int serverPort);

}
