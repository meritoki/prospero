package org.meritoki.prospero.library.model.terra.atmosphere;

import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.terra.atmosphere.temperature.Temperature;
import org.meritoki.prospero.library.model.terra.atmosphere.tornado.Tornado;
import org.meritoki.prospero.library.model.terra.atmosphere.wind.Wind;

public class Atmosphere extends Variable {
	
	public Atmosphere() {
		super("Atmosphere");
//		this.addChild(new Wind());
		this.addChild(new Cyclone());
		this.addChild(new Tornado());
//		this.addChild(new Temperature());
	}
}
