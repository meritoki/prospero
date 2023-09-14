package org.meritoki.prospero.library.model.node.cartography;

import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.unit.Point;

public class AzimuthalNorth extends Projection {

	private double centerLatitude = Math.toRadians(90);// * this.radians;
	private double centerLongitude = Math.toRadians(0);// * this.radians;

	public AzimuthalNorth() {
		super(1);
	}

	@Override
	public Point getPoint(double vertical, double latitude, double longitude) {
		Point point = null;
		latitude = Math.toRadians(latitude);// * this.radians;// MultiPolygon y
		longitude = Math.toRadians(longitude);// * this.radians; // MultiPolygon x
		if (latitude >= 0) {
			point = new Point();
			double c = Math.acos(Math.sin(this.centerLatitude) * Math.sin(latitude) + (Math.cos(this.centerLatitude)
					* Math.cos(latitude) * Math.cos(longitude - this.centerLongitude)));
			double k = c / Math.sin(c);
			double x = k * this.unit * 5000 * (Math.cos(latitude) * Math.sin(longitude - this.centerLongitude));
			double y = k * this.unit * 5000 * (Math.cos(this.centerLatitude) * Math.sin(latitude)
					- Math.sin(this.centerLatitude) * Math.cos(latitude) * Math.cos(longitude - this.centerLongitude));
			point.x = x;
			point.y = y;
		}
		if (point != null) {
			if(point.x > this.xMax) {
				this.xMax = point.x;
			}
			if(point.y > this.yMax) {
				this.yMax = point.y;
			}
			double x = point.x;
			double y = -point.y;
			double z = vertical;
//			Point point3D = new Point(x, y, z);
			return this.getPoint(new Point(x, y, z));
		} else {
			return null;
		}
	}

	@Override
	public List<Point> getGridPointList(double vertical, int latitudeInterval, int longitudeInterval) {
		List<Point> coordinateList = new ArrayList<>();
		Point coordinate;
		for (int i = -90; i < 90; i++) {
			for (int j = -180; j < 180; j += longitudeInterval) {
				double latitude = i;
				double longitude = j;
				if (latitude >= 0) {
					coordinate = this.getPoint(vertical, latitude, longitude);
					if (coordinate != null)
						coordinateList.add(coordinate);
				}
			}
		}
		for (int i = -90; i < 90; i += latitudeInterval) {
			for (int j = -180; j < 180; j++) {
				double latitude = i;
				double longitude = j;
				if (latitude >= 0) {
					coordinate = this.getPoint(vertical, latitude, longitude);
					if (coordinate != null)
						coordinateList.add(coordinate);
				}
			}
		}
		return coordinateList;
	}
	
	@Override
	public String toString() {
		return "AzimuthalNorth: {"+this.space.toString()+", scale:"+this.scale+"}";
	}
}
//@Override 
//public Point getPoint(double latitude, double longitude) {
//	return this.getPoint(0,latitude, longitude);
//}
//@Override
//public void paint(Graphics graphics) {
//	super.paint(graphics);
////	double latitude = 0;
////	double longitude = 0;
////	double c = Math.acos(Math.sin(this.centerLatitude) * Math.sin(latitude)
////			+ (Math.cos(this.centerLatitude) * Math.cos(latitude) * Math.cos(longitude - this.centerLongitude)));
////	int radius = (int) (c * this.scale);
////	int diameter = 2 * radius;
////	graphics.drawOval(0 - radius, 0 - radius, diameter, diameter);
////	graphics.drawString("0°", 0, -(radius + 10));
//}
