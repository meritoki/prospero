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
package org.meritoki.prospero.desktop.view.menu;

import java.awt.Image;
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

import org.meritoki.prospero.desktop.view.panel.CameraPanel;
import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.Camera;
import org.meritoki.prospero.library.model.node.Spheroid;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.node.cartography.Cartography;
import org.meritoki.prospero.library.model.terra.Terra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CameraPopupMenu extends JPopupMenu {
	/**
	 *
	 */
	private static final long serialVersionUID = 4405713496092773322L;
	static Logger logger = LoggerFactory.getLogger(CameraPopupMenu.class.getName());
	private JMenuItem saveMenuItem;
	private Model model;

	public CameraPopupMenu(Model m) {
		this.model = m;
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
					panel.repaint();
//					NodeController.savePanel(panel, path, "grid-" + uuid);
					for(Camera camera: model.cameraList) {
						if(camera != null) {
							Variable node = camera.getNode();
							Image image = camera.getImage();
							logger.info("CameraPopupMenu(model) image="+image);
							if(image != null) {
								String fileName;
								fileName = "grid-"+node.data+"-"+UUID.randomUUID().toString()+"-"+uuid+".png";
								try {
									NodeController.savePng(path, fileName, NodeController.toBufferedImage(image));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		});
		JMenu projectionMenu = new JMenu("Projection");
		JMenuItem defaultMenuItem = new JMenuItem("Default");
		defaultMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid) model.getCamera().getNode()).setSelectedProjection(Cartography.NULL);
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem equirectangularMenuItem = new JMenuItem("Equirectangular");
		equirectangularMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid) model.getCamera().getNode()).setSelectedProjection(Cartography.EQUIRECTANGULAR);
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem mercatorMenuItem = new JMenuItem("Mercator");
		mercatorMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid) model.getCamera().getNode()).setSelectedProjection(Cartography.MERCATOR);
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem azimuthalNorthMenuItem = new JMenuItem("Azimuthal North");
		azimuthalNorthMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid) model.getCamera().getNode()).setSelectedProjection(Cartography.AZIMUTHAL_NORTH);
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem azimuthalSouthMenuItem = new JMenuItem("Azimuthal South");
		azimuthalSouthMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((Spheroid) model.getCamera().getNode()).setSelectedProjection(Cartography.AZIMUTHAL_SOUTH);
				CameraPanel panel = (CameraPanel) getInvoker();
				panel.repaint();
			}
		});

		if (this.model.getCamera().getNode() instanceof Terra) {
			projectionMenu.add(defaultMenuItem);
			projectionMenu.add(equirectangularMenuItem);
			projectionMenu.add(mercatorMenuItem);
			projectionMenu.add(azimuthalNorthMenuItem);
			projectionMenu.add(azimuthalSouthMenuItem);
			this.add(projectionMenu);
		}
		this.add(this.saveMenuItem);
	}
}
//JMenuItem globeMenuItem = new JMenuItem("Globe");
//globeMenuItem.addActionListener(new ActionListener() {
//	public void actionPerformed(ActionEvent ev) {
//		model.setSelectedProjection(new Globe());
//		CameraPanel panel = (CameraPanel) getInvoker();
//		panel.repaint();
//	}
//});
