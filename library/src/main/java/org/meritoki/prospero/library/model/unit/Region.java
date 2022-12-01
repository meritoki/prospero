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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Region {
	static Logger logger = LogManager.getLogger(Region.class.getName());
	//20221109 Future Use Coordinate
	public double latitudeA;
	public double longitudeA;
	public double latitudeB;
	public double longitudeB;
	@JsonIgnore
	public List<Event> eventList;
	@JsonIgnore
	public HashMap<Time,List<Event>> eventMap;
	@JsonProperty
	private int hashCode;
	
	public Region() {
		this.hashCode = Objects.hash(this.latitudeA,this.longitudeB,this.latitudeB,this.longitudeB);
	}
	
	public Region(Object object) {
		if(object instanceof Region) {
			Region r = (Region)object;
			this.latitudeA = r.latitudeA;
			this.longitudeA = r.longitudeA;
			this.latitudeB = r.latitudeB;
			this.longitudeB = r.longitudeB;
			this.hashCode = Objects.hash(this.latitudeA,this.longitudeB,this.latitudeB,this.longitudeB);
		}
	}
	
	public Region(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
		this.latitudeA = latitudeA;
		this.longitudeA = longitudeA;
		this.latitudeB = latitudeB;
		this.longitudeB = longitudeB;
		this.hashCode = Objects.hash(this.latitudeA,this.longitudeB,this.latitudeB,this.longitudeB);
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Region that = (Region) o;
        return latitudeA == that.latitudeA && 
        		longitudeA == that.longitudeA && 
        		latitudeB == that.latitudeB && 
        		longitudeB == that.longitudeB;
    }
	
	public boolean contains(Event e) {
		for (Coordinate c : e.coordinateList) {
			if(this.contains(c)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(Coordinate coordinate) {
		boolean flag = false;
		flag = (latitudeA < coordinate.latitude && coordinate.latitude <latitudeB && longitudeA < coordinate.longitude && coordinate.longitude < longitudeB);
//		if(flag)logger.info(this+".contains("+coordinate+") flag="+flag);
		return flag;
	}
	
	public boolean contains(Tile tile) {
		boolean flag = false;
		flag = (latitudeA <= tile.coordinate.latitude && (tile.coordinate.latitude+tile.dimension) <= latitudeB && longitudeA <= tile.coordinate.longitude && (tile.coordinate.longitude+tile.dimension) <= longitudeB);
		return flag;
	}
	
	public String nameString() {
		return this.latitudeA+"_"+this.longitudeA+"_"+this.latitudeB+"_"+this.longitudeB;
	}
	
	@Override
	public String toString() {
		return this.latitudeA+","+this.longitudeA+":"+this.latitudeB+","+this.longitudeB;
	}
}
