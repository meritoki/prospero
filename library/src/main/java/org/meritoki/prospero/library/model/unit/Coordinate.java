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

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * <ul>
 * <li>20221115: Add Equals Method</li>
 * </ul>
 * @author jorodriguez
 *
 */
public class Coordinate implements Comparable<Coordinate> {

	@JsonIgnore
	public Calendar calendar;
	@JsonProperty
	public double latitude;
	@JsonProperty
	public double longitude;
	@JsonInclude(Include.NON_EMPTY)
	public Map<String, Object> attribute = new TreeMap<>();
	@JsonProperty
	public boolean flag;


	public Coordinate() {}
	
	public Coordinate(Coordinate coordinate) {
		this.calendar = coordinate.calendar;
		this.latitude = coordinate.latitude;
		this.longitude = coordinate.longitude;
		this.attribute = new TreeMap<>(coordinate.attribute);
		this.flag = coordinate.flag;
	}

	public void reset() {
		this.flag = false;
	}

	public boolean is() {
		return !((Double)latitude).isNaN()&!((Double)longitude).isNaN();
	}

	/**
	 * Need to Generalize, maybe not use Pressure as Default
	 */
	@JsonIgnore
	@Override
	public int compareTo(Coordinate p) {
		Integer pressureA = (int) this.attribute.get("pressure");
		Integer pressureB = (int) p.attribute.get("pressure");
		return pressureA.compareTo(pressureB);
	}

	public boolean containsCalendar(Calendar calendar) {
//		System.out.println(this.calendar.getTimeZone()+".containsCalendar("+calendar.getTimeZone());
//		System.out.println(this.calendar.get(Calendar.HOUR_OF_DAY)+".containsCalendar("+calendar.get(Calendar.HOUR_OF_DAY));
		if (this.calendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
				&& this.calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
				&& this.calendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)
				&& this.calendar.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY)
				&& this.calendar.get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE)
				&& this.calendar.get(Calendar.SECOND) == calendar.get(Calendar.SECOND)) {
//			System.out.println("containsCalendar("+calendar.getTime()+") true");
			return true;
		}
		return false;
	}

	@JsonIgnore
	public Date getDate() {
		return calendar.getTime();
	}

	@JsonIgnore
	public int getMonth() {
		int month = this.calendar.get(Calendar.MONTH) + 1;
		return month;
	}

	@JsonIgnore
	public int getYear() {
		int year = this.calendar.get(Calendar.YEAR);
		return year;
	}

	@JsonIgnore
	public void setColor(double n, int size) {
		double power = n * 0.9 / size;
		double H = power;// * 0.4; // Hue (note 0.4 = Green, see huge chart below)
		double S = 0.9; // Saturation
		double B = 0.9; // Brightness
		Color color = Color.getHSBColor((float) H, (float) S, (float) B);
		this.attribute.put("color", color);
	}

	@JsonIgnore
	public Color getColor() {
		Object o = this.attribute.get("color");
		if (o != null) {
			return (Color) o;
		}
		return Color.black;
	}
	
	@JsonIgnore
	public String getDateTime() {
		return Time.getDateString(Time.defaultFormat,this.calendar.getTime());
	}
	
	@JsonIgnore
	public Calendar getCalendar() {
		return this.calendar;
	}
	
	@JsonIgnore
	public Integer getPressure() {
		Object pressure = this.attribute.get("pressure");
		return (pressure != null)?(int)pressure:null;
	}
	
	@JsonIgnore
	public double getMeters() {
		return this.getMeters(this.getPressure());
	}
	
	@JsonIgnore
	public double getMeters(int pressure) {
		double p = (double) pressure;
		double p0 = 1013.25;
		double T = 15;
		double meters = ((Math.pow(p0 / p, 1 / 5.257) - 1) * (T + 273.15)) / 0.0065;
		// logger.info("getMeters("+level+") meters="+meters);
		return meters;
	}
	
	@JsonIgnore
	public Float getVorticity() {
		Object vorticity = this.attribute.get("vorticity");
		return (vorticity != null)?(float)vorticity:null;
	}
	
	public static double getArea(double dimension) {
		return dimension * dimension;
	}

	public static double getArea(double latitude, double longitude, double dimension) {
		return Math.cos(Math.abs(Math.toRadians(getCenterLatitude(latitude, dimension))));
	}

	public static double getCenterLatitude(double latitude, double dimension) {
		return latitude + (dimension / 2);
	}

	public static double getCenterLongitude(double longitude, double dimension) {
		return longitude + (dimension / 2);
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
}
//@JsonIgnore
//public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//@JsonIgnore
//@Override
//public String toString() {
//	String string = "";
//	ObjectWriter ow = new ObjectMapper().writer();
//	try {
//		string = ow.writeValueAsString(this);
//	} catch (IOException ex) {
//		System.err.println("IOException " + ex.getMessage());
//	}
//	return string;
//}
//public String toString() {
//	return this.flag +";"+this.latitude + ";" + this.longitude + ";" + ((this.calendar != null)?this.calendar.getTime():null) + ";" + this.attribute;
//}
//@JsonIgnore
//public Point point = new Point();