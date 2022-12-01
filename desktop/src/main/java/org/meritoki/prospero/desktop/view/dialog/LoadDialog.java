/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.desktop.view.dialog;

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
