/*
 * Copyright 2016-2023 Joaquin Osvaldo Rodriguez
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
//https://makersportal.com/blog/2018/11/25/goes-r-satellite-latitude-and-longitude-grid-projection-algorithm
package org.meritoki.prospero.library.model.solar.satellite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.Orbital;
import org.meritoki.prospero.library.model.node.Spheroid;
import org.meritoki.prospero.library.model.unit.Coordinate;

public class Satellite extends Orbital {

	static Logger logger = LogManager.getLogger(Satellite.class.getName());

	public Satellite() {

	}

	public static Coordinate getCoordinate(Spheroid spheroid, double longitude, double x, double y, double height) {
//		logger.info("getCoordinate("+(spheroid != null)+","+longitude+","+x+","+y+","+height+")");
		double radiusEquator = spheroid.radius * 1000;
		double radiusPolar = spheroid.c * 1000;
		double a = getA(x, y, radiusEquator, radiusPolar);
		double b = getB(x, y, height);
		double c = getC(height, radiusEquator);
		double r = getR(a, b, c);
		double sx = getSX(r, x, y);
		double sy = getSY(r, x);
		double sz = getSZ(r, x, y);
		Coordinate coordinate = new Coordinate();
		coordinate.latitude = getLatitude(radiusEquator, radiusPolar, height, sx, sy, sz);
		coordinate.longitude = getLongitude(longitude, height, sy, sx);
//		if(coordinate.is())
//			logger.info("getCoordinate(...) coordinate="+coordinate);
		return coordinate;
	}

	public static double getA(double x, double y, double radiusEquator, double radiusPolar) {
//		logger.info("getA("+x+","+y+","+radiusEquator+","+radiusPolar+")");
		return (Math.sin(x) * Math.sin(x)) + (Math.cos(x) * Math.cos(x)) * ((Math.cos(y) * Math.cos(y))
				+ (Math.pow(radiusEquator, 2) / Math.pow(radiusPolar, 2)) * (Math.sin(y) * Math.sin(y)));
	}

	public static double getB(double x, double y, double H) {
		return -2 * H * Math.cos(x) * Math.cos(y);
	}

	public static double getC(double H, double radiusEquator) {
		return Math.pow(H, 2) - Math.pow(radiusEquator, 2);
	}

	public static double getR(double a, double b, double c) {
//		logger.info("getR("+a+","+b+","+c+")");
		double result = (b * b) + (-4 * a * c);
		return (-b - Math.sqrt(result)) / (2 * a);
	}

	public static double getSX(double r, double x, double y) {
		return r * Math.cos(x) * Math.cos(y);
	}

	public static double getSY(double r, double x) {
		return -r * Math.sin(x);
	}

	public static double getSZ(double r, double x, double y) {
		return r * Math.cos(x) * Math.sin(y);
	}

	public static double getLatitude(double radiusEquator, double radiusPolar, double H, double sx, double sy,
			double sz) {
		return Math.toDegrees(Math.atan((Math.pow(radiusEquator, 2) / Math.pow(radiusPolar, 2)
				* (sz / Math.sqrt(Math.pow(H - sx, 2) + Math.pow(sy, 2))))));
	}

	public static double getLongitude(double longitude, double H, double sy, double sx) {
		return Math.toDegrees(Math.toRadians(longitude) - Math.atan(sy / (H - sx)));
	}
}
