package org.meritoki.prospero.library.model.node.cartography;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;

public class Mercator extends Projection {
	
	public Mercator() {
		super(1);
		this.zFlag = false;
	}

	@Override
	public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
		latitude *= -1;
		Coordinate coordinate = new Coordinate();
		coordinate.point.y = Math.log(Math.tan((Math.PI / 4) + (Math.toRadians(latitude)/2)));
	    coordinate.point.x = Math.toRadians(longitude) ;
	    double x = this.unit * 4000 * coordinate.point.x;
		double y = this.unit * 4000 * coordinate.point.y;
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
	
	@Override
	public String toString() {
		return "Mercator: {"+this.space.toString()+"}";
	}
}
//public final double DEFAULT_SCALE = 200;
