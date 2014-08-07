/**
 * 
 */
package com.touhiDroid.JFileUploader.Server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.touhiDroid.JFileUploader.Utils.Constants;

/**
 * @author Touhid
 * 
 */
@SuppressWarnings("serial")
public class ServerGUI extends JFrame {

	private JLabel labelDest = new JLabel("Destination path: ");
	private JTextField fieldDest = new JTextField(50);
	private JLabel labelPort = new JLabel("Server PORT: ");
	private JTextField fieldPort = new JTextField("5018", 50);

	private JLabel labelWaitingMsg = new JLabel(
			"Waiting for any client request ...");

	// private JFileChooser fileChooser = new JFileChooser();

	// private JButton buttonUpload = new JButton("Upload");
	private JButton btnPathSet = new JButton("Set Path");
	private JButton btnPortSet = new JButton("Set Port");

	private JLabel labelTotalProgress = new JLabel("Total Progress:");
	private JProgressBar totalProgBar = new JProgressBar(0, 100);

	private JLabel labelCurProgress = new JLabel("Current Progress:");
	private JProgressBar curProgBar = new JProgressBar(0, 100);

	private int serverPort = 5018;
	private String serverPath = "C:\\";

	public ServerGUI() {
		super("File Uploader - Server Side :: 1005018");

		// set up layout
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);

		// set up components
		// fileChooser.setMode(JFilePicker.MODE_OPEN);

		btnPathSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serverPath = fieldDest.getText();
				showMessage("Server save location set to: " + serverPath);
			}
		});
		btnPortSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serverPort = Integer.parseInt(fieldPort.getText());
				showMessage("Server-port set to: " + serverPort);
			}
		});

		curProgBar.setPreferredSize(new Dimension(125, 25));
		curProgBar.setStringPainted(true);
		totalProgBar.setPreferredSize(new Dimension(125, 25));
		totalProgBar.setStringPainted(true);

		// add components to the frame
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		add(labelDest, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridwidth = 1;
		add(fieldDest, constraints);

		constraints.gridx = 2;
		constraints.weightx = 1.0;
		constraints.gridwidth = 1;
		add(btnPathSet, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		add(labelPort, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridwidth = 1;
		add(fieldPort, constraints);

		constraints.gridx = 2;
		constraints.weightx = 1.0;
		constraints.gridwidth = 1;
		add(btnPortSet, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.CENTER;
		constraints.weightx = 1.0;
		add(labelWaitingMsg, constraints);

		pack();
		setLocationRelativeTo(null); // center on screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	public void setProgressBarsVisible() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		
		labelWaitingMsg.setVisible(false);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelTotalProgress, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(totalProgBar, constraints);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelCurProgress, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(curProgBar, constraints);
		add(btnPortSet, constraints);

		pack();
		setLocationRelativeTo(null); // center on screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void setProgressBarsInvisible() {
		labelWaitingMsg.setVisible(true);
		labelCurProgress.setVisible(false);
		curProgBar.setVisible(false);
		labelTotalProgress.setVisible(false);
		totalProgBar.setVisible(false);
	}

	public int getServerPortNumber() {
		if (serverPort <= 1024)
			serverPort = Constants.DEFAULT_PORT;
		return serverPort;
	}

	public String getDestinationPath() {
		if (serverPath.equals(null) || serverPath.length() == 0)
			serverPath = Constants.DEFAULT_DESTINATION;
		return serverPath;
	}
	
	public void setCurProgress(int progress){
		curProgBar.setValue(progress);
	}
	
	public void setTotalProgress(int progress){
		totalProgBar.setValue(progress);
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Server Info.",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ServerGUI();
	}

}
