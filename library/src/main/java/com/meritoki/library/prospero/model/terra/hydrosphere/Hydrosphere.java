package com.meritoki.library.prospero.model.terra.hydrosphere;

import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.terra.hydrosphere.ocean.Ocean;

public class Hydrosphere extends Variable {

	public Hydrosphere() {
		super("Hydrosphere");
		this.addChild(new Ocean());
	}
}
