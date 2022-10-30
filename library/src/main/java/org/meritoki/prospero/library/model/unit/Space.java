package org.meritoki.prospero.library.model.unit;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Space {
	static Logger logger = LogManager.getLogger(Space.class.getName());
	@JsonProperty
	public Vector3D eliptic = new Vector3D(0,0,0);
	@JsonProperty
	public Vector3D spherical = new Vector3D(0,0,0);
	@JsonProperty
	public Vector3D rectangular = new Vector3D(0,0,0);

	public String toString() {
		String string = "";
		string += "eliptic: "+eliptic+ " spherical: "+spherical+" rectangular: "+rectangular;
		return string;
	}
	
	public void subtract(Space space) {
		this.rectangular = this.rectangular.subtract(space.rectangular);
		logger.info("subtract("+space+") this.rectangular="+this.rectangular);
	}
	
	public Point getPoint() {
		return new Point(rectangular.getX(),rectangular.getY(),rectangular.getZ());
	}
	
//	public Point getPoint(double unit) {
//		return new Point(rectangular.getX()*unit,rectangular.getY()*unit,rectangular.getZ()*unit);
//	}
}
//public String time;
//public double match;
//public List<Triangle> triangleList;
