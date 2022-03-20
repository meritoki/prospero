package org.meritoki.prospero.library.model.solar.unit;

import java.io.IOException;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Space {

//	public String time;
//	public double match;
//	public List<Triangle> triangleList;
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
	
	public Point getPoint() {
		return new Point(rectangular.getX(),rectangular.getY(),rectangular.getZ());
	}
	

}
