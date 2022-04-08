package org.meritoki.prospero.library.model.camera;

import org.meritoki.prospero.library.model.unit.Point;

public class Camera {
	
	public double azimuth = 0;
	public double elevation = 0;
	public Point center = new Point(0,0,0);
	public float near = 32;
	public float nearToObj = 8f;
	
	public double getAzimuth() {
		return azimuth;
	}


	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}


	public double getElevation() {
		return elevation;
	}


	public void setElevation(double elevation) {
		this.elevation = elevation;
	}


	public Point getCenter() {
		return center;
	}


	public void setCenter(Point center) {
		this.center = center;
	}




	
	public Point getPoint(Point point) {
		Point p = null;
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
		// The following two lines fixed defects when viewing in 3 Dimensions
//		float near = 32; // distance from eye to near plane
//		float nearToObj = 8f; // 1.5// distance from near plane to center of object
		double x0 = point.x;
		double y0 = point.y;
		double z0 = point.z;
		// compute an orthographic projection
		float x1 = (float) (cosT * x0 + sinT * z0);
		float y1 = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);
		// now adjust things to get a perspective projection
		float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);
		x1 = x1 * near / (z1 + near + nearToObj);
		y1 = y1 * near / (z1 + near + nearToObj);
		p = new Point();
		p.x = x1;
		p.y = y1;
		p.z = z1;
		return p;
	}
}
