package org.meritoki.prospero.library.model.terra.biosphere;

import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.biosphere.city.City;
import org.meritoki.prospero.library.model.terra.biosphere.country.Country;

public class Biosphere extends Terra {

	public Biosphere() {
		super("Biosphere");
		this.addChild(new Country());
		this.addChild(new City());
	}
	
	public Biosphere(String name) {
		super(name);
	}
}
