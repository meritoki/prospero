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
package org.meritoki.prospero.library.model.solar.moon.luna;

import java.awt.Color;

import org.meritoki.prospero.library.model.solar.moon.Moon;
import org.meritoki.prospero.library.model.terra.cartography.Globe;
import org.meritoki.prospero.library.model.unit.Unit;

/**
 * 
 * <a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet/moonfact.html">https://nssdc.gsfc.nasa.gov/planetary/factsheet/moonfact.html</a>
 *
 */
public class Luna extends Moon {

    public Luna() {
    	super("Luna");
        this.mass = 7.3477e22;//Kilograms
        this.radius = 1738.1;//Kilometers
		this.a = this.radius;//Kilometers
		this.b = this.a;//Kilometers
		this.c = 1736.00;//Kilometers
        this.color = Color.RED;
        //N
        this.longitudeOfAscendingNode[0] = 125.1228;
        this.longitudeOfAscendingNode[1] = -0.0529538083;
        //i
        this.inclination[0] = 5.1454;
        this.inclination[1] = 0;
        //w
        this.argumentOfPeriapsis[0] = 318.0634;
        this.argumentOfPeriapsis[1] = 0.1643573223;
        //a
        this.semiMajorAxis[0] = (60.2666 * Unit.EARTH_RADII)/Unit.ASTRONOMICAL;//Astronomical Unit
        this.semiMajorAxis[1] = 0;//Astronomical Unit
        //e
        this.eccentricity[0] = 0.054900;//e//0.01671022
        this.eccentricity[1] = 0;//e
        //M
        this.meanAnomaly[0] = 115.3654;
        this.meanAnomaly[1] = 13.0649929509;
        this.projection = new Globe(this.a,this.b,this.c);
        this.orbitalPeriod = 27;
    }
}
