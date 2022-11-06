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
package org.meritoki.prospero.library.model.solar.planet.neptune;

import java.awt.Color;

import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.terra.cartography.Projection;

/**
 * 
 * <a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet/neptunefact.html">Reference</a>
 *
 */
public class Neptune extends Planet {

    public Neptune() {
        super("Neptune");
        this.mass = 1.024E26;//Kilograms
        this.radius = 24764;//Kilometers
        this.a = this.radius;//Kilometers
        this.b = this.a;//Kilometers
        this.c = 24341.0;//Kilometers
        this.color = Color.BLUE;
        this.defaultScale = 2097152.0;
        //N
        this.longitudeOfAscendingNode[0] = 131.6737;
        this.longitudeOfAscendingNode[1] = 0;
        //i
        this.inclination[0] = 1.7700;//i
        this.inclination[1] = -2.55E-7;//i
        //w
        this.argumentOfPeriapsis[0] = 272.8461;
        this.argumentOfPeriapsis[1] = -6.027E-6;
        //a
        this.semiMajorAxis[0] = 30.05826;//Astronomical Unit
        this.semiMajorAxis[1] = 3.313E-9;//Astronomical Unit
        //e
        this.eccentricity[0] = 0.008606;//e
        this.eccentricity[1] = 2.15E-9;// e
        //M
        this.meanAnomaly[0] = 260.2471;
        this.meanAnomaly[1] = 0.005995147;
        this.orbitalPeriod = 60182;
        this.angularVelocity =  1.083382527619075e-04;
        this.setProjection(new Projection(this.a, this.b, this.c));
    }
}