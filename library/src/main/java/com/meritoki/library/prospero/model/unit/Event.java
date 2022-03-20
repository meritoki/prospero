package com.meritoki.library.prospero.model.unit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
	@JsonProperty
	public String id;
	@JsonProperty
	public List<Coordinate> coordinateList = new ArrayList<Coordinate>();
	@JsonProperty
	public Map<String,Object> attribute = new TreeMap<>();
	@JsonProperty
	public Duration duration;
	@JsonProperty
	public boolean flag = false;
	public boolean print = false;
	
	public Event() {
		
	}
	
	public Event(String id, List<Coordinate> pointList) {
		this.id = id;
		this.coordinateList = pointList;
	}
	
	public void reset() {
		this.flag = false;
		for(Coordinate c: this.coordinateList) {
			c.reset();
		}
	}

	public boolean containsCalendar(Calendar calendar) {
		Date date = calendar.getTime();
		Date startDate = (this.getStartCoordinate() != null && this.getStartCoordinate().calendar != null)?this.getStartCoordinate().calendar.getTime():null;
		Date endDate = (this.getEndCoordinate() != null && this.getEndCoordinate().calendar != null)?this.getEndCoordinate().calendar.getTime():null;
		if(print)System.out.println("date="+date);
		if(print)System.out.println("startDate="+startDate);
		if(print)System.out.println("endDate="+endDate);
		boolean flag = (startDate != null && endDate != null)?startDate.before(date) && date.before(endDate) || startDate.equals(date) || endDate.equals(date):false;
		if(print)System.out.println("flag="+flag);
		return flag;
	}

	@JsonIgnore
	public Coordinate getStartCoordinate() {
		Coordinate coordinate = this.coordinateList.get(0);
		return coordinate;
	}

	@JsonIgnore
	public Coordinate getEndCoordinate() {
		Coordinate coordinate = this.coordinateList.get(this.coordinateList.size() - 1);
		return coordinate;
	}
	
	@JsonIgnore
	public Calendar getStartCalendar() {
		return this.getStartCoordinate().calendar;
	}
	
	@JsonIgnore
	public Calendar getEndCalendar() {
		return this.getEndCoordinate().calendar;
	}

	@JsonIgnore
	public Duration getDuration() {
		return new Duration(this.getStartCoordinate().calendar.getTime(), this.getEndCoordinate().calendar.getTime());
	}

	@JsonIgnore
	public Duration getDuration(Coordinate a, Coordinate b) {
		return new Duration(a.calendar.getTime(), b.calendar.getTime());
	}

	@JsonIgnore
	public Map<String, List<Coordinate>> getTimeCoordinateMap() {
		Map<String, List<Coordinate>> timeCoordinateMap = new HashMap<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date;
		List<Coordinate> coordinateList;
		for (Coordinate coordinate : this.coordinateList) {
			if (coordinate.calendar != null) {
				date = dateFormat.format(coordinate.calendar.getTime());
				coordinateList = timeCoordinateMap.get(date);
				if (coordinateList == null) {
					coordinateList = new ArrayList<>();
					coordinateList.add(coordinate);
				} else {
					coordinateList.add(coordinate);
				}
//				Collections.sort(coordinateList);
				timeCoordinateMap.put(date, coordinateList);
			}
		}
		timeCoordinateMap = new TreeMap<String, List<Coordinate>>(timeCoordinateMap);
		return timeCoordinateMap;
	}
	
	@JsonIgnore
	public boolean hasCoordinate() {
		boolean flag = false;
		for (Coordinate p : this.coordinateList) {
			if (p.flag) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * Haversine formula
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	@JsonIgnore
	public double getDistance(Coordinate x, Coordinate y) {// (lat1,lon1,lat2,lon2)
		int R = 6371; // Radius of the earth in km
		double dLat = (double) (Math.toRadians(y.latitude - x.latitude)); // deg2rad below
		double dLon = (double) (Math.toRadians(y.longitude - x.longitude));
		double a = (double) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(x.latitude))
				* Math.cos(Math.toRadians(y.latitude)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
		double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
		double d = R * c; // Distance in km
		double m = d * 1000; // Distance in m
		return m;
	}
}
