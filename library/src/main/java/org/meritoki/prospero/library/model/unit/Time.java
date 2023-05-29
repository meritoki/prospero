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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Would like Time to be the object that represents time where ever Calendar is
 * not used An object to pass times and parse them from Strings.
 */
public class Time {

	public static void main(String[] args) {
//		Date date = Time.getDate("djf");
		Interval i = Time.getAliasInterval("all", 2000, 2005);
		try {
			System.out.println(Time.getTimeList(1, i));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static Logger logger = LogManager.getLogger(Time.class.getName());
	public static String defaultTimeFormat = "yyyy-MM-dd HH:mm:ss";
	@JsonProperty
	public int year = -1;
	@JsonProperty
	public int month = -1;
	@JsonProperty
	public int day = -1;
	@JsonProperty
	public int hour = -1;
	@JsonProperty
	public int minute = -1;
	@JsonProperty
	public int second = -1;
	@JsonProperty
	public boolean flag;
	@JsonProperty
	private int hashCode;// hashCode fucks up because we initialize the variables publically and not with
							// the constructor
	@JsonIgnore
	public Calendar calendar;

	public Time() {
		this.hashCode = Objects.hash(this.year, this.month, this.day, this.hour, this.minute, this.second);
	}

	// Constructor that does all validation of String input
	public Time(String time) {

	}

	public Time(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.hashCode = Objects.hash(this.year, this.month, this.day, this.hour, this.minute, this.second);
	}

	/**
	 * Initialize Time Object with representation limit: year, month, day, hour,
	 * minute, second Depending on limit, Time retains more specific information.
	 * 
	 * @param value
	 * @param calendar
	 */
	public Time(String value, Calendar calendar) {
		if (value != null) {
			switch (value) {
			case "second": {
				second = calendar.get(Calendar.SECOND);
				minute = calendar.get(Calendar.MINUTE);
				hour = calendar.get(Calendar.HOUR_OF_DAY);
				day = calendar.get(Calendar.DAY_OF_MONTH);
				month = calendar.get(Calendar.MONTH) + 1;
				year = calendar.get(Calendar.YEAR);
			}
			case "minute": {
				minute = calendar.get(Calendar.MINUTE);
				hour = calendar.get(Calendar.HOUR_OF_DAY);
				day = calendar.get(Calendar.DAY_OF_MONTH);
				month = calendar.get(Calendar.MONTH) + 1;
				year = calendar.get(Calendar.YEAR);
			}
			case "hour": {
				hour = calendar.get(Calendar.HOUR_OF_DAY);
				day = calendar.get(Calendar.DAY_OF_MONTH);
				month = calendar.get(Calendar.MONTH) + 1;
				year = calendar.get(Calendar.YEAR);
				break;
			}
			case "day": {
				day = calendar.get(Calendar.DAY_OF_MONTH);
				month = calendar.get(Calendar.MONTH) + 1;
				year = calendar.get(Calendar.YEAR);
				break;
			}
			case "month": {
				month = calendar.get(Calendar.MONTH) + 1;
				year = calendar.get(Calendar.YEAR);
				break;
			}
			case "year": {
				year = calendar.get(Calendar.YEAR);
				break;
			}
			}
		}

	}

	/**
	 * 20230430 Problem w/ Hour Conditional Deprecate, Convert to Calendar and Use
	 * Before, After, and Equals
	 * 
	 * @param time
	 * @return
	 */
	public boolean lessThan(Time time) {

		boolean flag = true;
		if (flag && this.year != -1 && time.year != -1) {
			flag = this.year <= time.year;
		}
		if (flag && this.month != -1 && time.month != -1) {
			flag = this.month <= time.month;
		}
		if (flag && this.day != -1 && time.day != -1) {
			flag = this.day <= time.day;
		}
		if (flag && this.hour != -1 && time.hour != -1) {
			flag = this.hour <= time.hour;
		}
//		logger.info(this+".lessThan("+time+") flag="+flag);
		return flag;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Time that = (Time) o;
//		logger.info(this+".equals("+that+")");
		return this.year == that.year && this.month == that.month && this.day == that.day && this.hour == that.hour
				&& this.minute == that.minute && this.second == that.second;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	/**
	 * 20230417 The smallest index grouped by day thinking about adding grouping by
	 * hour and minute for some goes data
	 * 
	 * @return
	 */
	@JsonIgnore
	public Index getIndex() {
		Index index = new Index();
		Integer year = (this.year != -1) ? this.year : null;
		Integer month = (this.month != -1) ? this.month : null;
		Integer day = (this.day != -1) ? this.day : null;
//		logger.info("getIndex() year=" + year + " month=" + month + " day=" + day);
		index.startCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0, (day != null) ? day : 0, 24,
				0, 0);
		if (year != null) {
			if (month != null) {
				if (day != null) {
					index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
							(day != null) ? day : 0, 24, 0, 0);
				} else {
					// handle to end of month
					YearMonth yearMonthObject = YearMonth.of(year, month);
					day = yearMonthObject.lengthOfMonth();
				}
				index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
						(day != null) ? day : 0, 24, 0, 0);
			} else {
				// handle to end of year
				month = 12;
				YearMonth yearMonthObject = YearMonth.of(year, month);
				day = yearMonthObject.lengthOfMonth();
				index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
						(day != null) ? day : 0, 24, 0, 0);
			}
		}
		return index;
	}

	/**
	 * Non-functional. Goal is to modify to return Object List, if empty do nothing.
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isValid(String time) {
		boolean valid = true;
		List<Object> list = new ArrayList<>();
		String[] timeArray = new String[1];
		if (time.contains(",")) {
			timeArray = time.split(",");
		} else {
			timeArray[0] = time;
		}
		for (String t : timeArray) {
			if (t.contains("-")) {
				if (t.lastIndexOf('-') == t.indexOf('-') && t.indexOf('-') != 0 && t.indexOf('-') != t.length() - 1) {
					String[] dashArray = t.split("-");
					String[] range = new String[2];
					String d = dashArray[0];
					range[0] = d;
					valid = (valid) ? Time.isTime(d) : valid;
					d = dashArray[1];
					valid = (valid) ? Time.isTime(d) : valid;
					range[1] = d;
					list.add(range);
				} else {
					valid = false;
				}
			} else {
				valid = (valid) ? Time.isTime(t) : valid;
				list.add(t);
			}
		}
		return valid;
	}

	public static List<Interval> getIntervalList(String time, Time start, Time end) throws Exception {
		List<Interval> intervalList = null;
		if (time != null && !time.isEmpty()) {
			Interval interval;
			boolean valid = true;
			List<Object> list = new ArrayList<>();
			String[] timeArray = new String[1];
			if (time.contains(",")) {
				timeArray = time.split(",");
			} else {
				timeArray[0] = time;
			}
			for (String t : timeArray) {
				if (t.contains("-")) {
					if (t.lastIndexOf('-') == t.indexOf('-') && t.indexOf('-') != 0
							&& t.indexOf('-') != t.length() - 1) {
						String[] dashArray = t.split("-");
						String[] range = new String[2];
						String d = dashArray[0];
						range[0] = d;
						valid = (valid) ? Time.isTime(d) : valid;
						d = dashArray[1];
						valid = (valid) ? Time.isTime(d) : valid;
						range[1] = d;
						list.add(range);
					} else {
						valid = false;
					}
				} else {
					valid = (valid) ? Time.isTime(t) : valid;
					list.add(t);
				}
			}
			if (valid) {
				intervalList = new ArrayList<>();
				for (Object o : list) {
					if (o instanceof String) {// Time
						String s = (String) o;
						s = s.toLowerCase();
						if (Time.isAlias(s)) {
							interval = Time.getAliasInterval(s, start.year, end.year);
							if (interval != null) {
								intervalList.add(interval);
							}
						} else if (Time.isMonth(s)) {
							interval = Time.getMonthInterval(s, start.year, end.year);
							if (interval != null) {
								intervalList.add(interval);
							}
						} else if (Time.isDate(s) != null) {
							String type = Time.isDate(s);
							interval = Time.getDateInterval(Time.getDate(s), type);
							if (interval != null) {
								intervalList.add(interval);
							}
						}
					} else if (o instanceof String[]) {// Time Range
						String[] range = (String[]) o;
						String a = range[0];
						String b = range[1];
						if (Time.isAlias(a) && Time.isAlias(b)) {
							Interval intervalA = Time.getAliasInterval(a, start.year, end.year);
							Interval intervalB = Time.getAliasInterval(b, start.year, end.year);
							interval = new Interval();
							interval.start.month = intervalA.start.month;
							interval.start.day = intervalA.start.day;
							interval.end.month = intervalB.end.month;
							interval.end.day = intervalB.end.day;
							intervalList.add(interval);
						} else if (Time.isMonth(a) && Time.isMonth(b)) {
							Interval intervalA = Time.getMonthInterval(a, start.year, end.year);
							Interval intervalB = Time.getMonthInterval(b, start.year, end.year);
							interval = new Interval();
							interval.start.month = intervalA.start.month;
							interval.start.day = intervalA.start.day;
							interval.end.month = intervalB.end.month;
							interval.end.day = intervalB.end.day;
							intervalList.add(interval);
						} else if (Time.isDate(a) != null && Time.isDate(b) != null) {
							String typeA = Time.isDate(a);
							String typeB = Time.isDate(b);
							Interval intervalA = Time.getDateInterval(Time.getDate(a), typeA);
							Interval intervalB = Time.getDateInterval(Time.getDate(b), typeB);
							interval = new Interval();
							interval.start.year = intervalA.start.year;
							interval.start.month = intervalA.start.month;
							interval.start.day = intervalA.start.day;
							interval.start.hour = intervalA.start.hour;
							interval.start.minute = intervalA.start.minute;
							interval.end.year = intervalB.end.year;
							interval.end.month = intervalB.end.month;
							interval.end.day = intervalB.end.day;
							interval.end.hour = intervalB.end.hour;
							interval.end.minute = intervalA.end.minute;
							intervalList.add(interval);
						}
					}
				}
			} else {
				throw new Exception("invalid time format: " + time);
			}
		}
		return intervalList;
	}

	/**
	 * Solved a Core Problem, where an Interval may require independent depths
	 * <ul>
	 * <li>0 - Year</li>
	 * <li>1 - Month</li>
	 * <li>2 - Day</li>
	 * </ul>
	 * 
	 * @param depth
	 * @param i
	 * @return List
	 * @throws Exception
	 */
	public static List<Time> getTimeList(int depth, Interval i) throws Exception {
		List<Time> timeList = new ArrayList<>();
		Time time = null;
		Time start = i.start;
		Time end = i.end;
		Time t = i.getTime();
		if (t != null) {
			timeList.add(t);
		} else {
			if (depth >= 0) {
				for (int y = start.year; y <= end.year; y++) {
					if (depth >= 1) {
						if (start.month <= end.month) {
							for (int m = start.month; m <= end.month; m++) {
								if (depth >= 2) {
									int A = 1;
									int B = getYearMonthDays(y, m);
									if (m == start.month) {
										A = start.day;
									}
									if (m == end.month) {
										B = end.day;
									}
									for (int d = A; d <= B; d++) {
										time = new Time();
										time.year = y;
										time.month = m;
										time.day = d;
										timeList.add(time);
									}
								} else {
									time = new Time();
									time.year = y;
									time.month = m;
									timeList.add(time);
								}
							}
						} else {
							for (int m = 1; m <= end.month; m++) {
								if (depth >= 2) {
									int A = 1;
									int B = getYearMonthDays(y, m);
									if (m == start.month) {
										A = start.day;
									}
									if (m == end.month) {
										B = end.day;
									}
									for (int d = A; d <= B; d++) {
										time = new Time();
										time.year = y;
										time.month = m;
										time.day = d;
										timeList.add(time);
									}
								} else {
									time = new Time();
									time.year = y;
									time.month = m;
									timeList.add(time);
								}
							}
							for (int m = start.month; m <= 12; m++) {
								if (depth >= 2) {
									int A = 1;
									int B = getYearMonthDays(y, m);
									if (m == start.month) {
										A = start.day;
									}
									if (m == end.month) {
										B = end.day;
									}
									for (int d = A; d <= B; d++) {
										time = new Time();
										time.year = y;
										time.month = m;
										time.day = d;
										timeList.add(time);
									}
								} else {
									time = new Time();
									time.year = y;
									time.month = m;
									timeList.add(time);
								}
							}
						}
					} else {
						time = new Time();
						time.year = y;
						timeList.add(time);
					}
				}
			}
		}
		return timeList;
	}

	public static boolean isTime(String value) {
		boolean flag = false;
		flag = (!flag) ? Time.isAlias(value) : flag;
		flag = (!flag) ? Time.isMonth(value) : flag;
		flag = (!flag) ? (Time.isDate(value) == null) ? false : true : flag;
		return flag;
	}

	/**
	 * Function used to check if input is valid, see isValidTime Method
	 * 
	 * @param value
	 * @return
	 */
	public static String isDate(String value) {
		String type = null;
		type = (type == null) ? Time.isDate(value, "yyyy/MM/dd HH:mm:ss", "DATE_TIME") : type;
		type = (type == null) ? Time.isDate(value, "yyyy/MM/dd HH:mm", "DATE_MINUTE") : type;
		type = (type == null) ? Time.isDate(value, "yyyy/MM/dd HH", "DATE_HOUR") : type;
		type = (type == null) ? Time.isDate(value, "yyyy/MM/dd", "DATE") : type;
		type = (type == null) ? Time.isDate(value, "HH:mm:ss", "TIME") : type;
		type = (type == null) ? Time.isDate(value, "HH:mm", "MINUTE") : type;
		type = (type == null) ? Time.isDate(value, "HH", "HOUR") : type;
		type = (type == null) ? Time.isDate(value, "yyyy/MM", "YEAR_MONTH") : type;
		type = (type == null) ? Time.isDate(value, "yyyy", "YEAR") : type;
//		System.out.println("isDate("+value+") flag="+flag);
		return type;
	}

	public static String isDate(String value, String format, String label) {
		String type = null;
		DateFormat formatter;
		formatter = new SimpleDateFormat(format);
		formatter.setLenient(false);
		try {
			Date date = formatter.parse(value);
			type = label;
		} catch (ParseException e) {
			type = null;
		}
		return type;
	}

	public static boolean isMonth(String month) {
		boolean flag = false;
		if(month != null) {
		month = month.toLowerCase();
		switch (month) {
		case "january": {
			flag = true;
			break;
		}
		case "february": {
			flag = true;
			break;
		}
		case "march": {
			flag = true;
			break;
		}
		case "april": {
			flag = true;
			break;
		}
		case "may": {
			flag = true;
			break;
		}
		case "june": {
			flag = true;
			break;
		}
		case "july": {
			flag = true;
			break;
		}
		case "august": {
			flag = true;
			break;
		}
		case "september": {
			flag = true;
			break;
		}
		case "october": {
			flag = true;
			break;
		}
		case "november": {
			flag = true;
			break;
		}
		case "december": {
			flag = true;
			break;
		}
		}
		}
		return flag;
	}

	public static boolean isAlias(String alias) {
		boolean flag = false;
		if (alias != null) {
			alias = alias.toLowerCase();
			switch (alias) {
			case "all": {
				flag = true;
				break;
			}
			case "jja": {
				flag = true;
				break;
			}
			case "djf": {
				flag = true;
				break;
			}
			case "mam": {
				flag = true;
				break;
			}
			case "son": {
				flag = true;
				break;
			}
			}
		}
		return flag;
	}

	public static Interval getDateInterval(Date date, String type) {
		Interval interval = null;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY) - 3;
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		switch (type) {
		case "DATE_TIME": {
			interval = new Interval();
			interval.start.year = year;
			interval.start.month = month;
			interval.start.day = day;
			interval.start.hour = hour;
			interval.start.minute = minute;
			interval.start.second = second;
			interval.end.year = year;
			interval.end.month = month;
			interval.end.day = day;
			interval.end.hour = hour;
			interval.end.minute = minute;
			interval.end.second = second;
			break;
		}
		case "DATE_MINUTE": {
			interval = new Interval();
			interval.start.year = year;
			interval.start.month = month;
			interval.start.day = day;
			interval.start.hour = hour;
			interval.start.minute = minute;
			interval.end.year = year;
			interval.end.month = month;
			interval.end.day = day;
			interval.end.hour = hour;
			interval.end.minute = minute;
			break;
		}
		case "DATE_HOUR": {
			interval = new Interval();
			interval.start.year = year;
			interval.start.month = month;
			interval.start.day = day;
			interval.start.hour = hour;
			interval.end.year = year;
			interval.end.month = month;
			interval.end.day = day;
			interval.end.hour = hour;
			break;
		}
		case "DATE": {
			interval = new Interval();
			interval.start.year = year;
			interval.start.month = month;
			interval.start.day = day;
			interval.end.year = year;
			interval.end.month = month;
			interval.end.day = day;
			break;
		}
		case "TIME": {
			interval = new Interval();
			interval.start.hour = hour;
			interval.start.minute = minute;
			interval.start.second = second;
			interval.end.hour = hour;
			interval.end.minute = minute;
			interval.end.second = second;
			break;
		}
		case "YEAR_MONTH": {
			interval = new Interval();
			interval.start.year = year;
			interval.start.month = month;
			interval.start.day = day;
			interval.end.year = year;
			interval.end.month = month;
			interval.end.day = cal.getActualMaximum(Calendar.DATE);
			break;
		}
		case "YEAR": {
			interval = new Interval();
			interval.start.year = year;
			interval.start.month = 1;
			interval.start.day = 1;
			interval.end.year = year;
			interval.end.month = 12;
			interval.end.day = 31;
			break;
		}
		}
		return interval;
	}

	public static Interval getMonthInterval(String value, int startYear, int endYear) {
		Interval interval = null;
		Date date;
		try {
			date = new SimpleDateFormat("MMMM").parse(value);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH) + 1;
			interval = new Interval();
			interval.start.year = startYear;// this.getStartYear();
			interval.end.year = endYear;// this.getEndYear();
			interval.start.month = month;
			interval.start.day = 1;
			interval.end.month = month;
			interval.end.day = cal.getActualMaximum(Calendar.DATE);
//			interval.monthFlag = true;
		} catch (ParseException e) {
			// System.err.println(e.getMessage());
			interval = null;
		}
		return interval;
	}

	public static Interval getAliasInterval(String alias, int startYear, int endYear) {
		Interval interval = null;
		alias = alias.toLowerCase();
		switch (alias) {
		case "all": {
			interval = new Interval();
			interval.start.year = startYear;
			interval.end.year = endYear;
			interval.start.month = 1;
			interval.start.day = 1;
			interval.end.month = 12;
			interval.end.day = 31;
//			interval.allFlag = true;
			interval.alias = alias;
			break;
		}
		case "jja": {
			interval = new Interval();
			interval.start.year = startYear;// this.getStartYear();
			interval.end.year = endYear;// this.getEndYear();
			interval.start.month = 6;
			interval.start.day = 1;
			interval.end.month = 8;
			interval.end.day = 31;
//			interval.seasonFlag = true;
			interval.alias = alias;
			break;
		}
		case "djf": {
			interval = new Interval();
			interval.start.year = startYear;// this.getStartYear();
			interval.end.year = endYear;// this.getEndYear();
			interval.start.month = 12;
			interval.start.day = 1;
			interval.end.month = 2;
			interval.end.day = 28;
//			interval.seasonFlag = true;
			interval.alias = alias;
			break;
		}
		case "mam": {
			interval = new Interval();
			interval.start.year = startYear;// this.getStartYear();
			interval.end.year = endYear;// this.getEndYear();
			interval.start.month = 3;
			interval.start.day = 1;
			interval.end.month = 5;
			interval.end.day = 31;
//			interval.seasonFlag = true;
			interval.alias = alias;
			break;
		}
		case "son": {
			interval = new Interval();
			interval.start.year = startYear;// this.getStartYear();
			interval.end.year = endYear;// this.getEndYear();
			interval.start.month = 9;
			interval.start.day = 1;
			interval.end.month = 11;
			interval.end.day = 30;
//			interval.seasonFlag = true;
			interval.alias = alias;
			break;
		}
		}
		return interval;
	}

	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, this.year);
		calendar.set(Calendar.MONTH, this.month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, this.day);
		calendar.set(Calendar.HOUR_OF_DAY, (this.hour != -1) ? this.hour : 0);
		calendar.set(Calendar.MINUTE, (this.minute != -1) ? this.minute : 0);
		calendar.set(Calendar.SECOND, (this.second != -1) ? this.second : 0);
		return calendar;
	}

	public GregorianCalendar getCalendar(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	public static GregorianCalendar getCalendar(String format, String time) {
		GregorianCalendar calendar = new GregorianCalendar();
		Date date = Time.getDate(format, time);
		if (date != null)
			calendar.setTime(date);
		return calendar;
	}

	/**
	 * Function can invoke Get Date with any valid Format String to instantiate a
	 * Date.
	 * 
	 * @param value
	 * @return
	 */
	public static Date getDate(String value) {
		Date date = null;
		date = (date == null) ? Time.getDate(value, "yyyy/MM/dd HH:mm:ss") : date;
		date = (date == null) ? Time.getDate(value, "yyyy/MM/dd HH:mm") : date;
		date = (date == null) ? Time.getDate(value, "yyyy/MM/dd HH") : date;
		date = (date == null) ? Time.getDate(value, "yyyy/MM/dd") : date;
		date = (date == null) ? Time.getDate(value, "yyyy/MM") : date;
		date = (date == null) ? Time.getDate(value, "yyyy") : date;
		date = (date == null) ? Time.getDate(value, "HH:mm:ss") : date;
		date = (date == null) ? new Date() : date;
		return date;
	}

	public static Date getDate(String value, String format) {
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat((format == null) ? defaultTimeFormat : format);
		;
		try {
			date = formatter.parse(value);
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}

	public static String getDateString(String format, Date date) {
		String string = new SimpleDateFormat((format == null) ? defaultTimeFormat : format).format(date);
		return string;
	}

	public static String getDateFormattedString(String type, Date date) {
		String string = null;
		switch (type) {
		case "DATE_TIME": {
			break;
		}
		case "DATE_MINUTE": {
			break;
		}
		case "DATE_HOUR": {
			break;
		}
		}
		return string;
	}

	public static String getCalendarString(String format, Calendar calendar) {
		return getDateString(format, calendar.getTime());
	}

	public static int getDayOfYear(int year, int month, int day) {
//		logger.info("getDayOfYear("+year+","+month+","+day+")");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		if (day != -1) {
			calendar.set(Calendar.DAY_OF_MONTH, day);
		} else {
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		}
		int d = calendar.get(Calendar.DAY_OF_YEAR);
//		logger.info("getDayOfYear("+year+","+month+","+day+") d="+d);
		return d;
	}

	public static int getYearMonthDays(int year, int month) {
		System.out.println("getYearMonthDays(" + year + "," + month + ")");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, 1);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days;
	}

	public static Date getNineteenHundredJanuaryFirstDate(int hours) {
		GregorianCalendar g = new GregorianCalendar(1900, 0, 1, -1, 0, 0);
		g.add(Calendar.HOUR, hours); // adds one hour
		g.set(Calendar.MINUTE, 0);
		g.set(Calendar.SECOND, 0);
		g.set(Calendar.MILLISECOND, 0);
		return g.getTime();
	}

	public static Date getTwoThousandJanuaryFirstDate(int seconds) {
//		logger.info("getTwoThousandJanuaryFirstDate("+seconds+")");
		GregorianCalendar g = new GregorianCalendar(2000, 0, 1, -1, 0, 0);
		g.add(Calendar.HOUR, 0); // adds one hour
		g.set(Calendar.MINUTE, 0);
		g.set(Calendar.SECOND, seconds);
		g.set(Calendar.MILLISECOND, 0);
		return g.getTime();
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();// .withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}
//public static List<Time> getTimeList(Interval i) throws Exception {
////logger.info("load(query, " + i + ")");
//List<Time> timeList = new ArrayList<>();
//int startYear = i.startYear;
//int startMonth = (i.startMonth == -1) ? 1 : i.startMonth;
//Integer startDay = (i.startDay == -1) ? null : i.startDay;
//int endYear = i.endYear;
//int endMonth = (i.endMonth == -1) ? 12 : i.endMonth;
//Integer endDay = (i.endDay == -1) ? null : i.endDay;
//Time t = i.getTime();
//if(t != null) {
//timeList.add(t);
//} else if (i.allFlag) {
////logger.info("load(...) allFlag=" + i.allFlag);
//for (int y = startYear; y <= endYear; y++) {
//	for (int m = startMonth; m <= endMonth; m++) {
//		// y && m
//		Time time = new Time();
//		time.year = y;
//		time.month = m;
//		timeList.add(time);
//	}
//}
//} else if (i.startYear != -1 && i.endYear != -1) {
//int yearDifference = endYear - startYear - 1;
//if (yearDifference == -1) { // easiest case same year just iterate over months and done
//	// same year
//	for (int y = startYear; y <= endYear; y++) {
//		for (int m = startMonth; m <= endMonth; m++) {
//			// y && m
//			Time time = new Time();
//			time.year = y;
//			time.month = m;
//			timeList.add(time);
//		}
//	}
//} else {
//	// different years
//	// all range, season, month queries end up here
//	// in order for the single count method to work for DJF,
//	// we have to read DJF in order.
//	if (i.seasonFlag || i.monthFlag) {
//		if (startMonth <= endMonth) {
//			for (int y = startYear; y <= endYear; y++) {
//
//				for (int m = startMonth; m <= endMonth; m++) {
//					// y && m
//					Time time = new Time();
//					time.year = y;
//					time.month = m;
//					timeList.add(time);
//				}
//			}
//		} else {
//			for (int y = startYear; y <= endYear; y++) {
//
//				for (int m = 1; m <= endMonth; m++) {
//					// y && m
//					Time time = new Time();
//					time.year = y;
//					time.month = m;
//					timeList.add(time);
//				}
//				for (int m = startMonth; m <= 12; m++) {
//					// y && m
//					Time time = new Time();
//					time.year = y;
//					time.month = m;
//					timeList.add(time);
//				}
//			}
//		}
//	} else {
//		// this code does not work, it may have never worked
//		// update looks like this code works and Interval contains point is failing
//		for (int m = startMonth; m <= 12; m++) {
//			// startYear && m
//			Time time = new Time();
//			time.year = startYear;
//			time.month = m;
//			timeList.add(time);
//		}
//		for (int y = startYear + 1; y <= endYear - 1; y++) {// skipped is years are consecutive
//			for (int m = 1; m <= 12; m++) {
//				// y && m
//				Time time = new Time();
//				time.year = y;
//				time.month = m;
//				timeList.add(time);
//			}
//		}
//		for (int m = 1; m <= endMonth; m++) {
//			// endYear && m
//			Time time = new Time();
//			time.year = endYear;
//			time.month = m;
//			timeList.add(time);
//		}
//	}
//}
//} else {
//throw new Exception("invalid interval, start and end year initialized");
//}
//return timeList;
//}
//interval.startMonth = intervalA.startMonth;
//interval.startDay = intervalA.startDay;
//interval.endMonth = intervalB.endMonth;
//interval.endDay = intervalB.endDay;
//interval.startMonth = intervalA.startMonth;
//interval.startDay = intervalA.startDay;
//interval.endMonth = intervalB.endMonth;
//interval.endDay = intervalB.endDay;
//interval.startYear = intervalA.startYear;
//interval.startMonth = intervalA.startMonth;
//interval.startDay = intervalA.startDay;
//interval.startHour = intervalA.startHour;
//interval.startMinute = intervalA.startMinute;
//interval.endYear = intervalB.endYear;
//interval.endMonth = intervalB.endMonth;
//interval.endDay = intervalB.endDay;
//interval.endHour = intervalB.endHour;
//interval.endMinute = intervalA.endMinute;
//public String toString() {
//return ((year != -1) ? year : "") + ((month != -1) ? String.format("%02d", month) : "")
//		+ ((day != -1) ? String.format("%02d", day) : "")
//		+ ((hour != -1) ? String.format("%02d", hour) : "");
//}
//else if(i.dayFlag) {
//	if (startMonth <= endMonth) {
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= endMonth; m++) {
//				// y && m
//				if(startDay != null && endDay != null) {
//					int A = 1;
//					int B = getYearMonthDays(y,m);
//					if(m == startMonth) { 
//						A = startDay;
//					}
//					if(m == endMonth) {
//						B = endDay;
//					}
//					for(int d = A;d<=B;d++) {
//						Time time = new Time();
//						time.year = y;
//						time.month = m;
//						time.day = d;
//						timeList.add(time);
//					}
//				} else {
//					Time time = new Time();
//					time.year = y;
//					time.month = m;
//					timeList.add(time);
//				}
//			}
//		}
//	}
//} 
//if(s.equals("all")) {
//interval = Time.getAliasInterval(s,startYear,endYear);
//if (interval != null) {
//	interval.allFlag = true;
//	intervalList.add(interval);
//}
//} else 
//} else if (time.contains("-")) {
//if (time.lastIndexOf('-') == time.indexOf('-') && time.indexOf('-') != 0
//		&& time.indexOf('-') != time.length() - 1) {
//	dashArray = time.split("-");
//	String[] range = new String[2];
//	String d = dashArray[0];
//	range[0] = d;
//	valid = (valid) ? Time.isValidTime(d) : valid;
//	d = dashArray[1];
//	valid = (valid) ? Time.isValidTime(d) : valid;
//	range[1] = d;
//	list.add(range);
//} else {
//	valid = false;
//}
//} else {
//valid = (valid) ? Time.isValidTime(time) : valid;
//list.add(time);
//}