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
package org.meritoki.prospero.desktop.view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.desktop.view.menu.GridPopupMenu;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.cartography.Projection;


/**
 *
 * @author jorodriguez
 */
public class GridPanel extends javax.swing.JPanel
		implements KeyListener, MouseListener, MouseMotionListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6454415805080262715L;
	static Logger logger = LogManager.getLogger(GridPanel.class.getName());
	protected GridPopupMenu menu;
	public Model model;
	public double factor = 1.5;
	protected int mx, my;
	protected int azimuth = 0;
	protected int elevation = 0;
	public Terra terra;
	public Dimension dimension;

	/**
	 * Creates new form ProjectionPanel
	 */
	public GridPanel() {
		initComponents();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.setSize(1024,512);
//		Thread thread = new Thread(this);
//		thread.start();
	}

	public void setModel(Model model) {
		this.model = model;
		this.menu = new GridPopupMenu(this.model);
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		logger.debug("paint("+(graphics!=null)+")");
		this.dimension = this.getSize();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, (int)(this.getWidth()), (int)(this.getHeight()));
		graphics.translate((int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
		if (this.model != null) {
			Terra terra = ((Terra)this.model.getVariable("Terra"));
			try { 
				terra.paint(graphics);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		this.revalidate();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isControlDown()) {
			switch (e.getKeyChar()) {
			case '+': {
				double scale = ((Terra)this.model.getVariable("Terra")).projection.scale;
				scale = scale * this.factor;
				((Terra)this.model.getVariable("Terra")).projection.scale = scale;
				this.repaint();
				break;
			}
			case '-': {
				double scale = ((Terra)this.model.getVariable("Terra")).projection.scale;
				scale = scale / this.factor;
				((Terra)this.model.getVariable("Terra")).projection.scale = scale;
				this.repaint();
				break;
			}
			case '1': {// bottom
				Projection globe = ((Terra)this.model.getVariable("Terra")).projection;
				azimuth = 0;
				elevation = 0;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '2': {// top
				Projection globe = ((Terra)this.model.getVariable("Terra")).projection;
				azimuth = 180;
				elevation = 0;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '3': {
				Projection globe = ((Terra)this.model.getVariable("Terra")).projection;
				azimuth = 0;
				elevation = -90;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '4': {
				Projection globe = ((Terra)this.model.getVariable("Terra")).projection;
				azimuth = 180;
				elevation = 90;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '5': {
				Projection globe = ((Terra)this.model.getVariable("Terra")).projection;
				azimuth = 90;
				elevation = 0;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '6': {
				Projection projection = ((Terra)this.model.getVariable("Terra")).projection;
				azimuth = -90;
				elevation = 0;
				projection.setAzimuth(azimuth);
				projection.setElevation(elevation);
				repaint();
				break;
			}
			case '7': {
				Projection projection = ((Terra)this.model.getVariable("Terra")).projection;
				azimuth = 0;
				elevation = 55;
				projection.setAzimuth(azimuth);
				projection.setElevation(elevation);
				repaint();
				break;
			}
			case '8': {
				Projection globe = ((Terra)this.model.getVariable("Terra")).projection;
				azimuth = 23;
				elevation = 35;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
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
		mx = e.getX();
		my = e.getY();
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
	public void mouseDragged(MouseEvent e) {
		// get the latest mouse position
		if (this.model != null) {
			int new_mx = e.getX();
			int new_my = e.getY();
			// adjust angles according to the distance travelled by the mouse
			// since the last event
			azimuth -= new_mx - mx;
			elevation -= new_my - my;
			((Terra)this.model.getVariable("Terra")).projection.setAzimuth(azimuth);
			((Terra)this.model.getVariable("Terra")).projection.setElevation(elevation);
			// update our data
			mx = new_mx;
			my = new_my;
			repaint();
			e.consume();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	private void showSavePopupMenu(MouseEvent e) {
		if (menu != null)
			menu.show(e.getComponent(), e.getX(), e.getY());
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

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
//g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
//g2d.translate((int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
////g2d.dispose();
//this.setSize(this.dimension);
////this.doLayout();
//Graphics2D g2d = (Graphics2D) graphics;
//g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//g2d.setTransform(AffineTransform.getScaleInstance(scale, scale));
