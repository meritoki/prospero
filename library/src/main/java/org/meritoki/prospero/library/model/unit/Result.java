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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.MultiLineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Result {
	static Logger logger = LoggerFactory.getLogger(Result.class.getName());
	public Mode mode;
	public String message;
	public Map<String,Object> map = new HashMap<>();
	
	public Result() {
		this.mode = Mode.LOAD;
	}
	
	public Result(Mode mode) {
		this.mode = mode;
	}
	
	public Result(Mode mode, String message) {
		this.mode = mode;
		this.message = message;
	}
	
	public Time getTime() {
		Object object = this.map.get("time");
		Time time = null;
		if(object instanceof Time) {
			time = (Time)object;
		}
		return time;
	}
	
	public List<Index> getIndexList() {
		Object object = this.map.get("indexList");
		List<Index> indexList = new ArrayList<>();
		if(object != null) {
			indexList = (List<Index>)object; 
			if (indexList.size() == 0) {
				logger.warn("getIndexList() indexList.size() == 0");
			}
		}
		return indexList;
	}
	
	public List<MultiLineString> getMultiLineStringList() {
		Object object = this.map.get("multiLineStringList");
		List<MultiLineString> multiLineStringList = new ArrayList<>();
		if(object != null) {
			multiLineStringList = (List<MultiLineString>)object;
			if (multiLineStringList.size() == 0) {
				logger.warn("getMultiLineStringList() multiLineStringList.size() == 0");
			}
		}
		return multiLineStringList;
	}
	
	public List<Station> getStationList() {
		Object object = this.map.get("stationList");
		List<Station> stationList = new ArrayList<>();
		if(object != null) {
			stationList = (List<Station>)object; 
			if (stationList.size() == 0) {
				logger.warn("getStationList() stationList.size() == 0");
			}
		}
		return stationList;
	}
	
	public List<Event> getEventList() {
		Object object = this.map.get("eventList");
		List<Event> eventList = new ArrayList<>();
		if(object != null) {
			eventList = (List<Event>)object; 
			if (eventList.size() == 0) {
				logger.warn("getEventList() eventList.size() == 0");
			}
		}
		return eventList;
	}
	
	public List<Frame> getFrameList() {
		Object object = this.map.get("frameList");
		List<Frame> frameList = new ArrayList<>();
		if(object != null) {
			frameList = (List<Frame>)object; 
			if (frameList.size() == 0) {
				logger.warn("getFrameList() frameList.size() == 0");
			}
		}
		return frameList;
	}
	
	public List<NetCDF> getNetCDFList() {
		Object object = this.map.get("netCDFList");
		List<NetCDF> netCDFList = new ArrayList<>();
		if(object != null) {
			netCDFList = (List<NetCDF>)object; 
			if (netCDFList.size() == 0) {
				logger.warn("getNetCDFList() netCDFList.size() == 0");
			}
		}
		return netCDFList;
	}
	
	public List<Interval> getIntervalList() {
		Object object = this.map.get("intervalList");
		List<Interval> intervalList = new ArrayList<>();
		if(object != null) {
			intervalList = (List<Interval>)object; 
			if (intervalList.size() == 0) {
				logger.warn("getIntervalList() intervalList.size() == 0");
			}
		}
		return intervalList;
	}
	
	public List<Region> getRegionList() {
		Object object = this.map.get("regionList");
		List<Region> regionList = new ArrayList<>();
		if(object != null) {
			regionList = (List<Region>)object; 
			if (regionList.size() == 0) {
				logger.warn("getRegionList() regionList.size() == 0");
			}
		}
		return regionList;
	}
	
	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}
