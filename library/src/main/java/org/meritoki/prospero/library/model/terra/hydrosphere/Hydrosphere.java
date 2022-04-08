package org.meritoki.prospero.library.model.terra.hydrosphere;

import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.Ocean;

public class Hydrosphere extends Variable {

	public Hydrosphere() {
		super("Hydrosphere");
		this.addChild(new Ocean());
	}
}
