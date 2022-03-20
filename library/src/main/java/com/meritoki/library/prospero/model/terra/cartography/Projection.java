package com.meritoki.library.prospero.model.terra.cartography;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import com.meritoki.library.prospero.model.unit.Edge;
import com.meritoki.library.prospero.model.unit.Point;
import com.meritoki.library.prospero.model.unit.Coordinate;

public class Projection implements ProjectionInterface {

	public int width;
	public int height;
	public double scale;
	public double yMax = 0;
	public double xMax = 0;
	public int latitudeInterval = 15;
	public int longitudeInterval = 30;
	public int azimuth = 0;// 35;
	public int elevation = 0;// 30;
	public Point[] vertices;
	public Edge[] edges;
	public double radians = 0.0174532925199433;
	public boolean cubeFlag;
	public boolean print = false;
	
	public Projection() {
		this.vertices = new Point[8];
		this.vertices[0] = new Point(-1, -1, -1);
		this.vertices[1] = new Point(-1, -1, 1);
		this.vertices[2] = new Point(-1, 1, -1);
		this.vertices[3] = new Point(-1, 1, 1);
		this.vertices[4] = new Point(1, -1, -1);
		this.vertices[5] = new Point(1, -1, 1);
		this.vertices[6] = new Point(1, 1, -1);
		this.vertices[7] = new Point(1, 1, 1);
		this.edges = new Edge[12];
		this.edges[0] = new Edge(0, 1);
		this.edges[1] = new Edge(0, 2);
		this.edges[2] = new Edge(0, 4);
		this.edges[3] = new Edge(1, 3);
		this.edges[4] = new Edge(1, 5);
		this.edges[5] = new Edge(2, 3);
		this.edges[6] = new Edge(2, 6);
		this.edges[7] = new Edge(3, 7);
		this.edges[8] = new Edge(4, 5);
		this.edges[9] = new Edge(4, 6);
		this.edges[10] = new Edge(5, 7);
		this.edges[11] = new Edge(6, 7);
	}
	
	public void setAzimuth(int azimuth) {
		if(print)System.out.println("setAzimuth("+azimuth+")");
		this.azimuth = azimuth;
	}

	public void setElevation(int elevation) {
		if(print)System.out.println("setElevation("+elevation+")");
		this.elevation = elevation;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
		return null;
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
	
	public List<Coordinate> getMultiPolygonList(double vertical, List<MultiPolygon> multiPolygonList) {
		if(print)System.out.println("getMultiPolygonList("+vertical+","+multiPolygonList.size()+")");
		List<Coordinate> coordinateList = new ArrayList<>();
		org.locationtech.jts.geom.Coordinate[] a;
		Coordinate coordinate;
		for (MultiPolygon m : multiPolygonList) {
			a = m.getCoordinates();
			for (int i = 0; i < a.length; i++) {
				coordinate = this.getCoordinate(vertical, a[i].y, a[i].x);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		if(print)System.out.println("getMultiPolygonList("+vertical+","+multiPolygonList.size()+") coordinateList.size()="+coordinateList.size());
		return coordinateList;
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
	
	public List<Coordinate> getCoordinateList(double vertical, List<com.meritoki.library.prospero.model.unit.Coordinate> cList) {
		if(print)System.out.println("getCoordinateList("+vertical+", "+cList.size()+")");
		List<Coordinate> coordinateList = new ArrayList<>();
		Coordinate coordinate;
		for (com.meritoki.library.prospero.model.unit.Coordinate c : cList) {
			coordinate = this.getCoordinate(vertical, c.latitude, c.longitude);
			if (coordinate != null) {
				coordinate.flag = c.flag;
				coordinate.latitude = c.latitude;
				coordinate.longitude = c.longitude;
				coordinate.attribute.putAll(c.attribute);
				coordinateList.add(coordinate);
			}
			
		}
		if(print)System.out.println("getCoordinateList("+vertical+", "+cList.size()+") coordinateList.size()="+coordinateList.size());
		return coordinateList;
	}

	public List<Coordinate> getGridCoordinateList(double vertical, int latitudeInterval, int longitudeInterval) {
		if(print)System.out.println("getGridCoordinateList("+vertical+","+latitudeInterval+","+longitudeInterval+")");
		List<Coordinate> coordinateList = new ArrayList<>();
		Coordinate coordinate;
		for (int i = -90; i <= 90; i++) {
			for (int j = -180; j <= 180; j += longitudeInterval) {
				double latitude = i;
				double longitude = j;
				coordinate = this.getCoordinate(vertical, latitude, longitude);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		for (int i = -90; i <= 90; i += latitudeInterval) {
			for (int j = -180; j <= 180; j++) {
				double latitude = i;
				double longitude = j;
				coordinate = this.getCoordinate(vertical, latitude, longitude);
				if (coordinate != null)
					coordinateList.add(coordinate);
			}
		}
		if(print)System.out.println("getGridCoordinateList("+vertical+","+latitudeInterval+","+longitudeInterval+") coordinateList.size()="+coordinateList.size());
		return coordinateList;
	}
	
	public Coordinate getCoordinate(Point point3D) {
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
		float near = 3; // distance from eye to near plane
		float nearToObj = 1.5f; // distance from near plane to center of object
		double x0 = point3D.x;
		double y0 = point3D.y;
		double z0 = point3D.z;

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
	
	void drawWireframe(Graphics g) {
		double theta = Math.PI * azimuth / 180.0;
		double phi = Math.PI * elevation / 180.0;
		float cosT = (float) Math.cos(theta), sinT = (float) Math.sin(theta);
		float cosP = (float) Math.cos(phi), sinP = (float) Math.sin(phi);
		float cosTcosP = cosT * cosP, cosTsinP = cosT * sinP, sinTcosP = sinT * cosP, sinTsinP = sinT * sinP;
		java.awt.Point[] points;
		points = new java.awt.Point[vertices.length];
		int j;
		int scaleFactor = width / 4;
		float near = 3; // distance from eye to near plane
		float nearToObj = 1.5f; // distance from near plane to center of object
		for (j = 0; j < vertices.length; ++j) {
			int x0 = (int) vertices[j].x;
			int y0 = (int) vertices[j].y;
			int z0 = (int) vertices[j].z;

			// compute an orthographic projection
			float x1 = cosT * x0 + sinT * z0;
			float y1 = -sinTsinP * x0 + cosP * y0 + cosTsinP * z0;

			// now adjust things to get a perspective projection
			float z1 = cosTcosP * z0 - sinTcosP * x0 - sinP * y0;
			x1 = x1 * near / (z1 + near + nearToObj);
			y1 = y1 * near / (z1 + near + nearToObj);
			points[j] = new java.awt.Point((int) (scaleFactor * x1 + 0.5),
					(int) (scaleFactor * y1 + 0.5));
		}
		g.setColor(Color.black);
		for (j = 0; j < edges.length; ++j) {
			g.drawLine(points[edges[j].a].x, points[edges[j].a].y, points[edges[j].b].x, points[edges[j].b].y);
		}
	}
}
