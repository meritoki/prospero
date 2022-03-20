package com.meritoki.library.prospero.model.helios;

import com.meritoki.library.prospero.model.helios.photosphere.Photosphere;
import com.meritoki.library.prospero.model.node.Variable;

public class Helios extends Variable {

	public Helios() {
		super("Helios");
		this.addChild(new Photosphere());
	}
}
