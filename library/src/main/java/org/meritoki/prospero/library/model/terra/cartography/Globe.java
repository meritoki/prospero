package org.meritoki.prospero.library.model.terra.cartography;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Unit;


/**
 * 
 * <ol type="A">
 * <li><a href="https://www.geeksforgeeks.org/java-toradians-method-example/">https://www.geeksforgeeks.org/java-toradians-method-example/</a></li>
 * </ol>
 */
public class Globe extends Projection {

	public final int DEFAULT_SCALE = 1;

	public Globe() {
		super();
		this.setRadius(this.radius);
		this.setScale(DEFAULT_SCALE);
	}
    
    public Globe(double radius) {
		super();
		this.setRadius(radius);
		this.setScale(DEFAULT_SCALE);
	}
	
	public Globe(double a, double b, double c) {
		super();
		this.setRadius(a,b,c);
		this.setScale(DEFAULT_SCALE);
	}
	


	/**
	 * Parameterization
	 */
	@Override
	public Coordinate getCoordinate(double vertical, double latitude, double longitude) {
		latitude = Math.toRadians(latitude);// * this.radians;//A
		longitude = Math.toRadians(longitude);// * this.radians;//A
		//Kilometers
		double x = this.getA(this.unit) * Math.cos(latitude) * Math.cos(longitude);
		double z = this.getC(this.unit)  * Math.cos(latitude) * Math.sin(longitude);
		double y = -this.getB(this.unit) * Math.sin(latitude);
		Point point = new Point(x, y, z);
		Coordinate coordinate = this.getCoordinate(point);
		if (coordinate != null) {
			if (coordinate.point.x > this.xMax) {
				this.xMax = coordinate.point.x;
			}

			if (coordinate.point.y > this.yMax) {
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
		Point spacePoint = this.space.getPoint();
//		spacePoint.multiply(this.scale);
		point.subtract(spacePoint);
		
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
		float near = (float)(this.near*this.scale);
		float nearToObj = (float)(this.nearToObject*this.scale);
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