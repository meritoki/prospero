package org.meritoki.prospero.library.model.terra.biosphere;

import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.terra.biosphere.city.City;
import org.meritoki.prospero.library.model.terra.biosphere.country.Country;

public class Biosphere extends Variable {

	public Biosphere() {
		super("Biosphere");
		this.addChild(new Country());
		this.addChild(new City());
	}
}
