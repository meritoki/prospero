package com.meritoki.library.prospero.model.unit;

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
