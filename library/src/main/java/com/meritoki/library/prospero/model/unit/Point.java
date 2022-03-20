package com.meritoki.library.prospero.model.unit;

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