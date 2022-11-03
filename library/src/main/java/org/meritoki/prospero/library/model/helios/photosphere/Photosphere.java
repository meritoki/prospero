package org.meritoki.prospero.library.model.helios.photosphere;

import org.meritoki.prospero.library.model.helios.Helios;
import org.meritoki.prospero.library.model.helios.photosphere.spots.Spots;

public class Photosphere extends Helios {

	public Photosphere() {
		super("Photosphere");
		this.addChild(new Spots());
	}
}
