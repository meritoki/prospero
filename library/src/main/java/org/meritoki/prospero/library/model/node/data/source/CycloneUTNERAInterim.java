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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.ERAInterimEvent;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.memory.MemoryController;

public class CycloneUTNERAInterim extends CycloneSource {

	static Logger logger = LoggerFactory.getLogger(CycloneUTNERAInterim.class.getName());
//	public static String path = basePath+"prospero-data" + seperator + "UTN" + seperator + "File"+ seperator +"Data"+ seperator +"Cyclone"+ seperator +"cyclone-20200704";
//	public static String prefix = "100-125-150-200-250-300-400-500-600-700-850-925-";
	public static String extension = "xlsx";
	public int[] pressureArray = { 100, 125, 150, 200, 250, 300, 400, 500, 600, 700, 850, 925 };
	private final int startYear = 2001;
	private final int endYear = 2017;
	
	public CycloneUTNERAInterim() {
		super();
		this.setPressureArray(ERAInterimEvent.pressureArray);
		this.setRelativePath("UTN" + seperator + "File"+ seperator +"Data"+ seperator +"Cyclone"+ seperator +"cyclone-20200704"+seperator);
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
	
	public List<Event> read(int year, int month) {
		if(this.startYear <= year && year <= this.endYear) {
			logger.debug("read(" + year + "," + month + ")");
			this.setPrefix(this.getPressureString());
			String fileName = this.getFilePath(this.getPrefix() + "-" + year + "" + String.format("%02d", month) + "01" + "." + extension);
			List<Event> eventList = this.read(new File(fileName));
//			Calendar calendar;
//			Integer m = null;
//			Integer y = null;
//			ListIterator<Event> eventIterator = eventList.listIterator();
//			while(eventIterator.hasNext()) {
//				Event e = eventIterator.next();
//				calendar = e.getStartCalendar();
//				m = calendar.get(Calendar.MONTH)+1;
//				y = calendar.get(Calendar.YEAR);
//				if(year != y || month != m) {
////					logger.info("read(" + year + "," + month + ") y="+year+" m="+m);
//					eventIterator.remove();
//				}
//			}
			return eventList;
		}
		return null;
	}

	public List<Event> read(File file) {
		if(logger.isDebugEnabled()) {
			MemoryController.log();
			logger.debug("read(" + file + ")");
		}
		List<Event> eventList = null;
		try {
			FileInputStream excelFile = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			eventList = new ArrayList<Event>();
			CycloneEvent event = null;
			Coordinate coordinate = null;
			short id = 0;
			short idBuffer = 0;
			int level = 0;
			String date = null;
			float latitude;
			float longitude;
			float vorticity;
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Coordinate> coordinateList = new ArrayList<>();
			boolean flag = true;
			while (iterator.hasNext()) {
				if (!Thread.interrupted()) {
				Row currentRow = iterator.next();
				if (currentRow.getRowNum() == 0) {
					continue; // just skip the rows if row number is 0 or 1
				}
				Iterator<Cell> cellIterator = currentRow.iterator();
				idBuffer = Short.parseShort(cellIterator.next().getStringCellValue());
				if (flag) {
					id = idBuffer;
					flag = false;
				} else {
					if (id != idBuffer) {
						event = new ERAInterimEvent(coordinateList);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						event.id = sdf.format(event.getStartCalendar().getTime()) + "-"
								+ sdf.format(event.getEndCalendar().getTime()) + id;
						eventList.add(event);
						id = idBuffer;
						coordinateList = new ArrayList<>();
					}
				}
				level = Short.parseShort(cellIterator.next().getStringCellValue());
				date = cellIterator.next().getStringCellValue();
				latitude = Float.parseFloat(cellIterator.next().getStringCellValue());
				longitude = Float.parseFloat(cellIterator.next().getStringCellValue());
				vorticity = (float) cellIterator.next().getNumericCellValue();
				// Instantiate Point
				coordinate = new Coordinate();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(formatter.parse(date));
				coordinate.calendar = calendar;//formatter.parse(date);
				coordinate.latitude = latitude;
				if (longitude <= 180) {
					coordinate.longitude = longitude;
				} else {
					coordinate.longitude = longitude - 360;
				}
				coordinate.attribute.put("pressure", level);
				coordinate.attribute.put("vorticity", vorticity);
				coordinateList.add(coordinate);
				} else {
					break;
				}
			}
			workbook.close();
			
			event = new ERAInterimEvent(coordinateList);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			event.id = sdf.format(event.getStartCalendar().getTime()) + "-"
					+ sdf.format(event.getEndCalendar().getTime()) + id;
			eventList.add(event);
//			Calendar calendar = event.getStartCalendar();
//			Integer month = calendar.get(Calendar.MONTH);
//			Integer year = calendar.get(Calendar.YEAR);
//			eventList.add(event);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException " + e.getMessage());
		} catch (IOException e) {
			logger.error("IOException " + e.getMessage());
		} catch (ParseException e) {
			logger.error("ParseException " + e.getMessage());
		}
//		MemoryController.log();
		return eventList;
	}
}
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