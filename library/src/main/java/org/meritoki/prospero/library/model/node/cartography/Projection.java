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
package org.meritoki.prospero.library.model.node.cartography;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Polygon;
import org.meritoki.prospero.library.model.unit.Space;
import org.meritoki.prospero.library.model.unit.Unit;

/**
 * 
 * <ol type="A">
 * <li><a href=
 * "https://www.geeksforgeeks.org/java-toradians-method-example/">https://www.geeksforgeeks.org/java-toradians-method-example/</a></li>
 * <li><a href=
 * "https://en.wikipedia.org/wiki/Spherical_coordinate_system">https://en.wikipedia.org/wiki/Spherical_coordinate_system</a></li>
 * </ol>
 */
public class Projection implements ProjectionInterface {

	static Logger logger = LogManager.getLogger(Projection.class.getName());
	public Space space = new Space();
	public double radius = 1;
	public double a;
	public double b;
	public double c;
	public double unit = 1 / Unit.ASTRONOMICAL;
	public double scale = 7200000.0;;
	public double azimuth = 0;// 35;
	public double elevation = 0;// 30;
	public double obliquity = 0;
	public double angle;
	public double yMax = 0;
	public double xMax = 0;
	public float nearEye = 3;
	public float nearObject = 1.5f;
	public boolean zFlag = true;

	public Projection() {
	}

	public Projection(double radius) {
		this.setRadius(radius);
	}

	public Projection(double a, double b, double c) {
		this.setRadius(a, b, c);
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public void setUnit(double unit) {
		this.unit = unit;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		this.a = this.radius;
		this.b = this.radius;
		this.c = this.radius;
		this.nearObject = (float) this.radius;
		this.nearEye = 2 * this.nearObject;
	}

	public void setRadius(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.radius = (this.a * this.b * this.c) / 3;
		this.nearObject = (float) this.radius;
		this.nearEye = 2 * this.nearObject;
	}

	public void setAzimuth(double azimuth) {
//		logger.info("setAzimuth("+azimuth+")");
		this.azimuth = azimuth;
	}

	public void setElevation(double elevation) {
//		logger.info("setElevation("+elevation+")");
		this.elevation = elevation;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setNearEye(float width) {
		this.nearEye = width;
	}

	public void setNearObject(float height) {
		this.nearObject = height;
	}

	public double getA(double unit) {
		return this.a * unit;
	}

	public double getB(double unit) {
		return this.b * unit;
	}

	public double getC(double unit) {
		return this.c * unit;
	}

	public double getRadius(double unit) {
		return this.radius * unit;
	}

	public List<Point> getPointList(double vertical, List<org.locationtech.jts.geom.Point> pointList) {
		List<Point> coordinateList = new ArrayList<>();
		org.locationtech.jts.geom.Coordinate[] a;
		Point coordinate;
		for (org.locationtech.jts.geom.Point m : pointList) {
			a = m.getCoordinates();
			for (int i = 0; i < a.length; i++) {
				coordinate = this.getPoint(vertical, a[i].y, a[i].x);
//				if (coordinate != null)
				coordinateList.add(coordinate);
			}
		}
		return coordinateList;
	}

	public List<Polygon> getPolygonList(double vertical, List<MultiPolygon> multiPolygonList) {
		List<Polygon> polygonList = new ArrayList<>();
		org.locationtech.jts.geom.Coordinate[] a;
		Polygon polygon;
		for (MultiPolygon m : multiPolygonList) {
			a = m.getCoordinates();
			polygon = new Polygon();
			for (int i = 0; i < a.length; i++) {
				Point coordinate = this.getPoint(vertical, a[i].y, a[i].x);
				if (coordinate != null) {
					polygon.coordinateList.add(coordinate);
				}
			}
			polygonList.add(polygon);
		}
		logger.debug("getMultiPolygonList(" + vertical + "," + multiPolygonList.size() + ") coordinateList.size()="
				+ polygonList.size());
		return polygonList;
	}

	public List<Point> getMultiLineStringList(double vertical, List<MultiLineString> multiLineStringList) {
		List<Point> coordinateList = new ArrayList<>();
		org.locationtech.jts.geom.Coordinate[] a;
		Point coordinate;
		for (MultiLineString m : multiLineStringList) {
			a = m.getCoordinates();
			for (int i = 0; i < a.length; i++) {
				coordinate = this.getPoint(vertical, a[i].y, a[i].x);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		return coordinateList;
	}

	public List<Point> getCoordinateList(double vertical,
			List<org.meritoki.prospero.library.model.unit.Coordinate> coordinateList) {
		List<Point> pointList = new ArrayList<>();
		Point point;
		for (org.meritoki.prospero.library.model.unit.Coordinate coordinate : coordinateList) {
			point = this.getPoint(vertical, coordinate.latitude, coordinate.longitude);
			point.flag = coordinate.flag;

			point.attribute.putAll(coordinate.attribute);
			pointList.add(point);

		}
		logger.debug("getCoordinateList(" + vertical + ", " + coordinateList.size() + ") coordinateList.size()="
				+ pointList.size());
		return pointList;
	}

	public List<Point> getGridPointList(double vertical, int latitudeInterval, int longitudeInterval) {
		List<Point> coordinateList = new ArrayList<>();
		Point coordinate;
		int latitudeMax = 90;
		int latitudeMin = -90;
		int longitudeMax = 180;
		int longitudeMin = -180;
		for (int i = latitudeMin; i <= latitudeMax; i++) {
			for (int j = longitudeMin; j <= longitudeMax; j += longitudeInterval) {
				double latitude = i;
				double longitude = j;
				coordinate = this.getPoint(vertical, latitude, longitude);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		for (int i = latitudeMin; i <= latitudeMax; i += latitudeInterval) {
			for (int j = longitudeMin; j <= longitudeMax; j++) {
				double latitude = i;
				double longitude = j;
				coordinate = this.getPoint(vertical, latitude, longitude);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		logger.debug("getGridCoordinateList(" + vertical + "," + latitudeInterval + "," + longitudeInterval
				+ ") coordinateList.size()=" + coordinateList.size());
		return coordinateList;
	}

	/**
	 * Reference A: Spherical to Cartesian Coordinates
	 * 
	 * 
	 * 
	 * Standard Spherical to Cartesian Coordinates ISO 80000-2:2019
	 * <ul>
	 * <li>x = r * sin(theta) cos(phi)</li>
	 * <li>y = r * sin(theta) sin(phi)</li>
	 * <li>z = r * cos(theta)</li>
	 * </ul>
	 * 
	 * <ul>
	 * <li>r > 0</li>
	 * <li>theta [0,PI]</li>
	 * <li>phi [0,2PI]</li>
	 * </ul>
	 *
	 * @param vertical  ...,-2,-1,0,1,2,...
	 * @param latitude  (theta) -90,90
	 * @param longitude (phi) -180,180
	 */
	@Override
	public Point getPoint(double vertical, double latitude, double longitude) {
		latitude = Math.toRadians(latitude);
		longitude = Math.toRadians(longitude);
		latitude += Math.PI / 2;// Zeros -90 OR -PI/2
		longitude += Math.PI;// Zeros -180 OR -PI
		longitude = (longitude + Math.toRadians(angle)) % (2 * Math.PI);
		// Standard Conversion from Spherical to Cartesian Coordinates
		double x = this.getA(this.unit) * Math.sin(latitude) * Math.cos(longitude);
		double y = this.getB(this.unit) * Math.sin(latitude) * Math.sin(longitude);
		double z = this.getC(this.unit) * Math.cos(latitude);
		Point point = new Point(-x, -z, y);
		Point buffer = new Point(point);
		point.y = buffer.y * Math.cos(Math.toRadians(this.obliquity))
				- buffer.z * Math.sin(Math.toRadians(this.obliquity));
		point.z = buffer.y * Math.sin(Math.toRadians(this.obliquity))
				+ buffer.z * Math.cos(Math.toRadians(this.obliquity));
		this.zFlag = false;
		Point p = this.getSpacePoint(point);
		if (p != null) {
			if (p.x > this.xMax) {
				this.xMax = p.x;
			}
			if (p.y > this.yMax) {
				this.yMax = p.y;
			}
		}
		this.zFlag = true;
		return p;
	}

	public Point getSpacePoint(Point point) {
		Point spacePoint = this.space.getPoint();
		point.add(spacePoint);
		return this.getPoint(point);
	}

	/**
	 * 
	 * @param point
	 * @return
	 */
	public Point getPoint(Point point) {

		Point p = null;
		double theta = Math.toRadians(this.azimuth);// Math.PI * this.azimuth/ 180.0;
		double phi = Math.toRadians(this.elevation);// Math.PI * this.elevation/ 180.0;
		float cosTheta = (float) Math.cos(theta);
		float sinTheta = (float) Math.sin(theta);
		float cosPhi = (float) Math.cos(phi);
		float sinPhi = (float) Math.sin(phi);
		float cosTcosP = cosTheta * cosPhi;
		float cosTsinP = cosTheta * sinPhi;
		float sinTcosP = sinTheta * cosPhi;
		float sinTsinP = sinTheta * sinPhi;
		float near = (float) (this.nearEye * this.scale);// 3; // distance from eye to near plane
		float nearToObj = (float) (this.nearObject * this.scale);// 1.5f; // distance from near plane to center of
																	// object
		double x0 = point.x;
		double y0 = point.y;
		double z0 = point.z;
		float x1 = (float) (cosTheta * x0 + sinTheta * z0);// compute an orthographic projection
		float y1 = (float) (-sinTsinP * x0 + cosPhi * y0 + cosTsinP * z0);// compute an orthographic projection
		float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinPhi * y0);// now adjust things to get a perspective
																			// projection
		if (this.zFlag) {
			x1 = x1 * near / (z1 + near + nearToObj);
			y1 = y1 * near / (z1 + near + nearToObj);
			p = new Point(x1, y1, z1);
		} else {
			if (z1 < 0) {
				x1 = x1 * near / (z1 + near + nearToObj);
				y1 = y1 * near / (z1 + near + nearToObj);
				p = new Point(x1, y1, z1);
			}
		}
		return p;// new Point(x1, y1, z1);
	}

//	/**
//	 * Get Point Directly for Painting
//	 * @param point
//	 * @return
//	 */
//	public Point getPoint(Point point) {
//		Point p = null;
//		double theta = Math.toRadians(this.azimuth);//Math.PI * azimuth / 180.0;
//		double phi = Math.toRadians(this.elevation);//Math.PI * elevation / 180.0;
//		float cosT = (float) Math.cos(theta);
//		float sinT = (float) Math.sin(theta);
//		float cosP = (float) Math.cos(phi);
//		float sinP = (float) Math.sin(phi);
//		float cosTcosP = cosT * cosP;
//		float cosTsinP = cosT * sinP;
//		float sinTcosP = sinT * cosP;
//		float sinTsinP = sinT * sinP;
//		float nearEye = (float)(this.nearEye*this.scale);// distance from eye to near plane
//		float nearObject = (float)(this.nearObject*this.scale);//distance from near plane to center of object
//		double x0 = point.x;
//		double y0 = point.y;
//		double z0 = point.z;
//		float x1 = (float) (cosT * x0 + sinT * z0);// compute an orthographic projection
//		float y1 = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);// compute an orthographic projection
//		float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);// now adjust things to get a perspective projection
//		x1 = x1 * nearEye / (z1 + nearEye + nearObject);
//		y1 = y1 * nearEye / (z1 + nearEye + nearObject);
//		return new Point(x1,y1,z1);
//	}

	@Override
	public String toString() {
		return "Projection: {" + this.space.toString() + ", scale:" + this.scale + "}";
	}
}
//p = new Coordinate();
//p.point.x = x1;
//p.point.y = y1;
//if (coordinate != null) {
//coordinate.flag = c.flag;
//coordinate.latitude = c.latitude;
//coordinate.longitude = c.longitude;
//}
//latitude -= Math.toRadians(this.obliquity);
//latitude -= Math.PI;//Ends up
//latitude = (latitude - Math.toRadians(this.obliquity)) % Math.PI;
//double x = this.getA(this.unit) * Math.sin((Math.PI/2)-latitude) * Math.sin((Math.PI/2)-longitude);
//double z = this.getC(this.unit) * Math.cos(latitude) * Math.sin(longitude);
//double y = this.getB(this.unit) * Math.sin(latitude);
//p = new Point(x1,y1,z1);
//p.x = x1;
//p.y = y1;
//p.z = z1;
//coordinate.x = (int) (scale * x1 + 0.5);
//coordinate.y = (int) (scale * y1 + 0.5);
//public void setCenter(Space space) {
//logger.info("setCenter("+space+")");
//this.center = space;
//this.space.subtract(this.center);
//}
//public double radians = 0.0174532925199433;
//public Point[] vertices;
//public Edge[] edges;
//public boolean cubeFlag;
//void drawWireframe(Graphics g) {
//double theta = Math.PI * azimuth / 180.0;
//double phi = Math.PI * elevation / 180.0;
//float cosT = (float) Math.cos(theta), sinT = (float) Math.sin(theta);
//float cosP = (float) Math.cos(phi), sinP = (float) Math.sin(phi);
//float cosTcosP = cosT * cosP, cosTsinP = cosT * sinP, sinTcosP = sinT * cosP, sinTsinP = sinT * sinP;
//java.awt.Point[] points;
//points = new java.awt.Point[vertices.length];
//int j;
//int scaleFactor = width / 4;
//float near = 3; // distance from eye to near plane
//float nearToObj = 1.5f; // distance from near plane to center of object
//for (j = 0; j < vertices.length; ++j) {
//	int x0 = (int) vertices[j].x;
//	int y0 = (int) vertices[j].y;
//	int z0 = (int) vertices[j].z;
//
//	// compute an orthographic projection
//	float x1 = cosT * x0 + sinT * z0;
//	float y1 = -sinTsinP * x0 + cosP * y0 + cosTsinP * z0;
//
//	// now adjust things to get a perspective projection
//	float z1 = cosTcosP * z0 - sinTcosP * x0 - sinP * y0;
//	x1 = x1 * near / (z1 + near + nearToObj);
//	y1 = y1 * near / (z1 + near + nearToObj);
//	points[j] = new java.awt.Point((int) (scaleFactor * x1 + 0.5),
//			(int) (scaleFactor * y1 + 0.5));
//}
//g.setColor(Color.black);
//for (j = 0; j < edges.length; ++j) {
//	g.drawLine(points[edges[j].a].x, points[edges[j].a].y, points[edges[j].b].x, points[edges[j].b].y);
//}
//}
//this.vertices = new Point[8];
//this.vertices[0] = new Point(-1, -1, -1);
//this.vertices[1] = new Point(-1, -1, 1);
//this.vertices[2] = new Point(-1, 1, -1);
//this.vertices[3] = new Point(-1, 1, 1);
//this.vertices[4] = new Point(1, -1, -1);
//this.vertices[5] = new Point(1, -1, 1);
//this.vertices[6] = new Point(1, 1, -1);
//this.vertices[7] = new Point(1, 1, 1);
//this.edges = new Edge[12];
//this.edges[0] = new Edge(0, 1);
//this.edges[1] = new Edge(0, 2);
//this.edges[2] = new Edge(0, 4);
//this.edges[3] = new Edge(1, 3);
//this.edges[4] = new Edge(1, 5);
//this.edges[5] = new Edge(2, 3);
//this.edges[6] = new Edge(2, 6);
//this.edges[7] = new Edge(3, 7);
//this.edges[8] = new Edge(4, 5);
//this.edges[9] = new Edge(4, 6);
//this.edges[10] = new Edge(5, 7);
//this.edges[11] = new Edge(6, 7);
