package com.meritoki.library.prospero.model.terra.biosphere;

import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.terra.biosphere.city.City;
import com.meritoki.library.prospero.model.terra.biosphere.country.Country;

public class Biosphere extends Variable {

	public Biosphere() {
		super("Biosphere");
		this.addChild(new Country());
		this.addChild(new City());
	}
}
