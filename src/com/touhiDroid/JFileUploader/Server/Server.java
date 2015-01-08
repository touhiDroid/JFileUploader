package com.touhiDroid.JFileUploader.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.touhiDroid.JFileUploader.Interfaces.ConstraintGetter;
import com.touhiDroid.JFileUploader.Utils.Utils;
import com.touhiDroid.JFileUploader.models.ServerConstraint;

/**
 * @author Touhid
 */
public class Server implements Runnable, ConstraintGetter {

	private ServerGUI serverGUI;
	private ServerConstraint serverConstraint;
	private static Server server;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private FileOutputStream fos;
	private Object readObj;

	private String clientHead = "";
	private String curFileParent = "";
	private String destPath = "C:\\";
	private long studentId = 201005018;

	private Server() {
		studentId = 201005018;
		new ServerConstraintGUI((ConstraintGetter) this).setVisible(true);
	}

	@Override
	public void run() {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				System.out.println("showing server GUI");
				serverGUI = new ServerGUI();
				serverGUI.setVisible(true);
			}
		});

		try {
			System.out.println("Server constraints::\n"
					+ serverConstraint.toString());
			int p = Utils.DEFAULT_PORT;
			if (serverConstraint != null) {
				p = serverConstraint.getServerPort();
			}
			if (serverGUI != null)
				serverGUI.writeMessage("Waiting for client "
						+ "request at port number " + p + "...");
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(p);
			while (true) {
				System.out.println("Server is running! Waiting for file ...");
				Socket s = serverSocket.accept();
				System.out.println("Request accepted from InetAddress:"
						+ s.getInetAddress()
						+ " ...\nNow sending constraints...");
				// serverGUI.setProgressBarsVisible();
				sendServerConstraints(s);
				System.out.println("Student ID Got as " + studentId
						+ ", now receiving file");
				saveFileOrDirectory(s);
				// serverGUI.setProgressBarsInvisible();
			}
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendServerConstraints(Socket socket) {

		try {
			ois = new ObjectInputStream(socket.getInputStream());
			studentId = (long) ois.readLong();
			System.out.println("sendServerConstraints :: Student ID Read: "
					+ studentId);
			destPath = serverConstraint.getRootDirectory() + "\\" + studentId;
			System.out.println(destPath);

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject((ServerConstraint) serverConstraint);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveFileOrDirectory(final Socket socket) {
		try {
			if (ois == null)
				ois = new ObjectInputStream(socket.getInputStream());
			if (oos == null)
				oos = new ObjectOutputStream(socket.getOutputStream());
			fos = null;
			// 0. read the amount of files/folders
			int nf = ois.readInt();
			System.out.println("No. of files: " + nf);
			while (nf > 0) {
				// 1. Read is_directory indicator / file name for single file
				readObj = ois.readObject();
				if (readObj instanceof String) {
					if (readObj.toString().equals(Utils.KEY_DIRECTORY)) {
						// 2a. save the directory iteratively
						clientHead = (String) ois.readObject();// 2 decide
																// directory
						System.out.println("Client head: " + clientHead);
						saveDirectory((File) ois.readObject());
					} else {
						// 2b. save the requested single file
						curFileParent = readObj.toString();
						// TODO correct target
						saveFile(destPath, (String) ois.readObject());
					}
				} else {
					throwException("Server :: Filename can't be found or,"
							+ " isDirectory indic. can't be matched!");
				}
				--nf;
			}
			fos.close();
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

		File dir = new File(destPath, dirName);
		System.out.println("Dir. parent: " + dir.getParent());
		System.out.println("New Dir. to create: " + dir.getAbsolutePath());

		if (dir.mkdirs()) {
			System.out.println("Directory created: " + dirName);
			// TODO list & save files
			try {
				oos.writeObject("Directory created: " + dirName);
				oos.flush();
				readObj = ois.readObject();
				while (readObj instanceof String) {
					curFileParent = readObj.toString();
					if (curFileParent.equals(Utils.KEY_DIR_STOPPER)) {
						System.out.println("KEY_DIR_STOPPER got: "
								+ curFileParent);
						break;
					}
					String fileName = (String) ois.readObject();
					System.out.println("File to save : " + fileName);
					saveFile(getTargetLocation(), fileName);
					readObj = ois.readObject();
				}
			} catch (ClassNotFoundException cnfe) {
				showErrorMessage("ClassNotFoundException in saveDirectory()-175: \n"
						+ cnfe.toString());
			} catch (IOException ioe) {
				showErrorMessage("IOException in saveDirectory()-110: \n"
						+ ioe.toString());
			}
		} else {
			System.out.println("Failed to create directory! :: "
					+ dir.getAbsolutePath());
			try {
				oos.writeObject("Failed to create directory! :: "
						+ dir.getAbsolutePath());
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			showErrorMessage("Server :: Directory creation have failed!"
					+ "\nMay be this directory already exists: "
					+ dir.getAbsolutePath());
		}
	}

	private String getTargetLocation() {
		String curLocation = destPath + "\\"
				+ curFileParent.substring(clientHead.length());
		System.out.println("To save file location: " + curLocation);
		return curLocation;
	}

	private void saveFile(String targetLocation, String fileName) {
		try {
			System.out.println("Target Localtion: " + targetLocation
					+ ", fileName: " + fileName);
			File f = new File(targetLocation, fileName);
			long fileSize = ois.readLong();
			if (f.mkdirs())
				System.out.println("Directories for the file created.");
			if (f.exists())
				f.delete();
			fos = new FileOutputStream(f);
			byte[] buffer = new byte[Utils.BUFFER_SIZE];
			System.out.println("File name: " + fileName + ", File size="
					+ fileSize);

			// 3. Read file to the end.
			int bytesRead = 0;
			int counter = 0;
			do {
				counter = ois.readInt();
				System.out
						.println("**** saveFile::\n ** Chunk no.: " + counter);
				readObj = ois.readObject();
				if (!(readObj instanceof Integer)) {
					throwException("Server :: read obj. isn't an int");
				}
				bytesRead = (int) readObj;
				System.out.println("** Chunk no.: " + counter + ", bytes read="
						+ bytesRead);

				readObj = ois.readObject();
				if (!(readObj instanceof byte[])) {
					throwException("Server :: read obj. isn't a byte[]");
				}
				buffer = (byte[]) readObj;

				// 4. Write data to output file.
				fos.write(buffer, 0, bytesRead);
				System.out.println("bytes read: " + bytesRead);
				// TODO send ack512
				oos.writeObject("ack512-" + counter);
				oos.flush();
				// counter++;
				// if (fileSize > 0)
				// serverGUI
				// .setCurProgress((int) ((counter * bytesRead * 100) /
				// fileSize));
				// else
				// serverGUI.setCurProgress(100);
			} while (bytesRead == Utils.BUFFER_SIZE);
			System.out.println("File save successful");
			// serverGUI.setCurProgress(100);
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

	@SuppressWarnings("unused")
	private void showWarning(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Warning!",
				JOptionPane.WARNING_MESSAGE);
	}

	public static void main(String[] args) {
		System.out.println("Getting server...");
		server = new Server();

	}

	@Override
	public ServerConstraint getServerConstraints() {
		return serverConstraint;
	}

	@Override
	public void setServerConstraints(ServerConstraint sc) {
		serverConstraint = sc;
		// TODO Fix destination path from the client's sent student ID
		destPath = serverConstraint.getRootDirectory() + "\\" + studentId;
		System.out.println("Server contraint set");

		server.run();
	}

}
