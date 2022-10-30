package org.meritoki.prospero.library.model.helios;

import java.awt.Graphics;

import org.meritoki.prospero.library.model.helios.photosphere.Photosphere;
import org.meritoki.prospero.library.model.node.Grid;

public class Helios extends Grid {

	public Helios() {
		super("Helios");
		this.paint = true;
		this.addChild(new Photosphere());
	}
	
	@Override
	public void paint(Graphics graphics) throws Exception {		
		if(this.paint) {
			super.paint(graphics);
		}
	}
}
