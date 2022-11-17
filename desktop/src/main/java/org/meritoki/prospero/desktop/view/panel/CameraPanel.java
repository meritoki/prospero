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

//import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.desktop.view.menu.CameraPopupMenu;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.Orbital;
import org.meritoki.prospero.library.model.node.Spheroid;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.solar.Solar;
import org.meritoki.prospero.library.model.unit.Dimension;

/**
 *
 * @author jorodriguez
 */
public class CameraPanel extends javax.swing.JPanel
		implements KeyListener, MouseListener, MouseMotionListener, Runnable {

	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(CameraPanel.class.getName());
	protected CameraPopupMenu menu;
	public Model model;
//	public Variable node;
	public double factor = 1.5;
	protected int xDelta, yDelta;
	protected double azimuth = 0;
	protected double elevation = 0;
	protected double scale = 1;
	public Dimension dimension;
//	public List<Variable> nodeList = new ArrayList<>();
//	public int model.index = 0;

	/**
	 * Creates new form CameraPanel
	 */
	public CameraPanel() {
		this.initComponents();
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
//		this.setSize(1024, 512);
//		Thread thread = new Thread(this);
//		thread.start();
	}

	public void setModel(Model model) {
		this.model = model;
		this.menu = new CameraPopupMenu(this.model);
	}
	
	public void init() {
		if(this.model != null) {
		Dimension dimension = new Dimension(this.getWidth(), this.getHeight());
		this.setPreferredSize(new java.awt.Dimension((int) dimension.width,
		(int) ((this.model.nodeList.size()) * dimension.height)));
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				this.repaint();
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (this.model.getNode() != null) {
			int xDelta = e.getX();
			int yDelta = e.getY();
			this.azimuth -= xDelta - this.xDelta;
			this.elevation -= yDelta - this.yDelta;
			if (this.model.getNode() instanceof Spheroid) {
				Spheroid s = (Spheroid) this.model.getNode();
				s.setAzimuth(this.azimuth);
				s.setElevation(this.elevation);
			}
			this.xDelta = xDelta;
			this.yDelta = yDelta;
			this.repaint();
			e.consume();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		this.requestFocus();

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.xDelta = e.getX();
		this.yDelta = e.getY();
		e.consume();
		if (e.isPopupTrigger())
			showSavePopupMenu(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger())
			showSavePopupMenu(e);

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		logger.debug("keyPressed(" + e + ")");
		if (this.model != null && this.model.getNode() != null) {
			if (this.model.getNode() instanceof Spheroid) {
				Spheroid s = (Spheroid) this.model.getNode();
				double scale = s.getProjection().scale;
				e.consume();
				if (e.isControlDown()) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_UP: {
						if (model.index > 0) {
							model.index -= 1;
							this.repaint();
						}
						logger.info("keyPressed(e) UP model.index=" + model.index);
						break;
					}
					case KeyEvent.VK_LEFT: {
						if (model.index > 0) {
							model.index -= 1;
							this.repaint();
						}
						logger.info("keyPressed(e) LEFT model.index=" + model.index);
						break;
					}
					case KeyEvent.VK_DOWN: {
						if (model.index < this.model.nodeList.size() - 1) {
							model.index += 1;
							this.repaint();
						}
						logger.info("keyPressed(e) DOWN model.index=" + model.index);
						break;
					}
					case KeyEvent.VK_RIGHT: {
						if (model.index < this.model.nodeList.size() - 1) {
							model.index += 1;
							this.repaint();
						}
						logger.info("keyPressed(e) RIGHT model.index=" + model.index);
						break;
					}
					}
					switch (e.getKeyChar()) {
					case '+': {
						s.setScale(scale * 2);
						this.repaint();
						break;
					}
					case '-': {
						s.setScale(scale / 2);
						this.repaint();
						break;
					}
//					case KeyEvent.VK_UP: {
//						model.index = (model.index > 0)?model.index-1:model.index;
//						this.repaint();
//						break;
//					}
//					case KeyEvent.VK_DOWN: {
//						model.index = (model.index < this.nodeList.size())?model.index+1:model.index;
//						this.repaint();
//						break;
//					}
					case '1': {// bottom
						this.azimuth = 0;
						this.elevation = 0;
						s.setAzimuth(this.azimuth);
						s.setElevation(this.elevation);
						repaint();
						break;
					}
					case '2': {// top
						this.azimuth = 180;
						this.elevation = 0;
						s.setAzimuth(this.azimuth);
						s.setElevation(this.elevation);
						repaint();
						break;
					}
					case '3': {
						this.azimuth = 0;
						this.elevation = -90;
						s.setAzimuth(this.azimuth);
						s.setElevation(this.elevation);
						repaint();
						break;
					}
					case '4': {
						this.azimuth = 180;
						this.elevation = 90;
						s.setAzimuth(this.azimuth);
						s.setElevation(this.elevation);
						repaint();
						break;
					}
					case '5': {
						this.azimuth = 90;
						this.elevation = 0;
						s.setAzimuth(this.azimuth);
						s.setElevation(this.elevation);
						repaint();
						break;
					}
					case '6': {
						this.azimuth = -90;
						this.elevation = 0;
						s.setAzimuth(this.azimuth);
						s.setElevation(this.elevation);
						repaint();
						break;
					}
					case '7': {
						this.azimuth = 0;
						this.elevation = 55;
						s.setAzimuth(this.azimuth);
						s.setElevation(this.elevation);
						repaint();
						break;
					}
					case '8': {
						this.azimuth = 23;
						this.elevation = 35;
						s.setAzimuth(this.azimuth);
						s.setElevation(this.elevation);
						repaint();
						break;
					}
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	private void showSavePopupMenu(MouseEvent e) {
		this.menu = new CameraPopupMenu(this.model);
		if (menu != null)
			menu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		logger.debug("paint(" + (graphics != null) + ")");
		if (this.model != null) {
			Dimension dimension = new Dimension(this.getWidth(), this.getHeight());
			try {
				logger.debug(
						"paint(" + (graphics != null) + ") this.model.nodeList.size()=" + this.model.nodeList.size());
				
				if (this.model.nodeList.size() > 0) {
					for (int i = 0; i < this.model.nodeList.size(); i++) {
						Image image = createImage((int) dimension.width, (int) dimension.height);
						Variable node = this.model.getNode(i);
						if (node != null) {
							logger.debug("paint(" + (graphics != null) + ") node=" + node);
							node.dimension = dimension;
//							this.processNode(node);
							image = node.getImage(image);
							node.setImage(image);
						}
//						graphics.drawImage(image, 0, (int)(i*dimension.height), null);
						if (i == model.index) {
							graphics.drawImage(image, 0, 0, null);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
//	this.setPreferredSize(new java.awt.Dimension((int) dimension.width,
//	(int) ((this.nodeList.size()) * dimension.height)));

//	public Variable processNode(Variable node) {
//		logger.debug("processNode(" + node + ")");
//		Variable n = null;
//		if (node instanceof Solar) {
//			this.model.solar.setCenter(this.model.solar.sun.space);// Must Include Sun b/c Solar is Not Orbital
//			this.model.solar.setScale(this.model.solar.defaultScale);
//			this.model.solar.setAzimuth(this.model.defaultAzimuth);
//			this.model.solar.setElevation(this.model.defaultElevation);
//		} else if (node instanceof Orbital) {// 20221105 Why? To Re-Center Orbital in Screen w/ Respect to Time
////			logger.debug("paint(" + (graphics != null) + ") " + node + " instanceof Orbital");
//			logger.debug("processNode(" + node + ") instanceof Orbital");
//			Orbital o = (Orbital) node;
//			o.updateSpace();
//			this.model.solar.setCenter(o.space);// Must Include Sun b/c Solar is Not Orbital
//			this.model.solar.setScale(o.defaultScale);
//			// this.model.solar.setAzimuth(this.azimuth);
////			this.model.solar.setElevation(this.elevation);
//			n = this.model.solar;
//		} else if (node instanceof Spheroid) {
//			logger.debug("processNode(" + node + ") instanceof Spheroid");
////			logger.debug("paint(" + (graphics != null) + ") " + this.model.node + " instanceof Spheroid");
//			Spheroid s = (Spheroid) node;
//			this.azimuth = s.getProjection().azimuth;
//			this.elevation = s.getProjection().elevation;
//			this.scale = s.getProjection().scale;
//			this.model.solar.setAzimuth(this.azimuth);
//			this.model.solar.setElevation(this.elevation);
//			this.model.solar.setScale(this.scale);
//			Object root = s.getRoot();
//			while (root != null) {
//				if (root instanceof Orbital) {
//					Orbital o = (Orbital) root;
//					o.updateSpace();
//					this.model.solar.setCenter(o.space);
//
////				this.model.node = this.model.solar;
////				this.model.node = this.model.solar.sun;
//					break;
//				} else {
//					root = ((Variable) root).getRoot();
//				}
//			}
//		}
//		return n;
//	}

//	@Override
//	public void paint(Graphics graphics) {
//		super.paint(graphics);
////		logger.debug("paint(" + (graphics != null) + ")");
//		this.dimension = this.getSize();
//		graphics.setColor(Color.white);
//		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
//		graphics.translate((int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
//		if (this.model != null && this.model.node != null) {
//			if(this.model.node instanceof Orbital) {//20221105 Why? To Re-Center Orbital in Screen w/ Respect to Time
//				logger.debug("paint("+(graphics != null)+") "+this.model.node+" instanceof Orbital");
//				Orbital o = (Orbital)this.model.node;
//				o.updateSpace();
//				this.model.solar.setCenter(o.space);//Must Include Sun b/c Solar is Not Orbital
//				this.model.solar.setAzimuth(this.azimuth);
//				this.model.solar.setElevation(this.elevation);
//				this.node = this.model.solar;
//			} else if(this.model.node instanceof Spheroid) {
//				logger.debug("paint("+(graphics != null)+") "+this.model.node+" instanceof Spheroid");
//				Spheroid s = (Spheroid)this.model.node;
//				this.azimuth = s.getProjection().azimuth;
//				this.elevation = s.getProjection().elevation;
//				this.scale = s.getProjection().scale;
//				this.model.solar.setAzimuth(this.azimuth);
//				this.model.solar.setElevation(this.elevation);
//				this.model.solar.setScale(this.scale);
//				Object root = s.getRoot();
//				while(root != null) {
//					if(root instanceof Orbital) {
//						Orbital o = (Orbital)root;
//						o.updateSpace();
//						this.model.solar.setCenter(o.space);
//						
////						this.model.node = this.model.solar;
////						this.model.node = this.model.solar.sun;
//						break;
//					} else {
//						root = ((Variable)root).getRoot();
//					}
//				}
//				this.node = this.model.node;
//			}
//			
//			if (this.node != null) {
//				try {
////					this.model.solar.paint(graphics);
//					this.node.paint(graphics);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
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

	// Variables declaration - do not modify//GEN-BEGIN:variables
	// End of variables declaration//GEN-END:variables
}
//Variable node = this.model.node;
//Object root = o.getRoot();
//while(root != null) {
//	if(root instanceof Star) {
//		Star s = (Star)root;
//		node = s;
//		break;
//	} else {
//		root = ((Variable)root).getRoot();
//	}
//}
//Object root = s.getRoot();
//if(root instanceof Orbital) {
//	Orbital o = (Orbital)root;
//	o.updateSpace();
//	this.model.solar.sun.setCenter(o.space);
//}
//Object root = node.getRoot();
//while(root instanceof Variable && !((Variable)root).paint) {
//	root = ((Variable)root).getRoot();
//	node = (Variable)root;
//}
//while(node.getRoot() != null && !node.paint) {
//node = (Variable)node.getRoot();
//}
//graphics.fillRect(0, 0, (int)(this.getWidth()), (int)(this.getHeight()));
//graphics.translate((int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
