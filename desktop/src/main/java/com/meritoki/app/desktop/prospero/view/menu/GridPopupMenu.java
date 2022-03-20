package com.meritoki.app.desktop.prospero.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
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

import com.meritoki.app.desktop.prospero.controller.node.NodeController;
import com.meritoki.library.prospero.model.Model;
import com.meritoki.library.prospero.model.terra.cartography.AzimuthalNorth;
import com.meritoki.library.prospero.model.terra.cartography.AzimuthalSouth;
import com.meritoki.library.prospero.model.terra.cartography.Equirectangular;
import com.meritoki.library.prospero.model.terra.cartography.Globe;
import com.meritoki.library.prospero.model.terra.cartography.Mercator;
import com.meritoki.app.desktop.prospero.view.panel.GridPanel;
import com.meritoki.app.desktop.prospero.view.panel.SolarPanel;

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
				if (panel instanceof GridPanel) {
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
				model.setProjection(new Equirectangular());
				GridPanel panel = (GridPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem globeMenuItem = new JMenuItem("Globe");
		globeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				model.setProjection(new Globe());
				GridPanel panel = (GridPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem mercatorMenuItem = new JMenuItem("Mercator");
		mercatorMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				model.setProjection(new Mercator());
				GridPanel panel = (GridPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem azimuthalNorthMenuItem = new JMenuItem("Azimuthal North");
		azimuthalNorthMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				model.setProjection(new AzimuthalNorth());
				GridPanel panel = (GridPanel) getInvoker();
				panel.repaint();
			}
		});
		JMenuItem azimuthalSouthMenuItem = new JMenuItem("Azimuthal South");
		azimuthalSouthMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				model.setProjection(new AzimuthalSouth());
				GridPanel panel = (GridPanel) getInvoker();
				panel.repaint();
			}
		});
		projectionMenu.add(globeMenuItem);
		projectionMenu.add(equirectangularMenuItem);
		projectionMenu.add(mercatorMenuItem);
		projectionMenu.add(azimuthalNorthMenuItem);
		projectionMenu.add(azimuthalSouthMenuItem);
		this.add(projectionMenu);
		this.add(this.saveMenuItem);
	}
}
