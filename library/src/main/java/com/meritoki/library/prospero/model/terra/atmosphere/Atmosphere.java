package com.meritoki.library.prospero.model.terra.atmosphere;

import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.terra.atmosphere.cyclone.Cyclone;
import com.meritoki.library.prospero.model.terra.atmosphere.temperature.Temperature;
import com.meritoki.library.prospero.model.terra.atmosphere.tornado.Tornado;
import com.meritoki.library.prospero.model.terra.atmosphere.wind.Wind;

public class Atmosphere extends Variable {
	
	public Atmosphere() {
		super("Atmosphere");
//		this.addChild(new Wind());
		this.addChild(new Cyclone());
		this.addChild(new Tornado());
//		this.addChild(new Temperature());
	}
}
