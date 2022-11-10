/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.library.model.unit;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Point {
	@JsonProperty
	public double x, y, z;
	@JsonInclude(Include.NON_EMPTY)
	public Map<String, Object> attribute = new TreeMap<>();
	@JsonIgnore
	public boolean flag;
	
	public Point() {}
	
	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
	}
	
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