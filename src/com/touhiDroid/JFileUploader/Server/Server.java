package com.touhiDroid.JFileUploader.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.touhiDroid.JFileUploader.Utils.Utils;

/**
 * @author Touhid
 */
public class Server implements Runnable {

	private ServerGUI serverGUI;

	private ObjectInputStream ois;
	private FileOutputStream fos;
	private Object readObj;

	private String clientHead = "";
	private String curFileParent = "";

	@Override
	public void run() {
		try {
			serverGUI = new ServerGUI();
			ServerSocket serverSocket = new ServerSocket(
					serverGUI.getServerPortNumber());
			while (true) {
				System.out.println("Server is running! Waiting for file ...");
				Socket s = serverSocket.accept();
				System.out
						.println("Request accepted! Now saving file/folder...");
				// serverGUI.setProgressBarsVisible();
				saveFileOrDirectory(s);
				// serverGUI.setProgressBarsInvisible();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveFileOrDirectory(final Socket socket) {
		// ObjectOutputStream oos = new ObjectOutputStream(
		// socket.getOutputStream());
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			fos = null;
			// 1. Read is_directory indicator / file name for single file
			readObj = ois.readObject();
			if (readObj instanceof String) {
				if (readObj.toString().equals(Utils.KEY_DIRECTORY)) {
					// 2a. save the directory iteratively
					clientHead = (String) ois.readObject();// 2 decide directory
					System.out.println("Client head: " + clientHead);
					saveDirectory((File) ois.readObject());
				} else {
					// 2b. save the requested single file
					curFileParent = readObj.toString();
					saveFile(serverGUI.getDestinationPath(),
							(String) ois.readObject());
					fos.close();
				}
			} else {
				throwException("Server-53 :: FileOutputStream can't be initialized!");
			}
			ois.close();
		} catch (IOException ioe) {
			showErrorMessage("IOException: Server-76 ::" + ioe.toString());
		} catch (ClassNotFoundException cnfe) {
			showErrorMessage("ClassNotFoundException: Server-78 ::"
					+ cnfe.toString());
		} catch (Exception e) {
			showErrorMessage("Exception: Server-80 ::" + e.toString());
		}
	}

	private void saveDirectory(File file) {
		String dirName = file.getName();
		System.out.println("Directory got: " + dirName);

		File dir = new File(serverGUI.getDestinationPath(), dirName);
		System.out.println("Dir. parent: " + dir.getParent());

		if (dir.mkdir()) {
			System.out.println("Directory created: " + dirName);
			// TODO list & save files
			try {
				readObj = ois.readObject();
				while (readObj instanceof String) {
					curFileParent = readObj.toString();
					if (curFileParent.equals(Utils.KEY_DIR_STOPPER)) {
						System.out.println("KEY_DIR_STOPPER got: "
								+ curFileParent);
						break;
					}
					// TODO read file parent
					String fileName = (String) ois.readObject();
					System.out.println("File to save : " + fileName);
					saveFile(getTargetLocation(), fileName);
					readObj = ois.readObject();
				}
			} catch (ClassNotFoundException cnfe) {
				showErrorMessage("ClassNotFoundException in saveDirectory()-107: \n"
						+ cnfe.toString());
			} catch (IOException ioe) {
				showErrorMessage("IOException in saveDirectory()-110: \n"
						+ ioe.toString());
			}
		}
	}

	private String getTargetLocation() {
		String curLocation = serverGUI.getDestinationPath() + "\\"
				+ curFileParent.substring(clientHead.length());
		System.out.println("To save file location: " + curLocation);
		return curLocation;
	}

	private void saveFile(String targetLocation, String fileName) {
		try {
			File f = new File(targetLocation, fileName);
			long fileSize = ois.readLong();
			if (f.mkdirs())
				System.out.println("Directories for the file created.");
			if(f.exists())
				f.delete();
			fos = new FileOutputStream(f);
			byte[] buffer = new byte[Utils.BUFFER_SIZE];
			System.out.println("File name: " + fileName + ", File size="
					+ fileSize);

			// 3. Read file to the end.
			int bytesRead = 0;
			long counter = 0L;
			do {
				readObj = ois.readObject();
				if (!(readObj instanceof Integer)) {
					throwException("Server :: read obj. isn't an int");
				}
				bytesRead = (int) readObj;

				readObj = ois.readObject();
				if (!(readObj instanceof byte[])) {
					throwException("Server :: read obj. isn't a byte[]");
				}
				buffer = (byte[]) readObj;

				// 4. Write data to output file.
				fos.write(buffer, 0, bytesRead);
				System.out.println("bytes read: " + bytesRead);
				counter++;
				if (fileSize > 0)
					serverGUI
							.setCurProgress((int) ((counter * bytesRead * 100) / fileSize));
				else
					serverGUI.setCurProgress(100);
			} while (bytesRead == Utils.BUFFER_SIZE);
			System.out.println("File save successful");
			serverGUI.setCurProgress(100);
		} catch (FileNotFoundException fnfe) {
			showErrorMessage("FileNotFoundException: Server-163::"
					+ fnfe.toString());
		} catch (IOException ioe) {
			showErrorMessage("IOException: Server-165::" + ioe.toString());
		} catch (ClassNotFoundException cnfe) {
			showErrorMessage("ClassNotFoundException: Server-167::"
					+ cnfe.toString());
		} catch (Exception e) {
			showErrorMessage("Exception: Server-169::" + e.toString());
		}
	}

	public static void throwException(String message) throws Exception {
		throw new Exception(message);
	}

	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "ErrOrrrrrrrr",
				JOptionPane.ERROR_MESSAGE);
	}

	public static void main(String[] args) {
		new Server().run();
	}

}
