package org.meritoki.prospero.library.model.terra.cartography;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;

public class Globe extends Projection {

	public final int DEFAULT_SCALE = 256;
	public double radius = 1;

	public Globe() {
		super();
		this.scale = DEFAULT_SCALE;
	}
	
	@Override
	public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
		latitude = latitude * this.radians;
		longitude = longitude * this.radians; 
		double x = this.radius * Math.cos(latitude) * Math.cos(longitude);
		double z = this.radius * Math.cos(latitude) * Math.sin(longitude);
		double y = -this.radius * Math.sin(latitude);
		Point point = new Point(x, y, z);
		Coordinate coordinate = getCoordinate(point);
		
		if(coordinate != null) {
		if(coordinate.point.x > this.xMax) {
			this.xMax = coordinate.point.x;
		}
		
		if(coordinate.point.y > this.yMax) {
			this.yMax = coordinate.point.y;
		}
		}
//		if(coordinate != null) {
//			coordinate.latitude = latitude;
//			coordinate.longitude = longitude;
//		}
		return coordinate;
	}

	@Override
	public Coordinate getCoordinate(Point point) {
		Coordinate coordinate = null;
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
		float near = 3;
		float nearToObj = 1.5f;
		double x0 = point.x;
		double y0 = point.y;
		double z0 = point.z;
		float x1 = (float) (cosT * x0 + sinT * z0);
		float y1 = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);
		float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);
		if (z1 < 0) {
			x1 = x1 * near / (z1 + near + nearToObj);
			y1 = y1 * near / (z1 + near + nearToObj);
			coordinate = new Coordinate();
			coordinate.point.x = x1;
			coordinate.point.y = y1;
		}
		return coordinate;
	}
}