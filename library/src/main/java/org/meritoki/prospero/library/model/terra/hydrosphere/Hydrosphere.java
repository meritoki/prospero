package org.meritoki.prospero.library.model.terra.hydrosphere;

import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.Ocean;

public class Hydrosphere extends Terra {

	public Hydrosphere() {
		super("Hydrosphere");
		this.addChild(new Ocean());
	}
	public Hydrosphere(String name) {
		super(name);
	}
}
