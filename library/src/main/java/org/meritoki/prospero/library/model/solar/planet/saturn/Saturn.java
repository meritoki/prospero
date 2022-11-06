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
package org.meritoki.prospero.library.model.solar.planet.saturn;

import java.awt.Color;

import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.terra.cartography.Projection;

/**
 * 
 * <a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet/saturnfact.html">Reference</a>
 *
 */
public class Saturn extends Planet {

    public Saturn() {
        super("Saturn");
        this.mass =5.683e26;//Kilograms
        this.radius = 60268;//Kilometers
        this.a = this.radius;//Kilometers
        this.b = this.a;//Kilometers
        this.c = 54364.0;//Kilometers
        this.color = Color.CYAN;
        this.defaultScale = 1048576.0;
        //N
        this.longitudeOfAscendingNode[0] = 113.6634;
        this.longitudeOfAscendingNode[1] = 2.38980E-5;
        //i
        this.inclination[0] = 2.4886;
        this.inclination[1] = -1.081E-7;
        //w
        this.argumentOfPeriapsis[0] = 339.3939;
        this.argumentOfPeriapsis[1] = 2.97661E-5;
        //a
        this.semiMajorAxis[0] = 9.55475;//Astronomical Unit
        this.semiMajorAxis[1] = 0;//Astronomical Unit
        //e
        this.eccentricity[0] = 0.055546;
        this.eccentricity[1] = -9.499E-9;
        //M
        this.meanAnomaly[0] = 316.9670;
        this.meanAnomaly[1] = 0.0334442282;
        this.orbitalPeriod = 10759.22;
        this.angularVelocity = 1.636246173744684e-04;
        this.setProjection(new Projection(this.a, this.b, this.c));
    }
}
