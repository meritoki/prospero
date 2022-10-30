package org.meritoki.prospero.library.model.helios;

import java.awt.Graphics;

import org.meritoki.prospero.library.model.helios.photosphere.Photosphere;
import org.meritoki.prospero.library.model.solar.star.sun.Sun;

public class Helios extends Sun {

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
