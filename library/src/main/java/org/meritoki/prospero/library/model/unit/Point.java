package org.meritoki.prospero.library.model.unit;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Point {
	public double x, y, z;
	
	public Point() {}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(double X, double Y, double Z) {
		x = X;
		y = Y;
		z = Z;
	}
	
	public Point scale(double scale) {
		return new Point(this.x*scale,this.y*scale,this.z*scale);
	}
	
	public void add(Point point) {
		this.add(point.x,point.y,point.z);
	}
	
	public void add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public void subtract(Point point) {
		this.subtract(point.x,point.y,point.z);
	}
	
	public void subtract(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
	}
	
	public void multiply(double value) {
		this.x *= value;
		this.y *= value;
		this.z *= value;
	}
	
	@Override
	public boolean equals(Object object) {
		Point point = (object instanceof Point)? (Point)object:null;
		return (point != null)?this.x==point.x && this.y == point.y && this.z == point.z:false;
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}