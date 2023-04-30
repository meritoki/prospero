package org.meritoki.prospero.library.model.node.query;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.color.Scheme;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.Classification;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.Family;
import org.meritoki.prospero.library.model.unit.Analysis;
import org.meritoki.prospero.library.model.unit.Count;
import org.meritoki.prospero.library.model.unit.Duration;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Operator;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Script;
import org.meritoki.prospero.library.model.unit.Time;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Query {

	static Logger logger = LogManager.getLogger(Query.class.getName());
	@JsonIgnore
	public String uuid;
	@JsonProperty
	public Map<String, String> map = new TreeMap<>();
	@JsonIgnore
	public int index = -1;
	@JsonIgnore
	public List<Alias> alias = new ArrayList<>();
	@JsonIgnore
	public List<Object> objectList;
	@JsonIgnore
	public Calendar calendar = Calendar.getInstance();
	@JsonIgnore
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	@JsonIgnore
	public Script script = null;
	@JsonIgnore
	public Object object = null;

	public Query() {
		this.uuid = UUID.randomUUID().toString();
		this.initAlias();
	}

	public Query(Query q) {
		this.uuid = q.uuid;
		this.initAlias();
		this.map = new TreeMap<>(q.map);
		this.calendar = (Calendar) q.calendar.clone();
	}

	@JsonIgnore
	public boolean objectListAdd(Object object) {
		boolean flag = false;
		if (this.objectList != null) {
			synchronized (this.objectList) {
				this.objectList.add(object);
				this.objectList.notify();
				flag = true;
			}
		}
		return flag;
	}

	public boolean equals(Object object) {
		if (object instanceof Query) {
			Query q = (Query) object;
			boolean flag = this.getTime().equals(q.getTime()) && this.getSource().equals(q.getSource());// &&
																										// this.map.equals(q.map);
			boolean idFlag = (this.getID() != null) ? this.getID().equals(q.getID()) : false;
			flag = (flag) ? idFlag : false;
//			logger.debug(this+".equals("+q+") flag="+flag);
			return flag;
		}
		return false;
	}

	@JsonIgnore
	public boolean isReady() {
		return (this.getTime() != null) && (this.getSource() != null);
	}

	@JsonIgnore
	public String getFileName() {
		return this.getTime() + "-" + this.getSourceUUID();
	}

	@JsonIgnore
	public File getFile() {
		return new File(this.getFileName() + ".json");
	}

	@JsonIgnore
	public void initAlias() {
		Alias timeAlias = new Alias("time", new String[] { "time", "t" });
		Alias pressureAlias = new Alias("pressure", new String[] { "pressure", "p" });
		Alias elevationAlias = new Alias("elevation", new String[] { "elevation", "e" });
		Alias regionAlias = new Alias("region", new String[] { "region", "r" });
		Alias dimensionAlias = new Alias("dimension", new String[] { "dimension", "dim", "d" });
		Alias sourceAlias = new Alias("source", new String[] { "source", "src", "s" });
		this.alias.add(timeAlias);
		this.alias.add(pressureAlias);
		this.alias.add(elevationAlias);
		this.alias.add(regionAlias);
		this.alias.add(dimensionAlias);
		this.alias.add(sourceAlias);
	}

	public void addVariable(String variable) {
		String v = this.map.get("variable");
		if (v != null && v.length() > 0) {
			v += "," + variable;
			this.map.put("variable", v);
		} else {
			this.map.put("variable", variable);
		}
	}

	@JsonIgnore
	public void setTime(Calendar calendar) {
		String time = this.simpleDateFormat.format(calendar.getTime());// "yyyy/MM/dd HH:mm:ss"
		this.map.put("time", time);
	}

	public void setWindow(Calendar start, Calendar end) {
		this.map.put("window", Time.getCalendarString("YYYY/MM/dd HH:mm:ss", start) + ","
				+ Time.getCalendarString("YYYY/MM/dd HH:mm:ss", end));
	}

	@JsonIgnore
	public String getName() throws Exception {
		String name = this.map.get("name");
		if (name == null) {
			name = this.generateName();
		}
		return name;
	}

	@JsonIgnore
	public String generateName() throws Exception {
		StringBuilder name = new StringBuilder();
		if (this.getVariable() != null) {
			name.append("var");
			name.append("-");
			name.append(this.getVariable().replace(",", "_"));
			name.append("-");
		}
		if (this.getSource() != null) {
			name.append("src");
			name.append("-");
			name.append(this.getSource().replace(" ", "_"));
			name.append("-");
		}
		if (this.getTime() != null) {
			name.append("time");
			name.append("-");
			name.append(this.getTime().replace(",", "_").replace("/", "").replace(" ", "-").replace(":", ""));
			name.append("-");
		}

		if (this.getGroup() != null) {
			name.append("group");
			name.append("-");
			name.append(this.getGroup().replace(",", "_"));
			name.append("-");
		}
		// Dimension
		if (this.getDimension() != null) {
			name.append("dimension");
			name.append("-");
			name.append(String.valueOf(this.getDimension()));
			name.append("-");
		}
		if (this.getRegion() != null) {
			name.append("region");
			name.append("-");
			name.append("(" + this.getRegion().replace(",", "_").replace(":", ")-(").replace(";", ")-(") + ")");
			name.append("-");
		}
		if (this.getFamily() != null) {
			name.append("family");
			name.append("-");
			name.append(this.getFamily().replace(",", "_"));
			name.append("-");
		}
		if (this.getClassification() != null) {
			name.append("class");
			name.append("-");
			name.append(this.getClassification().replace(",", "_"));
			name.append("-");
		}
		if (this.getPressure() != null) {
			name.append("pressure");
			name.append("-");
			name.append(this.getPressure().replace(",", "_"));
			name.append("-");
		}
		name.deleteCharAt(name.length() - 1);
		return name.toString().toLowerCase();
	}

	@JsonIgnore
	public Analysis getAnalysis() {
		String value = this.map.get("analysis");
		Analysis analysis = Analysis.MEAN;
		if (value != null) {
			analysis = Analysis.valueOf(value.toUpperCase());
		}
		return analysis;
	}

	@JsonIgnore
	public Scheme getScheme() {
		String s = this.map.get("scheme");
		Scheme scheme = null;
		if (s != null) {
			s.toUpperCase();
			scheme = Scheme.valueOf(s);
		}
		return scheme;
	}

	@JsonIgnore
	public String getSource() {
		String source = this.map.get("source");
//		if(source == null) {
//			source = "NULL";
//		}
		return source;
	}

	@JsonIgnore
	public String getSourceUUID() {
		String source = this.map.get("sourceUUID");
		return source;
	}

	@JsonIgnore
	public List<String> getSourceList() {
		return this.getSourceList(this.getSource());
	}

	@JsonIgnore
	public List<String> getSourceList(String source) {
		List<String> sourceList = new ArrayList<>();
		if (source != null) {
			if (source.contains(",")) {
				String[] array = source.split(",");
				for (String t : array) {
					sourceList.add(t);
				}
			} else {
				sourceList.add(source);
			}
		}
		return sourceList;
	}

	@JsonIgnore
	public String getVariable() {
		String variable = map.get("variable");
		return variable;
	}

	@JsonIgnore
	public List<String> getVariableList() {
		return this.getVariableList(this.getVariable());
	}

	@JsonIgnore
	public List<String> getVariableList(String variable) {
		List<String> variableList = new ArrayList<>();
		if (variable != null) {
			if (variable.contains(",")) {
				String[] array = variable.split(",");
				for (String t : array) {
					t = t.trim();
					variableList.add(t);
				}
			} else {
				variableList.add(variable);
			}
		}
		return variableList;
	}

	@JsonIgnore
	public Double[] getMeter() {
		String meter = map.get("meter");
		Double[] array = new Double[0];
		if (meter != null) {
			String[] meterArray = meter.split(",");
			array = new Double[meterArray.length];
			for (int i = 0; i < array.length; i++) {
				array[i] = Double.parseDouble(meterArray[i]);
			}
		}
		return array;
	}

	@JsonIgnore
	public Calendar[] getWindow() throws ParseException {
		String window = this.map.get("window");
		Calendar[] array = new Calendar[0];
		if (window != null) {
//			System.out.println("window: "+window);
			String[] windowArray = window.split(",");
			array = new Calendar[windowArray.length];
			for (int i = 0; i < windowArray.length; i++) {
//				System.out.println("windowArray["+i+"]: "+windowArray[i]);
				Calendar c = Calendar.getInstance();
				Date d = this.simpleDateFormat.parse(windowArray[i]);
				c.setTime(d);
				array[i] = c;
			}
		}
//		System.out.println("array.length="+array.length);
		return array;
	}

	/**
	 * In Future, support time djf;2001/01-2001/02 This feature changes the Plot
	 * Visible Range to the second parameter While preserving the original time
	 * query style Semi-colon is used because in the original time syntax it was
	 * never used
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getTime() {
		String time = map.get("time");
//		if(time == null) {
//			time = "";
//		}
		return time;
	}

	@JsonIgnore
	public List<String> getTimeList() {
		return this.getTimeList(this.getTime());
	}

	@JsonIgnore
	public List<String> getTimeList(String time) {
		List<String> timeList = new ArrayList<>();
		if (time != null) {
			if (time.contains(",")) {
				String[] array = time.split(",");
				for (String t : array) {
					timeList.add(t);
				}
			} else {
				timeList.add(time);
			}
		}
		return timeList;
	}

	@JsonIgnore
	public String[] getTimeArray() {
		return this.getTimeArray(this.getTime());
	}

	@JsonIgnore
	public String[] getTimeArray(String time) {
		String[] array = new String[1];
		if (time != null) {
			if (time.contains(",")) {
				array = time.split(",");
			} else {
				array[0] = time;
			}
		}
		return array;
	}

//	@JsonIgnore
//	public List<Interval> getIntervalList(int startYear, int endYear) throws Exception {
//		return Time.getIntervalList(this.getTime(), startYear, endYear);
//	}

	@JsonIgnore
	public List<Interval> getIntervalList(Time start, Time end) throws Exception {
		// Here is where we check if window can be used.
		Calendar[] window = this.getWindow();
		if (window.length > 0) {
			Time windowStart = new Time("second", window[0]);
			Time windowEnd = new Time("second", window[1]);
			if (start.lessThan(windowStart)) {
				start = windowStart;
			}
			if (windowEnd.lessThan(end)) {
				end = windowEnd;
			}
		}
		return Time.getIntervalList(this.getTime(), start, end);
	}

	@JsonIgnore
	public Boolean getBand() {
		String band = this.map.get("band");
		if (band != null) {
			return Boolean.valueOf(band);
		}
		return false;
	}

	@JsonIgnore
	public Boolean getStack() {
		String stack = this.map.get("stack");
		if (stack != null) {
			return Boolean.valueOf(stack);
		}
		return false;
	}

	@JsonIgnore
	public Boolean getTrajectory() {
		String trajectory = this.map.get("trajectory");
		if (trajectory != null) {
			return Boolean.valueOf(trajectory);
		}
		return false;
	}

	@JsonIgnore
	public Boolean getClear() {
		String clear = this.map.get("clear");
		if (clear != null) {
			return Boolean.valueOf(clear);
		}
		return false;
	}

	@JsonIgnore
	public Boolean getAverage() {
		String average = this.map.get("average");
		if (average != null) {
			return Boolean.valueOf(average);
		}
		return false;
	}

	@JsonIgnore
	public Double getInterval() {
		String interval = this.map.get("interval");
		if (interval != null) {
			return Double.valueOf(interval);
		}
		return 1.0;
	}

	@JsonIgnore
	public Double getSignificance() {
		String significance = this.map.get("significance");
		if (significance != null) {
			return Double.valueOf(significance);
		}
		return 0.95;
	}

	@JsonIgnore
	public Operator getOperator() {
		String operator = this.map.get("operator");
		Operator o = Operator.AND;
		if (operator != null) {
			operator = operator.toUpperCase();
			o = Operator.valueOf(operator);
		}
		return o;
	}

	@JsonIgnore
	public Boolean getSum() {
		String sum = this.map.get("sum");
		if (sum != null) {
			return Boolean.valueOf(sum);
		}
		return false;
	}

	@JsonIgnore
	public String getGroup() {
		String group = this.map.get("group");
		if (group == null) {
			group = "month";
		}
		return group;
	}

	@JsonIgnore
	public List<String> getGroupList() {
		return this.getGroupList(this.getGroup());
	}

	@JsonIgnore
	public List<String> getGroupList(String group) {
		List<String> groupList = new ArrayList<>();
		if (group.contains(",")) {
			String[] array = group.split(",");
			for (String t : array) {
				groupList.add(t);
			}
		} else {
			groupList.add(group);
		}
		return groupList;
	}

	@JsonIgnore
	public String getSeason() {
		String average = this.map.get("season");
		if (average == null) {
			average = "djf,mam,jja,son";
		}
		return average;
	}

	@JsonIgnore
	public String getRegression() {
		String regression = map.get("regression");
		if (regression == null) {
			regression = "all";
		}
		return regression;
	}

	@JsonIgnore
	public String getID() {
		return map.get("id");
	}

	@JsonIgnore
	public List<String> getIDList() {
		return this.getIDList(this.getID());
	}

	@JsonIgnore
	public List<String> getIDList(String string) {
		List<String> stringList = new ArrayList<>();
		if (string != null) {
			if (string.contains(",")) {
				String[] array = string.split(",");
				for (String t : array) {
					stringList.add(t);
				}
			} else {
				stringList.add(string);
			}
		}
		return stringList;
	}

	@JsonIgnore
	public String getPressure() {
		return map.get("pressure");
	}

	@JsonIgnore
	public Count getCount() throws Exception {
		return this.getCount(map.get("count"));
	}

//	@JsonIgnore
//	public int getCount(String count) throws Exception {
//		int c = 0;
//		if (count != null && !count.isEmpty()) {
//			boolean valid = true;
//			try {
//				c = Integer.parseInt(count);
//			} catch (NumberFormatException e) {
//				valid = false;
//			}
//			if (c < 0) {
//				valid = false;
//			}
//			if (!valid) {
//				throw new Exception("getCount(" + count + ") invalid");
//			}
//		}
//		return c;
//	}

	/**
	 * Example input: >2, <2, or =2
	 * 
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@JsonIgnore
	public Count getCount(String count) throws Exception {
		Count c = null;
		if (count != null && !count.isEmpty()) {
			c = new Count();
			if (count.indexOf(">") == 0) {
				c.operator = '>';
				c.value = Integer.parseInt(count.substring(1).trim());
			} else if (count.indexOf("<") == 0) {
				c.operator = '<';
				c.value = Integer.parseInt(count.substring(1).trim());
			} else if (count.indexOf("=") == 0) {
				c.operator = '=';
				c.value = Integer.parseInt(count.substring(1).trim());
			} else {
				throw new Exception("getCount(" + count + ") invalid");
			}
		}
		return c;
	}

	@JsonIgnore
	public List<Integer> getPressureList() {
		return this.getPressureList(this.getPressure());
	}

	@JsonIgnore
	public List<Integer> getPressureList(String pressure) {
		List<Integer> pressureList = new ArrayList<>();
		if (pressure != null) {
			if (pressure.contains(",")) {
				String[] array = pressure.split(",");
				for (String t : array) {
					pressureList.add(Integer.parseInt(t));
				}
			} else {
				pressureList.add(Integer.parseInt(pressure));
			}
		}
		return pressureList;
	}

	@JsonIgnore
	public String getElevation() {
		return map.get("elevation");
	}

	@JsonIgnore
	public double[] getRange() {
		return this.getRange(this.map.get("range"));
	}

	@JsonIgnore
	public double[] getRange(String string) {
		double[] range = new double[0];
		if (string != null) {
			String[] stringArray = string.split(":");
			if (stringArray.length == 2) {
				range = new double[2];
				try {
					range[0] = Double.parseDouble(stringArray[0]);
					range[1] = Double.parseDouble(stringArray[1]);
				} catch (NumberFormatException e) {
					range = new double[0];
				}
			}
		}
		return range;
	}

	@JsonIgnore
	public Double getDimension() throws Exception {
		return this.getDimension(map.get("dimension"));
	}

	@JsonIgnore
	public Double getDimension(String dimension) throws Exception {
		Double d = null;
		if (dimension != null && !dimension.isEmpty()) {
			boolean valid = true;
			try {
				d = Double.parseDouble(dimension);
			} catch (NumberFormatException e) {
				valid = false;
			}
			if (d <= 0) {
				valid = false;
			}
			if (!valid) {
				throw new Exception("getDimension(" + dimension + ") invalid");
			}
		}
		return d;
	}

	@JsonIgnore
	public double getResolution() throws Exception {
		return this.getResolution(map.get("resolution"));
	}

	@JsonIgnore
	public int getResolution(String resolution) throws Exception {
		int r = 1;
		if (resolution != null && !resolution.isEmpty()) {
			boolean valid = true;
			try {
				r = Integer.parseInt(resolution);
			} catch (NumberFormatException e) {
				valid = false;
			}
			if (r <= 0 || r > 1000) {
				valid = false;
			}
			if (!valid) {
				throw new Exception("invalid resolution format: " + resolution);
			}
		}
		return r;
	}

	@JsonIgnore
	public String getDuration() {
		return map.get("duration");
	}

	@JsonIgnore
	public List<Duration> getDurationList() throws Exception {
		return this.getDurationList(this.getDuration());
	}

	@JsonIgnore
	public List<Duration> getDurationList(String duration) throws Exception {
		List<Duration> durationList = null;
		if (duration != null && !duration.isEmpty()) {
			durationList = new ArrayList<>();
			Duration d = null;
			boolean valid = true;
			String[] spaceArray = duration.split(" ");
			if (spaceArray.length == 2) {
				String inequality = spaceArray[0];
				String unit = spaceArray[1];
				String value = null;
				char symbol = ' ';
				if (inequality.contains("<") && inequality.indexOf("<") == 0) {
					symbol = '<';
					value = inequality.substring(1, inequality.length());
				} else if (inequality.contains(">") && inequality.indexOf(">") == 0) {
					symbol = '>';
					value = inequality.substring(1, inequality.length());
				} else {
					valid = false;
				}
				unit = unit.toLowerCase();
				if (unit.equals("day") || unit.equals("days")) {
					switch (symbol) {
					case '>': {
						d = new Duration();
						try {
							d.startDays = Long.parseLong(value);
							d.endDays = Long.MAX_VALUE;
							durationList.add(d);
						} catch (NumberFormatException e) {
							valid = false;
						}
						break;
					}
					case '<': {
						d = new Duration();
						try {
							d.startDays = 0;
							d.endDays = Long.parseLong(value);
							durationList.add(d);
						} catch (NumberFormatException e) {
							valid = false;
						}
						break;
					}
					}
				} else if (unit.equals("hour") || unit.equals("hours")) {
					switch (symbol) {
					case '>': {
						d = new Duration();
						try {
							d.startHours = Long.parseLong(value);
							d.endHours = Long.MAX_VALUE;
							durationList.add(d);
						} catch (NumberFormatException e) {
							valid = false;
						}
						break;
					}
					case '<': {
						d = new Duration();
						try {
							d.startHours = 0;
							d.endHours = Long.parseLong(value);
							durationList.add(d);
						} catch (NumberFormatException e) {
							valid = false;
						}
						break;
					}
					}
				} else {
					valid = false;
				}
			} else {
				valid = false;
			}

			if (!valid) {
				throw new Exception("invalid duration format: " + duration);
			}
		}
		return durationList;
	}

	@JsonIgnore
	public void put(String key, String object) {
		this.map.put(this.getAlias(key), object);
	}

	@JsonIgnore
	public String getAlias(String key) {
		for (Alias alias : this.alias) {
			String string = alias.getValue(key.toLowerCase());
			if (string != null) {
				return string;
			}
		}
		return key;
	}

	@JsonIgnore
	public Object get(String key) {
		return this.map.get(key);
	}

	@JsonIgnore
	public Object remove(String key) {
		return this.map.remove(key);
	}

	@JsonIgnore
	public String[] getFilter() {
		List<String> attributeList = this.getList();
		String attribute = (this.index >= 0 && this.index < this.getList().size()) ? attributeList.get(this.index)
				: null;
		if (attribute != null) {
			String[] split = attribute.split("=");
			return split;
		} else {
			return new String[2];
		}
	}

	@JsonIgnore
	public void setIndex(int index) {
//		System.out.println("setIndex("+index+")");
		this.index = index;
	}

	@JsonIgnore
	public List<String> getList() {
		List<String> attributeList = new ArrayList<>();
		for (Entry<String, String> entry : this.map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			attributeList.add(key + "=" + value);
		}
		return attributeList;
	}

	@JsonIgnore
	public String getRegion() {
		String region = map.get("region");
		if (region == null) {
			region = "-90,-180:0,180";
		}
		return region;
	}

	@JsonIgnore
	public List<Region> getRegionList() throws Exception {
		return this.getRegionList(this.getRegion());
	}

	@JsonIgnore
	public List<Region> getRegionList(String region) throws Exception {
		List<Region> regionList = null;
		Region r = null;
		Double latitude = null;
		Double longitude = null;
		if (region != null && !region.isEmpty()) {
			regionList = new ArrayList<>();
			String[] colonArray;

			String[] barArray = new String[1];
			if (region.contains("|")) {
				barArray = region.split("\\|");
			} else {
				barArray[0] = region;
			}
//			System.out.println("barArray.length="+barArray.length);
			for (String b : barArray) {
				String c = null;
				boolean valid = true;
				if (b.contains(";")) {
					String[] array = b.split(";");
					b = array[0];
					c = array[1];
				}
				if (c != null) {
					String[] array = c.split("x");
					latitude = Double.parseDouble(array[0]);
					longitude = Double.parseDouble(array[1]);
				}
				if (b.contains(":")) {
					if (b.lastIndexOf(':') == b.indexOf(':') && b.indexOf(':') != 0
							&& b.indexOf(':') != b.length() - 1) {
						colonArray = b.split(":");
						if (colonArray.length == 2) {
							String pointA = colonArray[0];
							String pointB = colonArray[1];
							String[] pointAArray = pointA.split(",");
							String[] pointBArray = pointB.split(",");
							if (pointAArray.length == 2 && pointBArray.length == 2) {
								r = new Region();
								try {
									r.latitudeA = Double.parseDouble(pointAArray[0]);
									r.longitudeA = Double.parseDouble(pointAArray[1]);
									r.latitudeB = Double.parseDouble(pointBArray[0]);
									r.longitudeB = Double.parseDouble(pointBArray[1]);
								} catch (NumberFormatException e) {
									logger.error("NumberFormatException " + e.getMessage());
									valid = false;
								}
							}
						} else {
							valid = false;
						}
					} else {
						valid = false;
					}
				} else {
					valid = false;
				}
				if (valid) {
					if (r != null) {
						if (latitude != null && longitude != null) {
							Region tmp = new Region(r);
							int x = (int) (Math.abs(tmp.latitudeB - tmp.latitudeA) / latitude);
							int y = (int) (Math.abs(tmp.longitudeB - tmp.longitudeA) / longitude);
							for (int i = 0; i < x; i++) {
								for (int j = 0; j < y; j++) {
									double m = tmp.latitudeA + (i * latitude);
									double n = tmp.longitudeA + (j * longitude);
									r = new Region(m, n, m + latitude, n + longitude);
									regionList.add(r);
								}
							}
						} else {
							regionList.add(r);
						}
					}
				} else {
					throw new Exception("invalid region format: " + region);
				}
			}
		}
//		System.out.println("regionList.size()="+regionList.size());
		return regionList;
	}

	@JsonIgnore
	public String getFamily() {
		String family = this.map.get("family");
		return family;
	}

	@JsonIgnore
	public List<Family> getFamilyList() {
		return this.getFamilyList(this.getFamily());
	}

	@JsonIgnore
	public List<Family> getFamilyList(String family) {
		List<Family> familyList = new ArrayList<>();
		if (family != null) {
			if (family.contains(",")) {
				String[] array = family.split(",");
				for (String t : array) {
					t = t.toUpperCase();
					familyList.add(Family.valueOf(t));
				}
			} else {
				family = family.toUpperCase();
				familyList.add(Family.valueOf(family));
			}
		}
		return familyList;
	}

	@JsonIgnore
	public String getClassification() {
		String classification = this.map.get("classification");
		return classification;
	}

	@JsonIgnore
	public List<Classification> getClassificationList() {
		return this.getClassificationList(this.getClassification());
	}

	@JsonIgnore
	public List<Classification> getClassificationList(String classification) {
		List<Classification> classificationList = new ArrayList<>();
		if (classification != null) {
			if (classification.contains(",")) {
				String[] array = classification.split(",");
				for (String t : array) {
					t = t.toUpperCase();
					classificationList.add(Classification.valueOf(t));
				}
			} else {
				classification = classification.toUpperCase();
				classificationList.add(Classification.valueOf(classification));
			}
		}
		return classificationList;
	}

	/**
	 * Example input: >2, <2, or =2
	 * 
	 * @param count
	 * @return
	 * @throws Exception
	 */
//	public Count getCount(String count) throws Exception {
//		Count c = this.count;
//		if (count != null && !count.isEmpty()) {
//			if (count.indexOf(">") == 0) {
//				c.operator = '>';
//				c.value = Integer.parseInt(count.substring(1).trim());
//			} else if (count.indexOf("<") == 0) {
//				c.operator = '<';
//				c.value = Integer.parseInt(count.substring(1).trim());
//			} else if (count.indexOf("=") == 0) {
//				c.operator = '=';
//				c.value = Integer.parseInt(count.substring(1).trim());
//			} else {
//				throw new Exception("invalid count format: " + count);
//			}
//		}
//		return c;
//	}

	@JsonIgnore
	public Map<Family, List<Classification>> getFamilyClassMap() {
		return CycloneEvent.getFamilyClassMap(this.getFamilyList(), this.getClassificationList());
	}

	@JsonIgnore
	public Script getScript() throws Exception {
		Script script = new Script();
		Operator operator = this.getOperator();
		switch (operator) {
		case AND: {
			script.queryList.add(this);
			break;
		}
		case OR: {
			List<String> variableList = this.getVariableList();
			List<String> sourceList = this.getSourceList();
			List<String> timeList = this.getTimeList();
			List<Region> regionList = this.getRegionList();
			List<String> groupList = this.getGroupList();
			List<Integer> pressureList = this.getPressureList();
			String regression = this.getRegression();
			boolean average = this.getAverage();
			boolean sum = this.getSum();
			Map<Family, List<Classification>> familyClassMap = this.getFamilyClassMap();
			List<Query> queryList = new ArrayList<>();
			queryList.add(new Query());
			if (timeList.size() > 0) {
				List<Query> qList = new ArrayList<>();
				Iterator<Query> queryIterator = queryList.iterator();
				while (queryIterator.hasNext()) {
					Query q = queryIterator.next();
					for (String t : timeList) {
						Query query = new Query(q);
						query.map.put("time", t);
						qList.add(query);
					}
					queryIterator.remove();
				}
				queryList = qList;
			}
			if (sourceList.size() > 0) {
				List<Query> qList = new ArrayList<>();
				Iterator<Query> queryIterator = queryList.iterator();
				while (queryIterator.hasNext()) {
					Query q = queryIterator.next();
					for (String s : sourceList) {
						Query query = new Query(q);
						query.map.put("source", s);
						qList.add(query);
					}
					queryIterator.remove();
				}
				queryList = qList;
			}
			if (variableList.size() > 0) {
				List<Query> qList = new ArrayList<>();
				Iterator<Query> queryIterator = queryList.iterator();
				while (queryIterator.hasNext()) {
					Query q = queryIterator.next();
					for (String s : variableList) {
						Query query = new Query(q);
						query.map.put("variable", s);
						qList.add(query);
					}
					queryIterator.remove();
				}
				queryList = qList;
			}
			if (groupList.size() > 0) {
				List<Query> qList = new ArrayList<>();
				Iterator<Query> queryIterator = queryList.iterator();
				while (queryIterator.hasNext()) {
					Query q = queryIterator.next();
					for (String g : groupList) {
						Query query = new Query(q);
						query.map.put("group", g);
						qList.add(query);
					}
					queryIterator.remove();
				}
				queryList = qList;
			}
			if (regionList != null && regionList.size() > 0) {
				List<Query> qList = new ArrayList<>();
				Iterator<Query> queryIterator = queryList.iterator();
				while (queryIterator.hasNext()) {
					Query q = queryIterator.next();
					for (Region t : regionList) {
						Query query = new Query(q);
						query.map.put("region", t.toString());
						qList.add(query);
					}
					queryIterator.remove();
				}
				queryList = qList;
			}
			if (pressureList.size() > 0) {
				List<Query> qList = new ArrayList<>();
				Iterator<Query> queryIterator = queryList.iterator();
				while (queryIterator.hasNext()) {
					Query q = queryIterator.next();
					for (Integer g : pressureList) {
						Query query = new Query(q);
						query.map.put("pressure", String.valueOf(g));
						qList.add(query);
					}
					queryIterator.remove();
				}
				queryList = qList;
			}
			if (familyClassMap.size() > 0) {
				List<Query> qList = new ArrayList<>();
				Iterator<Query> queryIterator = queryList.iterator();
				while (queryIterator.hasNext()) {
					Query q = queryIterator.next();
					for (Entry<Family, List<Classification>> entry : familyClassMap.entrySet()) {
						Query query = new Query(q);
						Family family = entry.getKey();
						List<Classification> cList = entry.getValue();
						if (cList != null && cList.size() > 0) {
							for (Classification c : cList) {
								query = new Query(q);
								if (c != null) {
									query.map.put("family", family.toString());
									query.map.put("classification", c.toString());
								} else {
									query.map.put("family", family.toString());
								}
								qList.add(query);
							}
						} else {
							query.map.put("family", family.toString());
							qList.add(query);
						}

					}
					queryIterator.remove();
				}
				queryList = qList;
			}
			for (Query q : queryList) {
				if (regression != null) {
					q.map.put("regression", regression);
				}
				q.map.put("average", String.valueOf(average));
				q.map.put("sum", String.valueOf(sum));
				q.map.put("window", this.map.get("window"));
				q.map.put("dimension", this.map.get("dimension"));
				q.map.put("duration", this.map.get("duration"));
				q.map.put("range", this.map.get("range"));
			}
			script.queryList = queryList;
			break;
		}
		}
		return script;
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
//this.calendar = calendar;
//if(time == null) {
//time = this.simpleDateFormat.format(this.calendar.getTime());//"yyyy/MM/dd HH:mm:ss"
//}
//} else {
//String b = region;
//if (b.contains(":")) {
//	if (b.lastIndexOf(':') == b.indexOf(':') && b.indexOf(':') != 0
//			&& b.indexOf(':') != b.length() - 1) {
//		colonArray = b.split(":");
//		if (colonArray.length == 2) {
//			String pointA = colonArray[0];
//			String pointB = colonArray[1];
//			String[] pointAArray = pointA.split(",");
//			String[] pointBArray = pointB.split(",");
//			if (pointAArray.length == 2 && pointBArray.length == 2) {
//				r = new Region();
//				try {
//					r.latitudeA = Double.parseDouble(pointAArray[0]);
//					r.longitudeA = Double.parseDouble(pointAArray[1]);
//					r.latitudeB = Double.parseDouble(pointBArray[0]);
//					r.longitudeB = Double.parseDouble(pointBArray[1]);
//					regionList.add(r);
//				} catch (NumberFormatException e) {
//					valid = false;
//				}
//			}
//		} else {
//			valid = false;
//		}
//	} else {
//		valid = false;
//	}
//} else {
//	valid = false;
//}
//}
