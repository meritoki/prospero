package org.meritoki.prospero.library.model.node.cartography;

import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;

public class AzimuthalSouth extends Projection {

	public final int DEFAULT_SCALE = 256;
	private double centerLatitude = Math.toRadians(-90);// * this.radians;
	private double centerLongitude = Math.toRadians(0);// * this.radians;

	public AzimuthalSouth() {
		super();
		this.scale = DEFAULT_SCALE;
	}

	@Override
	public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
		Coordinate coordinate = null;
		latitude = Math.toRadians(latitude);// * this.radians;// MultiPolygon y
		longitude = Math.toRadians(longitude);// * this.radians; // MultiPolygon x
		if (latitude <= 0) {
			coordinate = new Coordinate();
			double c = Math.acos(Math.sin(this.centerLatitude) * Math.sin(latitude) + (Math.cos(this.centerLatitude)
					* Math.cos(latitude) * Math.cos(longitude - this.centerLongitude)));
			double k = c / Math.sin(c);
			double x = k * (Math.cos(latitude) * Math.sin(longitude - this.centerLongitude));
			double y = k * (Math.cos(this.centerLatitude) * Math.sin(latitude)
					- Math.sin(this.centerLatitude) * Math.cos(latitude) * Math.cos(longitude - this.centerLongitude));
			coordinate.point.x = x;
			coordinate.point.y = y;
		}
		if (coordinate != null) {
			if(coordinate.point.x > this.xMax) {
				this.xMax = coordinate.point.x;
			}
			if(coordinate.point.y > this.yMax) {
				this.yMax = coordinate.point.y;
			}
			double x = coordinate.point.x;
			double y = -coordinate.point.y;
			double z = vertical;
			Point point = new Point(x, y, z);
			return this.getCoordinate(point);
		} else {
			return null;
		}
	}

	@Override
	public List<Coordinate> getGridCoordinateList(double vertical, int latitudeInterval, int longitudeInterval) {
		List<Coordinate> coordinateList = new ArrayList<>();
		Coordinate coordinate;
		for (int i = -90; i < 90; i++) {
			for (int j = -180; j < 180; j += longitudeInterval) {
				double latitude = i;
				double longitude = j;
				if (latitude <= 0) {
					coordinate = this.getCoordinate(vertical, latitude, longitude);
					if (coordinate != null)
						coordinateList.add(coordinate);
				}
			}
		}
		for (int i = -90; i < 90; i += latitudeInterval) {
			for (int j = -180; j < 180; j++) {
				double latitude = i;
				double longitude = j;
				if (latitude <= 0) {
					coordinate = this.getCoordinate(vertical, latitude, longitude);
					if (coordinate != null)
						coordinateList.add(coordinate);
				}
			}
		}
		return coordinateList;
	}
}
//@Override 
//public Coordinate getCoordinate(double latitude, double longitude) {
//	return this.getCoordinate(0,latitude, longitude);
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
