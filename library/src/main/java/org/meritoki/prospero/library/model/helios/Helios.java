package org.meritoki.prospero.library.model.helios;

import java.awt.Graphics;

import org.meritoki.prospero.library.model.helios.photosphere.Photosphere;
import org.meritoki.prospero.library.model.node.Grid;

public class Helios extends Grid {

	public Helios() {
		super("Helios");
		this.addChild(new Photosphere());
	}
	
	public Helios(String name) {
		super(name);
		this.defaultScale = 50000.0;
	}
	
	@Override
	public void paint(Graphics graphics) throws Exception {		
		super.paint(graphics);
	}
}
