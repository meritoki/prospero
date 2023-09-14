/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.library.model.terra.atmosphere;

import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.atmosphere.cloud.Cloud;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.terra.atmosphere.geopotential.Geopotential;
import org.meritoki.prospero.library.model.terra.atmosphere.pressure.SeaLevelPressure;
import org.meritoki.prospero.library.model.terra.atmosphere.temperature.Temperature;
import org.meritoki.prospero.library.model.terra.atmosphere.tornado.Tornado;
import org.meritoki.prospero.library.model.terra.atmosphere.vorticity.Vorticity;
import org.meritoki.prospero.library.model.terra.atmosphere.wind.Wind;

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
		this.addChild(new Cloud());
		this.addChild(new Wind());
		this.addChild(new Cyclone());
		this.addChild(new Tornado());
		this.addChild(new SeaLevelPressure());
		this.addChild(new Vorticity());
		this.addChild(new Geopotential());
		this.addChild(new Temperature());
	}
	
	public Atmosphere(String name) {
		super(name);
	}
}

