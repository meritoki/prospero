package org.meritoki.prospero.library.model.unit;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Citation
 * <ol type="A">
 * <li><a href=
 * "https://en.wikipedia.org/wiki/Elliptic_coordinate_system">https://en.wikipedia.org/wiki/Elliptic_coordinate_system</a></li>
 *
 */
public class Space {
	static Logger logger = LogManager.getLogger(Space.class.getName());
//	@JsonProperty
//	public Vector3D elliptic = new Vector3D(0,0,0);
//	@JsonProperty
//	public Vector3D spherical = new Vector3D(0,0,0);
	@JsonProperty
	public Vector3D rectangular = new Vector3D(0,0,0);
	
	public Space() {
		
	}
	
	public Space(Space space) {
		this.rectangular = new Vector3D(space.rectangular.getX(),space.rectangular.getY(),space.rectangular.getZ());
	}

	public String toString() {
		String string = "";
		string += "rectangular: "+rectangular;//eliptic: "+elliptic+ " spherical: "+spherical+" 
		return string;
	}
	
	public void add(Space space) {
		this.rectangular = this.rectangular.add(space.rectangular);
		logger.info("add("+space+") this.rectangular="+this.rectangular);
	}
	
	public void subtract(Space space) {
		logger.info("subtract("+space+") A this.rectangular="+this.rectangular);
		this.rectangular = this.rectangular.subtract(space.rectangular);
		logger.info("subtract("+space+") B this.rectangular="+this.rectangular);
	}
	
	public Point getPoint() {
		return new Point(this.rectangular.getX(),this.rectangular.getY(),this.rectangular.getZ());
	}
	
//	public Point getPoint(double unit) {
//		return new Point(rectangular.getX()*unit,rectangular.getY()*unit,rectangular.getZ()*unit);
//	}
}
//public String time;
//public double match;
//public List<Triangle> triangleList;
