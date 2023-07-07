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
package org.meritoki.prospero.library.model.node.data.source;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.ERA5Event;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.ERAInterimEvent;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.utn.library.stacker.model.Collection;
import org.utn.library.stacker.model.Dot;
import org.utn.library.stacker.model.Stack;
import org.utn.library.stacker.model.Track;

import com.meritoki.library.controller.memory.MemoryController;
import com.meritoki.library.controller.node.NodeController;

public class CycloneUTNERA5Test extends CycloneSource {
	static Logger logger = LoggerFactory.getLogger(CycloneUTNERA5Test.class.getName());
	private final String merged = "";
	private final int startYear = 1979;
	private final int endYear = 2019;
	private final Time startTime = new Time(1979,1,1,-1,-1,-1);
	private final Time endTime = new Time(2019,12,31,-1,-1,-1);
	public static String extension = "json";
	public String defaultTimeFormat = "yyyy-MM-dd HH:mm:ss";

	public CycloneUTNERA5Test() {
		super();
		this.single = true;
		this.setPressureArray(ERAInterimEvent.pressureArray);
		this.setPrefix(merged);
		this.setRelativePath("UTN" + seperator + "File" + seperator + "Data" + seperator + "Cyclone" + seperator + "202103");
	}

	@Override
	public int getStartYear() {
		return this.startYear;
	}

	@Override
	public int getEndYear() {
		return this.endYear;
	}
	
	@Override
	public Time getStartTime() {
		return this.startTime;
	}

	@Override
	public Time getEndTime() {
		return this.endTime;
	}

	public List<Event> read(int year, int month) throws Exception {
		logger.info("read(" + year + "," + month + ")");
		String yearMonth = year + "" + String.format("%02d", month) + "01";
		if("m".equals(this.order)) {
			this.setPrefix(merged);
		} else {
			this.setPrefix(this.getPressureString());
		}
		List<Event> eventList = this.read(new File(this.getFilePath(yearMonth + seperator + "stack" + seperator
				+ "collection" + seperator + this.getPrefix() + "-" + yearMonth + "." + extension)));
//		Calendar calendar;
//		Integer m = null;
//		Integer y = null;
//		ListIterator<Event> eventIterator = eventList.listIterator();
//		while (eventIterator.hasNext()) {
//			Event e = eventIterator.next();
//			calendar = e.getStartCalendar();
//			m = calendar.get(Calendar.MONTH) + 1;
//			y = calendar.get(Calendar.YEAR);
//			if (year != y || month != m) {
////				logger.info("read(" + year + "," + month + ") y="+year+" m="+m);
//				eventIterator.remove();
//			}
//		}
		return eventList;
	}

	public List<Event> read(File file) throws Exception {
		if (logger.isDebugEnabled()) {
			MemoryController.log();
			logger.debug("read(" + file + ")");
		}
		List<Event> eventList = new ArrayList<>();
		Object object = NodeController.openJson(file, Collection.class);
		if (object != null) {
			Collection collection = (Collection) object;
			Map<Integer, Stack> stackMap = collection.stackMap;
			Map<Integer, Track> trackMap;
			Map<Integer, Dot> dotMap;
			for (Entry<Integer, Stack> stackEntry : stackMap.entrySet()) {
				if (!Thread.interrupted()) {
					int id = stackEntry.getValue().id;
//					String date = stackEntry.getValue().getFirstDot().startDate;
					List<Coordinate> coordinateList = new ArrayList<Coordinate>();
					trackMap = stackEntry.getValue().trackMap;
					for (Entry<Integer, Track> trackEntry : trackMap.entrySet()) {
						if (!Thread.interrupted()) {
							dotMap = trackEntry.getValue().dotMap;
							for (Entry<Integer, Dot> dotEntry : dotMap.entrySet()) {
								if (!Thread.interrupted()) {
									Coordinate point = new Coordinate();
									double latitude = dotEntry.getValue().lat;
									double longitude = dotEntry.getValue().lon;
									point.calendar = this.getCalendar(dotEntry.getValue().startDate,
											dotEntry.getValue().frame);
									int pressure = dotEntry.getValue().gph;
									float vorticity = (float) (dotEntry.getValue().module * Math.pow(10.0, -5.0) * -1);
									point.latitude = (float) latitude;
									if (longitude < 180) {
										point.longitude = (float) longitude;
									} else {
										point.longitude = (float) (longitude - 360);
									}
									point.attribute.put("pressure", pressure);
									point.attribute.put("vorticity", vorticity);
									coordinateList.add(point);
								} else {
									throw new InterruptedException();
								}
							}
						} else {
							throw new InterruptedException();
						}
					}
					CycloneEvent event = new ERAInterimEvent(coordinateList);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					event.id = sdf.format(event.getStartCalendar().getTime()) + "-"
							+ sdf.format(event.getEndCalendar().getTime()) + id;
					eventList.add(event);
				} else {
					throw new InterruptedException();
				}
			}

		}
		return eventList;
	}

	public Calendar getCalendar(String time, int index) {
		Date dateB = Time.getDate(time, "yyyyMMdd");
		Calendar calendar = this.getCalendar(dateB);
		int product = index * 6;
		calendar.add(Calendar.HOUR_OF_DAY, (product));
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
//		System.out.println(hour);
//		if(hour %2 == 0) {
//			hour++;
//		}
		calendar.set(Calendar.HOUR_OF_DAY, hour);
//		System.out.println("getCalendar("+time+","+index+") calendar="+calendar.getTime());
		return calendar;
	}

	public Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(this.timeZone));
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}


}
//@Override
//public int[] getPressureArray() {
//	return this.pressureTestArray;
//}
//public static String order = "tb";
//public static String prefix = "125-175-225-300-400-500-600-700-775-825-875-925-975-";
//public static String merged = -";
//public static String topBottomPrefix = "100-125-150-175-200-225-250-300-400-500-600-700-775-825-850-875-925-975-";// "125-175-225-300-400-500-600-700-775-825-875-925-975-";
//public static String topBottomTestPrefix = "100-125-150-200-250-300-400-500-600-700-850-925-";
//public static String bottomTopPrefix = "975-925-875-825-775-700-600-500-400-300-225-175-125-";
//public int[] pressureArray = { 50, 100, 125, 150, 175, 200, 225, 250, 300, 400, 500, 600, 700, 775, 825, 850, 875, 925,
//		975 };
//public int[] pressureTestArray = { 100, 125, 150,200, 250, 300, 400, 500, 600, 700, 850, 925,
//		975 };
//this.setBasePath("/home/jorodriguez/Drive/Test/");
//this.setRelativePath("era-5");
//switch (order) {
//case "tb": {
//	prefix = topBottomTestPrefix;//20230516 Test Added
//	break;
//}
//case "bt": {
//	prefix = bottomTopPrefix;
//	break;
//}
//case "m": {
//	prefix = merged;
//	break;
//}
//}
//String fileName = path + seperator + yearMonth + seperator + "stack" + seperator + "collection" + seperator
//		+ prefix + yearMonth + "." + extension;
//return this.read(new File(fileName));
//public List<Event> eventMapGet(int y, int m) {
//if(this.eventMap == null) {
//	this.eventMap = new HashMap<>();
//}
//List<Event> eList = this.eventMap.get(y + "" + m);
//if (eList == null) {
//	eList = (List<Event>)this.read(y, m);
//	if (eList != null) {
//		this.eventMap.put(y + "" + m, eList);
//	} else {
//		eList = new ArrayList<>();
//	}
//}
//return eList;
//}
//public List<CycloneEvent> read(int year, int month) {
//logger.info("read(" + year + "," + month + ")");
//String yearMonth = year + "" + String.format("%02d", month) + "01";
//switch (this.order) {
//case "tb": {
//	prefix = topBottomPrefix;
//	break;
//}
//case "bt": {
//	prefix = bottomTopPrefix;
//}
//}
//String fileName = path + seperator + yearMonth + seperator + "stack" + seperator + "collection" + seperator
//		+ prefix + yearMonth + "." + extension;
//return this.read(new File(fileName));
//}
//public static char seperator = File.separatorChar;
//public static String path = basePath + "prospero-data" + seperator + "UTN" + seperator + "File" + seperator + "Data"
//		+ seperator + "Cyclone" + seperator + "202103";
//@Override
//public Object get(Calendar calendar) {
//	return this.eventMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}

//@Override
//public Object get(Calendar calendar) {
//	return this.eventMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}
//@Override
//public List<CycloneEvent> eventMapGet(int y, int m) {
//	logger.info("eventMapGet(" + y + ", " + m + ")");
//	if (this.eventMap == null)
//		this.eventMap = new HashMap<>();
//	List<CycloneEvent> eList = this.eventMap.get(y + "" + m);
//	if (eList == null) {
//		eList = (List<CycloneEvent>)this.read(y, m);
//		if (eList != null) {
//			this.eventMap.put(y + "" + m, eList);
//		} else {
//			eList = new ArrayList<>();
//		}
//	}
//	eList = new ArrayList<>(eList);
//	return eList;
//}

//public List<CycloneEvent> eventMapGet(String time) {
//	logger.info("eventMapGet(" + time + ")");
//	if (this.eventMap == null)
//		this.eventMap = new HashMap<>();
//	List<CycloneEvent> eList = this.eventMap.get(time);
//	if (eList != null) {
//		eList = new ArrayList<>(eList);
//	}
//	return eList;
//}
//public void query(Filter query) throws Exception {
//logger.info("query(" + query + ")");
//this.eventList = null;
////this.unsupportedException("resolution", query.resolution);
//this.levelList = this.getLevelList(query.pressure, this.levelArray);
//this.intervalList = this.getIntervalList(query.time);
//this.regionList = this.getRegionList(query.region);
////this.durationList = this.getDurationList(query.duration);
//this.dimension = this.getDimension(query.dimension);
////this.count = this.getCount(query.levelCount);
////this.familyList = query.familyList;
////this.classList = query.classList;
////this.month = query.month;
////this.year = query.year;
////this.stack = query.stack;
////this.band = query.band;
////this.cube = query.cube;
//this.order = "tb";// query.order;
//if (intervalList != null) {
//	this.eventList =  this.eventMapGet(query.time);
//	if(this.eventList == null) {
//		this.eventList = new ArrayList<>();
//		for (Interval i : this.intervalList) {
//			this.eventList.addAll(this.getEventList(i));
//		}
//		this.eventList = this.eventList.stream().distinct().collect(Collectors.toList());
//		this.eventMap.put(query.time, eventList);
//	}
////	this.eventList = this.getCountEventList(this.eventList);
//	this.resetFlags(eventList);
//	boolean intervalFlag = false;
//	boolean levelFlag = false;
//	boolean regionFlag = false;
//	boolean durationFlag = false;
//	boolean familyFlag = false;
//	boolean classFlag = false;
//	for (CycloneEvent e : this.eventList) {
//		durationFlag = false;
//		familyFlag = false;
//		classFlag = false;
//		for (Coordinate p : e.coordinateList) {
//			intervalFlag = false;
//			levelFlag = false;
//			regionFlag = false;
//			for (Interval i : intervalList) {
//				if (i.contains(p)) {
//					intervalFlag = true;
//					break;
//				}
//			}
//			if (this.levelList != null && this.levelList.size() > 0) {
//				for (Integer l : this.levelList) {
//					int lev = (int) p.attribute.map.get("pressure");
//					if (l == lev) {
//						levelFlag = true;
//					}
//				}
//			} else {
//				levelFlag = true;
//			}
//			if (this.regionList != null && this.regionList.size() > 0) {
//				for (Region r : this.regionList) {
//					if (r.contains(p)) {
//						regionFlag = true;
//					}
//				}
//			} else {
//				regionFlag = true;
//			}
//			p.flag = intervalFlag && levelFlag && regionFlag;
//		}
//		if (this.durationList != null && this.durationList.size() > 0) {
//			for (Duration d : this.durationList) {
//				if (d.contains(e.getDuration())) {
//					durationFlag = true;
//					break;
//				}
//			}
//		} else {
//			durationFlag = true;
//		}
//		if (this.familyList != null && this.familyList.size() > 0) {
//			for (Family depth : this.familyList) {
//				if (e.family != null && depth == e.family) {
//					familyFlag = true;
//					break;
//				}
//			}
//		} else {
//			familyFlag = true;
//		}
//
//		if (this.classList != null && this.classList.size() > 0) {
//			for (Classification type : this.classList) {
//				if (type == e.classification) {
//					classFlag = true;
//					break;
//				}
//			}
//		} else {
//			classFlag = true;
//		}
//		e.flag = durationFlag && familyFlag && classFlag;
//	}
//	Iterator<CycloneEvent> it = this.eventList.iterator();
//	while (it.hasNext()) {
//		CycloneEvent e = it.next();
//		if (!e.flag || !e.hasCoordinate())
//			it.remove();
//	}
//} else {
//	throw new Exception("time must be defined");
//}
//}
//public void similarTest(List<Event> eventList) {
//for (Event event : eventList) {
//	for (Event e : eventList) {
//		event.isSimilar(e, 1);
//	}
//}
//}
//public List<Event> getLevelCountEventList(List<Event> eventList) {
//Iterator<Event> bIterator = eventList.iterator();
//while (bIterator.hasNext()) {
//	Event b = bIterator.next();
//	if (b.getLevelCount() < this.levelCount) {
//		bIterator.remove();
//	}
//}
//return eventList;
//}
//public List<Event> getSingleCountList(List<Event> listA, List<Event> listB) {
//System.out.println("getSingleCountList("+listA+","+listB+")");
//if(listB != null) {
//	for(Event a: listA) {
//		Iterator<Event> bIterator = listB.iterator();
//		while(bIterator.hasNext()) {
//			Event b = bIterator.next();
//			if(a.isSimilar(b, 1)) {
//				bIterator.remove();
//			}
//		}
//	}
//}
////System.out.println("getSingleCountList("+listA+","+listB+") listB="+listB);
//return listB;
//}

//public List<Event> getSingleCountEventList(List<Event> eventList) {
//System.out.println("getSingleCountList(" + eventList+")");
//if (eventList != null) {
//	Iterator<Event> eventIterator = eventList.iterator();
//	Event bufferEvent = null;
//	while (eventIterator.hasNext()) {
//		Event event = eventIterator.next();
//		if(bufferEvent != null) {
//			if (bufferEvent.isSimilar(event, 1)) {
//				eventIterator.remove();
//			}
//		}
//		bufferEvent = event;
//	}
//}
//return eventList;
//}

//public List<Event> getEventList(Interval i) throws Exception {
//List<Event> eventList = new ArrayList<>();
//int startYear = (i.startYear == -1) ? this.startYear : i.startYear;
//int startMonth = (i.startMonth == -1) ? 1 : i.startMonth;
//int endYear = (i.endYear == -1) ? this.endYear : i.endYear;
//int endMonth = (i.endMonth == -1) ? 12 : i.endMonth;
//List<Event> bufferList = n//	public List<Event> getLevelCountEventList(List<Event> eventList) {
//Iterator<Event> bIterator = eventList.iterator();
//while (bIterator.hasNext()) {
//	Event b = bIterator.next();
//	if (b.getLevelCount() < this.levelCount) {
//		bIterator.remove();
//	}
//}
//return eventList;
//}ull;
//List<Event> loadList;
//if (i.startYear >= this.startYear && i.endYear <= this.endYear) {
//	if (i.startYear == -1 && i.endYear == -1) {
//		if (startMonth <= endMonth) {
//			for (int y = startYear; y <= endYear; y++) {
//				for (int m = startMonth; m <= endMonth; m++) {
//					loadList = this.eventMapGet(y, m);
//					loadList = (bufferList != null)?this.getSingleCountList(bufferList, loadList):this.getSingleCountList(eventList, loadList);
//					eventList.addAll(loadList);
//					bufferList = loadList;
//				}
//			}
//		} else {
//			for (int y = startYear; y <= endYear; y++) {
//				for (int m = startMonth; m <= 12; m++) {
//					loadList = this.eventMapGet(y, m);
//					loadList = (bufferList != null)?this.getSingleCountList(bufferList, loadList):this.getSingleCountList(eventList, loadList);
//					eventList.addAll(loadList);
//					bufferList = loadList;
//				}
//				for (int m = 1; m <= endMonth; m++) {
//					loadList = this.eventMapGet(y, m);
//					loadList = (bufferList != null)?this.getSingleCountList(bufferList, loadList):this.getSingleCountList(eventList, loadList);
//					eventList.addAll(loadList);
//					bufferList = loadList;
//				}
//			}
//		}
//	} else {
//		int yearDifference = endYear - startYear - 1;
//		if (yearDifference == -1) {// same year
//			for (int y = startYear; y <= endYear; y++) {
//				for (int m = startMonth; m <= endMonth; m++) {
//					loadList = this.eventMapGet(y, m);
//					loadList = (bufferList != null)?this.getSingleCountList(bufferList, loadList):this.getSingleCountList(eventList, loadList);
//					eventList.addAll(loadList);
//					bufferList = loadList;
//				}
//			}
//		} else if (yearDifference > -1) {
//			for (int m = startMonth; m <= 12; m++) {
//				loadList = this.eventMapGet(startYear, m);
//				loadList = (bufferList != null)?this.getSingleCountList(bufferList, loadList):this.getSingleCountList(eventList, loadList);
//				eventList.addAll(loadList);
//				bufferList = loadList;
//			}
//			for (int y = startYear + 1; y <= endYear - 1; y++) {// skipped is years are consecutive
//				for (int m = 1; m <= 12; m++) {
//					loadList = this.eventMapGet(y, m);
//					loadList = (bufferList != null)?this.getSingleCountList(bufferList, loadList):this.getSingleCountList(eventList, loadList);
//					eventList.addAll(loadList);
//					bufferList = loadList;
//				}
//			}
//			for (int m = 1; m <= endMonth; m++) {
//				loadList = this.eventMapGet(endYear, m);
//				loadList = (bufferList != null)?this.getSingleCountList(bufferList, loadList):this.getSingleCountList(eventList, loadList);
//				eventList.addAll(loadList);
//				bufferList = loadList;
//			}
//		}
//	}
//} else {
//	throw new Exception("invalid time, valid time between " + this.startYear + " and " + this.endYear);
//}
//return eventList;
//}
