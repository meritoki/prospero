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
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meritoki.module.library.model.Node;

public class Source extends Node {// implements SourceInterface

	static Logger logger = LogManager.getLogger(Source.class.getName());
	@JsonIgnore
	public static char seperator = File.separatorChar;
	public String basePath = "." + seperator;
	public String relativePath = null;
	public String fileName = null;
	public String timeZone = "GMT-3";
	public boolean calendarFlag;
	public double dimension = 2;
	public int resolution = 1;
	public List<Interval> intervalList;
	public List<Region> regionList;
	private final int startYear = -1;
	private final int endYear = -1;

	public Source() {
		this.filter = false;
	}

	public Source(String name) {
		super(name);
		this.filter = false;
	}

	@JsonIgnore
	public void setBasePath(String basePath) {
		logger.debug("setBasePath(" + basePath + ")");
		this.basePath = basePath;
	}

	@JsonIgnore
	public void setRelativePath(String relativePath) {
		logger.debug("setRelativePath(" + relativePath + ")");
		this.relativePath = relativePath;
	}

	@JsonIgnore
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@JsonIgnore
	public String getPath() {
		return this.basePath + seperator + this.relativePath + seperator;
	}

	@JsonIgnore
	public String getFilePath() {
		return this.getPath() + this.fileName;
	}

	@JsonIgnore
	public String getFilePath(String fileName) {
		return this.getPath() + fileName;
	}

	public List<String> getWildCardFileList(Path path, String pattern) throws IOException {
		List<String> matchList = new ArrayList<String>();
		FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
				FileSystem fileSystem = FileSystems.getDefault();
				PathMatcher pathMatcher = fileSystem.getPathMatcher(pattern);
				Path pathFileName = path.getFileName();
				if (pathMatcher.matches(pathFileName)) {
					matchList.add(pathFileName.toString());
				}
				return FileVisitResult.CONTINUE;
			}
		};
		Files.walkFileTree(path, fileVisitor);
		return matchList;
	}

	@Override
	protected void defaultState(Object object) {
		if (object instanceof Query) {
			Query query = (Query) object;
			try {
				this.query(query);
			} catch (InterruptedException e) {
				logger.warn("InterruptedException " + e.getMessage());
				query.objectList.add(new Result(Mode.EXCEPTION, e.getMessage()));
			} catch (Exception e) {
				logger.warn("Exception " + e.getMessage());
				e.printStackTrace();
				query.objectList.add(new Result(Mode.EXCEPTION, e.getMessage()));
			}
		}
	}

	public void interrupt() {
		logger.info("interrupt()");
		this.thread.interrupt();
	}

	public void query(Query query) throws Exception {
	}

	public int getStartYear() {
		return this.startYear;
	}

	public int getEndYear() {
		return this.endYear;
	}

	public List<String> getDateList(String startDate, String endDate) {
		List<String> dateList = new ArrayList<>();
		DateFormat formater = new SimpleDateFormat("yyyyMMdd");
		Calendar beginCalendar = Calendar.getInstance();
		Calendar finishCalendar = Calendar.getInstance();
		try {
			beginCalendar.setTime(formater.parse(startDate));
			finishCalendar.setTime(formater.parse(endDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		while (beginCalendar.before(finishCalendar)) {
			// add one month to date per loop
			String date = formater.format(beginCalendar.getTime()).toUpperCase();
			beginCalendar.add(Calendar.MONTH, 1);
			dateList.add(date);
		}
		return dateList;
	}

	public int getYearMonthDays(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		int days = calendar.getActualMaximum(Calendar.DATE);
		return days;
	}

	public void unsupportedException(String field, String value) throws Exception {
		if (value != null && !value.isEmpty())
			throw new Exception("source does not support " + field);
	}

	public <T> void unsupportedException(String field, List<T> value) throws Exception {
		if (value != null && value.size() > 0)
			throw new Exception("source does not support " + field);
	}
}
//public int getInteger(String value) throws Exception {
//return Integer.parseInt(value);
//}
//public boolean isAlias(String alias) {
//boolean flag = false;
//alias = alias.toLowerCase();
//switch (alias) {
//case "jja": {
//	flag = true;
//	break;
//}
//case "djf": {
//	flag = true;
//	break;
//}
//case "mam": {
//	flag = true;
//	break;
//}
//case "son": {
//	flag = true;
//	break;
//}
//}
//return flag;
//}
//
//public boolean isMonth(String month) {
//boolean flag = false;
//month = month.toLowerCase();
//switch (month) {
//case "january": {
//	flag = true;
//	break;
//}
//case "february": {
//	flag = true;
//	break;
//}
//case "march": {
//	flag = true;
//	break;
//}
//case "april": {
//	flag = true;
//	break;
//}
//case "may": {
//	flag = true;
//	break;
//}
//case "june": {
//	flag = true;
//	break;
//}
//case "july": {
//	flag = true;
//	break;
//}
//case "august": {
//	flag = true;
//	break;
//}
//case "september": {
//	flag = true;
//	break;
//}
//case "october": {
//	flag = true;
//	break;
//}
//case "november": {
//	flag = true;
//	break;
//}
//case "december": {
//	flag = true;
//	break;
//}
//}
//return flag;
//}

//public Date getDate(String value) {
//Date date = null;
//date = (date == null) ? this.getDate(value, "yyyy/MM/dd HH:mm:ss") : date;
//date = (date == null) ? this.getDate(value, "yyyy/MM/dd") : date;
//date = (date == null) ? this.getDate(value, "HH:mm:ss") : date;
//date = (date == null) ? this.getDate(value, "yyyy/MM") : date;
//date = (date == null) ? this.getDate(value, "yyyy") : date;
//return date;
//}
//
//public Date getDate(String value, String format) {
//Date date = null;
//DateFormat formatter;
//formatter = new SimpleDateFormat(format);
//try {
//	date = formatter.parse(value);
//} catch (ParseException e) {
//	date = null;
//}
//return date;
//}

//public Interval getDateInterval(Date date, String type) {
//Interval interval = null;
//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//cal.setTime(date);
//int year = cal.get(Calendar.YEAR);
//int month = cal.get(Calendar.MONTH) + 1;
//int day = cal.get(Calendar.DAY_OF_MONTH);
//int hour = cal.get(Calendar.HOUR_OF_DAY) - 3;
//switch (type) {
//case "DATE_TIME": {
//	interval = new Interval();
//	interval.startYear = year;
//	interval.startMonth = month;
//	interval.startDay = day;
//	interval.startHour = hour;
//	interval.endYear = year;
//	interval.endMonth = month;
//	interval.endDay = day;
//	interval.endHour = hour;
//	break;
//}
//case "DATE": {
//	interval = new Interval();
//	interval.startYear = year;
//	interval.startMonth = month;
//	interval.startDay = day;
//	interval.endYear = year;
//	interval.endMonth = month;
//	interval.endDay = day;
//	break;
//}
//case "TIME": {
//	interval = new Interval();
//	interval.startHour = hour;
//	interval.endHour = hour;
//	break;
//}
//case "YEAR_MONTH": {
//	interval = new Interval();
//	interval.startYear = year;
//	interval.startMonth = month;
//	interval.startDay = 1;
//	interval.endYear = year;
//	interval.endMonth = month;
//	interval.endDay = cal.getActualMaximum(Calendar.DATE);
//	break;
//}
//case "YEAR": {
//	interval = new Interval();
//	interval.startYear = year;
//	interval.startMonth = 1;
//	interval.startDay = 1;
//	interval.endYear = year;
//	interval.endMonth = 12;
//	interval.endDay = 31;
//	break;
//}
//}
//return interval;
//}

//public String isDate(String value) {
////System.out.println("isDate("+value+")");
//String flag = null;
//flag = (flag == null) ? this.isDate(value, "yyyy/MM/dd HH:mm:ss", "DATE_TIME") : flag;
//flag = (flag == null) ? this.isDate(value, "yyyy/MM/dd", "DATE") : flag;
//flag = (flag == null) ? this.isDate(value, "HH:mm:ss", "TIME") : flag;
//flag = (flag == null) ? this.isDate(value, "yyyy/MM", "YEAR_MONTH") : flag;
//flag = (flag == null) ? this.isDate(value, "yyyy", "YEAR") : flag;
////System.out.println("isDate("+value+") flag="+flag);
//return flag;
//}
//
//public String isDate(String value, String format, String label) {
//String type = null;
//DateFormat formatter;
//formatter = new SimpleDateFormat(format);
//formatter.setLenient(false);
//try {
//	Date date = formatter.parse(value);
//	type = label;
//} catch (ParseException e) {
//	type = null;
//}
//return type;
//}

//public boolean isValidTime(String value) {
//boolean flag = false;
//flag = (!flag) ? Time.isSeason(value) : flag;
//flag = (!flag) ? Time.isMonth(value) : flag;
//flag = (!flag) ? (this.isDate(value) == null) ? false : true : flag;
//return flag;
//}

//public Interval getMonthInterval(String value) {
//Interval interval = null;
//Date date;
//try {
//	date = new SimpleDateFormat("MMMM").parse(value);
//	Calendar cal = Calendar.getInstance();
//	cal.setTime(date);
//	int month = cal.get(Calendar.MONTH) + 1;
//	interval = new Interval();
//	interval.startYear = this.getStartYear();
//	interval.endYear = this.getEndYear();
//	interval.startMonth = month;
//	interval.startDay = 1;
//	interval.endMonth = month;
//	interval.endDay = cal.getActualMaximum(Calendar.DATE);
//	interval.monthFlag = true;
//} catch (ParseException e) {
//	// System.err.println(e.getMessage());
//	interval = null;
//}
//return interval;
//}

//public Interval getAliasInterval(String alias) {
//Interval interval = null;
//alias = alias.toLowerCase();
//switch (alias) {
//case "jja": {
//	interval = new Interval();
//	interval.startYear = this.getStartYear();
//	interval.endYear = this.getEndYear();
//	interval.startMonth = 6;
//	interval.startDay = 1;
//	interval.endMonth = 8;
//	interval.endDay = 31;
//	interval.seasonFlag = true;
//	break;
//}
//case "djf": {
//	interval = new Interval();
//	interval.startYear = this.getStartYear();
//	interval.endYear = this.getEndYear();
//	interval.startMonth = 12;
//	interval.startDay = 1;
//	interval.endMonth = 2;
//	interval.endDay = 28;
//	interval.seasonFlag = true;
//	break;
//}
//case "mam": {
//	interval = new Interval();
//	interval.startYear = this.getStartYear();
//	interval.endYear = this.getEndYear();
//	interval.startMonth = 3;
//	interval.startDay = 1;
//	interval.endMonth = 5;
//	interval.endDay = 31;
//	interval.seasonFlag = true;
//	break;
//}
//case "son": {
//	interval = new Interval();
//	interval.startYear = this.getStartYear();
//	interval.endYear = this.getEndYear();
//	interval.startMonth = 9;
//	interval.startDay = 1;
//	interval.endMonth = 11;
//	interval.endDay = 30;
//	interval.seasonFlag = true;
//	break;
//}
//}
//return interval;
//}
//public Object box(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
//return null;
//}
//
//public List<Frame> frameMapGet(int year, int month) {
//return null;
//}
//public double getDimension(String dimension) throws Exception {
//double d = 2;
//if (dimension != null && !dimension.isEmpty()) {
//	boolean valid = true;
//	try {
//		d = Double.parseDouble(dimension);
//	} catch (NumberFormatException e) {
//		valid = false;
//	}
//	if (d <= 0) {
//		valid = false;
//	}
//	if (!valid) {
//		throw new Exception("invalid dimension format: " + dimension);
//	}
//}
//return d;
//}

//public int getResolution(String resolution) throws Exception {
//int r = 1;
//if (resolution != null && !resolution.isEmpty()) {
//	boolean valid = true;
//	try {
//		r = Integer.parseInt(resolution);
//	} catch (NumberFormatException e) {
//		valid = false;
//	}
//	if (r <= 0 || r > 1000) {
//		valid = false;
//	}
//	if (!valid) {
//		throw new Exception("invalid resolution format: " + resolution);
//	}
//}
//return r;
//}
//@Override
//public Object get() {
//	// TODO Auto-generated method stub
//	return null;
//}
//
//@Override
//public Object get(Calendar calendar) {
//	return null;
//}
//public List<Region> getRegionList(String region) throws Exception {
//List<Region> regionList = null;
//if (region != null && !region.isEmpty()) {
//	regionList = new ArrayList<>();
//	Region r;
//	String[] colonArray;
//	boolean valid = true;
//	if (region.contains("|")) {
//		String[] barArray = region.split("\\|");
//		for (String b : barArray) {
//			if (b.contains(":")) {
//				if (b.lastIndexOf(':') == b.indexOf(':') && b.indexOf(':') != 0
//						&& b.indexOf(':') != b.length() - 1) {
//					colonArray = b.split(":");
//					if (colonArray.length == 2) {
//						String pointA = colonArray[0];
//						String pointB = colonArray[1];
//						String[] pointAArray = pointA.split(",");
//						String[] pointBArray = pointB.split(",");
//						if (pointAArray.length == 2 && pointBArray.length == 2) {
//							r = new Region();
//							try {
//								r.latitudeA = Double.parseDouble(pointAArray[0]);
//								r.longitudeA = Double.parseDouble(pointAArray[1]);
//								r.latitudeB = Double.parseDouble(pointBArray[0]);
//								r.longitudeB = Double.parseDouble(pointBArray[1]);
//								regionList.add(r);
//							} catch (NumberFormatException e) {
//								logger.error("NumberFormatException "+e.getMessage());
//								valid = false;
//							}
//						}
//					} else {
//						valid = false;
//					}
//				} else {
//					valid = false;
//				}
//			} else {
//				valid = false;
//			}
//		}
//	} else {
//		String b = region;
//		if (b.contains(":")) {
//			if (b.lastIndexOf(':') == b.indexOf(':') && b.indexOf(':') != 0
//					&& b.indexOf(':') != b.length() - 1) {
//				colonArray = b.split(":");
//				if (colonArray.length == 2) {
//					String pointA = colonArray[0];
//					String pointB = colonArray[1];
//					String[] pointAArray = pointA.split(",");
//					String[] pointBArray = pointB.split(",");
//					if (pointAArray.length == 2 && pointBArray.length == 2) {
//						r = new Region();
//						try {
//							r.latitudeA = Double.parseDouble(pointAArray[0]);
//							r.longitudeA = Double.parseDouble(pointAArray[1]);
//							r.latitudeB = Double.parseDouble(pointBArray[0]);
//							r.longitudeB = Double.parseDouble(pointBArray[1]);
//							regionList.add(r);
//						} catch (NumberFormatException e) {
//							valid = false;
//						}
//					}
//				} else {
//					valid = false;
//				}
//			} else {
//				valid = false;
//			}
//		} else {
//			valid = false;
//		}
//	}
//	if (!valid) {
//		throw new Exception("invalid region format: " + region);
//	}
//}
//return regionList;
//}
