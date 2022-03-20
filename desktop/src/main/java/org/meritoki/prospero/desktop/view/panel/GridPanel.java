/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meritoki.prospero.desktop.view.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.desktop.view.menu.GridPopupMenu;
import org.meritoki.prospero.desktop.view.menu.SavePopupMenu;

import com.meritoki.library.prospero.model.Model;
import com.meritoki.library.prospero.model.solar.planet.earth.*;
import com.meritoki.library.prospero.model.terra.Terra;
import com.meritoki.library.prospero.model.terra.cartography.Projection;

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
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		graphics.translate((int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
		if (this.model != null) {
			Terra terra = ((Earth)this.model.getVariable("Earth")).terra;
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
				double scale = ((Earth)this.model.getVariable("Earth")).terra.projection.scale;
				scale = scale * this.factor;
				((Earth)this.model.getVariable("Earth")).terra.projection.scale = scale;
				this.repaint();
				break;
			}
			case '-': {
				double scale = ((Earth)this.model.getVariable("Earth")).terra.projection.scale;
				scale = scale / this.factor;
				((Earth)this.model.getVariable("Earth")).terra.projection.scale = scale;
				this.repaint();
				break;
			}
			case '1': {// bottom
				Projection globe = ((Earth)this.model.getVariable("Earth")).terra.projection;
				azimuth = 0;
				elevation = 0;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '2': {// top
				Projection globe = ((Earth)this.model.getVariable("Earth")).terra.projection;
				azimuth = 180;
				elevation = 0;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '3': {
				Projection globe = ((Earth)this.model.getVariable("Earth")).terra.projection;
				azimuth = 0;
				elevation = -90;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '4': {
				Projection globe = ((Earth)this.model.getVariable("Earth")).terra.projection;
				azimuth = 180;
				elevation = 90;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '5': {
				Projection globe = ((Earth)this.model.getVariable("Earth")).terra.projection;
				azimuth = 90;
				elevation = 0;
				globe.setAzimuth(azimuth);
				globe.setElevation(elevation);
				repaint();
				break;
			}
			case '6': {
				Projection projection = ((Earth)this.model.getVariable("Earth")).terra.projection;
				azimuth = -90;
				elevation = 0;
				projection.setAzimuth(azimuth);
				projection.setElevation(elevation);
				repaint();
				break;
			}
			case '7': {
				Projection projection = ((Earth)this.model.getVariable("Earth")).terra.projection;
				azimuth = 0;
				elevation = 55;
				projection.setAzimuth(azimuth);
				projection.setElevation(elevation);
				repaint();
				break;
			}
			case '8': {
				Projection globe = ((Earth)this.model.getVariable("Earth")).terra.projection;
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
			((Earth)this.model.getVariable("Earth")).terra.projection.setAzimuth(azimuth);
			((Earth)this.model.getVariable("Earth")).terra.projection.setElevation(elevation);
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
