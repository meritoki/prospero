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
package org.meritoki.prospero.library.model.solar.planet.uranus;

import java.awt.Color;

import org.meritoki.prospero.library.model.node.cartography.Projection;
import org.meritoki.prospero.library.model.solar.planet.Planet;

/**
 * 
 * <a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet/uranusfact.html">Reference</a>
 *
 */
public class Uranus extends Planet {

	/**
	 * 
	 */
    public Uranus() {
        super("Uranus");
        this.mass = 8.681E25;//Kilograms
        this.radius = 25559;//Kilometers
        this.a = this.radius;//Kilometers
        this.b = this.a;//Kilometers
        this.c = 24973.0;//Kilometers
        this.color = Color.CYAN;
        this.defaultScale = 2097152.0;
        //N
        this.longitudeOfAscendingNode[0] = 74.0005;
        this.longitudeOfAscendingNode[1] = 1.3978E-5;
        //i
        this.inclination[0] = 0.7733;
        this.inclination[1] = 1.9E-8;
        //w
        this.argumentOfPeriapsis[0] = 96.6612;
        this.argumentOfPeriapsis[1] = 3.0565E-5;
        //a
        this.semiMajorAxis[0] = 19.18171;//Astronomical Unit
        this.semiMajorAxis[1] = -1.55E-8;//Astronomical Unit
        //e
        this.eccentricity[0] = 0.047318;
        this.eccentricity[1] = 7.45E-9;
        //M
        this.meanAnomaly[0] = 142.5905;
        this.meanAnomaly[1] = 0.011725806;
        this.obliquity = 97.77;
        this.orbitalPeriod = 30688.5;
        this.angularVelocity = -1.041365902144588e-04;
        this.setProjection(new Projection(this.a, this.b, this.c));
    }
}
//this.centroid = (Orbital)this.getRoot();
