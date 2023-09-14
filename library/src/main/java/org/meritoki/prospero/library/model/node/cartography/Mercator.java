package org.meritoki.prospero.library.model.node.cartography;

import org.meritoki.prospero.library.model.unit.Point;

public class Mercator extends Projection {
	
	public Mercator() {
		super(1);
	}

	@Override
	public Point getPoint(double vertical, double latitude, double longitude) {
		latitude *= -1;
		Point point = new Point();
		point.y = Math.log(Math.tan((Math.PI / 4) + (Math.toRadians(latitude)/2)));
	    point.x = Math.toRadians(longitude) ;
	    double x = this.unit * 4000 * point.x;
		double y = this.unit * 4000 * point.y;
		double z = vertical * this.unit * 4000;
		if(point.x > this.xMax) {
			this.xMax = point.x;
		}
		if(point.y > this.yMax) {
			this.yMax = point.y;
		}
		return this.getPoint(new Point(x, y, z));
	}
	
	@Override
	public String toString() {
		return "Mercator: {"+this.space.toString()+"}";
	}
}
//public final double DEFAULT_SCALE = 200;
