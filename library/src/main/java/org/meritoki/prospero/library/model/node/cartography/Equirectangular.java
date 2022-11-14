package org.meritoki.prospero.library.model.node.cartography;

import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Point;

public class Equirectangular extends Projection {

	public Equirectangular() {
		super(1);
	}

	@Override
	public Point getPoint(double vertical,double latitude, double longitude) {
		latitude *= -1;
		Point point = new Point();
		point.y = Math.toRadians(latitude);
		point.x = Math.toRadians(longitude);
		if (point != null) {
			if(point.x > this.xMax) {
				this.xMax = point.x;
			}
			if(point.y > this.yMax) {
				this.yMax = point.y;
			}
		}
		double x = this.unit * 4000 * point.x ;
		double y = this.unit * 4000 * point.y;
		double z = vertical * this.unit * 4000;
		Point point3D = new Point(x, y, z);
		return this.getPoint(point3D);
	}
	
	@Override
	public String toString() {
		return "Equirectangular: {"+this.space.toString()+"}";
	}
}
//@Override 
//public Point getPoint(double vertical, double latitude, double longitude) {
//	return this.getPoint(vertical,latitude, longitude);
//}
