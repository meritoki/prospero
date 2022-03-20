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

import org.meritoki.prospero.desktop.view.menu.SavePopupMenu;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.solar.Solar;
import org.meritoki.prospero.library.model.solar.unit.*;

/**
 *
 * @author jorodriguez
 */
public class SolarPanel extends javax.swing.JPanel implements KeyListener, MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -55582891078473200L;
	public Model model;
	public SavePopupMenu savePopupMenu;
	protected int mx, my;
	protected int azimuth = 0;
	protected int elevation = 0;
    /**
     * Creates new form SolarPanel
     */
    public SolarPanel() {
        initComponents();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.setSize(1024,512);
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.savePopupMenu = new SavePopupMenu(this.model);
    }
    
	private void showSavePopupMenu(MouseEvent e) {
		if (savePopupMenu != null)
			savePopupMenu.show(e.getComponent(), e.getX(), e.getY());
	}
    
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.getWidth(),this.getHeight());
		graphics.translate((int) (this.getWidth() / 2.0), (int) (this.getHeight() / 2.0));
		if(this.model != null) {
			try {
				this.model.getVariable("Solar").paint(graphics);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		double scale = ((Solar)this.model.getVariable("Solar")).scale;
		if (e.isControlDown()) {
			switch (e.getKeyChar()) {
			case '+': {
//				if (scale < 1.024E-4) {
					((Solar)this.model.getVariable("Solar")).setScale(scale * 2);
//				}
				this.repaint();
				break;
			}
			case '-': {
//				if (scale > 8.0E-8) {
					((Solar)this.model.getVariable("Solar")).setScale(scale / 2);
//				}
				this.repaint();
				break;
			}
			}
		}
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
		if (this.model != null) {
			int new_mx = e.getX();
			int new_my = e.getY();
			// adjust angles according to the distance travelled by the mouse
			// since the last event
			azimuth -= new_mx - mx;
			elevation -= new_my - my;
			((Solar)this.model.getVariable("Solar")).setAzimuth(azimuth);
			((Solar)this.model.getVariable("Solar")).setElevation(elevation);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
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
    }

	// </editor-fold>//GEN-END:initComponents





    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
