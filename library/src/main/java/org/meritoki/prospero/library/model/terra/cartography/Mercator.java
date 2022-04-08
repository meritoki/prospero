package org.meritoki.prospero.library.model.terra.cartography;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;

public class Mercator extends Projection {
	
	public final double DEFAULT_SCALE = 200;
	
	public Mercator() {
		super();
		this.scale = DEFAULT_SCALE;
	}

	@Override
	public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
		latitude *= -1;
		Coordinate coordinate = new Coordinate();
		coordinate.point.y = Math.log(Math.tan((Math.PI / 4) + (Math.toRadians(latitude)/2)));
	    coordinate.point.x = Math.toRadians(longitude) ;
	    double x = coordinate.point.x;
		double y = coordinate.point.y;
		double z = 0;
		if(coordinate.point.x > this.xMax) {
			this.xMax = coordinate.point.x;
		}
		if(coordinate.point.y > this.yMax) {
			this.yMax = coordinate.point.y;
		}
		Point point3D = new Point(x, y, z);
		return this.getCoordinate(point3D);
	}
}
