package com.meritoki.library.prospero.model.helios.photosphere;

import com.meritoki.library.prospero.model.helios.photosphere.spots.Spots;
import com.meritoki.library.prospero.model.node.Variable;

public class Photosphere extends Variable {

	public Photosphere() {
		super("Photosphere");
		this.addChild(new Spots());
	}
}
