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
package org.meritoki.prospero.desktop.view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import org.meritoki.prospero.desktop.view.menu.PlotPopupMenu;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.plot.Plot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jorodriguez
 */
public class PlotPanel extends javax.swing.JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7660685341781127285L;
	static Logger logger = LoggerFactory.getLogger(PlotPanel.class.getName());
	public PlotPopupMenu plotPopupMenu;
	public Model model;
	public Plot plot;
	public List<Plot> plotList;
	public List<Image> imageList;

	/**
	 * Creates new form TrackPanel
	 */
	public PlotPanel() {
		this.addMouseListener(this);
		this.setFocusable(true);
		this.setBackground(Color.white);
	}

	public void setModel(Model model) {
		this.model = model;
		this.plotPopupMenu = new PlotPopupMenu(this.model);
	}

	public void init() {
		if (this.model != null) {
			try {
				this.plotList = this.model.getPlotList();
				int width = this.getWidth();
				int height = 256;
				for (int i = 0; i < this.plotList.size(); i++) {
					Plot plot = this.plotList.get(i);
					if (plot != null) {
//						graphics.setColor(Color.white);
						plot.setPanelWidth(width);
						plot.setPanelHeight(height);
						Image image = createImage(width, height);
						image = plot.getImage(image);
						plot.setImage(image);
//						graphics.drawImage(image, 0, i * height, null);
					}
				}
				this.setPreferredSize(new Dimension(width, (this.plotList.size()) * height));
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.repaint();
		}
	}

	public void setPlot(Plot plot) {
		this.plot = plot;
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		if (this.model != null) {
			for (int i = 0; i < this.plotList.size(); i++) {
				Plot plot = this.plotList.get(i);
				if (plot != null) {
					graphics.setColor(Color.white);
					Image image = plot.getImage();
					graphics.drawImage(image, 0, i * 256, null);
				}
			}
		}
	}

//	@Override
//	public void paint(Graphics graphics) {
//		super.paint(graphics);
//		if (this.model != null) {
//			try {
//				this.plotList = this.model.getPlotList();
//				int width = this.getWidth();
//				int height = 256;
//				for (int i = 0; i < this.plotList.size();i++) {
//					Plot plot = this.plotList.get(i);
//					if(plot != null) {
//						graphics.setColor(Color.white);
//						plot.setPanelWidth(width);
//						plot.setPanelHeight(height);
//						Image image = createImage(width, height);
//						image = plot.getImage(image);
//						plot.setImage(image);
//						graphics.drawImage(image, 0, i * height, null);
//					}
//				} 
//				this.setPreferredSize(new Dimension(width,(this.plotList.size())*height));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	private void showPlotPopupMenu(MouseEvent e) {
		System.out.println("showSavepopupMenu(" + e + ")");
		if (plotPopupMenu != null) {
			System.out.println("showSavepopupMenu(" + e + ") savePopupMenu != null");
			plotPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.requestFocus();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		e.consume();
		if (e.isPopupTrigger())
			showPlotPopupMenu(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

//	/**
//	 * This method is called from within the constructor to initialize the form.
//	 * WARNING: Do NOT modify this code. The content of this method is always
//	 * regenerated by the Form Editor.
//	 */
//	@SuppressWarnings("unchecked")
//	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
	}// </editor-fold>//GEN-END:initComponents
//
	// Variables declaration - do not modify//GEN-BEGIN:variables
	// End of variables declaration//GEN-END:variables
}
//public void init() {
//if (this.model != null) {
//	logger.debug("init() this.model != null");
//	this.removeAll();
//	logger.debug("init() this.removeAll()");
//	this.setBackground(Color.white);
//	this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
//	try {
//		List<Plot> plotList = this.model.getPlotList();
//		for (Plot plot : plotList) {
//			PlotPanel plotPanel = new PlotPanel(plot);
//			plotPanel.setModel(this.model);
//			this.add(plotPanel);
//			this.revalidate();
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
////	this.revalidate();
//	this.repaint();
//}
//}
