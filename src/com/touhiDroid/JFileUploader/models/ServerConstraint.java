/**
 * 
 */
package com.touhiDroid.JFileUploader.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Touhid
 * 
 */
public class ServerConstraint implements Serializable {
	/**
	 * Auto-gen ID for ServerConstraint
	 */
	private static final long serialVersionUID = -5806154327982781674L;

	private String rootDirectory, sIds, fileTypes, fileNames;
	private int sIdStart, sIdEnd, maxFileSizeInKB, maxNoFiles, serverPort;
	private boolean isMultiFiles = false, isFolder = false;

	public ServerConstraint(String rootDirectory, String sIds,
			String fileTypes, String fileNames, int sIdStart, int sIdEnd,
			int maxFileSizeInKB, int maxNoFiles, boolean isMultiFiles,
			boolean isFolder, int serverPort) {
		super();
		this.rootDirectory = rootDirectory;
		this.sIds = sIds;
		this.fileTypes = fileTypes;
		this.fileNames = fileNames;
		this.sIdStart = sIdStart;
		this.sIdEnd = sIdEnd;
		this.maxFileSizeInKB = maxFileSizeInKB;
		this.maxNoFiles = maxNoFiles;
		this.isMultiFiles = isMultiFiles;
		this.isFolder = isFolder;
		this.serverPort = serverPort;
	}

	@Override
	public String toString() {
		return "Root dir: " + rootDirectory + "\n File Types: " + fileTypes
				+ "\n File names: " + fileNames + "\n SIDs: " + sIds
				+ "\n Std. ID Start: " + sIdStart + "\nStd. ID End : " + sIdEnd
				+ "\n : Max. Size: " + maxFileSizeInKB
				+ "\n Max no. of files: " + maxNoFiles + "\nMultiple files: "
				+ isMultiFiles + "\nIs folders: " + isFolder
				+ "\nServer Port: " + serverPort;
	}

	/**
	 * @return the rootDirectory
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * @param rootDirectory
	 *            the rootDirectory to set
	 */
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	/**
	 * @return the sIds
	 */
	public String getsIds() {
		return sIds;
	}

	/**
	 * @param sIds
	 *            the sIds to set
	 */
	public void setsIds(String sIds) {
		this.sIds = sIds;
	}

	/**
	 * @return the fileTypes
	 */
	public String getFileTypes() {
		return fileTypes;
	}

	/**
	 * @param fileTypes
	 *            the fileTypes to set
	 */
	public void setFileTypes(String fileTypes) {
		this.fileTypes = fileTypes;
	}

	/**
	 * @return the fileNames
	 */
	public String getFileNames() {
		return fileNames;
	}

	/**
	 * @param fileNames
	 *            the fileNames to set
	 */
	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

	/**
	 * @return the sIdStart
	 */
	public int getsIdStart() {
		return sIdStart;
	}

	/**
	 * @param sIdStart
	 *            the sIdStart to set
	 */
	public void setsIdStart(int sIdStart) {
		this.sIdStart = sIdStart;
	}

	/**
	 * @return the sIdEnd
	 */
	public int getsIdEnd() {
		return sIdEnd;
	}

	/**
	 * @param sIdEnd
	 *            the sIdEnd to set
	 */
	public void setsIdEnd(int sIdEnd) {
		this.sIdEnd = sIdEnd;
	}

	/**
	 * @return the maxFileSizeInKB
	 */
	public int getMaxFileSizeInKB() {
		return maxFileSizeInKB;
	}

	/**
	 * @param maxFileSizeInKB
	 *            the maxFileSizeInKB to set
	 */
	public void setMaxFileSizeInKB(int maxFileSizeInKB) {
		this.maxFileSizeInKB = maxFileSizeInKB;
	}

	/**
	 * @return the maxNoFiles
	 */
	public int getMaxNoFiles() {
		return maxNoFiles;
	}

	/**
	 * @param maxNoFiles
	 *            the maxNoFiles to set
	 */
	public void setMaxNoFiles(int maxNoFiles) {
		this.maxNoFiles = maxNoFiles;
	}

	/**
	 * @return the isMultiFiles
	 */
	public boolean isMultiFiles() {
		return isMultiFiles;
	}

	/**
	 * @param isMultiFiles
	 *            the isMultiFiles to set
	 */
	public void setMultiFiles(boolean isMultiFiles) {
		this.isMultiFiles = isMultiFiles;
	}

	/**
	 * @return the isFolder
	 */
	public boolean isFolder() {
		return isFolder;
	}

	/**
	 * @param isFolder
	 *            the isFolder to set
	 */
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public static ArrayList<String> parseFileTypes(String fileTypes2) {
		StringTokenizer t = new StringTokenizer(fileTypes2,", ");
		ArrayList<String> ftList=new ArrayList<String>();
		while(t.hasMoreTokens())
			ftList.add(t.nextToken().toLowerCase());
		return ftList;
	}

	public static ArrayList<String> parseFileNames(String fileNames2) {
		StringTokenizer t = new StringTokenizer(fileNames2,", ");
		ArrayList<String> ftList=new ArrayList<String>();
		while(t.hasMoreTokens())
			ftList.add(t.nextToken());
		return ftList;
	}

}
