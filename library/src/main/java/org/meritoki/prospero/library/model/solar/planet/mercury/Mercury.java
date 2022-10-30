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
package org.meritoki.prospero.library.model.solar.planet.mercury;

import java.awt.Color;

import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.terra.cartography.Globe;

/**
 * 
 * <a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet/mercuryfact.html">Reference</a>
 *
 */
public class Mercury extends Planet {

	public Mercury() {
		super("Mercury");
		this.mass = 3.30104E23;//Kilograms
		this.radius = 2440.5;//Kilometers
        this.a = this.radius;//Kilometers
        this.b = this.a;//Kilometers
        this.c = 2438.3;//Kilometers
		this.color = Color.green;
		//N
		this.longitudeOfAscendingNode[0] = 48.3313;
		this.longitudeOfAscendingNode[1] = 3.24587E-5;
		//i
		this.inclination[0] = 7.0047;
		this.inclination[1] = 5.00E-8;
		//w
		this.argumentOfPeriapsis[0] = 29.1241;
		this.argumentOfPeriapsis[1] = 1.01444E-5;
		//a
		this.semiMajorAxis[0] = 0.38709893;//Astronomical Unit
		this.semiMajorAxis[1] = 0;//Astronomical Unit
		//e
		this.eccentricity[0] = 0.205635;
		this.eccentricity[1] = 5.59E-10;
		//M
		this.meanAnomaly[0] = 168.6562;
		this.meanAnomaly[1] = 4.0923344368;
		this.orbitalPeriod = 87.9691;
		this.angularVelocity = 1.240013441242619e-06;
        this.projection = new Globe(this.a,this.b,this.c);
	}
}
