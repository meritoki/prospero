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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = CycloneEvent.class) })
public class Event {

	@JsonIgnore
	static Logger logger = LoggerFactory.getLogger(Event.class.getName());
	@JsonProperty
	public String id;
	@JsonProperty
	public List<Coordinate> coordinateList = new ArrayList<Coordinate>();
	@JsonProperty
	public Map<String, Object> attribute = new TreeMap<>();
	@JsonProperty
	public Duration duration;
	@JsonIgnore
	public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@JsonIgnore
	public boolean flag = false;

	public Event() {
	}

	public Event(Event event) {
		this.id = event.id;
		this.coordinateList = new ArrayList<>(event.coordinateList);
		this.attribute = new TreeMap<>(event.attribute);
		this.duration = event.duration;
		this.flag = event.flag;
	}

	public Event(List<Coordinate> coordinateList) {
		this.coordinateList = coordinateList;
		Collections.sort(this.coordinateList, Comparator.comparing(Coordinate::getCalendar).thenComparing(Coordinate::getPressure));
	}

	public Event(String id, List<Coordinate> coordinateList) {
		this.id = id;
		this.coordinateList = coordinateList;
		Collections.sort(this.coordinateList, Comparator.comparing(Coordinate::getCalendar).thenComparing(Coordinate::getPressure));
	}

	public void reset() {
		this.flag = false;
		for (Coordinate c : this.coordinateList) {
			c.reset();
		}
	}

	public boolean containsCalendar(Calendar calendar) {
		Date date = calendar.getTime();
		Date startDate = (this.getStartCoordinate() != null && this.getStartCoordinate().calendar != null)
				? this.getStartCoordinate().calendar.getTime()
				: null;
		Date endDate = (this.getEndCoordinate() != null && this.getEndCoordinate().calendar != null)
				? this.getEndCoordinate().calendar.getTime()
				: null;
		logger.debug("date=" + date);
		logger.debug("startDate=" + startDate);
		logger.debug("endDate=" + endDate);
		boolean flag = (startDate != null && endDate != null)
				? startDate.before(date) && date.before(endDate) || startDate.equals(date) || endDate.equals(date)
				: false;
		logger.debug("flag=" + flag);
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
		Duration duration = new Duration(a.calendar.getTime(), b.calendar.getTime());
		logger.debug("getDuration(...) duration="+duration);
		return duration;
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
		int earthRadius = 6371; // Radius of the earth in km
		double latitude = (double) (Math.toRadians(y.latitude - x.latitude)); // deg2rad below
		double longitude = (double) (Math.toRadians(y.longitude - x.longitude));
		double a = (double) (Math.sin(latitude / 2) * Math.sin(latitude / 2) + Math.cos(Math.toRadians(x.latitude))
				* Math.cos(Math.toRadians(y.latitude)) * Math.sin(longitude / 2) * Math.sin(longitude / 2));
		double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
		double kilometers = earthRadius * c; // Distance in km
		double meters = kilometers * 1000; // Distance in m
		// logger.info("getDistance("+x+","+y+") m="+meters);
		return meters;
	}

	public static List<Event> getSelectedEventList(List<Event> eventList) {
		List<Event> eList = new ArrayList<>();
		for (Event e : eventList) {
			if (e.flag) {
				eList.add(e);
			}
		}
		return eList;
	}

	public static List<Event> getSelectedEventList(List<Event> eventList, Calendar calendar) {
		List<Event> eList = new ArrayList<>();
		for (Event e : eventList) {
			if (e.flag && e.containsCalendar(calendar)) {
				eList.add(e);
			}
		}
		return eList;
	}

	/**
	 * Return Map containing Time as String Mapped to Coordinate List
	 * 
	 * @return
	 */
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
				Collections.sort(coordinateList);
				timeCoordinateMap.put(date, coordinateList);
			}
		}
		timeCoordinateMap = new TreeMap<String, List<Coordinate>>(timeCoordinateMap);
//		logger.info("getTimeCoordinateMap() timeCoordinateMap="+timeCoordinateMap);
		return timeCoordinateMap;
//		return this.getTimeCoordinateMap(this.coordinateList);
	}
	
	/**
	 * Please ensure that Coordinate List contains only one Coordinate per Pressure.
	 * Otherwise, the last Coordinate for a Given Pressure it added and may cause defects
	 * @param coordinateList
	 * @return
	 */
	public Map<Integer,Coordinate> getPressureCoordinateMap(List<Coordinate> coordinateList) {
		Map<Integer,Coordinate> pressureCoordinateMap = new HashMap<>();
		for(Coordinate c: coordinateList) {
			Integer pressure = c.getPressure();
			pressureCoordinateMap.put(pressure,c);
		}
		return pressureCoordinateMap;
	}

	/**
	 * Difference is Coordinate Flag Check
	 * 
	 * @param coordinateList
	 * @return
	 */
	@JsonIgnore
	public Map<String, List<Coordinate>> getTimeCoordinateMap(List<Coordinate> coordinateList) {
		Map<String, List<Coordinate>> timeCoordinateMap = new HashMap<>();
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date;
		List<Coordinate> cList;
		for (Coordinate c : coordinateList) {
			if (c.flag) {
				date = c.getDateTime();// dateFormat.format(c.calendar.getTime());
				cList = timeCoordinateMap.get(date);
				if (cList == null) {
					cList = new ArrayList<>();
					cList.add(c);
				} else {
					cList.add(c);
				}
				Collections.sort(cList);
				timeCoordinateMap.put(date, cList);
			}
		}
		timeCoordinateMap = new TreeMap<String, List<Coordinate>>(timeCoordinateMap);
		return timeCoordinateMap;
	}

	/**
	 * Function Returns Coordinate List with Averaged Latitude and Longitude per
	 * Time
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<Coordinate> getTimeCoordinateList() {
		Map<String, List<Coordinate>> timeCoordinateMap = this.getTimeCoordinateMap();
		List<Coordinate> coordinateList = new ArrayList<Coordinate>();
		Coordinate coordinate;
		Date date;
		Calendar calendar;
		for (Map.Entry<String, List<Coordinate>> entry : timeCoordinateMap.entrySet()) {
			try {
				date = this.dateFormat.parse(entry.getKey());
				calendar = Calendar.getInstance();
				calendar.setTime(date);
				coordinate = this.getAverageCoordinate(entry.getValue(), calendar);
				if (coordinate != null) {
					coordinateList.add(coordinate);
				}
			} catch (ParseException e) {
				logger.error("ParseException " + e.getMessage());
			}
		}
		return coordinateList;
	}

	public List<String> getTimeList() {
		List<String> timeList = new ArrayList<>();
		for (Coordinate c : this.coordinateList) {
			String dateTime = c.getDateTime();
			if (!timeList.contains(dateTime)) {
				timeList.add(dateTime);
			}
		}
		return timeList;
	}

	@JsonIgnore
	public boolean hasCoordinate() {
		boolean flag = false;
		for (Coordinate c : this.coordinateList) {
			if (c.flag) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 202304281758 Code Review Function Set Calendar Coordinate List Uses Calendar
	 * Parameter Set Coordinate Flags to True or False Given Contains Calendar
	 * Condition
	 * 
	 * @param calendar
	 */
	public void setCalendarCoordinateList(Calendar calendar) {
		for (Coordinate c : this.coordinateList) {
			if (c.containsCalendar(calendar)) {
				c.flag = true;
			} else {
				c.flag = false;
			}
		}
	}

	/**
	 * 202304281749 Get Coordinate List Code Review Return a Copied List of
	 * Coordinates Where Coordinate Flag is True
	 * 
	 * @return
	 */
	public List<Coordinate> getCoordinateList() {
		List<Coordinate> coordinateList = new ArrayList<>();
		for (Coordinate c : this.coordinateList) {
			if (c.flag) {
				coordinateList.add(new Coordinate(c));
			}
		}
		return coordinateList;
	}

//	/**
//	 * 202304281749 Get Average Coordinate List Code Review
//	 * 
//	 * @param calendar
//	 * @return
//	 */
//	public List<Coordinate> getAverageCoordinateList(Calendar calendar) {
//		return getAverageCoordinateList(this.getCoordinateList(),calendar);
//	}

	/**
	 * 202304281750 Get Average Coordinate List Code Review
	 * 
	 * @param cList
	 * @param calendar
	 * @return
	 */
//	public List<Coordinate> getAverageCoordinateList(List<Coordinate> cList, Calendar calendar) {
//		List<Coordinate> coordinateList = new ArrayList<>();
//		Coordinate coordinate = this.getAverageCoordinate(cList,calendar);
//		if(coordinate != null)
//			coordinateList.add(coordinate);
//		return coordinateList; 
//	}

	/**
	 * 202304281751 Get Average Coordinate Code Review
	 * 
	 * @param cList
	 * @param calendar
	 * @return
	 */
	@JsonIgnore
	public Coordinate getAverageCoordinate(List<Coordinate> coordinateList, Calendar calendar) {
		Coordinate coordinate = null;
// 		202304281805 Code Review Commented Out Due to Redundancy
//		List<Coordinate> coordinateList = new ArrayList<>();
//		for (Coordinate c : cList) {
//			coordinateList.add(new Coordinate(c));
//		}
		if (coordinateList.size() > 0) {
			double latitudeSum = 0;
			double longitudeSum = 0;
			double latitude;
			double longitude;
//			202304281817 Suspicious Code: Entire For Loop is Used to Correct Longitudes, Suspicious Code
			for (int i = 0; i < coordinateList.size(); i++) {
				if (i + 1 < coordinateList.size()) {
					Coordinate a = coordinateList.get(i);
					Coordinate b = coordinateList.get(i + 1);
//					Correct Longitude Before Calculating Difference
					double difference = Math.abs(a.longitude - b.longitude);
					if (difference >= 180) {
						if (a.longitude < 0) {
							a.longitude += 360;
						}
						if (b.longitude < 0) {
							b.longitude += 360;
						}
					}
				}
			}
//			202304281817 Suspicious Code: Here the List Corrected is Used to Compute a Sum for Latitude and Longitude
			for (Coordinate c : coordinateList) {
				latitudeSum += c.latitude;
				longitudeSum += c.longitude;
			}
			latitude = latitudeSum / coordinateList.size();
			longitude = longitudeSum / coordinateList.size();
//			202304281817 Suspicious Code: Again, Longitude is Corrected
			if (longitude > 180) {
				longitude -= 360;
			}
			coordinate = new Coordinate();
			coordinate.latitude = latitude;
			coordinate.longitude = longitude;
			coordinate.calendar = calendar;
			coordinate.flag = true;
		}
		return coordinate;
	}
}
