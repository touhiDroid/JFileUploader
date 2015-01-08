package com.touhiDroid.JFileUploader.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.touhiDroid.JFileUploader.Interfaces.UploadListener;
import com.touhiDroid.JFileUploader.Utils.Utils;
import com.touhiDroid.JFileUploader.models.ServerConstraint;

public class Client implements UploadListener {

	private ServerConstraint serverConstraints;

	private ClientGUI clientGUI;
	private Socket clientSocket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private ArrayList<String> uploadedFileList;

	// private String serverAddress;
	// private int serverPort;

	private void uploadFile(File file) {
		Long fileSize = (Long) file.length();
		System.out.println("File name: " + file.getName() + ", File size: "
				+ fileSize);
		clientGUI.writeMessage("File name: " + file.getName() + ", File size: "
				+ fileSize);
		// ObjectInputStream ois = new
		// ObjectInputStream(clientSocket.getInputStream());
		try {
			String fileParent = file.getParent();
			System.out.println("Uploading file's parent: " + fileParent);
			oos.writeObject(fileParent);
			oos.writeObject(file.getName());
			oos.writeLong(fileSize);

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[Utils.BUFFER_SIZE];
			int bytesRead = 0;

			int counter = 0;
			while ((bytesRead = fis.read(buffer)) > 0) {
				oos.writeInt(counter);// Starting byte number
				oos.writeObject(bytesRead); // Size of the segment
				// Write file data as 512 bytes each time
				oos.writeObject(Arrays.copyOf(buffer, buffer.length));
				System.out.println(">> No. of segement: " + counter
						+ "\n   Bytes written: " + bytesRead);
				counter++;
				// TODO read ack512.
				try {
					String ack = (String) ois.readObject();
					if (ack.startsWith("ack512")) {
						System.out.println("** Acknwledgement read :) :: "
								+ ack);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					showErrorMessage("Error reading acknowledment signal");
				}
				// if (fileSize > 0)
				// clientGUI
				// .setCurProgress((int) ((++counter * bytesRead * 100) /
				// fileSize));
			}
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File transfer complete.");
		uploadedFileList.add(file.getName());
	}

	/**
	 * Upload only the parent-directory first, then all the files inside it with
	 * any level of listing
	 */
	private void uploadDirectory(File directory) {
		System.out.println("Directory name: " + directory.getName()
				+ ", Directory file amount: " + directory.list().length);
		try {
			oos.writeObject(Utils.KEY_DIRECTORY);
			// Tell the server the client's head for directory making decision
			oos.writeObject(directory.getParent());
			oos.writeObject(directory);
			String s = (String) ois.readObject();
			if (s.startsWith("Failed")) {
				showErrorMessage("Something has gone wrong in the server!\n"
						+ s);
				oos.close();
				ois.close();
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// List & upload only files with any nested listing
		File[] fileList = directory.listFiles();
		// int counter = 0;
		for (File f : fileList) {
			if (f.isFile())
				uploadFile(f);
			else
				uploadFolder(f);
			// clientGUI
			// .setTotalProgress((int) (++counter * 100 / fileList.length));
		}
		// Tell the server to stop accepting any more files for this time
		System.out.println(">>>>>> Now Stopping the transfer <<<<<");
		// clientGUI.setTotalProgress(100);
		try {
			oos.writeObject(Utils.KEY_DIR_STOPPER);
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

	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "ErrOrrrrrrrr",
				JOptionPane.ERROR_MESSAGE);
	}

	public static void main(String[] args) {
		// listIPAddresses();
		new Client(args);
	}

	@SuppressWarnings("unused")
	private static void listIPAddresses() {
		try {
			InetAddress localhost = InetAddress.getLocalHost();

			// this code assumes IPv4 is used
			byte[] ip = localhost.getAddress();
			System.out.println("Raw IP: " + ip[0] + "." + ip[1] + "." + ip[2]);

			for (int i = 1; i <= 254; i++) {
				ip[3] = (byte) i;
				InetAddress address;
				address = InetAddress.getByAddress(ip);
				if (address.isReachable(100)) {
					// machine is turned on and can be pinged
					System.out.println("Address reachable: "
							+ address.getHostAddress());
				} else if (!address.getHostAddress().equals(
						address.getHostName())) {
					// machine is known in a DNS lookup
					System.out.println("Address known but unreachable: "
							+ address.getHostAddress());
				} else {
					// the host address and host name are equal, meaning the
					// host
					// name could not be resolved
					System.out
							.println("the host address and host name are equal,"
									+ "meaning the host name could not be resolved "
									+ address.getHostAddress());
				}
			}
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public Client(String[] args) {
		String fileName = "";
		uploadedFileList = new ArrayList<String>();
		try {
			fileName = args[0];
			File[] fa = new File[] { new File(fileName) };
			startUpload(fa, Utils.DEFAULT_DESTINATION, Utils.DEFAULT_PORT);
		} catch (Exception e) {
			// System.out.println("Enter the name of the file :");
			// fileName = new Scanner(System.in).nextLine();
			clientGUI = new ClientGUI((UploadListener) this);
			clientGUI.setVisible(true);
		}
	}

	@Override
	public void connectAndGetServerConstraints(String serverAddress,
			int serverPort) {
		try {
			InetAddress ia = InetAddress.getByName(serverAddress);
			System.out.println("InetAddress: " + ia + ", port=" + serverPort);
			clientSocket = new Socket(ia, serverPort);

			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			long id = clientGUI.getStudentID();
			oos.writeLong(id);
			oos.flush();
			System.out.println("Student ID written: " + id);

			ois = new ObjectInputStream(clientSocket.getInputStream());
			ServerConstraint sc = (ServerConstraint) ois.readObject();
			System.out.println(sc.toString());

			if (checkStudentIDAndSetConstraints(sc)) {
				clientGUI.writeMessage(sc.toString());
			} else {
				clientSocket.close();
				oos.close();
				ois.close();
				showErrorMessage("Please try again with a new student ID.");
			}
		} catch (SocketException se) {
			showErrorMessage("SocketException caught during "
					+ "initializing client's socket.\n" + se.toString());
			se.printStackTrace();
		} catch (IOException ioe) {
			showErrorMessage("IOException caught during "
					+ "initializing client's connection.\n" + ioe.toString());
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void startUpload(File[] files, String serverAddress, int serverPort) {

		for (File f : files) {
			if (f.isFile() && !checkConstraints(f)) {
				// TO_DO add file filter as types & check parsed file names
				showErrorMessage("Please try again with revised constraints.");
				System.err.println("file constraints failed!");
				return;
			}
			if (uploadedFileList.contains(f.getName())) {
				System.out.println("File is already uploaded!"
						+ " Confirming resubmission ...");
				JOptionPane pane = new JOptionPane(
						"This file is already uploaded!\n"
								+ "Would you like to overwrite or upload a copy?");
				Object[] options = new String[] { "Overwrite", "Upload A Copy" };
				JDialog dialog = pane.createDialog(new JFrame(),
						"Confirm Resubmission");
				dialog.show();
				Object obj = pane.getValue();
				int r = -1;
				for (int k = 0; k < options.length; k++)
					if (options[k].equals(obj))
						r = k;
				if (r == 1)
					f.renameTo(new File("Copy_of_" + f.getName()));
			} else {
				System.out.println("New file's got to upload ...");
			}
		}

		try {
			// tell server the file amount b4 sending any file
			System.out.println("All constraints ok.");
			oos.writeInt(files.length);
			oos.flush();
			// Now send all files / folders
			for (File file : files) {
				System.out.println("Trying to upload file: "
						+ file.getAbsolutePath());
				if (file.isFile()) {
					uploadFile(file);
				} else {
					uploadDirectory(file);
				}
			}
			oos.close();
			ois.close();
			clientSocket.close();
		} catch (SocketException se) {
			showErrorMessage("SocketException caught during "
					+ "initializing client's socket.\n" + se.toString());
		} catch (IOException ioe) {
			showErrorMessage("IOException caught during "
					+ "initializing client's connection.\n" + ioe.toString());
		}
	}

	private boolean checkStudentIDAndSetConstraints(ServerConstraint sc) {
		this.serverConstraints = sc;
		long sID = clientGUI.getStudentID();
		if (isStudentIdInRange(sID)) {
			clientGUI.resetFileChooser(sc);
			clientGUI.setBrowserVisible(true);
			return true;
		} else {
			showErrorMessage("Student ID Not in range.");
			return false;
		}
	}

	private boolean isStudentIdInRange(long sID) {
		long s = serverConstraints.getsIdStart(), e = serverConstraints
				.getsIdEnd();
		if (sID >= s && sID <= e)
			return true;
		StringTokenizer sTokens = new StringTokenizer(
				serverConstraints.getsIds(), ", ");
		while (sTokens.hasMoreTokens()) {
			if (sID == Long.parseLong(sTokens.nextToken()))
				return true;
		}
		return false;
	}

	private boolean checkConstraints(File file) {
		// Check file extension
		String fileName = file.getName();
		String ext = fileName.substring(fileName.lastIndexOf('.'))
				.toLowerCase();
		ArrayList<String> fextList = ServerConstraint
				.parseFileTypes(serverConstraints.getFileTypes());
		boolean f = false;
		for (String s : fextList)
			if (ext.equals(s)) {
				f = true;
				break;
			}
		if (!f) {
			showErrorMessage("The selected file type \'*" + ext
					+ "\' is not permitted!");
			return false;
		}

		// Check file name
		String fNames = serverConstraints.getFileNames();
		if (fNames.equals(null) || fNames.length() == 0)
			return true;
		ArrayList<String> fNameList = ServerConstraint
				.parseFileNames(serverConstraints.getFileNames());
		f = false;
		ext = file.getName();
		for (String s : fNameList)
			if (ext.equals(s)) {
				f = true;
				break;
			}
		if (!f) {
			showErrorMessage("The selected file name \'*" + ext
					+ "\' is not permitted!");
			return false;
		}

		// Check file size
		System.out.println("checkConstraints :: File length: "+file.length());
		if (file.length() > (serverConstraints.getMaxFileSizeInKB() / 1024)) {
			showErrorMessage("The selected file name \'*" + ext
					+ "\' is not permitted!");
			return false;
		}

		return false;
	}
}
