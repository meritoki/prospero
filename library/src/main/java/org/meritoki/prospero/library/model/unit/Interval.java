package org.meritoki.prospero.library.model.unit;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Interval {

	static Logger logger = LogManager.getLogger(Interval.class.getName());
	@JsonProperty
	public int startYear = -1;
	@JsonProperty
	public int startMonth = -1;
	@JsonProperty
	public int startDay = -1;
	@JsonProperty
	public int startHour = -1;
	@JsonProperty
	public int startMinute = -1;
	@JsonProperty
	public int startSecond = -1;
	@JsonProperty
	public int endYear = -1;
	@JsonProperty
	public int endMonth = -1;
	@JsonProperty
	public int endDay = -1;
	@JsonProperty
	public int endHour = -1;
	@JsonProperty
	public int endMinute = -1;
	@JsonProperty
	public int endSecond = -1;
//	public boolean defect = false;
	@JsonProperty
	public String alias;
	public boolean allFlag;
	public boolean yearFlag;
	public boolean seasonFlag;
	public boolean monthFlag;
	public boolean dateFlag;
	public boolean timeFlag;
	
	
	public boolean contains(Coordinate coordinate) {
		boolean flag = false;
		Date date = coordinate.calendar.getTime();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		Calendar startCalendar = new GregorianCalendar(this.startYear,this.startMonth-1,this.startDay,(this.startHour != -1)?this.startHour:0,(this.startMinute != -1)?this.startMinute:0,(this.startSecond != -1)?this.startSecond:0);
		Calendar endCalendar = new GregorianCalendar(this.endYear,this.endMonth-1,this.endDay,(this.endHour != -1)?this.endHour:0,(this.endMinute != -1)?this.endMinute:0,(this.endSecond != -1)?this.endSecond:0);
		Date startDate = startCalendar.getTime();
		Date endDate = endCalendar.getTime();
		flag = !(date.before(startDate) || date.after(endDate)) ;//(startDate.equals(date) && date.equals(endDate)) || 
//		if(flag) {
//			logger.debug(this+".contains("+coordinate+") date="+date);
//		}
		return flag;
	}
	
	public boolean contains(Event coordinate) {
		boolean flag = false;
		Date date = coordinate.getStartCalendar().getTime();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		Calendar startCalendar = new GregorianCalendar(this.startYear,this.startMonth-1,this.startDay,(this.startHour != -1)?this.startHour:0,(this.startMinute != -1)?this.startMinute:0,(this.startSecond != -1)?this.startSecond:0);
		Calendar endCalendar = new GregorianCalendar(this.endYear,this.endMonth-1,this.endDay,(this.endHour != -1)?this.endHour:0,(this.endMinute != -1)?this.endMinute:0,(this.endSecond != -1)?this.endSecond:0);
		Date startDate = startCalendar.getTime();
		Date endDate = endCalendar.getTime();
		flag = !(date.before(startDate) || date.after(endDate)) ;//(startDate.equals(date) && date.equals(endDate)) || 
//		if(flag) {
//			logger.debug(this+".contains("+coordinate+") date="+date);
//		}
		return flag;
	}
//	/**
//	 * This function has a history of problems which I have not documented well
//	 * commenting out this line: 1 <=month && month <= endMonth appeared to fix a
//	 * particular defect which I cannot remember now, must check github
//	 * but somehow I have to add the commented out line back.
//	 * @param coordinate
//	 * @return
//	 */
//	public boolean contains(Coordinate coordinate) {
//		boolean flag = false;
//		Date date = coordinate.calendar.getTime();
////		logger.info(this+".contains("+coordinate+") date="+date);
//		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//		cal.setTime(date);
//		int year = cal.get(Calendar.YEAR);
////		int month = cal.get(Calendar.MONTH) + 1;
////		int day = cal.get(Calendar.DAY_OF_MONTH);
////		int hour = cal.get(Calendar.HOUR_OF_DAY) - 3;
//		if(this.seasonFlag) {
//			if(startMonth < endMonth) {
//				for(int y=this.startYear; y <= this.endYear; y++) {
//					Calendar startCalendar = new GregorianCalendar(y,this.startMonth-1,this.startDay);
//					Calendar endCalendar = new GregorianCalendar(y,this.endMonth-1,this.endDay);
//					Date startDate = startCalendar.getTime();
//					Date endDate = endCalendar.getTime();
//					flag = !(date.before(startDate) || date.after(endDate));
//					if(flag) {
//						break;
//					}
//				}
//			} else {
//				//this is where djf is handled, b/c startMonth 12 > endMonth 2
//				//
//				if(year == this.startYear) {
//					Calendar startCalendar = new GregorianCalendar(this.startYear,0,1);
//					Calendar endCalendar = new GregorianCalendar(this.startYear,this.endMonth-1,this.endDay);
//					Date startDate = startCalendar.getTime();
//					Date endDate = endCalendar.getTime();
//					flag = !(date.before(startDate) || date.after(endDate));
//				} else if(year == this.endYear) {
//					Calendar startCalendar = new GregorianCalendar(this.endYear,this.startMonth-1,this.startDay);
//					Calendar endCalendar = new GregorianCalendar(this.endYear,11,this.endDay);
//					Date startDate = startCalendar.getTime();
//					Date endDate = endCalendar.getTime();
//					flag = !(date.before(startDate) || date.after(endDate));
//				}
//				if(!flag) {
//					for(int y=this.startYear; y <= this.endYear; y++) {
//						if(y+1<=this.endYear) {
//							Calendar startCalendar = new GregorianCalendar(y,this.startMonth-1,this.startDay);
//							Calendar endCalendar = new GregorianCalendar(y+1,this.endMonth-1,this.endDay);
//							Date startDate = startCalendar.getTime();
//							Date endDate = endCalendar.getTime();
//							flag = !(date.before(startDate) || date.after(endDate));
//							if(flag) {
//								break;
//							}
//						}
//					}
//				}
//			}
//		} else if (monthFlag) {
//			for(int y=this.startYear; y <= this.endYear; y++) {
//				Calendar startCalendar = new GregorianCalendar(y,this.startMonth-1,this.startDay);
//				Calendar endCalendar = new GregorianCalendar(y,this.endMonth-1,this.endDay);
//				Date startDate = startCalendar.getTime();
//				Date endDate = endCalendar.getTime();
//				
//				flag = !(date.before(startDate) || date.after(endDate));
//				if(flag) {
//					break;
//				}
//			}
//		} else {
//			Calendar startCalendar = new GregorianCalendar(this.startYear,this.startMonth-1,this.startDay,(this.startHour != -1)?this.startHour:0,(this.startMinute != -1)?this.startMinute:0,(this.startSecond != -1)?this.startSecond:0);
//			Calendar endCalendar = new GregorianCalendar(this.endYear,this.endMonth-1,this.endDay,(this.endHour != -1)?this.endHour:0,(this.endMinute != -1)?this.endMinute:0,(this.endSecond != -1)?this.endSecond:0);
//			Date startDate = startCalendar.getTime();
//			Date endDate = endCalendar.getTime();
//			logger.info(startDate+"|"+date+"|"+endDate);
//			flag = (startDate.equals(date) && date.equals(endDate));//!(date.before(startDate) || date.after(endDate)) || ;
//		}
//		return flag;
//	}
	
	public boolean contains(Frame frame) {
//		System.out.println("contains("+frame+") startYear="+startYear+" endYear="+endYear);
		boolean flag = true;
		Date date = new Date(frame.milliseconds);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY) - 3;
		if(flag && startYear > -1 && endYear > -1) {
			flag = (startYear<=year && year <= endYear);
		}
		if(flag && startMonth > -1 && endMonth > -1) {
			if(startMonth <= endMonth && startMonth<=month && month <= endMonth) {
				flag = true;
			} else if(startMonth > endMonth && startMonth<=month && month <=12 || 1 <=month && month <= endMonth) {
				flag = true;
			} 
//			else if(!defect){
//				flag = false;
//			}
		}
		if(flag && startDay > -1 && endDay > -1) {
			flag = (startDay<=day && day <= endDay);
		}
		if(flag && startHour > -1 && endHour > -1) {
			flag = (startHour<=hour && hour <= endHour);
		}
		return flag;
	}
	
	public Calendar getStart() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,this.startYear);
		calendar.set(Calendar.MONTH,this.startMonth-1);
		calendar.set(Calendar.DAY_OF_MONTH,this.startDay);
		calendar.set(Calendar.HOUR_OF_DAY,(this.startHour != -1)?this.startHour:0);
		calendar.set(Calendar.MINUTE,(this.startMinute != -1)?this.startMinute:0);
		calendar.set(Calendar.SECOND,(this.startSecond != -1)?this.startSecond:0);
		return calendar;
	}
	
	public Calendar getEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,this.endYear);
		calendar.set(Calendar.MONTH,this.endMonth-1);
		calendar.set(Calendar.DAY_OF_MONTH,this.endDay);
		calendar.set(Calendar.HOUR_OF_DAY,(this.endHour != -1)?this.endHour:0);
		calendar.set(Calendar.MINUTE,(this.endMinute != -1)?this.endMinute:0);
		calendar.set(Calendar.SECOND,(this.endSecond != -1)?this.endSecond:0);
		return calendar;
	}
	
	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			logger.error("IOException " + ex.getMessage());
		}
		return string;
	}
	
//	public String toString() {
//		return "startYear="+startYear+",startMonth="+startMonth+",startDay="+startDay+",startHour="+startHour+",endYear="+endYear+",endMonth="+endMonth+",endDay="+endDay+",endHour="+endHour;
//	}
}
//System.out.println("contains("+point+") year="+year);
//System.out.println("contains("+point+") month="+month);
//System.out.println("contains("+point+") day="+day);
//System.out.println("contains("+point+") hour="+hour);
//System.out.println("contains("+point+") startMonth="+startMonth);
//System.out.println("contains("+point+") endMonth="+endMonth);
//if(flag && startYear > -1 && endYear > -1) {
//	flag = (startYear<=year && year <= endYear);
//}
//if(flag && startMonth > -1 && endMonth > -1) {
//	if(startMonth <= endMonth && startMonth<=month && month <= endMonth) {
//		System.out.println("A");
//		flag = true;
//	} 
//	else if(startMonth > endMonth && startMonth<=month && month <=12) {/// || 1 <=month && month <= endMonth) {
//		System.out.println("B");
//		flag = true;
//	} else if(1 <=month && month <= endMonth) {
//		System.out.println("C");
//		flag = true;
//	} else {
//		System.out.println("FAIL "+point);
//		flag = false;
//	}
//}
//if(flag && startDay > -1 && endDay > -1) {
//	flag = (startDay<=day && day <= endDay);
//}
//if(flag && startHour > -1 && endHour > -1) {
//	flag = (startHour<=hour && hour <= endHour);
//}
//if(flag) 
//	System.out.println("contains("+point+") flag="+flag);
///**
//* This function has a history of problems which I have not documented well
//* commenting out this line: 1 <=month && month <= endMonth appeared to fix a
//* particular defect which I cannot remember now, must check github
//* but somehow I have to add the commented out line back.
//* @param point
//* @return
//*/
//public boolean contains(Point point) {
//	boolean flag = true;
//	Date date = point.date;
//	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//	cal.setTime(date);
//	int year = cal.get(Calendar.YEAR);
//	int month = cal.get(Calendar.MONTH) + 1;
//	int day = cal.get(Calendar.DAY_OF_MONTH);
//	int hour = cal.get(Calendar.HOUR_OF_DAY) - 3;
////	System.out.println("contains("+point+") year="+year);
////	System.out.println("contains("+point+") month="+month);
////	System.out.println("contains("+point+") day="+day);
////	System.out.println("contains("+point+") hour="+hour);
////	System.out.println("contains("+point+") startMonth="+startMonth);
////	System.out.println("contains("+point+") endMonth="+endMonth);
//	if(flag && startYear > -1 && endYear > -1) {
//		flag = (startYear<=year && year <= endYear);
//	}
//	if(flag && startMonth > -1 && endMonth > -1) {
//		if(startMonth <= endMonth && startMonth<=month && month <= endMonth) {
//			System.out.println("A");
//			flag = true;
//		} 
//		else if(startMonth > endMonth && startMonth<=month && month <=12) {/// || 1 <=month && month <= endMonth) {
//			System.out.println("B");
//			flag = true;
//		} else if(1 <=month && month <= endMonth) {
//			System.out.println("C");
//			flag = true;
//		} else {
//			System.out.println("FAIL "+point);
//			flag = false;
//		}
//	}
//	if(flag && startDay > -1 && endDay > -1) {
//		flag = (startDay<=day && day <= endDay);
//	}
//	if(flag && startHour > -1 && endHour > -1) {
//		flag = (startHour<=hour && hour <= endHour);
//	}
////	System.out.println("contains("+point+") flag="+flag);
//	return flag;
//}

