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
package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.TableModel;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Duration;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Link;
import org.meritoki.prospero.library.model.unit.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@JsonTypeInfo(use = Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = ERA5Event.class), @Type(value = ERAInterimEvent.class) })
public class CycloneEvent extends Event {

	@JsonIgnore
	static Logger logger = LoggerFactory.getLogger(CycloneEvent.class.getName());
	@JsonProperty
	public List<Integer> pressureList;
	@JsonProperty
	public Family family = Family.NULL;
	@JsonProperty
	public Classification classification = Classification.NULL;

	public CycloneEvent() {

	}

	public CycloneEvent(List<Coordinate> coordinateList) {
		super(coordinateList);
		this.classify();
	}

	public CycloneEvent(String id, List<Coordinate> coordinateList) {
		super(id, coordinateList);
		this.classify();
	}

	public CycloneEvent(CycloneEvent event) {
		super(event.id, new ArrayList<>(event.coordinateList));
		this.classify();
	}

	public void classify() {

	}

	@JsonIgnore
	public boolean equals(Object object) {
		boolean flag = false;
		if (object != null && object instanceof CycloneEvent) {
			flag = this.id.equals(((CycloneEvent) object).id);
		}
		return flag;
	}

	@JsonIgnore
	public static Map<Family, List<Classification>> getFamilyClassMap(List<Family> familyArray,
			List<Classification> classArray) {
		Map<Family, List<Classification>> map = new HashMap<>();
		for (Family f : familyArray) {
			List<Classification> cList = new ArrayList<>();
			cList.add(null);
			map.put(f, cList);
		}
		for (Classification c : classArray) {
			switch (c) {
			case LOW: {
				List<Classification> cList = map.get(Family.SHALLOW);
				if (cList == null) {
					cList = new ArrayList<>();
				}
				cList.add(Classification.LOW);
				map.put(Family.SHALLOW, cList);
				break;
			}
			case MID: {
				List<Classification> cList = map.get(Family.SHALLOW);
				if (cList == null) {
					cList = new ArrayList<>();
				}
				cList.add(Classification.MID);
				map.put(Family.SHALLOW, cList);
				break;
			}
			case UPPER: {
				List<Classification> cList = map.get(Family.SHALLOW);
				if (cList == null) {
					cList = new ArrayList<>();
				}
				cList.add(Classification.UPPER);
				map.put(Family.SHALLOW, cList);
				break;
			}
			case LOW_MID: {
				List<Classification> cList = map.get(Family.INTERMEDIATE);
				if (cList == null) {
					cList = new ArrayList<>();
				}
				cList.add(Classification.LOW_MID);
				map.put(Family.INTERMEDIATE, cList);
				break;
			}
			case MID_UPPER: {
				List<Classification> cList = map.get(Family.INTERMEDIATE);
				if (cList == null) {
					cList = new ArrayList<>();
				}
				cList.add(Classification.MID_UPPER);
				map.put(Family.INTERMEDIATE, cList);
				break;
			}
			default:
				break;
			}
		}
		logger.info("getFamilyClassMap(...) map=" + map);
		return map;
	}
	
	public static TableModel getTimeTableModel(List<Event> eventList, Calendar calendar) {
		Object[] objectArray = getTimeObjectArray(eventList, calendar);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}
	
	public static Object[] getTimeObjectArray(List<Event> eventList, Calendar calendar) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (eventList != null) {
			eventList = getSelectedEventList(eventList, calendar);
			if (eventList.size() > 0) {
				for (int i = 0; i < eventList.size(); i++) {
					Event e = eventList.get(i);
					if (e instanceof CycloneEvent) {
						CycloneEvent event = (CycloneEvent) e;
						Integer[] pressureArray = new Integer[0];
						if(event instanceof ERA5Event) {
							pressureArray = ERA5Event.pressureArray;

						} else if(event instanceof ERAInterimEvent) {
							pressureArray = ERAInterimEvent.pressureArray;
						}
						if (i == 0) {
							columnArray = Table.getColumnNames(5+pressureArray.length).toArray();
							dataMatrix = new Object[eventList.size() + 1][5+pressureArray.length];
							dataMatrix[i][0] = "color";
							dataMatrix[i][1] = "id";
							dataMatrix[i][2] = "calendar";
							dataMatrix[i][3] = "latitude";
							dataMatrix[i][4] = "longitude";
							for(int p =0;p<pressureArray.length;p++) {
								dataMatrix[i][5+p] = pressureArray[p];
							}

						}
						Object color = event.attribute.get("color");
						event.setCalendarCoordinateList(calendar);
						List<Coordinate> coordinateList = event.getCoordinateList();
						Coordinate c = event.getAverageCoordinate(event.getCoordinateList(), calendar);
						dataMatrix[i + 1][0] = (color != null && color instanceof Color)?"#"+Integer.toHexString(((Color)color).getRGB()).substring(2):"NA";
						dataMatrix[i + 1][1] = event.id;
						dataMatrix[i + 1][2] = dateFormat.format(calendar.getTime());
						dataMatrix[i + 1][3] = c.latitude;
						dataMatrix[i + 1][4] = c.longitude;
						int pressure;
						for(int p =0;p<pressureArray.length;p++) {
							pressure = pressureArray[p];
							for(Coordinate coordinate:coordinateList) {
								int cPressure = coordinate.getPressure();
								if(cPressure == pressure) {
									dataMatrix[i+1][5+p] = String.valueOf(coordinate.getVorticity());
								}
							}
						}

					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}

	public static TableModel getTableModel(List<Event> eventList,Calendar calendar) {
		Object[] objectArray = getObjectArray(eventList,calendar);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	public static Object[] getObjectArray(List<Event> eventList, Calendar calendar) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (eventList != null) {
			eventList = getSelectedEventList(eventList,calendar);
			if (eventList.size() > 0) {
				for (int i = 0; i < eventList.size(); i++) {
					Event e = eventList.get(i);
					if (e instanceof CycloneEvent) {
						CycloneEvent event = (CycloneEvent) e;
						if (i == 0) {
							columnArray = Table.getColumnNames(18).toArray();
							dataMatrix = new Object[eventList.size() + 1][18];
							dataMatrix[i][0] = "color";
							dataMatrix[i][1] = "id";
							dataMatrix[i][2] = "startCalendar";
							dataMatrix[i][3] = "endCalendar";
							dataMatrix[i][4] = "duration (days)";
							dataMatrix[i][5] = "family";
							dataMatrix[i][6] = "classification";
							dataMatrix[i][7] = "distance (meters)";
							dataMatrix[i][8] = "maxTimeLevelCount";
							dataMatrix[i][9] = "totalPressureCount";
							dataMatrix[i][10] = "lowermostLevel";
							dataMatrix[i][11] = "uppermostLevel";
							dataMatrix[i][12] = "genesisLowermostLevel";
							dataMatrix[i][13] = "genesisUppermostLevel";
							dataMatrix[i][14] = "lysisLowermostLevel";
							dataMatrix[i][15] = "lysisUppermostLevel";
							dataMatrix[i][16] = "speed (meters/second)";
							dataMatrix[i][17] = "meanVorticity";
						}
						Object color = event.attribute.get("color");
						dataMatrix[i + 1][0] = (color != null && color instanceof Color)?"#"+Integer.toHexString(((Color)color).getRGB()).substring(2):"NA";
						dataMatrix[i + 1][1] = event.id;
						dataMatrix[i + 1][2] = dateFormat.format(event.getStartCalendar().getTime());
						dataMatrix[i + 1][3] = dateFormat.format(event.getEndCalendar().getTime());
						dataMatrix[i + 1][4] = event.getDuration().days;
						dataMatrix[i + 1][5] = (event.family != null) ? event.family.toString() : "NULL";
						dataMatrix[i + 1][6] = (event.classification != null) ? event.classification.toString()
								: "NULL";
						dataMatrix[i + 1][7] = event.getDistance();
						dataMatrix[i + 1][8] = event.getMaxTimeLevelCount();
						dataMatrix[i + 1][9] = event.getPressureCount();
						dataMatrix[i + 1][10] = event.getLowerMostLevel();
						dataMatrix[i + 1][11] = event.getUpperMostLevel();
						dataMatrix[i + 1][12] = event.getGenesisLowermostLevel();
						dataMatrix[i + 1][13] = event.getGenesisUppermostLevel();
						dataMatrix[i + 1][14] = event.getLysisLowermostLevel();
						dataMatrix[i + 1][15] = event.getLysisUppermostLevel();
						dataMatrix[i + 1][16] = event.getMeanSpeed();
						dataMatrix[i + 1][17] = event.getMeanVorticity();
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}



	@JsonIgnore
	public boolean containsDate(int year, int month) {
		boolean flag = false;
		String date = year + "" + String.format("%02d", month);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		for (Coordinate p : this.coordinateList) {
			String pointDate = sdf.format(p.calendar);
			if (date.equals(pointDate)) {
				flag = true;
				break;
			}
		}
		if (flag)
			System.out.println("containsDate(" + year + ", " + month + ") flag=" + flag);
		return flag;
	}

	@JsonIgnore
	public boolean isSimilar(CycloneEvent event, double threshold) {
		boolean flag = false;
		if (!event.id.equals(this.id)) {
			double total = this.coordinateList.size();
			double count = 0;
			for (Coordinate point : this.coordinateList) {
				for (Coordinate eventPoint : event.coordinateList) {
					if (point.calendar.equals(eventPoint.calendar) && point.latitude == eventPoint.latitude
							&& point.longitude == eventPoint.longitude) {
						count++;
					}
				}
			}
			double quotient = (count / total);
			if (quotient >= threshold) {
				flag = true;
			}
		}
		return flag;
	}

	@JsonIgnore
	@Override
	public Coordinate getStartCoordinate() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		Coordinate coordinate = null;
		List<String> keys = new ArrayList<>(timePointMap.keySet());// public Date date;
		if (keys.size() > 0) {
			String key = keys.get(0);
			List<Coordinate> pointList = timePointMap.get(key);
			int size = pointList.size();
			coordinate = pointList.get(size - 1);
		}
		// System.out.println("getStartCoordinate() coordiante="+coordinate);
		return coordinate;
	}

	@JsonIgnore
	public List<Integer> getPressureList() {
		List<Integer> pressureList = new ArrayList<>();
		for (Coordinate c : this.coordinateList) {
			int pressure = (int) c.attribute.get("pressure");
			if (!pressureList.contains(pressure)) {
				pressureList.add(pressure);
			}
		}
		Collections.sort(pressureList);// ascending order
		return pressureList;
	}

	@JsonIgnore
	public int getPressureCount() {
		return this.getPressureList().size();
	}

	@JsonIgnore
	@Override
	public Coordinate getEndCoordinate() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		Coordinate coordinate = null;// this.coordinateList.get(this.coordinateList.size() - 1);
		int size = timePointMap.size();
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		if (keys.size() > 0) {
			String key = keys.get(size - 1);
			List<Coordinate> coordinateList = timePointMap.get(key);
			size = coordinateList.size();
			coordinate = coordinateList.get(size - 1);
		}
		return coordinate;
	}

	@JsonIgnore
	public List<Integer> getSelectedPressureList() {
		List<Integer> pressureList = new ArrayList<>();
		for (Coordinate c : this.coordinateList) {
			if (c.flag) {
				pressureList.add((Integer) c.attribute.get("pressure"));
			}
		}
		return pressureList;
	}

	@JsonIgnore
	public Map<Integer, List<Coordinate>> getPressureCoordinateListMap(List<Coordinate> coordinateList) {
		Map<Integer, List<Coordinate>> pressureCoordinateMap = new HashMap<>();
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Integer pressure;
		List<Coordinate> cList;
		for (Coordinate c : coordinateList) {
//			if (c.flag) { 20230624 Defect Fix Trajectory Persistence
				pressure = (Integer) c.attribute.get("pressure");// dateFormat.format(c.calendar.getTime());
				cList = pressureCoordinateMap.get(pressure);
				if (cList == null) {
					cList = new ArrayList<>();
					cList.add(c);
				} else {
					cList.add(c);
				}
//				Collections.sort(cList);
				pressureCoordinateMap.put(pressure, cList);
//			}
		}
		pressureCoordinateMap = new TreeMap<Integer, List<Coordinate>>(pressureCoordinateMap);
		return pressureCoordinateMap;
	}

	@JsonIgnore
	public Map<Integer, List<Coordinate>> getPressureCoordinateListMap() {
		Map<Integer, List<Coordinate>> pressureCoordinateMap = this.getPressureCoordinateListMap(this.coordinateList);
		Map<Integer, List<Coordinate>> pressureLinkMap = new TreeMap<>();
		List<String> timeList = this.getTimeList();
		for (Map.Entry<Integer, List<Coordinate>> entry : pressureCoordinateMap.entrySet()) {
			Integer pressure = entry.getKey();
			List<Coordinate> linkList = pressureLinkMap.get(pressure);
			if (linkList == null) {
				linkList = new ArrayList<>();
			}
			List<Coordinate> coordinateList = entry.getValue();
			for (int i = 0; i < coordinateList.size(); i++) {
				Coordinate a = coordinateList.get(i);
				linkList.add(a);
				if (i + 1 < coordinateList.size()) {
					Coordinate b = coordinateList.get(i + 1);
					int aIndex = timeList.indexOf(a.getDateTime());
					int bIndex = timeList.indexOf(b.getDateTime());
					if ((aIndex + 1) == bIndex) {
						Coordinate c = null;
						Coordinate d = null;
						if (a.longitude > 0 && b.longitude < 0) {
							double difference = a.longitude - b.longitude;
							if (difference >= 180) {
								b.longitude += 360;
								double slope = this.getSlope(a, b);
								double angle = this.getAngle(slope);
								double xA = 180 - a.longitude;
								double hypotenuseA = this.getHypotenuse(Math.toRadians(angle), xA);
								double yA = this.getY(hypotenuseA, xA);
								double y = Math.abs(a.latitude - b.latitude);
								double yB = y - yA;
								if (a.latitude < b.latitude) {
									c = new Link(a.latitude + yA, 180, Link.STOP);
									d = new Link(b.latitude - yB, -180, Link.START);
								} else if (a.latitude > b.latitude) {
									c = new Link(a.latitude - yA, 180, Link.STOP);
									d = new Link(b.latitude + yB, -180, Link.START);
								}
								b.longitude -= 360;
							}
						} else if (b.longitude > 0 && a.longitude < 0) {
							double difference = b.longitude - a.longitude;
							if (difference >= 180) {
								a.longitude += 360;
								double slope = this.getSlope(b, a);
								double angle = this.getAngle(slope);
								double xB = 180 - b.longitude;
								double hypotenuseB = this.getHypotenuse(Math.toRadians(angle), xB);
								double yB = this.getY(hypotenuseB, xB);
								double y = Math.abs(a.latitude - b.latitude);
								double yA = y - yB;
								if (a.latitude < b.latitude) {
									c = new Link(a.latitude + yB, -180, Link.STOP);
									d = new Link(b.latitude - yA, 180, Link.START);
								} else if (a.latitude > b.latitude) {
									c = new Link(a.latitude - yB, -180, Link.STOP);
									d = new Link(b.latitude + yA, 180, Link.START);
								}
								a.longitude -= 360;
							}
						}
						if (c != null && d != null) {
							linkList.add(c);
							linkList.add(d);
						}
					}
				}
			}
			pressureLinkMap.put(pressure, linkList);
		}
		return pressureLinkMap;
	}



//	@JsonIgnore
//	public Coordinate getAverageCoordinate(List<Coordinate> cList, Calendar calendar) {
//		Coordinate coordinate = null;
//		List<Coordinate> coordinateList = new ArrayList<>();
//		for (Coordinate c : cList) {
//			coordinateList.add(new Coordinate(c));
//		}
//		if (coordinateList.size() > 0) {
//			double latitudeSum = 0;
//			double longitudeSum = 0;
//			double latitude;
//			double longitude;
//			for (int i = 0; i < coordinateList.size(); i++) {
//				if (i + 1 < coordinateList.size()) {
//					Coordinate a = coordinateList.get(i);
//					Coordinate b = coordinateList.get(i + 1);
//					double difference = Math.abs(a.longitude - b.longitude);
//					if (difference >= 180) {
//						if (a.longitude < 0) {
//							a.longitude += 360;
//						}
//						if (b.longitude < 0) {
//							b.longitude += 360;
//						}
//					}
//				}
//			}
//			for (Coordinate c : coordinateList) {
//				latitudeSum += c.latitude;
//				longitudeSum += c.longitude;
//			}
//			latitude = latitudeSum / coordinateList.size();
//			longitude = longitudeSum / coordinateList.size();
//			if (longitude > 180) {
//				longitude -= 360;
//			}
//			coordinate = new Coordinate();
//			coordinate.latitude = latitude;
//			coordinate.longitude = longitude;
//			coordinate.calendar = calendar;
////			point.calendar = date;
//		}
//		return coordinate;
//	}

	@JsonIgnore
	public List<Coordinate> getCorrectedTimeCoordinateList() {
		List<Coordinate> timeCoordinateList = this.getTimeCoordinateList();
		List<Coordinate> coordinateList = new ArrayList<>();
		Coordinate a;
		Coordinate b;
		Coordinate c;
		Coordinate d;
		for (int i = 0; i < timeCoordinateList.size(); i++) {
			a = timeCoordinateList.get(i);
			coordinateList.add(a);
			if (i + 1 < timeCoordinateList.size()) {
				b = timeCoordinateList.get(i + 1);
				c = null;
				d = null;
				if (a.longitude > 0 && b.longitude < 0) {
					double difference = a.longitude - b.longitude;
					if (difference >= 180) {
						b.longitude += 360;
						double slope = this.getSlope(a, b);
						double angle = this.getAngle(slope);
						double xA = 180 - a.longitude;
						double hypotenuseA = this.getHypotenuse(Math.toRadians(angle), xA);
						double yA = this.getY(hypotenuseA, xA);
						double y = Math.abs(a.latitude - b.latitude);
						double yB = y - yA;
						if (a.latitude < b.latitude) {
							c = new Link(a.latitude + yA, 180, Link.STOP);
							d = new Link(b.latitude - yB, -180, Link.START);
						} else if (a.latitude > b.latitude) {
							c = new Link(a.latitude - yA, 180, Link.STOP);
							d = new Link(b.latitude + yB, -180, Link.START);
						}
						b.longitude -= 360;
					}
				} else if (b.longitude > 0 && a.longitude < 0) {
					double difference = b.longitude - a.longitude;
					if (difference >= 180) {
						a.longitude += 360;
						double slope = this.getSlope(b, a);
						double angle = this.getAngle(slope);
						double xB = 180 - b.longitude;
						double hypotenuseB = this.getHypotenuse(Math.toRadians(angle), xB);
						double yB = this.getY(hypotenuseB, xB);
						double y = Math.abs(a.latitude - b.latitude);
						double yA = y - yB;
						if (a.latitude < b.latitude) {
							c = new Link(a.latitude + yB, -180, Link.STOP);
							d = new Link(b.latitude - yA, 180, Link.START);
						} else if (a.latitude > b.latitude) {
							c = new Link(a.latitude - yB, -180, Link.STOP);
							d = new Link(b.latitude + yA, 180, Link.START);
						}
						a.longitude -= 360;
					}
				}
				if (c != null && d != null) {
					coordinateList.add(c);
					coordinateList.add(d);
				}
			}
		}
		return coordinateList;
	}

	@JsonIgnore
	public double getSlope(Coordinate a, Coordinate b) {
		return (b.latitude - a.latitude) / (b.longitude - a.longitude);
	}

	@JsonIgnore
	public double getAngle(double slope) {
		return (double) Math.atan(slope);
	}

	@JsonIgnore
	public double getHypotenuse(double angle, double x) {
		return (double) (x / Math.cos(angle));
	}

	@JsonIgnore
	public double getY(double hypotenuse, double leg) {
		return (double) Math.sqrt(Math.pow(hypotenuse, 2) - Math.pow(leg, 2));
	}

	@JsonIgnore
	public List<Coordinate> getHalfTimeCoordinateList() {
		Map<String, List<Coordinate>> timeCoordinateMap = this.getTimeCoordinateMap(this.coordinateList);
		int size = timeCoordinateMap.size();
		int half = size / 2;
		List<Coordinate> coordinateList = new ArrayList<>();
//		if(half > 0) {
		List<String> keys = new ArrayList<>(timeCoordinateMap.keySet());
		if (keys.size() > 0) {
			String key = keys.get(half);
			coordinateList = timeCoordinateMap.get(key);
		}
//		}
		return coordinateList;
	}

	/**
	 * 
	 * @param pressure - can be null or a positive Integer
	 * @return
	 */
	@JsonIgnore
	public Coordinate getHalfTimeLowerMostCoordinate(Integer pressure) {
		List<Coordinate> coordinateList = this.getHalfTimeCoordinateList();
		int size = coordinateList.size();
		Coordinate coordinate = null;
		if (size > 0) {
			if (pressure != null) {
				for (Coordinate c : coordinateList) {
					if (c.attribute.get("pressure").equals(pressure)) {
						coordinate = c;
						break;
					}
				}
			} else {
				coordinate = coordinateList.get(size - 1);
			}
		}
		return coordinate;
	}

	@JsonIgnore
	public Coordinate getMeanCoordinate(Coordinate x, Coordinate y) {
		Coordinate a = new Coordinate(x);
		Coordinate b = new Coordinate(y);
		double difference = a.longitude - b.longitude;
		if (difference >= 180) {
			if (a.longitude < 0) {
				a.longitude += 360;
			}
			if (b.longitude < 0) {
				b.longitude += 360;
			}
		}
		double longitude = (a.longitude + b.longitude) / 2;
		if (longitude > 180) {
			longitude -= 360;
		}
		Coordinate mean = new Coordinate();
		mean.latitude = (a.latitude + b.latitude) / 2;
		mean.longitude = longitude;
		return mean;
	}

	/**
	 * Review
	 * @return
	 */
	@JsonIgnore
	public double getMeanVorticity() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		double vorticity = 0;
		double vorticitySum = 0;
		for (List<Coordinate> cList : timePointMap.values()) {
			vorticity = 0;
			if (cList.size() > 0) {
				for (Coordinate c : cList) {
					vorticity += (float) c.attribute.get("vorticity");
				}
				vorticitySum += (vorticity / cList.size());
			}
		}
		return vorticitySum / (timePointMap.values().size());
	}

	@JsonIgnore
	public double getMeanSpeed() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();//Receives Map sorted by Time Strings
		int size = timePointMap.size();
		String keyA;
		String keyB;
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		List<Integer> pressureList = this.getPressureList();
		List<Coordinate> coordinateListA;
		List<Coordinate> coordinateListB;
		List<Double> meanList = new ArrayList<>();
		int coordinateCount;
		double coordinateSum;
		double mean;
		for (int i = 0; i < size; i++) {
			if (i + 1 < size) {
				keyA = keys.get(i);
				keyB = keys.get(i + 1);
				coordinateListA = timePointMap.get(keyA);
				coordinateListB = timePointMap.get(keyB);
				Map<Integer,Coordinate> pressureCoordinateMapA = this.getPressureCoordinateMap(coordinateListA);
				Map<Integer,Coordinate> pressureCoordinateMapB = this.getPressureCoordinateMap(coordinateListB);
				coordinateCount = 0;
				coordinateSum = 0;
				for (Integer pressure : pressureList) {// iterate through all possible levels
					Coordinate coordinateA = pressureCoordinateMapA.get(pressure);
					Coordinate coordinateB = pressureCoordinateMapB.get(pressure);
					if(coordinateA != null && coordinateB != null) {
						coordinateCount++;
						double distance = this.getDistance(coordinateA, coordinateB);//meters
						Duration duration = this.getDuration(coordinateA, coordinateB);//seconds
						double speed = distance / duration.seconds;
////						logger.info("getMeanSpeed() speed="+speed);
						coordinateSum += speed;
					}
				}
//				for (Integer pressure : pressureList) {// iterate through all possible levels
//					for (Coordinate coordinateA : coordinateListA) {
//						for (Coordinate coordinateB : coordinateListB) {
//							int levelA = (int) coordinateA.attribute.get("pressure");
//							int levelB = (int) coordinateB.attribute.get("pressure");
//							if (pressure == levelA && pressure == levelB) {
//								count++;
//								double distance = this.getDistance(coordinateA, coordinateB);
//								Duration duration = this.getDuration(coordinateA, coordinateB);
//								double speed = distance / duration.seconds;
////								logger.info("getMeanSpeed() speed="+speed);
//								dataSum += speed;
//							}
//						}
//					}
//				}
				mean = (coordinateCount > 0) ? coordinateSum / coordinateCount : 0;
				meanList.add(mean);
			}
		}
		double speedMeanSum = 0;
		for (Double m : meanList) {
			speedMeanSum += m;
		}

		return (meanList.size() > 0) ? speedMeanSum / meanList.size() : 0;
	}

	@JsonIgnore
	public void setPointColor() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		int size = timePointMap.size();
		int count = 0;
		List<Coordinate> pointList;
		for (Map.Entry<String, List<Coordinate>> entry : timePointMap.entrySet()) {
			pointList = entry.getValue();
			for (Coordinate p : pointList) {
				p.setColor(count, size);
			}
			count++;
		}
	}

	@JsonIgnore
	public void setPointColor(List<Coordinate> coordianteList) {
		int size = 0;
		int count = 0;
		for (Coordinate c : coordianteList) {
			if (!(c instanceof Link)) {
				size += 1;
			}
		}
		for (Coordinate c : coordianteList) {
			c.setColor(count, size);
			if (!(c instanceof Link)) {
				count++;
			}
		}
	}

	/**
	 * Function returns the highest pressure level, which is the lowest Integer
	 * value
	 * 
	 * @return
	 */
	@JsonIgnore
	public int getMinLevel() {
		return this.getPressureList().get(0);
	}

	@JsonIgnore
	public int getMaxLevel() {
		return this.getPressureList().get(this.getPressureCount() - 1);
	}

	@JsonIgnore
	public int getGenesisLowermostLevel() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		String key = keys.get(0);
		List<Coordinate> pointList = timePointMap.get(key);
		int max = Integer.MIN_VALUE;
		for (Coordinate p : pointList) {
			int level = (int) p.attribute.get("pressure");
			if (level > max) {
				max = level;
			}
		}
		return max;
	}

	@JsonIgnore
	public int getGenesisUppermostLevel() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		String key = keys.get(0);
		List<Coordinate> pointList = timePointMap.get(key);
		int min = Integer.MAX_VALUE;
		for (Coordinate p : pointList) {
			int level = (int) p.attribute.get("pressure");
			if (level < min) {
				min = level;
			}
		}
		return min;
	}

	@JsonIgnore
	public int getLysisLowermostLevel() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		int size = timePointMap.size();
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		String key = keys.get(size - 1);
		List<Coordinate> pointList = timePointMap.get(key);
		int max = Integer.MIN_VALUE;
		for (Coordinate p : pointList) {
			int level = (int) p.attribute.get("pressure");
			if (level > max) {
				max = level;
			}
		}
		return max;
	}

	@JsonIgnore
	public int getLysisUppermostLevel() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		int size = timePointMap.size();
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		String key = keys.get(size - 1);
		List<Coordinate> pointList = timePointMap.get(key);
		int min = Integer.MAX_VALUE;
		for (Coordinate p : pointList) {
			int level = (int) p.attribute.get("pressure");
			if (level < min) {
				min = level;
			}
		}
		return min;
	}

	@JsonIgnore
	public int getLowerMostLevel() {
		return this.getMaxLevel();
	}

	@JsonIgnore
	public int getUpperMostLevel() {
		return this.getMinLevel();
	}

	@JsonIgnore
	public int getMaxTimeLevelCount() {
		Map<String, List<Coordinate>> timeCoordinateMap = this.getTimeCoordinateMap();
		int max = 0;
		int value = 0;
		for (Map.Entry<String, List<Coordinate>> entry : timeCoordinateMap.entrySet()) {
			value = entry.getValue().size();
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	@JsonIgnore
	public double getSpeed() {
		double speed = this.getDistance() / this.getDuration().seconds;
		return speed;
	}

	@JsonIgnore
	public List<Integer> getMonthList() {
		List<Integer> monthList = new ArrayList<>();
		for (Coordinate p : this.coordinateList) {
			if (!monthList.contains(p.getMonth())) {
				monthList.add(p.getMonth());
			}
		}
		return monthList;
	}

	@JsonIgnore
	public List<Coordinate> getLowerMostPointList() {
		List<Coordinate> pointList = new ArrayList<>();
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		int size;
		for (Map.Entry<String, List<Coordinate>> entry : timePointMap.entrySet()) {
			size = entry.getValue().size();
			pointList.add(entry.getValue().get(size - 1));
		}
		return pointList;
	}

	@JsonIgnore
	public double getDistance() {
		List<Coordinate> pointList = this.getLowerMostPointList();
		double distance = 0;
		for (int i = 0; i < pointList.size(); i++) {
			if (i + 1 < pointList.size())
				distance += this.getDistance(pointList.get(i), pointList.get(i + 1));
		}
		return distance;
	}

	@JsonIgnore
	public List<Coordinate> getSpeedCoordinateList() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap(this.coordinateList);
		int size = timePointMap.size();
		List<Coordinate> coordinateList = new ArrayList<>();
		String keyA;
		String keyB;
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		List<Integer> levelList = this.getPressureList();
		List<Coordinate> coordinateListA;
		List<Coordinate> coordinateListB;
		int count;
		double speedSum;
		double speedMean;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < size; i++) {
			if (i + 1 < size) {
				keyA = keys.get(i);
				keyB = keys.get(i + 1);
				coordinateListA = timePointMap.get(keyA);
				coordinateListB = timePointMap.get(keyB);
				count = 0;
				speedSum = 0;
				for (Integer level : levelList) {// iterate through all possible levels
					for (Coordinate pointA : coordinateListA) {
						for (Coordinate pointB : coordinateListB) {
							int levelA = (int) pointA.attribute.get("pressure");
							int levelB = (int) pointB.attribute.get("pressure");
							if (pointA.flag && pointB.flag && level == levelA && level == levelB) {
								count++;
								double distance = this.getDistance(pointA, pointB);
								Duration duration = this.getDuration(pointA, pointB);
								double speed = distance / duration.seconds;
								speedSum += speed;
							}
						}
					}
				}
				speedMean = (count > 0) ? speedSum / count : 0;
				Date dateA;
				try {
					dateA = dateFormat.parse(keyA);
					Date dateB = dateFormat.parse(keyB);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(dateA);
					Coordinate coordinateA = this.getAverageCoordinate(new ArrayList<>(coordinateListA), calendar);
					calendar.setTime(dateB);
					Coordinate coordinateB = this.getAverageCoordinate(new ArrayList<>(coordinateListB), calendar);
					Coordinate coordinate = this.getMeanCoordinate(coordinateA, coordinateB);
					coordinate.calendar = Calendar.getInstance();
					coordinate.calendar.setTime(dateA);
					coordinate.attribute.put("speed", speedMean);
					coordinateList.add(coordinate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return coordinateList;
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			logger.error("IOException " + ex.getMessage());
		}
		return string;
	}
}
//20230428 
//if(event instanceof ERA5Event) {
//	int[] pressureArray = ((ERA5Event)event).pressureArray;
//	for(int p =0;p<pressureArray.length;p++) {
//		dataMatrix[i][2+p] = pressureArray[p];
//	}
//} else if(event instanceof ERAInterimEvent) {
//	int[] pressureArray = ((ERAInterimEvent)event).pressureArray;
//	for(int p =0;p<pressureArray.length;p++) {
//		dataMatrix[i][2+p] = pressureArray[p];
//	}
//}
//20230428 Failed Implementation
//if(e instanceof ERA5Event) {
//	int[] pressureArray = ((ERA5Event)e).pressureArray;
//	int pressure;
//	for(int p =0;p<pressureArray.length;p++) {
//		pressure = pressureArray[p];
//		for(Coordinate coordinate:coordinateList) {
//			int cPressure = coordinate.getPressure();
//			if(cPressure == pressure) {
//				dataMatrix[i+1][2+p] = 1;
//			}
//			
//		}
//		
//	}
//} else if(e instanceof ERAInterimEvent) {
//	int[] pressureArray = ((ERAInterimEvent)e).pressureArray;
//	for(int p =0;p<pressureArray.length;p++) {
//		dataMatrix[i+1][2+p] = pressureArray[p];
//	}
//}

//dataMatrix[i + 1][2] = dateFormat.format(event.getEndCalendar().getTime());
//dataMatrix[i + 1][3] = event.getDuration().days;
//dataMatrix[i + 1][4] = (event.family != null) ? event.family.toString() : "NULL";
//dataMatrix[i + 1][5] = (event.classification != null) ? event.classification.toString()
//		: "NULL";
//dataMatrix[i + 1][6] = event.getDistance();
//dataMatrix[i + 1][7] = event.getMaxTimeLevelCount();
//dataMatrix[i + 1][8] = event.getPressureCount();
//dataMatrix[i + 1][9] = event.getLowerMostLevel();
//dataMatrix[i + 1][10] = event.getUpperMostLevel();
//dataMatrix[i + 1][11] = event.getGenesisLowermostLevel();
//dataMatrix[i + 1][12] = event.getGenesisUppermostLevel();
//dataMatrix[i + 1][13] = event.getLysisLowermostLevel();
//dataMatrix[i + 1][14] = event.getLysisUppermostLevel();
//dataMatrix[i + 1][15] = event.getMeanSpeed();
//dataMatrix[i + 1][16] = event.getMeanVorticity();
//System.out.println("count="+count);
//System.out.println("total="+total);
//System.out.println(this+".isSimilar("+event+", "+threshold+") flag=true"); 
//logger.info("getSpeed() speed=" + speed);
//public String toString() {
//return this.id;
//}
//@JsonIgnore
//public void classify() {
//	int maxTimeLevelCount = this.getMaxTimeLevelCount();// this.getLevelList().size();//
//	int lowerMostLevel = this.getLowerMostLevel();
//	if (maxTimeLevelCount == 2 || maxTimeLevelCount == 3) {
//		this.family = Family.SHALLOW;
//		if (lowerMostLevel >= 700) {
//			this.classification = Classification.LOW;
//		} else if (lowerMostLevel >= 400) {
//			this.classification = Classification.MID;
//		} else if (lowerMostLevel >= 125) {
//			this.classification = Classification.UPPER;
//		}
//	} else if (maxTimeLevelCount == 4 || maxTimeLevelCount == 5) {
//		this.family = Family.INTERMEDIATE;
//		if (lowerMostLevel >= 500) {
//			this.classification = Classification.LOW_MID;
//		} else if (lowerMostLevel >= 125) {
//			this.classification = Classification.MID_UPPER;
//		}
//	} else if (maxTimeLevelCount >= 6) {
//		this.family = Family.DEEP;
//	}
//}
