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

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Result {
	protected Logger logger = Logger.getLogger(Result.class.getName());
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
	
	public List<Event> getEventList() {
		Object object = this.map.get("eventList");
		List<Event> eventList = new ArrayList<>();
		if(object != null) {
			eventList = (List<Event>)object; 
			if (eventList.size() == 0) {
				logger.warn("load(...) eventList.size() == 0");
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
				logger.warn("load(...) frameList.size() == 0");
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
				logger.warn("load(...) netCDFList.size() == 0");
			}
		}
		return netCDFList;
	}
	
	public List<Interval> getIntervalList() {
		Object object = this.map.get("intervalList");
		List<Interval> eventList = new ArrayList<>();
		if(object != null) {
			eventList = (List<Interval>)object; 
			if (eventList.size() == 0) {
				logger.warn("load(...) intervalList.size() == 0");
			}
		}
		return eventList;
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
