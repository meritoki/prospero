package org.meritoki.prospero.library.model.helios.photosphere;

import org.meritoki.prospero.library.model.helios.photosphere.spots.Spots;
import org.meritoki.prospero.library.model.node.Variable;

public class Photosphere extends Variable {

	public Photosphere() {
		super("Photosphere");
		this.addChild(new Spots());
	}
}
