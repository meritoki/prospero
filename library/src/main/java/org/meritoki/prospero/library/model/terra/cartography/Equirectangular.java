package org.meritoki.prospero.library.model.terra.cartography;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;

public class Equirectangular extends Projection {

	public final double DEFAULT_SCALE = 200;

	public Equirectangular() {
		super();
		this.scale = DEFAULT_SCALE;
	}
	
//	@Override 
//	public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
//		return this.getCoordinate(vertical,latitude, longitude);
//	}

	@Override
	public Coordinate getCoordinate(double vertical,double latitude, double longitude) {
		latitude *= -1;
		Coordinate coordinate = new Coordinate();
		coordinate.point.y = Math.toRadians(latitude);
		coordinate.point.x = Math.toRadians(longitude);
		if (coordinate != null) {
			if(coordinate.point.x > this.xMax) {
				this.xMax = coordinate.point.x;
			}
			if(coordinate.point.y > this.yMax) {
				this.yMax = coordinate.point.y;
			}
		}
		double x = coordinate.point.x;
		double y = coordinate.point.y;
		double z = vertical;
		Point point3D = new Point(x, y, z);
		return this.getCoordinate(point3D);
	}
}
