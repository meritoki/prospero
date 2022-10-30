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
package org.meritoki.prospero.library.model.solar.planet.mars;

import java.awt.Color;

import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.terra.cartography.Globe;

/**
 * 
 * <a href=
 * "https://nssdc.gsfc.nasa.gov/planetary/factsheet/marsfact.html">Reference</a>
 *
 */
public class Mars extends Planet {

	public Mars() {
		super("Mars");
		this.mass = 6.39e23;// Kilograms
		this.radius = 3396.2;// Kilometers
		this.a = this.radius;// Kilometers
		this.b = this.a;// Kilometers
		this.c = 3376.2;// Kilometers
		this.color = Color.RED;
		// N
		this.longitudeOfAscendingNode[0] = 49.5574;
		this.longitudeOfAscendingNode[1] = 2.11081E-5;
		// i
		this.inclination[0] = 1.8497;
		this.inclination[1] = -1.78E-8;
		// w
		this.argumentOfPeriapsis[0] = 286.5016;
		this.argumentOfPeriapsis[1] = 2.92961E-5;
		// a
		this.semiMajorAxis[0] = 1.523688;// Astronomical Unit
		this.semiMajorAxis[1] = 0;// Astronomical Unit
		// e
		this.eccentricity[0] = 0.093405;
		this.eccentricity[1] = 2.516E-9;
		// M
		this.meanAnomaly[0] = 18.6021;
		this.meanAnomaly[1] = 0.5240207766;
		this.orbitalPeriod = 686.980;
		this.angularVelocity = 7.088218127178316e-05;
		this.projection = new Globe(this.a, this.b, this.c);
	}
}