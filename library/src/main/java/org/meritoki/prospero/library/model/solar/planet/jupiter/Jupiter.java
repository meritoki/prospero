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
package org.meritoki.prospero.library.model.solar.planet.jupiter;

import java.awt.Color;

import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.terra.cartography.Globe;

/**
 * 
 * <a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet/jupiterfact.html">Reference</a>
 *
 */
public class Jupiter extends Planet {

	public Jupiter() {
		super("Jupiter");
		this.mass = 1.89813e27;//Kilograms
		this.radius = 71492;//Kilometers
		this.a = this.radius;//Kilometers
		this.b = this.a;//Kilometers
		this.c = 66854.0;//Kilometers
		this.color = Color.PINK;
		this.defaultScale = 1048576.0;
		//N
		this.longitudeOfAscendingNode[0] = 100.4542;//Degrees
		this.longitudeOfAscendingNode[1] = 2.76854E-5;//Degrees
		//i
		this.inclination[0] = 1.3030;//Degrees
		this.inclination[1] = -1.557E-7;//Degrees
		//w
		this.argumentOfPeriapsis[0] = 273.8777;//Degrees
		this.argumentOfPeriapsis[1] = 1.64505E-5;//Degrees
		//a
		this.semiMajorAxis[0] = 5.20256;//Astronomical Unit
		this.semiMajorAxis[1] = 0;//Astronomical Unit
		//e
		this.eccentricity[0] = 0.048498;//Degrees
		this.eccentricity[1] = 4.469E-9;//Degrees
		//M
		this.meanAnomaly[0] = 19.8950;//Degrees
		this.meanAnomaly[1] = 0.0830853001;//Degrees
		this.angularVelocity = 1.773408215404907e-04;
		this.orbitalPeriod = 4332.589;//*24*60*60;//days
		this.setProjection(new Globe(this.a, this.b, this.c));
//		this.projection = new Globe(this.a,this.b,this.c);
//		this.projection.setNear((float)this.radius);
//		this.projection.setNearToObject((float)this.radius+1000);
	}
}
//public static double getMin(List<Index> list) {
//double min = Double.MAX_VALUE;
//for (Index d : list) {
//	if (d.value < min) {
//		min = d.value;
//	}
//}
//System.out.println("getMin(...) min=" + min);
//return min;
//}
//
//public static double getVolume(double radius) {
//return (4 / 3) * Math.PI * Math.pow(radius, 3);
//}
//public static double getMax(List<Index> list) {
//double max = Double.MIN_VALUE;
//for (Index d : list) {
//	if (d.value > max) {
//		max = d.value;
//	}
//}
//System.out.println("getMax(...) max=" + max);
//return max;
//}
//
//public static double getZero(double min, double max) {
//double zero = ((max - min) / 2) + min;
//System.out.println("getZero(...) zero=" + zero);
//return zero;
//}
//
//public static List<Index> getZeroList(List<Index> list) {
////System.out.println("getZeroList(...,"+mass+")");
//double zero = getZero(getMin(list), getMax(list));
//List<Index> zeroList = new ArrayList<>();
//for (int i = 0; i < list.size(); i++) {
//	Index d = list.get(i);
//	d.value -= zero;
//	zeroList.add(d);
//}
//return zeroList;
//}
