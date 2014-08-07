package com.touhiDroid.JFileUploader.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.touhiDroid.JFileUploader.Interfaces.UploadListener;
import com.touhiDroid.JFileUploader.Utils.Constants;

public class Client implements UploadListener {

	private ClientGUI clientGUI;
	private Socket clientSocket;
	private ObjectOutputStream oos;

	// private String serverAddress;
	// private int serverPort;

	private void uploadFile(File file) {
		Long fileSize = (Long) file.length();
		System.out.println("File name: " + file.getName() + ", File size: "
				+ fileSize);
		// ObjectInputStream ois = new
		// ObjectInputStream(clientSocket.getInputStream());
		try {
			String fileParent = file.getParent();
			System.out.println("Upload file's parent: " + fileParent);
			oos.writeObject(fileParent);
			oos.writeObject(file.getName());
			oos.writeLong(fileSize);

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[Constants.BUFFER_SIZE];
			int bytesRead = 0;

			int counter = 0;
			while ((bytesRead = fis.read(buffer)) > 0) {
				oos.writeObject(bytesRead);
				oos.writeObject(Arrays.copyOf(buffer, buffer.length));
				System.out.println("bytes written: " + bytesRead);
				if (fileSize > 0)
					clientGUI
							.setCurProgress((int) ((++counter * bytesRead * 100) / fileSize));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File transfer complete.");
	}

	/**
	 * Upload only the parent-directory first, then all the files inside it with
	 * any level of listing
	 */
	private void uploadDirectory(File directory) {
		System.out.println("Directory name: " + directory.getName()
				+ ", Directory file amount: " + directory.list().length);
		try {
			oos.writeObject(Constants.KEY_DIRECTORY);
			// Tell the server the client's head for directory making decision
			oos.writeObject(directory.getParent());
			oos.writeObject(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// List & upload only files with any nested listing
		File[] fileList = directory.listFiles();
		int counter = 0;
		for (File f : fileList) {
			if (f.isFile())
				uploadFile(f);
			else
				uploadFolder(f);
			clientGUI
					.setTotalProgress((int) (++counter * 100 / fileList.length));
		}
		// Tell the server to stop accepting any more files for this time
		System.out.println(">>>>>> Now Stopping the transfer <<<<<");
		clientGUI.setTotalProgress(100);
		try {
			oos.writeObject(Constants.KEY_DIR_STOPPER);
		} catch (IOException ioe) {
			showErrorMessage("IOException while trying to stop the directory "
					+ "upload flow:\n" + ioe.toString());
		}
	}

	/**
	 * A recursive function to list all files inside any folder with any level
	 * of listing & then upload the file to the server
	 */
	private void uploadFolder(File folder) {
		System.out.println("Folder name: " + folder.getName()
				+ ", Folder file amount: " + folder.list().length);
		File[] fileList = folder.listFiles();
		for (File f : fileList) {
			if (f.isFile())
				uploadFile(f);
			else
				uploadFolder(f);
		}
	}

	@Override
	public void startUpload(File file, String serverAddress, int serverPort) {
		try {
			clientSocket = new Socket(InetAddress.getByName(serverAddress),
					serverPort);
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			if (file.isFile()) {
				uploadFile(file);
			} else {
				uploadDirectory(file);
			}
			oos.close();
			clientSocket.close();
		} catch (SocketException se) {
			showErrorMessage("SocketException caught during "
					+ "initializing client's socket.\n" + se.toString());
		} catch (IOException ioe) {
			showErrorMessage("IOException caught during "
					+ "initializing client's connection.\n" + ioe.toString());
		}
	}

	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "ErrOrrrrrrrr",
				JOptionPane.ERROR_MESSAGE);
	}

	public static void main(String[] args) {
		new Client(args);
	}

	public Client(String[] args) {
		String fileName = "";
		try {
			fileName = args[0];
			startUpload(new File(fileName), Constants.DEFAULT_DESTINATION,
					Constants.DEFAULT_PORT);
		} catch (Exception e) {
			// System.out.println("Enter the name of the file :");
			// fileName = new Scanner(System.in).nextLine();
			clientGUI = new ClientGUI((UploadListener) this);
		}
	}
}
