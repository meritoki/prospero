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
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
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

public class CycloneUTNERAInterimTest extends CycloneSource {

	static Logger logger = LoggerFactory.getLogger(CycloneUTNERAInterimTest.class.getName());
//	public static String prefix = "100-125-150-200-250-300-400-500-600-700-850-925-";
	public static String prefix = "925-850-700-600-500-400-300-250-200-150-125-100-";
	public static String extension = "json";
//	public int[] pressureArray = { 100, 125, 150, 200, 250, 300, 400, 500, 600, 700, 850, 925 };
	private final int startYear = 2001;
	private final int endYear = 2017;

	public CycloneUTNERAInterimTest() {
		super();
		this.setPressureArray(new Integer[]{ 100, 125, 150, 200, 250, 300, 400, 500, 600, 700, 850, 925 });
		this.setBasePath("/home/jorodriguez/Drive/Test/");
		this.setRelativePath("output/era-interim-202306181145");
	}

	@Override
	public int getStartYear() {
		return this.startYear;
	}

	@Override
	public int getEndYear() {
		return this.endYear;
	}

//	@Override
//	public int[] getPressureArray() {
//		return this.pressureArray;
//	}
	
	

	public List<Event> read(int year, int month) throws Exception {
		if (this.startYear <= year && year <= this.endYear) {
			logger.debug("read(" + year + "," + month + ")");
			String yearMonth = year + "" + String.format("%02d", month) + "01";
			List<Event> eventList = this.read(new File(this.getFilePath(yearMonth + seperator + "stack" + seperator
					+ "collection" + seperator + prefix + yearMonth + "." + extension)));
			return eventList;
		}
		return null;
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
									Coordinate coordinate = new Coordinate();
									double latitude = dotEntry.getValue().lat;
									double longitude = dotEntry.getValue().lon;
									coordinate.calendar = this.getCalendar(dotEntry.getValue().startDate,
											dotEntry.getValue().frame);
									int pressure = dotEntry.getValue().gph;
									float vorticity = (float) (dotEntry.getValue().module * Math.pow(10.0, -5.0) * -1);
									if(latitude < 90) {
										coordinate.latitude = (float) latitude;
									} else {
										coordinate.latitude = (float) (latitude - 180);
									}
									if (longitude < 180) {
										coordinate.longitude = (float) longitude;
									} else {
										coordinate.longitude = (float) (longitude - 360);
									}
									coordinate.attribute.put("pressure", pressure);
									coordinate.attribute.put("vorticity", vorticity);
									coordinateList.add(coordinate);
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
//Calendar calendar;
//Integer m = null;
//Integer y = null;
//ListIterator<Event> eventIterator = eventList.listIterator();
//while (eventIterator.hasNext()) {
//	Event e = eventIterator.next();
//	calendar = e.getStartCalendar();
//	m = calendar.get(Calendar.MONTH) + 1;
//	y = calendar.get(Calendar.YEAR);
//	if (year != y || month != m) {
//		eventIterator.remove();
//	}
//}
//public List<Event> read(File file) {
//if(logger.isDebugEnabled()) {
//	MemoryController.log();
//	logger.debug("read(" + file + ")");
//}
//List<Event> eventList = null;
//try {
//	FileInputStream excelFile = new FileInputStream(file);
//	Workbook workbook = new XSSFWorkbook(excelFile);
//	Sheet datatypeSheet = workbook.getSheetAt(0);
//	Iterator<Row> iterator = datatypeSheet.iterator();
//	eventList = new ArrayList<Event>();
//	CycloneEvent event = null;
//	Coordinate point = null;
//	short id = 0;
//	short idBuffer = 0;
//	int level = 0;
//	String date = null;
//	float latitude;
//	float longitude;
//	float vorticity;
//	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	List<Coordinate> pointList = new ArrayList<>();
//	boolean flag = true;
//	while (iterator.hasNext()) {
//		if (!Thread.interrupted()) {
//		Row currentRow = iterator.next();
//		if (currentRow.getRowNum() == 0) {
//			continue; // just skip the rows if row number is 0 or 1
//		}
//		Iterator<Cell> cellIterator = currentRow.iterator();
//		idBuffer = Short.parseShort(cellIterator.next().getStringCellValue());
//		if (flag) {
//			id = idBuffer;
//			flag = false;
//		} else {
//			if (id != idBuffer) {
//				event = new ERAInterimEvent(date + "-" + Integer.toString(id), pointList);
//				eventList.add(event);
//				id = idBuffer;
//				pointList = new ArrayList<>();
//			}
//		}
//		level = Short.parseShort(cellIterator.next().getStringCellValue());
//		date = cellIterator.next().getStringCellValue();
//		latitude = Float.parseFloat(cellIterator.next().getStringCellValue());
//		longitude = Float.parseFloat(cellIterator.next().getStringCellValue());
//		vorticity = (float) cellIterator.next().getNumericCellValue();
//		// Instantiate Point
//		point = new Coordinate();
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(formatter.parse(date));
//		point.calendar = calendar;//formatter.parse(date);
//		point.latitude = latitude;
//		if (longitude < 180) {
//			point.longitude = longitude;
//		} else {
//			point.longitude = longitude - 360;
//		}
//		point.attribute.put("pressure", level);
//		point.attribute.put("vorticity", vorticity);
//		pointList.add(point);
//		} else {
//			break;
//		}
//	}
//	workbook.close();
//	
//	event = new ERAInterimEvent(date + "-" + Integer.toString(id), pointList);
////	Calendar calendar = event.getStartCalendar();
////	Integer month = calendar.get(Calendar.MONTH);
////	Integer year = calendar.get(Calendar.YEAR);
//	eventList.add(event);
//} catch (FileNotFoundException e) {
//	logger.error("FileNotFoundException " + e.getMessage());
//} catch (IOException e) {
//	logger.error("IOException " + e.getMessage());
//} catch (ParseException e) {
//	logger.error("ParseException " + e.getMessage());
//}
////MemoryController.log();
//return eventList;
//}
//this.setBasePath("/home/jorodriguez/Drive/Test/");
//this.setRelativePath("era-interim-north");
//@Override
//public List<CycloneEvent> eventMapGet(int y, int m) {
//	if(this.eventMap == null) {
//		this.eventMap = new HashMap<>();
//	}
//	List<CycloneEvent> eList = this.eventMap.get(y + "" + m);
//	if (eList == null) {
//		eList = (List<CycloneEvent>)this.read(y, m);
//		if (eList != null) {
//			this.eventMap.put(y + "" + m, eList);
//		} else {
//			eList = new ArrayList<>();
//		}
//	}
//	return eList;
//}
//@Override
//public Object get(Calendar calendar) {
//	return this.eventMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}