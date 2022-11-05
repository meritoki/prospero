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
package org.meritoki.prospero.library.model.node;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.solar.Solar;
import org.meritoki.prospero.library.model.solar.planet.jupiter.Jupiter;
import org.meritoki.prospero.library.model.solar.planet.saturn.Saturn;
import org.meritoki.prospero.library.model.solar.planet.uranus.Uranus;
import org.meritoki.prospero.library.model.solar.star.sun.Sun;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Space;
import org.meritoki.prospero.library.model.unit.Unit;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Citation
 * <ol type="A">
 * <li><a href=
 * "https://stjarnhimlen.se/comp/ppcomp.html">https://stjarnhimlen.se/comp/ppcomp.html</a></li>
 * <li><a href=
 * "http://www.stjarnhimlen.se/comp/tutorial.html">http://www.stjarnhimlen.se/comp/tutorial.html</a></li>
 * </ol>
 */
public class Orbital extends Grid {

	static Logger logger = LogManager.getLogger(Orbital.class.getName());
	public Calendar referenceCalendar = (new GregorianCalendar(2000, 0, 0, 0, 0, 0));
	public double orbitalPeriod;
	public double obliquity;
	public double angularVelocity;
	public double[] semiMajorAxis = new double[3];// a
	public double[] eccentricity = new double[3];// e
	public double[] inclination = new double[3];// i
	public double[] meanLongitude = new double[3];// l
	public double[] longitudeOfPeriapsis = new double[3];
	public double[] longitudeOfAscendingNode = new double[3];// N
	public double[] argumentOfPeriapsis = new double[3];
	public double[] meanAnomaly = new double[3];
	public double rotation;
	public Orbital centroid;

	public Orbital() {
		super("Orbital");
	}

	public Orbital(String name) {
		super(name);
	}

	@JsonIgnore
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		this.updateSpace();
		this.setCenter(this.center);
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.setCalendar(calendar);
		}
	}
	
	public void updateSpace() {
		super.updateSpace();
		Object root = this.getRoot();
		if (root instanceof Orbital) {
			logger.info(this.name+".updateSpace() root="+root);
			this.centroid = (Orbital)root;
			this.space = this.getSpace(this.calendar, this.centroid);
			this.buffer = new Space(this.space);
			this.projection.setSpace(this.buffer);
			logger.info(this.name+".updateSpace() this.space="+space);
		}
	}
	
	@JsonIgnore
	@Override
	public void setCenter(Space center) {
		super.setCenter(center);
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			if(n instanceof Orbital) {
				Orbital o = (Orbital)n;
				o.setCenter(center);
			}
		}
	}
	
	public List<Point> getOrbit(Orbital centroid) {
		LinkedList<Point> vertexList = new LinkedList<>();
		if (this.orbitalPeriod > 0) {
			double resolution = 512.0;
			double increment = (this.orbitalPeriod*24) / resolution;
			Calendar calendar = (Calendar) this.calendar.clone();
			double count = 0;
			while (count <= resolution) {
				Space space = this.getSpace(calendar,centroid);
				space.subtract(this.center);
				Point position = this.projection.getPoint(space.getPoint());//this.getSpace(calendar,centroid).getPoint());
				calendar.add(Calendar.HOUR, (int) (Math.round(increment))); // number of days to ad
				count++;
				vertexList.add(position);
			}
		}
		return vertexList;
	}

	public Vector3D getDirection(Energy energy) {
		Vector3D direction = this.space.rectangular.subtract(energy.space.rectangular);
		if (centroid != null)
			direction = direction.subtract(centroid.space.rectangular);
		if (direction.getNorm() != 0)
			direction = direction.normalize();
		return direction;
	}

	public double getMeanAnomaly(double d) {
		return this.meanAnomaly[0] + this.meanAnomaly[1] * d;
	}

	/**
	 * Use test date 19 april 1990 to verify model is producing the correct values
	 * 
	 * N = longitude of the ascending node
	 * 
	 * i = inclination to the ecliptic (plane of the Earth's orbit)
	 * 
	 * w = argument of perihelion
	 * 
	 * a = semi-major axis, or mean distance from Sun
	 * 
	 * e = eccentricity (0=circle, 0-1=ellipse, 1=parabola)
	 * 
	 * M = mean anomaly (0 at perihelion; increases uniformly with time)
	 * 
	 * Please note that a, the semi-major axis, is given in Earth radii for the
	 * Moon, but in Astronomical Units for the Sun and all the planets.
	 * 
	 * @param calendar
	 * @return Space
	 */
	public Space getSpace(Calendar calendar, Orbital centroid) {
		double t = this.getTime(calendar);
		double N = this.longitudeOfAscendingNode[0] + this.longitudeOfAscendingNode[1] * t;
		double i = this.inclination[0] + this.inclination[1] * t;
		double w = this.argumentOfPeriapsis[0] + this.argumentOfPeriapsis[1] * t;
		double a = this.semiMajorAxis[0] + this.semiMajorAxis[1] * t;
		double e = this.eccentricity[0] + this.eccentricity[1] * t;
		double M = this.getMeanAnomaly(t);
		N = this.rev(N);
		i = this.rev(i);
		w = this.rev(w);
		M = this.rev(M);
		double longitudeCorrection = 0;
		double latitudeCorrection = 0;
		// A - Perturabations
		if (this instanceof Jupiter) {
			Sun sun = (Sun) this.getRoot();
			Saturn saturn = (Saturn) sun.getVariable("Saturn");
			double Ms = saturn.getMeanAnomaly(t);
			double one = -0.332 * Math.sin(Math.toRadians((2 * M) - (5 * Ms) - 67.6));
			double two = -0.056 * Math.sin(Math.toRadians(2 * M - 2 * Ms + 21));
			double three = +0.042 * Math.sin(Math.toRadians(3 * M - 5 * Ms + 21));
			double four = -0.036 * Math.sin(Math.toRadians(M - 2 * Ms));
			double five = +0.022 * Math.cos(Math.toRadians(M - Ms));
			double six = +0.023 * Math.sin(Math.toRadians(2 * M - 3 * Ms + 52));
			double seven = -0.016 * Math.sin(Math.toRadians(M - 5 * Ms - 69));
			longitudeCorrection = one + two + three + four + five + six + seven;
		} else if (this instanceof Saturn) {
			Sun sun = (Sun) this.getRoot();
			Jupiter jupiter = (Jupiter) sun.getVariable("Jupiter");
			double Mj = jupiter.getMeanAnomaly(t);
			double one = 0.812 * Math.sin(Math.toRadians(2 * Mj - 5 * M - 67.6));
			double two = -0.229 * Math.cos(Math.toRadians(2 * Mj - 4 * M - 2));
			double three = +0.119 * Math.sin(Math.toRadians(Mj - 2 * M - 3));
			double four = +0.046 * Math.sin(Math.toRadians(2 * Mj - 6 * M - 69));
			double five = +0.014 * Math.sin(Math.toRadians(Mj - 3 * M - 32));
			longitudeCorrection = one + two + three + four + five;
			double six = -0.020 * Math.cos(Math.toRadians(2 * Mj - 4 * M - 2));
			double seven = 0.018 * Math.sin(2 * Mj - 6 * M - 49);
			latitudeCorrection = six + seven;
		} else if (this instanceof Uranus) {
			Sun sun = (Sun) this.getRoot();
			Saturn saturn = (Saturn) sun.getVariable("Saturn");
			Jupiter jupiter = (Jupiter) sun.getVariable("Jupiter");
			double Ms = saturn.getMeanAnomaly(t);
			double Mj = jupiter.getMeanAnomaly(t);
			double one = +0.040 * Math.sin(Math.toRadians(Ms - 2 * M + 6));
			double two = +0.035 * Math.sin(Math.toRadians(Ms - 3 * M + 33));
			double three = -0.015 * Math.sin(Math.toRadians(Mj - M + 20));
			longitudeCorrection = one + two + three;
		}
		logger.debug(this.name + ":{N: " + N + ", i:" + i + ", w: " + w + ", a: " + a + ", e: " + e + ", M:" + M + "}");
		// A - Solving Kepler's Equation
		M = Math.toRadians(M);
		N = Math.toRadians(N);
		i = Math.toRadians(i);
		w = Math.toRadians(w);
		double E = M + e * Math.sin(M) * (1.0 + e * Math.cos(M));
		double error = 1;
		double E0 = E;
		while (error > 0.0001) { // 0.005
			double E1 = E0 - (E0 - e * Math.sin(E0) - M) / (1 - e * Math.cos(E0));
			error = Math.abs(E0 - E1);
			E0 = E1;
		}
		E = E0;
		// A - Spheroid Distance and true anomaly
		double xv = a * (Math.cos(E) - e);
		double yv = a * (Math.sqrt(1.0 - e * e) * Math.sin(E));
		double v = Math.atan2(yv, xv);
		double r = Math.sqrt(xv * xv + yv * yv);
		// A - Calculate The Position in space
		double xh = r * (Math.cos(N) * Math.cos(v + w) - Math.sin(N) * Math.sin(v + w) * Math.cos(i));
		double yh = r * (Math.sin(N) * Math.cos(v + w) + Math.cos(N) * Math.sin(v + w) * Math.cos(i));
		double zh = r * Math.sin(v + w) * Math.sin(i);
		// A - Correct for Perturbations
		double lonecl = Math.toRadians(Math.toDegrees(Math.atan2(yh, xh)) + longitudeCorrection);// longitude
		double latecl = Math
				.toRadians(Math.toDegrees(Math.asin(zh / Math.sqrt(xh * xh + yh * yh))) + latitudeCorrection);// latitude
		// A - Initialize Space Object that will hold all spatial representations
		Space space = new Space();
		Vector3D eliptic = new Vector3D(xh, yh, zh);
		Vector3D spherical = new Vector3D(r, lonecl, latecl);
		Vector3D rectangular = new Vector3D(r * Math.sin(lonecl) * Math.cos(latecl),
				r * Math.sin(lonecl) * Math.sin(latecl), r * Math.cos(lonecl));
		// A - Place Spheroid in Orbit around Centroid
		if (centroid != null) {
//			space.elliptic = this.centroid.space.elliptic.add(eliptic);
//			space.spherical = spherical;
			space.rectangular = this.centroid.space.rectangular.add(rectangular);
		}
		return space;
	}
	
	public void setCentroid(Orbital centroid) {
		this.centroid = centroid;
	}

//	public Coordinate getCoordinate(Point point) {
//		System.out.println("azimuth=" + azimuth);
//		System.out.println("elevation=" + elevation);
//		Coordinate coordinate = null;
//		double theta = Math.PI * azimuth / 180.0;
//		double phi = Math.PI * elevation / 180.0;
//		float cosT = (float) Math.cos(theta);
//		float sinT = (float) Math.sin(theta);
//		float cosP = (float) Math.cos(phi);
//		float sinP = (float) Math.sin(phi);
//		float cosTcosP = cosT * cosP;
//		float cosTsinP = cosT * sinP;
//		float sinTcosP = sinT * cosP;
//		float sinTsinP = sinT * sinP;
//		float near = 16;
//		float nearToObj = 1.5f;
//		double x0 = point.x;
//		double y0 = point.y;
//		double z0 = point.z;
//		float x1 = (float) (cosT * x0 + sinT * z0);
//		float y1 = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);
//		float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);
////		if (z1 < 0) {
//		x1 = x1 * near / (z1 + near + nearToObj);
//		y1 = y1 * near / (z1 + near + nearToObj);
//		coordinate = new Coordinate();
//		coordinate.point.x = x1;
//		coordinate.point.y = y1;
////		}
//		return coordinate;
//	}

	/**
	 * Returns time in days with decimal representing Hour, Minute, and Second
	 * 
	 * @param calendar
	 * @return
	 */
	public double getTime(Calendar calendar) {
//		System.out.println(calendar.getTime());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		double UT = this.getUniversalTime(hour, minute, second);
		if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
			year = -year;
		}
		int t = (367 * year) - 7 * (year + (month + 9) / 12) / 4 - 3 * ((year + (month - 9) / 7) / 100 + 1) / 4
				+ 275 * month / 9 + day - 730515;
		return t + UT / 24.0;
	}

	/**
	 * In this function we are trying to write time as hours and fractions of an
	 * hour.
	 * 
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public double getUniversalTime(int hour, int minute, int second) {
		return hour + (minute / 60.0) + (second / 3600.0);
	}

	public double rev(double x) {
		double rv = x - Math.round(x / 360.0) * 360;
		if (rv < 0.0) {
			rv = rv + 360;
		}
		return rv;
	}

//	public List<Point> getOrbit() {
//		LinkedList<Point> vertexList = new LinkedList<>();
//		if (this.orbitalPeriod != 0) {
//			int resolution = 100;
//			double increment = this.orbitalPeriod / resolution;
//			Calendar c = (Calendar) this.calendar.clone();
//			double count = 0;
//			while (count <= this.orbitalPeriod) {
//				Point position = this.projection.getPoint(this.getSpace(c).getPoint());
//				c.add(Calendar.DATE, (int) (Math.round(increment))); // number of days to ad
//				count += increment;
//				vertexList.add(position);
//			}
//		}
//		return vertexList;
//	}
//	
	public double getOrbitDistance() {
		return this.angularVelocity * (this.orbitalPeriod * 24 * 60 * 60);
	}

	public double getAngularVelocity(double radius) {
		return Math.sqrt(Unit.G * this.getMass() / radius);
	}

	public double getRotationCorrection(Calendar a) {
		double t = this.referenceCalendar.getTime().getTime();
		double T = a.getTime().getTime();
		double difference = (T - t) / 3600000;
//    	System.out.println("difference: "+difference);
		double remainder = difference % this.rotation;
//    	System.out.println("remainder: "+remainder);
//    	System.out.println("rotation:"+ this.rotation);
		double ratio = remainder / this.rotation;
		double angle = ratio * 360;
		return angle;
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
		List<Point> vertexList = this.getOrbit(this.centroid);
		graphics.setColor(Color.gray);
		for (int i = 1; i < vertexList.size(); i++) {
			graphics.drawLine((int) (vertexList.get(i - 1).x * this.projection.scale),
					(int) (vertexList.get(i - 1).y * this.projection.scale),
					(int) (vertexList.get(i).x * this.projection.scale),
					(int) (vertexList.get(i).y * this.projection.scale));
		}
		graphics.setColor(this.color);
		Point point = this.projection.getPoint(this.projection.space.getPoint());//this.buffer
		double x = point.x * this.projection.scale;
		double y = point.y * this.projection.scale;
		graphics.setColor(this.color);
//		double radius = 8;
//		x = x - (radius / 2);
//		y = y - (radius / 2);
//		graphics.fillOval((int) x, (int) y, (int) radius, (int) radius);
		graphics.setColor(Color.black);
		String text = this.name.substring(0, 1).toUpperCase() + this.name.substring(1) + "";
		int width = graphics.getFontMetrics().stringWidth(text);
		x = x - (width / 2);
		y -= 2;
		graphics.drawString(text, (int) x,
				(int) y);
	}
}
//this.buffer = new Space(this.space);
//this.buffer.subtract(this.center);
//this.projection.setSpace(this.buffer);
//public static void main(String[] args) {
//Orbital orbital = new Orbital();
//System.out.println(orbital.getTime(orbital.referenceCalendar));
//System.out.println(orbital.getTime(new GregorianCalendar(1990, 3, 19, 0, 0, 0)));
//System.out.println(orbital.getTime(new GregorianCalendar(1990, 3, 19, 12, 0, 0)));
//}
//public double getOrbitLength() {
////List<Point>
////for(int i = 0; i <)
//}

//public List<Vector3D> getOrbit() {
//LinkedList<Vector3D> vertexList = new LinkedList<Vector3D>();
//if (this.orbitalPeriod != 0) {
//int resolution = 100;
//double increment = this.orbitalPeriod / resolution;
//Calendar c = (Calendar) this.calendar.clone();
//double count = 0;
//while (count <= this.orbitalPeriod) {
//	Vector3D position = this.getSpace(c).rectangular;
//	c.add(Calendar.DATE, (int) (Math.round(increment))); // number of days to ad
//	count += increment;
//	vertexList.add(position);
//}
//}
//return vertexList;
//}
//public double toRadians(double d) {
//return Math.PI * d / 180;
//}
//
//public double toDegrees(double rad) {
//return rev(180.0 * rad / Math.PI);
//}
//public double dateToJulian(Calendar date) {
//int year = date.get(Calendar.YEAR);
//int month = date.get(Calendar.MONTH) + 1;
//int day = date.get(Calendar.DAY_OF_MONTH);
//int hour = date.get(Calendar.HOUR_OF_DAY);
//int minute = date.get(Calendar.MINUTE);
//int second = date.get(Calendar.SECOND);
//
//double extra = (100.0 * year) + month - 190002.5;
//return (367.0 * year) - (Math.floor(7.0 * (year + Math.floor((month + 9.0) / 12.0)) / 4.0))
//		+ Math.floor((275.0 * month) / 9.0) + day + ((hour + ((minute + (second / 60.0)) / 60.0)) / 24.0)
//		+ 1721013.5 - ((0.5 * extra) / Math.abs(extra)) + 0.5;
//}
//public Space getSpace(Calendar c) {
//double T = this.dateToJulian(this.startTime);
//double t = this.dateToJulian(c);
//double d = t - T;
//double N = this.longitudeOfAscendingNode[0] + this.longitudeOfAscendingNode[1] * d;
//double i = this.inclination[0] + this.inclination[1] * d;
//double w = this.argumentOfPeriapsis[0] + this.argumentOfPeriapsis[1] * d;
//double a = this.semiMajorAxis[0]+this.semiMajorAxis[1]*d;
//double e = this.eccentricity[0] + this.eccentricity[1] * d;
//double M = this.meanAnomaly[0] + this.meanAnomaly[1] * d;
//N = this.rev(N);
//i = this.rev(i);
//w = this.rev(w);
//M = this.rev(M);
//double E = M + (180 / Math.PI) * e * Math.sin(toRadians(M)) * (1.0 + e * Math.cos(toRadians(M)));
//double error = 1;
//while (error > 0.005) {
//	double E1 = E - (E - (180 / Math.PI) * e * Math.sin(toRadians(E)) - M) / (1 - e * Math.cos(toRadians(E)));
//	error = Math.abs(E - E1);
//	E = E1;
//}
////xv = r * cos(v) = cos(E) - e
////yv = r * sin(v) = sqrt(1.0 - e*e) * sin(E)
//double x = a * (Math.cos(toRadians(E)) - e);
//double y = a * (Math.sqrt(1.0 - e * e) * Math.sin(toRadians(E)));
//double r = Math.sqrt(x * x + y * y);
//double v = toDegrees(Math.atan2(y, x));
//double n_rad = toRadians(N);
//double xw_rad = toRadians(v + w);
//double i_rad = toRadians(i);
//// Now we know the Moon's position in the plane of the lunar orbit. To
//// compute the Moon's position in ecliptic coordinates, we apply these
//// formulae:
//double xeclip = r * (Math.cos(n_rad) * Math.cos(xw_rad) - Math.sin(n_rad) * Math.sin(xw_rad) * Math.cos(i_rad));
//double yeclip = r * (Math.sin(n_rad) * Math.cos(xw_rad) + Math.cos(n_rad) * Math.sin(xw_rad) * Math.cos(i_rad));
//double zeclip = r * Math.sin(xw_rad) * Math.sin(i_rad);
//
//Space space = new Space();
//double RA = toDegrees(Math.atan2(yeclip, xeclip));
//double Decl = toDegrees(Math.asin(zeclip / r));
//Vector3D eliptic = new Vector3D(xeclip, yeclip, zeclip);
//Vector3D spherical = new Vector3D(r,RA,Decl);
//space.eliptic = eliptic;
//space.spherical = spherical;
//space.time = this.getCalendarString(null, c);
//return space;
//}
