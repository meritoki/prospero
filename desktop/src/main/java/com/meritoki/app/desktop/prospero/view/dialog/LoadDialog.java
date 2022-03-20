package com.meritoki.app.desktop.prospero.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoadDialog extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2490807514620882775L;

	public LoadDialog(java.awt.Frame parent, boolean modal) {
		JPanel panel = new JPanel(new GridBagLayout());
		Icon icon = new ImageIcon(this.getClass().getResource("/Load.gif"));
		JLabel label = new JLabel(icon);
		panel.add(label);
		panel.add(new JLabel("Please Wait..."), new GridBagConstraints());
		this.getContentPane().add(panel);
		this.setSize(128, 100);
		this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setModal(modal);
	}
}
