package org.meritoki.prospero.library.model.terra.atmosphere;

import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.atmosphere.cloud.Cloud;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.terra.atmosphere.pressure.SeaLevelPressure;
import org.meritoki.prospero.library.model.terra.atmosphere.tornado.Tornado;
import org.meritoki.prospero.library.model.terra.atmosphere.wind.Wind;
import org.meritoki.prospero.library.model.terra.gravitation.Gravitation;

public class Atmosphere extends Terra {
	
	public Atmosphere() {
		super("Atmosphere");
		this.addChild(new Wind());
		this.addChild(new Cyclone());
		this.addChild(new Tornado());
		this.addChild(new SeaLevelPressure());
		this.addChild(new Gravitation());
		this.addChild(new Cloud());
	}
	
	public Atmosphere(String name) {
		super(name);
	}
}
//this.addChild(new Temperature());
