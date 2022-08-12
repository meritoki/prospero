package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Duration;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Link;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@JsonTypeInfo(use = Id.CLASS,
include = JsonTypeInfo.As.PROPERTY,
property = "type")
@JsonSubTypes({
@Type(value = ERA5Event.class),@Type(value = ERAInterimEvent.class)
})
public class CycloneEvent extends Event {

	@JsonIgnore
	static Logger logger = LogManager.getLogger(CycloneEvent.class.getName());
	@JsonProperty
	public List<Integer> pressureList;
	@JsonProperty
	public Family family;
	@JsonProperty
	public Classification classification;

	public CycloneEvent() {
	}

	public CycloneEvent(String id, List<Coordinate> pointList) {
		super(id, pointList);
//		this.classify();
	}

	public CycloneEvent(CycloneEvent event) {
		super(event.id, new ArrayList<>(event.coordinateList));
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
			}
		}
		logger.info("getFamilyClassMap(...) map="+map);
		return map;
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
//			System.out.println("count="+count);
//			System.out.println("total="+total);
//			System.out.println(this+".isSimilar("+event+", "+threshold+") flag=true");
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
		for (Coordinate p : this.coordinateList) {
			int pressure = (int) p.attribute.get("pressure");
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
	public List<Integer> getSelectedLevelList() {
		List<Integer> levelList = new ArrayList<>();
		for (Coordinate p : this.coordinateList) {
			if (p.flag) {
				levelList.add((Integer) p.attribute.get("pressure"));
			}
		}
		return levelList;
	}

	@JsonIgnore
	public Map<String, List<Coordinate>> getTimePointMap(List<Coordinate> pointList) {
//		if (this.timePointMap == null) {
		Map<String, List<Coordinate>> timePointMap = new HashMap<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date;
		List<Coordinate> pList;
		for (Coordinate p : pointList) {
			if (p.flag) {
				date = dateFormat.format(p.calendar.getTime());
				pList = timePointMap.get(date);
				if (pList == null) {
					pList = new ArrayList<>();
					pList.add(p);
				} else {
					pList.add(p);
				}
				Collections.sort(pList);
				timePointMap.put(date, pList);
			}
		}
		timePointMap = new TreeMap<String, List<Coordinate>>(timePointMap);
//		}
		return timePointMap;
	}

	@JsonIgnore
	public List<Coordinate> getTimePointList() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		List<Coordinate> pointList = new ArrayList<Coordinate>();
		Coordinate point;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		for (Map.Entry<String, List<Coordinate>> entry : timePointMap.entrySet()) {
			try {
				date = dateFormat.parse(entry.getKey());
				point = this.getAveragePoint(entry.getValue(), date);
				if (point != null) {
					pointList.add(point);
				}
			} catch (ParseException e) {
				logger.error("ParseException " + e.getMessage());
			}
		}
		return pointList;
	}

	@JsonIgnore
	public Coordinate getAveragePoint(List<Coordinate> pList, Date date) {
		Coordinate point = null;
		List<Coordinate> pointList = new ArrayList<>();
		for (Coordinate p : pList) {
			pointList.add(new Coordinate(p));
		}
		if (pointList.size() > 0) {
			double latitudeSum = 0;
			double longitudeSum = 0;
			double latitude;
			double longitude;
			for (int i = 0; i < pointList.size(); i++) {
				if (i + 1 < pointList.size()) {
					Coordinate pointA = pointList.get(i);
					Coordinate pointB = pointList.get(i + 1);
					double difference = Math.abs(pointA.longitude - pointB.longitude);
					if (difference >= 180) {
						if (pointA.longitude < 0) {
							pointA.longitude += 360;
						}
						if (pointB.longitude < 0) {
							pointB.longitude += 360;
						}
					}
				}
			}
			for (Coordinate p : pointList) {
				latitudeSum += p.latitude;
				longitudeSum += p.longitude;
			}
			latitude = latitudeSum / pointList.size();
			longitude = longitudeSum / pointList.size();
			if (longitude > 180) {
				longitude -= 360;
			}
			point = new Coordinate();
			point.latitude = latitude;
			point.longitude = longitude;
//			point.calendar = date;
		}
		return point;
	}

	@JsonIgnore
	public List<Coordinate> getCorrectedTimePointList() {
		List<Coordinate> timePointList = this.getTimePointList();
		List<Coordinate> pointList = new ArrayList<>();
		Coordinate pointA;
		Coordinate pointB;
		Coordinate pointC;
		Coordinate pointD;
		for (int i = 0; i < timePointList.size(); i++) {
			pointA = timePointList.get(i);
			pointList.add(pointA);
			if (i + 1 < timePointList.size()) {
				pointB = timePointList.get(i + 1);
				pointC = null;
				pointD = null;
				if (pointA.longitude > 0 && pointB.longitude < 0) {
					double difference = pointA.longitude - pointB.longitude;
					if (difference >= 180) {
						pointB.longitude += 360;
						double slope = this.getSlope(pointA, pointB);
						double angle = this.getAngle(slope);
						double xA = 180 - pointA.longitude;
						double hypotenuseA = this.getHypotenuse(Math.toRadians(angle), xA);
						double yA = this.getY(hypotenuseA, xA);
						double y = Math.abs(pointA.latitude - pointB.latitude);
						double yB = y - yA;
						if (pointA.latitude < pointB.latitude) {
							pointC = new Link(pointA.latitude + yA, 180, Link.STOP);
							pointD = new Link(pointB.latitude - yB, -180, Link.START);
						} else if (pointA.latitude > pointB.latitude) {
							pointC = new Link(pointA.latitude - yA, 180, Link.STOP);
							pointD = new Link(pointB.latitude + yB, -180, Link.START);
						}
						pointB.longitude -= 360;
					}
				} else if (pointB.longitude > 0 && pointA.longitude < 0) {
					double difference = pointB.longitude - pointA.longitude;
					if (difference >= 180) {
						pointA.longitude += 360;
						double slope = this.getSlope(pointB, pointA);
						double angle = this.getAngle(slope);
						double xB = 180 - pointB.longitude;
						double hypotenuseB = this.getHypotenuse(Math.toRadians(angle), xB);
						double yB = this.getY(hypotenuseB, xB);
						double y = Math.abs(pointA.latitude - pointB.latitude);
						double yA = y - yB;
						if (pointA.latitude < pointB.latitude) {
							pointC = new Link(pointA.latitude + yB, -180, Link.STOP);
							pointD = new Link(pointB.latitude - yA, 180, Link.START);
						} else if (pointA.latitude > pointB.latitude) {
							pointC = new Link(pointA.latitude - yB, -180, Link.STOP);
							pointD = new Link(pointB.latitude + yA, 180, Link.START);
						}
						pointA.longitude -= 360;
					}
				}
				if (pointC != null && pointD != null) {
					pointList.add(pointC);
					pointList.add(pointD);
				}
			}
		}
		return pointList;
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
	public List<Coordinate> getHalfTimePointList() {
		Map<String, List<Coordinate>> timePointMap = this.getTimePointMap(this.coordinateList);
		int size = timePointMap.size();
		int half = size / 2;
		List<Coordinate> pointList = new ArrayList<>();
//		if(half > 0) {
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		if (keys.size() > 0) {
			String key = keys.get(half);
			pointList = timePointMap.get(key);
		}
//		}
		return pointList;
	}

	@JsonIgnore
	public Coordinate getHalfTimeLowerMostPoint(Integer gph) {
		List<Coordinate> pointList = this.getHalfTimePointList();
		int size = pointList.size();
		Coordinate point = null;
		if (size > 0) {
			if (gph != null) {
				for (Coordinate p : pointList) {
					if (p.attribute.get("pressure").equals(gph)) {
						point = p;
						break;
					}
				}
			} else {
				point = pointList.get(size - 1);
			}
		}
		return point;
	}

	@JsonIgnore
	public Coordinate getMeanPoint(Coordinate x, Coordinate y) {
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

	@JsonIgnore
	public double getMeanSpeed() {
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		int size = timePointMap.size();
		String keyA;
		String keyB;
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		List<Integer> levelList = this.getPressureList();
		List<Coordinate> pointListA;
		List<Coordinate> pointListB;
		List<Double> speedMeanList = new ArrayList<>();
		int count;
		double speedSum;
		double speedMean;
		for (int i = 0; i < size; i++) {
			if (i + 1 < size) {
				keyA = keys.get(i);
				keyB = keys.get(i + 1);
				pointListA = timePointMap.get(keyA);
				pointListB = timePointMap.get(keyB);
				count = 0;
				speedSum = 0;
				for (Integer level : levelList) {// iterate through all possible levels
					for (Coordinate pointA : pointListA) {
						for (Coordinate pointB : pointListB) {
							int levelA = (int) pointA.attribute.get("pressure");
							int levelB = (int) pointB.attribute.get("pressure");
							if (level == levelA && level == levelB) {
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
				speedMeanList.add(speedMean);
			}
		}
		double speedMeanSum = 0;
		for (Double mean : speedMeanList) {
			speedMeanSum += mean;
		}

		return (speedMeanList.size() > 0) ? speedMeanSum / speedMeanList.size() : 0;
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
	public void setPointColor(List<Coordinate> pointList) {
		int size = 0;
		int count = 0;
		for (Coordinate p : pointList) {
			if (!(p instanceof Link)) {
				size += 1;
			}
		}
		for (Coordinate p : pointList) {
			p.setColor(count, size);
			if (!(p instanceof Link)) {
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
		Map<String, List<Coordinate>> timePointMap = this.getTimeCoordinateMap();
		int max = 0;
		int value = 0;
		for (Map.Entry<String, List<Coordinate>> entry : timePointMap.entrySet()) {
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
//		logger.info("getSpeed() speed=" + speed);
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

//	public String toString() {
//		return this.id;
//	}

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
	public List<Coordinate> getSpeedPointList() {
		Map<String, List<Coordinate>> timePointMap = this.getTimePointMap(this.coordinateList);
		int size = timePointMap.size();
		List<Coordinate> pointList = new ArrayList<>();
		String keyA;
		String keyB;
		List<String> keys = new ArrayList<>(timePointMap.keySet());
		List<Integer> levelList = this.getPressureList();
		List<Coordinate> pointListA;
		List<Coordinate> pointListB;
		int count;
		double speedSum;
		double speedMean;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < size; i++) {
			if (i + 1 < size) {
				keyA = keys.get(i);
				keyB = keys.get(i + 1);
				pointListA = timePointMap.get(keyA);
				pointListB = timePointMap.get(keyB);
				count = 0;
				speedSum = 0;
				for (Integer level : levelList) {// iterate through all possible levels
					for (Coordinate pointA : pointListA) {
						for (Coordinate pointB : pointListB) {
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
					Coordinate pointA = this.getAveragePoint(pointListA, dateA);
					Coordinate pointB = this.getAveragePoint(pointListB, dateB);
					Coordinate point = this.getMeanPoint(pointA, pointB);
					point.calendar = Calendar.getInstance();
					point.calendar.setTime(dateA);
					point.attribute.put("speed", speedMean);
					pointList.add(point);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return pointList;
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
