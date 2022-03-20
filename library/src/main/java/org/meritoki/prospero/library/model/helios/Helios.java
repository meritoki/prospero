package org.meritoki.prospero.library.model.helios;

import org.meritoki.prospero.library.model.helios.photosphere.Photosphere;
import org.meritoki.prospero.library.model.node.Variable;

public class Helios extends Variable {

	public Helios() {
		super("Helios");
		this.addChild(new Photosphere());
	}
}
