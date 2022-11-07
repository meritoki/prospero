package org.meritoki.prospero.library.model.terra.atmosphere;

import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.atmosphere.cloud.Cloud;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.terra.atmosphere.pressure.SeaLevelPressure;
import org.meritoki.prospero.library.model.terra.atmosphere.tornado.Tornado;
import org.meritoki.prospero.library.model.terra.atmosphere.wind.Wind;
import org.meritoki.prospero.library.model.terra.gravitation.Gravitation;

/**
 * Citation
 * <ol type="A">
 * <li><a href=
 * "https://en.wikipedia.org/wiki/Atmosphere_of_Earth">https://en.wikipedia.org/wiki/Atmosphere_of_Earth</a></li>
 * <li><a href=
 * "https://en.wikipedia.org/wiki/Atmospheric_pressure">https://en.wikipedia.org/wiki/Atmospheric_pressure</a></li>
 * </ol>
 */
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
