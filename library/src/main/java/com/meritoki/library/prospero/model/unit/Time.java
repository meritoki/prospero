package com.meritoki.library.prospero.model.unit;

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

public class Time {

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
	private int hashCode;//hashCode fucks up because we initialize the variables publically and not with the constructor
	@JsonIgnore
	public Calendar calendar;

	public Time() {
		this.hashCode = Objects.hash(this.year,this.month,this.day,this.hour,this.minute,this.second);
	}
	
	public Time(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.hashCode = Objects.hash(this.year,this.month,this.day,this.hour,this.minute,this.second);
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Time that = (Time) o;
        return this.year == that.year && 
        		this.month == that.month && 
        		this.day == that.day && 
        		this.hour == that.hour && 
        		this.minute == that.minute && 
        		this.second == that.second;
    }
	
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
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

	public static List<Interval> getIntervalList(String time, int startYear, int endYear) throws Exception {
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
						valid = (valid) ? Time.isValidTime(d) : valid;
						d = dashArray[1];
						valid = (valid) ? Time.isValidTime(d) : valid;
						range[1] = d;
						list.add(range);
					} else {
						valid = false;
					}
				} else {
					valid = (valid) ? Time.isValidTime(t) : valid;
					list.add(t);
				}
			}
			if (valid) {
				intervalList = new ArrayList<>();
				for (Object o : list) {
					if (o instanceof String) {
						String s = (String) o;
						s = s.toLowerCase();
						if (Time.isAlias(s)) {
							interval = Time.getAliasInterval(s, startYear, endYear);
							if (interval != null) {
								intervalList.add(interval);
							}
						} else if (Time.isMonth(s)) {
							interval = Time.getMonthInterval(s, startYear, endYear);
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
					} else if (o instanceof String[]) {
						String[] range = (String[]) o;
						String a = range[0];
						String b = range[1];
						if (Time.isAlias(a) && Time.isAlias(b)) {
							Interval intervalA = Time.getAliasInterval(a, startYear, endYear);
							Interval intervalB = Time.getAliasInterval(b, startYear, endYear);
							interval = new Interval();
							interval.startMonth = intervalA.startMonth;
							interval.startDay = intervalA.startDay;
							interval.endMonth = intervalB.endMonth;
							interval.endDay = intervalB.endDay;
							intervalList.add(interval);
						} else if (Time.isMonth(a) && Time.isMonth(b)) {
							Interval intervalA = Time.getMonthInterval(a, startYear, endYear);
							Interval intervalB = Time.getMonthInterval(b, startYear, endYear);
							interval = new Interval();
							interval.startMonth = intervalA.startMonth;
							interval.startDay = intervalA.startDay;
							interval.endMonth = intervalB.endMonth;
							interval.endDay = intervalB.endDay;
							intervalList.add(interval);
						} else if (Time.isDate(a) != null && Time.isDate(b) != null) {
							String typeA = Time.isDate(a);
							String typeB = Time.isDate(b);
							Interval intervalA = Time.getDateInterval(Time.getDate(a), typeA);
							Interval intervalB = Time.getDateInterval(Time.getDate(b), typeB);
							interval = new Interval();
							interval.startYear = intervalA.startYear;
							interval.startMonth = intervalA.startMonth;
							interval.startDay = intervalA.startDay;
							interval.startHour = intervalA.startHour;
							interval.endYear = intervalB.endYear;
							interval.endMonth = intervalB.endMonth;
							interval.endDay = intervalB.endDay;
							interval.endHour = intervalB.endHour;
							intervalList.add(interval);
						}
					}
				}
			} else {
				throw new Exception("invalid time format: " + time);
			}
		}
//		logger.info("getIntervalList(" + time + ") intervalList=" + intervalList);
		return intervalList;
	}

	public static List<Time> getTimeList(Interval i) throws Exception {
//		logger.info("load(query, " + i + ")");
		List<Time> timeList = new ArrayList<>();
		int startYear = i.startYear;
		int startMonth = (i.startMonth == -1) ? 1 : i.startMonth;
		int endYear = i.endYear;
		int endMonth = (i.endMonth == -1) ? 12 : i.endMonth;
		if (i.allFlag) {
//			logger.info("load(...) allFlag=" + i.allFlag);
			for (int y = startYear; y <= endYear; y++) {
				for (int m = startMonth; m <= endMonth; m++) {
					// y && m
					Time time = new Time();
					time.year = y;
					time.month = m;
					timeList.add(time);
				}
			}
		} else if (i.startYear != -1 && i.endYear != -1) {
			int yearDifference = endYear - startYear - 1;
			if (yearDifference == -1) { // easiest case same year just iterate over months and done
				// same year
				for (int y = startYear; y <= endYear; y++) {
					for (int m = startMonth; m <= endMonth; m++) {
						// y && m
						Time time = new Time();
						time.year = y;
						time.month = m;
						timeList.add(time);
					}
				}
			} else {
				// different years
				// all range, season, month queries end up here
				// in order for the single count method to work for DJF,
				// we have to read DJF in order.
				if (i.seasonFlag || i.monthFlag) {
					if (startMonth <= endMonth) {
						for (int y = startYear; y <= endYear; y++) {

							for (int m = startMonth; m <= endMonth; m++) {
								// y && m
								Time time = new Time();
								time.year = y;
								time.month = m;
								timeList.add(time);
							}
						}
					} else {
						for (int y = startYear; y <= endYear; y++) {

							for (int m = 1; m <= endMonth; m++) {
								// y && m
								Time time = new Time();
								time.year = y;
								time.month = m;
								timeList.add(time);
							}
							for (int m = startMonth; m <= 12; m++) {
								// y && m
								Time time = new Time();
								time.year = y;
								time.month = m;
								timeList.add(time);
							}
						}
					}
				} else {
					// this code does not work, it may have never worked
					// update looks like this code works and Interval contains point is failing
					for (int m = startMonth; m <= 12; m++) {
						// startYear && m
						Time time = new Time();
						time.year = startYear;
						time.month = m;
						timeList.add(time);
					}
					for (int y = startYear + 1; y <= endYear - 1; y++) {// skipped is years are consecutive
						for (int m = 1; m <= 12; m++) {
							// y && m
							Time time = new Time();
							time.year = y;
							time.month = m;
							timeList.add(time);
						}
					}
					for (int m = 1; m <= endMonth; m++) {
						// endYear && m
						Time time = new Time();
						time.year = endYear;
						time.month = m;
						timeList.add(time);
					}
				}
			}
		} else {
			throw new Exception("invalid interval, start and end year initialized");
		}
		 return timeList;
	}

	public static boolean isValidTime(String value) {
		boolean flag = false;
//		flag = (!flag) ? Time.isAlias(value) : flag;
		flag = (!flag) ? Time.isAlias(value) : flag;
		flag = (!flag) ? Time.isMonth(value) : flag;
		flag = (!flag) ? (Time.isDate(value) == null) ? false : true : flag;
		return flag;
	}

	public static String isDate(String value) {
//		System.out.println("isDate("+value+")");
		String flag = null;
		flag = (flag == null) ? Time.isDate(value, "yyyy/MM/dd HH:mm:ss", "DATE_TIME") : flag;
		flag = (flag == null) ? Time.isDate(value, "yyyy/MM/dd", "DATE") : flag;
		flag = (flag == null) ? Time.isDate(value, "HH:mm:ss", "TIME") : flag;
		flag = (flag == null) ? Time.isDate(value, "yyyy/MM", "YEAR_MONTH") : flag;
		flag = (flag == null) ? Time.isDate(value, "yyyy", "YEAR") : flag;
//		System.out.println("isDate("+value+") flag="+flag);
		return flag;
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
		return flag;
	}

	public static boolean isAlias(String alias) {
		boolean flag = false;
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
		switch (type) {
		case "DATE_TIME": {
			interval = new Interval();
			interval.startYear = year;
			interval.startMonth = month;
			interval.startDay = day;
			interval.startHour = hour;
			interval.endYear = year;
			interval.endMonth = month;
			interval.endDay = day;
			interval.endHour = hour;
			interval.dateFlag = true;
			interval.timeFlag = true;
			break;
		}
		case "DATE": {
			interval = new Interval();
			interval.startYear = year;
			interval.startMonth = month;
			interval.startDay = day;
			interval.endYear = year;
			interval.endMonth = month;
			interval.endDay = day;
			interval.dateFlag = true;
			break;
		}
		case "TIME": {
			interval = new Interval();
			interval.startHour = hour;
			interval.endHour = hour;
			interval.timeFlag = true;
			break;
		}
		case "YEAR_MONTH": {
			interval = new Interval();
			interval.startYear = year;
			interval.startMonth = month;
			interval.startDay = 1;
			interval.endYear = year;
			interval.endMonth = month;
			interval.endDay = cal.getActualMaximum(Calendar.DATE);
			interval.yearFlag = true;
			interval.monthFlag = true;
			break;
		}
		case "YEAR": {
			interval = new Interval();
			interval.startYear = year;
			interval.startMonth = 1;
			interval.startDay = 1;
			interval.endYear = year;
			interval.endMonth = 12;
			interval.endDay = 31;
			interval.yearFlag = true;
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
			interval.startYear = startYear;// this.getStartYear();
			interval.endYear = endYear;// this.getEndYear();
			interval.startMonth = month;
			interval.startDay = 1;
			interval.endMonth = month;
			interval.endDay = cal.getActualMaximum(Calendar.DATE);
			interval.monthFlag = true;
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
			interval.startYear = startYear;
			interval.endYear = endYear;
			interval.startMonth = 1;
			interval.startDay = 1;
			interval.endMonth = 12;
			interval.endDay = 31;
			interval.allFlag = true;
			interval.alias = alias;
			break;
		}
		case "jja": {
			interval = new Interval();
			interval.startYear = startYear;// this.getStartYear();
			interval.endYear = endYear;// this.getEndYear();
			interval.startMonth = 6;
			interval.startDay = 1;
			interval.endMonth = 8;
			interval.endDay = 31;
			interval.seasonFlag = true;
			interval.alias = alias;
			break;
		}
		case "djf": {
			interval = new Interval();
			interval.startYear = startYear;// this.getStartYear();
			interval.endYear = endYear;// this.getEndYear();
			interval.startMonth = 12;
			interval.startDay = 1;
			interval.endMonth = 2;
			interval.endDay = 28;
			interval.seasonFlag = true;
			interval.alias = alias;
			break;
		}
		case "mam": {
			interval = new Interval();
			interval.startYear = startYear;// this.getStartYear();
			interval.endYear = endYear;// this.getEndYear();
			interval.startMonth = 3;
			interval.startDay = 1;
			interval.endMonth = 5;
			interval.endDay = 31;
			interval.seasonFlag = true;
			interval.alias = alias;
			break;
		}
		case "son": {
			interval = new Interval();
			interval.startYear = startYear;// this.getStartYear();
			interval.endYear = endYear;// this.getEndYear();
			interval.startMonth = 9;
			interval.startDay = 1;
			interval.endMonth = 11;
			interval.endDay = 30;
			interval.seasonFlag = true;
			interval.alias = alias;
			break;
		}
		}
		return interval;
	}

	public GregorianCalendar getCalendar(String format, String time) {
		GregorianCalendar calendar = new GregorianCalendar();
		Date date = this.getDate(format, time);
		if (date != null)
			calendar.setTime(date);
		return calendar;
	}

	public GregorianCalendar getCalendar(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	public static Date getDate(String value) {
		Date date = null;
		date = (date == null) ? Time.getDate(value, "yyyy/MM/dd HH:mm:ss") : date;
		date = (date == null) ? Time.getDate(value, "yyyy/MM/dd") : date;
		date = (date == null) ? Time.getDate(value, "HH:mm:ss") : date;
		date = (date == null) ? Time.getDate(value, "yyyy/MM") : date;
		date = (date == null) ? Time.getDate(value, "yyyy") : date;
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

	public static String getCalendarString(String format, Calendar calendar) {
		return getDateString(format, calendar.getTime());
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

	public static Date getNineteenHundredJanuaryFirstDate(int time) {
		GregorianCalendar g = new GregorianCalendar(1900, 0, 1, -1, 0, 0);
		g.add(Calendar.HOUR, time); // adds one hour
		g.set(Calendar.MINUTE, 0);
		g.set(Calendar.SECOND, 0);
		g.set(Calendar.MILLISECOND, 0);
		return g.getTime();
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
	
//	public String toString() {
//		return ((year != -1) ? year : "") + ((month != -1) ? String.format("%02d", month) : "")
//				+ ((day != -1) ? String.format("%02d", day) : "")
//				+ ((hour != -1) ? String.format("%02d", hour) : "");
//	}
}
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