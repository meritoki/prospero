package org.meritoki.prospero.library.model.node.cartography;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;

public class Equirectangular extends Projection {

	public Equirectangular() {
		super(1);
		this.zFlag = false;
	}

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
		double x = this.unit * 4000 * coordinate.point.x ;
		double y = this.unit * 4000 * coordinate.point.y;
		double z = vertical;
		Point point3D = new Point(x, y, z);
		return this.getCoordinate(point3D);
	}
	
	@Override
	public String toString() {
		return "Equirectangular: {"+this.space.toString()+"}";
	}
}
//@Override 
//public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
//	return this.getCoordinate(vertical,latitude, longitude);
//}
