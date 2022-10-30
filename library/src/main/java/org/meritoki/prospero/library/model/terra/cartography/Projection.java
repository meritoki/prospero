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
package org.meritoki.prospero.library.model.terra.cartography;

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

public class Projection implements ProjectionInterface {

	static Logger logger = LogManager.getLogger(Projection.class.getName());
	public Space space = new Space();
	public double radius = 1;
    public double a;
    public double b;
    public double c;
    public double unit = 1/Unit.ASTRONOMICAL;
	public double scale;
	public int azimuth = 0;// 35;
	public int elevation = 0;// 30;
	public double yMax = 0;
	public double xMax = 0;
	public int latitudeInterval = 15;
	public int longitudeInterval = 30;

	public float near = 3;
	public float nearToObject = 1.5f;
	
	public Projection() {}
	
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
		this.nearToObject= (float)this.radius;
		this.near = 2 * this.nearToObject;
	}
	
	public void setRadius(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.radius = this.a;
		this.nearToObject= (float)this.a;
		this.near = 2 * this.nearToObject;
	}
	
	public void setAzimuth(int azimuth) {
//		if(print)System.out.println("setAzimuth("+azimuth+")");
		this.azimuth = azimuth;
	}

	public void setElevation(int elevation) {
//		if(print)System.out.println("setElevation("+elevation+")");
		this.elevation = elevation;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setNear(float width) {
		this.near = width;
	}

	public void setNearToObject(float height) {
		this.nearToObject = height;
	}

	/**
	 * 
	 */
	@Override
	public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
		return null;
	}
	
	public double getA(double unit) {
		return this.a*unit;
	}

	public double getB(double unit) {
		return this.b*unit;
	}

	public double getC(double unit) {
		return this.c*unit;
	}
	
	public double getRadius(double unit) {
		return this.radius*unit;
	}


	public List<Coordinate> getPointList(double vertical, List<org.locationtech.jts.geom.Point> pointList) {
		List<Coordinate> coordinateList = new ArrayList<>();
		org.locationtech.jts.geom.Coordinate[] a;
		Coordinate coordinate;
		for (org.locationtech.jts.geom.Point m : pointList) {
			a = m.getCoordinates();
			for (int i = 0; i < a.length; i++) {
				coordinate = this.getCoordinate(vertical, a[i].y, a[i].x);
				if (coordinate != null)
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
				Coordinate coordinate = this.getCoordinate(vertical, a[i].y, a[i].x);
				if (coordinate != null) {
					polygon.coordinateList.add(coordinate);
				}
			}
			polygonList.add(polygon);
		}
		logger.debug("getMultiPolygonList("+vertical+","+multiPolygonList.size()+") coordinateList.size()="+polygonList.size());
		return polygonList;
	}
	
	public List<Coordinate> getMultiLineStringList(double vertical, List<MultiLineString> multiLineStringList) {
		List<Coordinate> coordinateList = new ArrayList<>();
		org.locationtech.jts.geom.Coordinate[] a;
		Coordinate coordinate;
		for (MultiLineString m : multiLineStringList) {
			a = m.getCoordinates();
			for (int i = 0; i < a.length; i++) {
				coordinate = this.getCoordinate(vertical, a[i].y, a[i].x);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		return coordinateList;
	}
	
	public List<Coordinate> getCoordinateList(double vertical, List<org.meritoki.prospero.library.model.unit.Coordinate> cList) {
		List<Coordinate> coordinateList = new ArrayList<>();
		Coordinate coordinate;
		for (org.meritoki.prospero.library.model.unit.Coordinate c : cList) {
			coordinate = this.getCoordinate(vertical, c.latitude, c.longitude);
			if (coordinate != null) {
				coordinate.flag = c.flag;
				coordinate.latitude = c.latitude;
				coordinate.longitude = c.longitude;
				coordinate.attribute.putAll(c.attribute);
				coordinateList.add(coordinate);
			}
			
		}
		logger.debug("getCoordinateList("+vertical+", "+cList.size()+") coordinateList.size()="+coordinateList.size());
		return coordinateList;
	}

	public List<Coordinate> getGridCoordinateList(double vertical, int latitudeInterval, int longitudeInterval) {
		List<Coordinate> coordinateList = new ArrayList<>();
		Coordinate coordinate;
		int latitudeMax = 90;
		int latitudeMin = -90;
		int longitudeMax = 180;
		int longitudeMin = -180;
		for (int i = latitudeMin; i <= latitudeMax; i++) {
			for (int j = longitudeMin; j <= longitudeMax; j += longitudeInterval) {
				double latitude = i;
				double longitude = j;
				coordinate = this.getCoordinate(vertical, latitude, longitude);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		for (int i = latitudeMin; i <= latitudeMax; i++) {
			for (int j = longitudeMin; j <= longitudeMax; j++) {
				double latitude = i;
				double longitude = j;
				coordinate = this.getCoordinate(vertical, latitude, longitude);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		logger.debug("getGridCoordinateList("+vertical+","+latitudeInterval+","+longitudeInterval+") coordinateList.size()="+coordinateList.size());
		return coordinateList;
	}
	
	public Coordinate getCoordinate(Point point) {
		Coordinate coordinate = null;
		double theta = Math.PI * azimuth/ 180.0;
		double phi = Math.PI * elevation/ 180.0;
		float cosT = (float) Math.cos(theta);
		float sinT = (float) Math.sin(theta);
		float cosP = (float) Math.cos(phi);
		float sinP = (float) Math.sin(phi);
		float cosTcosP = cosT * cosP;
		float cosTsinP = cosT * sinP;
		float sinTcosP = sinT * cosP;
		float sinTsinP = sinT * sinP;
		float near = (float)(this.near*this.scale);//3; // distance from eye to near plane
		float nearToObj = (float)(this.nearToObject*this.scale);//1.5f; // distance from near plane to center of object
		double x0 = point.x;
		double y0 = point.y;
		double z0 = point.z;

		// compute an orthographic projection
		float x1 = (float) (cosT * x0 + sinT * z0);
		float y1 = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);

		// now adjust things to get a perspective projection
		float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);
//		if (z1 < 0) {
		x1 = x1 * near / (z1 + near + nearToObj);
		y1 = y1 * near / (z1 + near + nearToObj);
		coordinate = new Coordinate();
//			coordinate.x = (int) (scale * x1 + 0.5);
//			coordinate.y = (int) (scale * y1 + 0.5);
		coordinate.point.x = x1;
		coordinate.point.y = y1;
		

//		}
		// x1 and y1 overlap of the other side of globe
		// the 0.5 is to round off when converting to int
		return coordinate;
	}
	
	public Point getPoint(Point point) {
		Point p = null;
		double theta = Math.PI * azimuth / 180.0;
		double phi = Math.PI * elevation / 180.0;
		float cosT = (float) Math.cos(theta);
		float sinT = (float) Math.sin(theta);
		float cosP = (float) Math.cos(phi);
		float sinP = (float) Math.sin(phi);
		float cosTcosP = cosT * cosP;
		float cosTsinP = cosT * sinP;
		float sinTcosP = sinT * cosP;
		float sinTsinP = sinT * sinP;
		// The following two lines fixed defects when viewing in 3 Dimensions
		float near = (float)(this.near*this.scale);//32; // distance from eye to near plane
		float nearToObj = (float)(this.nearToObject*this.scale);//8f; // 1.5// distance from near plane to center of object
		double x0 = point.x;
		double y0 = point.y;
		double z0 = point.z;
		// compute an orthographic projection
		float x1 = (float) (cosT * x0 + sinT * z0);
		float y1 = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);
		// now adjust things to get a perspective projection
		float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);
		x1 = x1 * near / (z1 + near + nearToObj);
		y1 = y1 * near / (z1 + near + nearToObj);
		p = new Point();
		p.x = x1;
		p.y = y1;
		p.z = z1;
		return p;
	}
	

}
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
