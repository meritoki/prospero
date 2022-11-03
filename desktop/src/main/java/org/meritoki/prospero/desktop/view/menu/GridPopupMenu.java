/*
 * Copyright 2022 Joaquin Osvaldo Rodriguez
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
package org.meritoki.prospero.desktop.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.desktop.controller.node.NodeController;
import org.meritoki.prospero.desktop.view.panel.CameraPanel;
import org.meritoki.prospero.desktop.view.panel.SolarPanel;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.Spheroid;
import org.meritoki.prospero.library.model.terra.cartography.AzimuthalNorth;
import org.meritoki.prospero.library.model.terra.cartography.AzimuthalSouth;
import org.meritoki.prospero.library.model.terra.cartography.Equirectangular;
import org.meritoki.prospero.library.model.terra.cartography.Globe;
import org.meritoki.prospero.library.model.terra.cartography.Mercator;

public class GridPopupMenu extends JPopupMenu {
	/**
	 *
	 */
	private static final long serialVersionUID = 4405713496092773322L;
	static Logger logger = LogManager.getLogger(GridPopupMenu.class.getName());
	private JMenuItem saveMenuItem;
	private Model model;

	public GridPopupMenu(Model model) {
		this.model = model;
		this.saveMenuItem = new JMenuItem("Save");

		this.saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Date dateTime = Calendar.getInstance().getTime();
				String date = new SimpleDateFormat("yyyyMMdd").format(dateTime);
				String time = new SimpleDateFormat("HHmm").format(dateTime);
				String path = "output" + File.separatorChar + date + File.separatorChar + time;
				File directory = new File(path);
				if (!directory.exists()) {
					directory.mkdirs();
				}
				String uuid = UUID.randomUUID().toString();
				JPanel panel = (JPanel) getInvoker();
				if (panel instanceof CameraPanel) {
					NodeController.savePanel(panel, path, "grid-" + uuid);
				} else if (panel instanceof SolarPanel) {
					NodeController.savePanel(panel, path, "solar-" + uuid);
				}
			}
		});
		JMenu projectionMenu = new JMenu("Projection");
		JMenuItem equirectangularMenuItem = new JMenuItem("Equirectangular");
		equirectangularMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid)model.node).setProjection(new Equirectangular());
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});
//		JMenuItem globeMenuItem = new JMenuItem("Globe");
//		globeMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
//				model.setProjection(new Globe());
//				CameraPanel panel = (CameraPanel) getInvoker();
//				panel.repaint();
//			}
//		});
		JMenuItem mercatorMenuItem = new JMenuItem("Mercator");
		mercatorMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid)model.node).setProjection(new Mercator());
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem azimuthalNorthMenuItem = new JMenuItem("Azimuthal North");
		azimuthalNorthMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid)model.node).setProjection(new AzimuthalNorth());
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem azimuthalSouthMenuItem = new JMenuItem("Azimuthal South");
		azimuthalSouthMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid)model.node).setProjection(new AzimuthalSouth());
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});
//		projectionMenu.add(globeMenuItem);
		projectionMenu.add(equirectangularMenuItem);
		projectionMenu.add(mercatorMenuItem);
		projectionMenu.add(azimuthalNorthMenuItem);
		projectionMenu.add(azimuthalSouthMenuItem);
		this.add(projectionMenu);
		this.add(this.saveMenuItem);
	}
}
