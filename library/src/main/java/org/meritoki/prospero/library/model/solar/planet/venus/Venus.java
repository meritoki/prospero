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
package org.meritoki.prospero.library.model.solar.planet.venus;

import java.awt.Color;

import org.meritoki.prospero.library.model.node.cartography.Projection;
import org.meritoki.prospero.library.model.solar.planet.Planet;

/**
 * 
 * <a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet/venusfact.html">Reference</a>
 *
 */
public class Venus extends Planet {

	/**
	 * 
	 */
    public Venus() {
        super("Venus");
        this.mass = 4.867e24;//Kilograms
        this.radius = 6051.8; //Kilometers
        this.a = this.radius;//Kilometers
        this.b = this.a;//Kilometers
        this.c = this.a;//Kilometers
        this.color = Color.DARK_GRAY;
        this.defaultScale = 8388608.0;
        //N
        this.longitudeOfAscendingNode[0] = 76.6799;
        this.longitudeOfAscendingNode[1] = 2.46590E-5;
        //i
        this.inclination[0] = 3.3946;
        this.inclination[1] = 2.75E-8;
        //w
        this.argumentOfPeriapsis[0] = 54.8910;
        this.argumentOfPeriapsis[1] = 1.38374E-5;
        //a
        this.semiMajorAxis[0] = 0.723330;//Astronomical Unit
        this.semiMajorAxis[1] = 0;//Astronomical Unit
        //e
        this.eccentricity[0] = 0.006773;
        this.eccentricity[1] = -1.302E-9;
        //M
        this.meanAnomaly[0] = 48.0052;
        this.meanAnomaly[1] = 1.6021302244;
        this.orbitalPeriod = 225;
        this.angularVelocity = 2.99e-07;
        this.obliquity = 177.36;
        this.setProjection(new Projection(this.a, this.b, this.c));
    }
}
//this.centroid = (Orbital)this.getRoot();