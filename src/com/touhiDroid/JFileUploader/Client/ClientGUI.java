/**
 * 
 */
package com.touhiDroid.JFileUploader.Client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.touhiDroid.JFileUploader.Interfaces.UploadListener;
import com.touhiDroid.JFileUploader.Utils.Utils;

/**
 * @author Touhid
 * 
 */
@SuppressWarnings("serial")
public class ClientGUI extends JFrame {

	private UploadListener uploadListener;

	private JLabel labelURL = new JLabel("Server URL: ");
	private JTextField fieldURL = new JTextField("localhost", 50);
	private JLabel labelPort = new JLabel("Server PORT: ");
	private JTextField fieldPort = new JTextField("5018", 5);

	private JFileChooser fileChooser = new JFileChooser();

	private JButton buttonUpload = new JButton("Upload");

	private JLabel labelTotalProgress = new JLabel("Total Progress:");
	private JProgressBar totalProgBar = new JProgressBar(0, 100);

	private JLabel labelCurProgress = new JLabel("Current Progress:");
	private JProgressBar curProgBar = new JProgressBar(0, 100);

	public ClientGUI(UploadListener upListener) {
		super("File Uploader - Client Side :: 1005018");
		uploadListener = upListener;

		// set up layout
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);

		// set up components
		// fileChooser.setMode(JFilePicker.MODE_OPEN);

		buttonUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setProgressBarsVisible();
				File f = fileChooser.getSelectedFile();
				String port = fieldPort.getText();
				if (port.equals(null))
					port = Utils.DEFAULT_PORT + "";
				int portNumber = Integer.parseInt(port);
				// System.out.println("File name: " + f.getName()
				// + ", Server: " + fieldURL.getText() + ", Port number: "
				// + portNumber);
				if (portNumber < 1025 || portNumber > 65535)
					portNumber = Utils.DEFAULT_PORT;
				uploadListener.startUpload(f, fieldURL.getText(), portNumber);
			}
		});

		curProgBar.setPreferredSize(new Dimension(200, 30));
		curProgBar.setStringPainted(true);
		totalProgBar.setPreferredSize(new Dimension(200, 30));
		totalProgBar.setStringPainted(true);

		// add components to the frame
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(labelURL, constraints);

		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		add(fieldURL, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		add(labelPort, constraints);

		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		add(fieldPort, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		add(fileChooser, constraints);

		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		add(buttonUpload, constraints);

		pack();
		setLocationRelativeTo(null); // center on screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	private void setProgressBarsVisible() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelTotalProgress, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(totalProgBar, constraints);

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelCurProgress, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(curProgBar, constraints);
		pack();
		setLocationRelativeTo(null); // center on screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void setCurProgress(int progress){
		curProgBar.setValue(progress);
	}
	
	public void setTotalProgress(int progress){
		totalProgBar.setValue(progress);
	}

	// public void actionPerformed(ActionEvent ae) {
	// if (ae.getActionCommand().equals("Button1")) {
	// JOptionPane.showMessageDialog(null, "Button 1 Clicked");
	// }
	// if (ae.getActionCommand().equals("Button2")) {
	// JOptionPane.showMessageDialog(null, "Button 2 Clicked");
	// }
	// }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ClientGUI(null);
	}

}
