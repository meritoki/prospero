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
package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.speed;

import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Speed extends Cyclone {

	static Logger logger = LoggerFactory.getLogger(Speed.class.getName());

	public Speed() {
		super("Speed");
		this.unit = "m/s";
		this.format = "##.##E0";
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void setMatrix(List<Event> eventList) {
		List<Time> timeList = this.setSpeedCoordinateMatrix(this.dataMatrix, this.coordinateMatrix, eventList);
		for (Time t : timeList) {
			if (!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
		this.tileList = this.getTileList();
		this.bandList = this.getBandList(this.tileList);
		this.initTileMinMax();
	}

	public List<Time> setSpeedCoordinateMatrix(float[][][] speedMatrix, int[][][] coordinateMatrix,
			List<Event> eventList) {
		List<Time> timeList = null;
		Time startTime = new Time("month", this.startCalendar);
		Time endTime = new Time("month", this.endCalendar);
		if (eventList != null) {
			timeList = new ArrayList<>();
			for (Event e : eventList) {
				if (e.flag) {
					Coordinate c = ((CycloneEvent) e).getHalfTimeLowerMostCoordinate(null);
					if (c != null) {
						Time time = new Time(c.getYear(), c.getMonth(), -1, -1, -1, -1);
						if (startTime.lessThan(time) && time.lessThan(endTime)) {
							int x = (int) (((c.latitude + this.latitude) / 2 * this.resolution) % (this.latitude * this.resolution));
							int y = (int) (((c.longitude + this.longitude / 2) * this.resolution)
									% (this.longitude * this.resolution));
							int z = c.getMonth() - 1;
							coordinateMatrix[x][y][z]++;
							speedMatrix[x][y][z] += ((CycloneEvent) e).getMeanSpeed();
							if (!timeList.contains(time)) {
								timeList.add(time);
							}
						}
					}
				}
			}
		}
		return timeList;
	}

	@Override
	public Index getIndex(Time key, List<Event> eventList) {
		int[][][] coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
		float[][][] speedMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
		Index index = null;
		List<Time> timeList = this.setSpeedCoordinateMatrix(speedMatrix, coordinateMatrix, eventList);
		this.initMonthArray(timeList);
		this.initYearMap(timeList);
		List<Tile> tileList = this.getTileList(coordinateMatrix, speedMatrix);
		if (this.averageFlag) {
			index = Tile.getAverage(key, tileList);
		} else if (this.sumFlag) {
			index = Tile.getSum(key, tileList);
		} else {
			index = super.getIndex(key, eventList);
		}
		return index;
	}
}
//@Override
//public List<Tile> getTileList() {
//	return this.getTileList(this.coordinateMatrix, this.dataMatrix);
//}
//
//public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] speedMatrix) {
//	List<Tile> tileList = new ArrayList<>();
//	int yearCount = this.getYearCount();
//	int monthCount = this.getMonthCount();
//	Tile tile;
//	int count;
//	float speed;
//	float mean;
//	float meanSum;
//	float value;
//	for (int i = 0; i < coordinateMatrix.length; i += this.dimension) {
//		for (int j = 0; j < coordinateMatrix[i].length; j += this.dimension) {
//			meanSum = 0;
//			for (int m = 0; m < 12; m++) {
//				count = 0;
//				speed = 0;
//				for (int a = i; a < (i + this.dimension); a++) {
//					for (int b = j; b < (j + this.dimension); b++) {
//						if (a < this.latitude && b < this.longitude) {
//							count += coordinateMatrix[a][b][m];
//							speed += speedMatrix[a][b][m];
//						}
//					}
//				}
//				mean = (count > 0) ? speed / count : 0;
//				count = this.monthArray[m];
//				mean = (count > 0) ? mean / count : mean;
//				meanSum += mean;
//			}
//			value = meanSum;
//			if (this.monthFlag) {
//				value /= monthCount;
//			} else if (this.yearFlag) {
//				value /= yearCount;// value /= ((double) this.getMonthCount() / (double) yearCount);
//			}
//			tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution,
//					this.dimension, value);
//			if (this.region != null) {
//				if (this.region.contains(tile)) {
//					tileList.add(tile);
//				}
//			} else if (this.regionList != null) {
//				for (Region region : this.regionList) {
//					if (region.contains(tile)) {
//						tileList.add(tile);
//						break;
//					}
//				}
//			} else {
//				tileList.add(tile);
//			}
//		}
//	}
//
//	return tileList;
//}
//Time time = new Time(c.getYear(), c.getMonth(), -1, -1, -1, -1);
//@Override
//public void setEventList(List<Event> eventList, boolean reset) {
////	logger.info("setEventList("+eventList.size()+","+reset+")");
//	if (reset) {
//		this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
//		this.speedMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
//		this.dateList = new ArrayList<>();
//	}
//	for (Event e : eventList) {
//		if (e.flag) {
//			Coordinate p = ((CycloneEvent) e).getHalfTimeLowerMostPoint(null);
//			if (p != null) {
//				coordinateMatrix[(int) ((p.latitude + this.latitude)
//						* this.resolution)][(int) ((p.longitude + this.longitude / 2) * this.resolution)][p
//								.getMonth() - 1]++;
//				speedMatrix[(int) ((p.latitude + this.latitude)
//						* this.resolution)][(int) ((p.longitude + this.longitude / 2) * this.resolution)][p
//								.getMonth() - 1] += ((CycloneEvent) e).getMeanSpeed();
//				String date = p.getYear() + "-" + p.getMonth();
//				if (!this.dateList.contains(date)) {
//					this.dateList.add(date);
//				}
//			}
//		}
//	}
//}
//@Override
//public void addSeriesIndex(Series series, Time key, List<Event> eventList) {
////	logger.info("setIndexList("+series.indexList.size()+"," + eventMap.size() + ","+reset+")");
////	if(reset) {
////		series.indexList = new ArrayList<>();		
////	}
////	if (eventMap.size() > 0) {
//		int[][][] bufferCoordinateMatrix = this.coordinateMatrix;
//		float[][][] bufferSpeedMatrix = this.speedMatrix;
//		List<String> bufferDateList = this.dateList;
//		{
////		for (Entry<Time, List<Event>> eventEntry : eventMap.entrySet()) {
////			Time key = eventEntry.getKey();
////			String[] keyArray = key.split(",");
////			Index index = new Index();
////			Integer year = (keyArray.length > 0) ? Integer.parseInt(keyArray[0]) : null;
////			Integer month = (keyArray.length > 1) ? Integer.parseInt(keyArray[1]) : null;
////			Integer day = (keyArray.length > 2) ? Integer.parseInt(keyArray[2]) : null;
//			Index index = new Index();
//			Integer year = (key.year != -1) ? key.year : null;
//			Integer month = (key.month != -1) ? key.month : null;
//			Integer day = (key.day != -1) ? key.day : null;
//			index.startCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//					(day != null) ? day : 0, 24, 0, 0);
//			if(year != null) {
//				if(month != null) {
//					if(day != null) {
//						index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0, (day != null) ? day : 0,
//								24, 0, 0);
//					} else {
//						//handle to end of month
//						YearMonth yearMonthObject = YearMonth.of(year, month);
//						day = yearMonthObject.lengthOfMonth();}
//						index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0, (day != null) ? day : 0,
//							24, 0, 0);
//				} else {
//					//handle to end of year
//					month = 12;
//					YearMonth yearMonthObject = YearMonth.of(year, month);
//					day = yearMonthObject.lengthOfMonth();
//					index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0, (day != null) ? day : 0,
//						24, 0, 0);
//				}
//			}
////			List<Event> eList = eventEntry.getValue();
////			this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude
////					* resolution)][monthCount];
////			this.speedMatrix = new float[(int) (latitude * resolution)][(int) (longitude
////					* resolution)][monthCount];
//			this.setEventList(eventList,true);
//			this.initMonthArray();
//			this.initYearMap();
//			List<Tile> tileList = this.getTileList();
//			if (average) {
//				StandardDeviation standardDeviation = new StandardDeviation();
//				Mean mean = new Mean();
//				for (Tile tile : tileList) {
//					standardDeviation.increment(tile.value);
//					mean.increment(tile.value);
//				}
//				double value = mean.getResult();
//				if (!Double.isNaN(value)) {
//					index.value = value;
//					index.map.put("N", standardDeviation.getN());
//					index.map.put("standardDeviation", standardDeviation.getResult());
//					if (index.value > 0 || index.value < 0) {
////						indexList.add(index);
//						series.addIndex(index);
////						this.addIndex(index);
//					}
//				}
//			} else if (sum) {
//				double sum = 0;
//				for (Tile tile : tileList) {
//					sum += tile.value;
//				}
//				index.value = sum;
//				if (index.value > 0 || index.value < 0) {
////					indexList.add(index);
//					series.addIndex(index);
////					this.addIndex(index);
//				}
//			} else {
//				for (Tile tile : tileList) {
//					Index i = new Index(index);
//					i.value = tile.value;
//					i.map.put("latitude",tile.latitude);
//					i.map.put("longitude",tile.longitude);
//					i.map.put("dimension",tile.dimension);
////					indexList.add(i);
//					series.addIndex(i);
////					this.addIndex(i);
//				}
//			}
//		}
//		this.coordinateMatrix = bufferCoordinateMatrix;
//		this.speedMatrix = bufferSpeedMatrix;
//		this.dateList = bufferDateList;
////	}
////	return indexList;
//}
//}
//this.initMonthArray();
//this.initYearMap();
//public void setEventList(List<Event> eventList) {
//
//	
//	if (eventList != null) {
////		this.regionList = cycloneSource.regionList;
////		this.dimension = cycloneSource.dimension;
////		this.monthFlag = cycloneSource.month;
////		this.yearFlag = cycloneSource.year;
////		this.stackFlag = cycloneSource.stack;
////		this.bandFlag = cycloneSource.band;
////		this.cubeFlag = cycloneSource.cube;
//		this.initPointMatrix(eventList);
//		if (this.stackFlag) {
//			List<Integer> levelList = this.getEventLevelList(eventList);
//			for (Integer level : levelList) {
//				this.coordinateMatrix = this.pointMatrixMap.get(level);
//				this.speedMatrix = this.speedMatrixMap.get(level);
//				List<Tile> tileList = this.getTileList(this.coordinateMatrix, this.speedMatrix);
//				this.tileListMap.put(level.toString(), tileList);
//			}
//		} else {
//			this.tileList = this.getTileList(this.coordinateMatrix, this.speedMatrix);
//			if (this.bandFlag) {
//				List<Double> tileLatitudeList = this.getTileLatitudeList(this.tileList);
//				for (Double latitude : tileLatitudeList) {
//					List<Tile> bandTileList = new ArrayList<>();
//					for (Tile t : this.tileList) {
//						if (latitude.equals(t.latitude)) {
//							bandTileList.add(t);
//						}
//					}
//					Band band = new Band(bandTileList, latitude);
//					this.bandList.add(band);
//				}
//			}
//		}
//		this.initTileMinMax();
//	}
//
//}
//

//@Override
//public void paint(Graphics graphics) throws Exception {
//	if (this.load) {
//		this.init();
//		super.paint(graphics);
//	}
//}
//String date;
//if (this.stackFlag) {
//	List<Integer> levelList = this.getEventLevelList(eventList);
//	for (Integer level : levelList) {
//		this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//		this.speedMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//		for (Event e : eventList) {
//			Coordinate p = ((CycloneEvent)e).getHalfTimeLowerMostPoint(level);
//			if (p != null && p.flag && ((Integer) p.attribute.map.get("level")).equals(level)) {
//				this.coordinateMatrix[(int) ((p.latitude + this.latitude)
//						* this.resolution)][(int) ((p.longitude + this.longitude / 2) * this.resolution)][p
//								.getMonth() - 1]++;
//				this.speedMatrix[(int) ((p.latitude + this.latitude)
//						* this.resolution)][(int) ((p.longitude + this.longitude / 2) * this.resolution)][p
//								.getMonth() - 1] += ((CycloneEvent)e).getMeanSpeed();
//				date = p.getYear() + "-" + p.getMonth();
//				if (!this.dateList.contains(date)) {
//					this.dateList.add(date);
//				}
//			}
//		}
//		this.pointMatrixMap.put(level, this.coordinateMatrix);
//		this.speedMatrixMap.put(level, this.speedMatrix);
//	}
//} else {
//public List<Index> getIndexList(String season, String average, String variable, List<Event> eventList) {
//	List<Index> indexList = null;
//	if (eventList != null) {
//		indexList = new ArrayList<>();
//		Map<String, List<Event>> eventMap = new TreeMap<>();
//		for (Event e : eventList) {
//			Calendar calendar = e.getStartCalendar();
//			Integer day = null;
//			Integer month = null;
//			Integer year = null;
//			if(average == null) {
//				average = "day";
//			}
//			switch(average) {
//			case "day": {
//				day = calendar.get(Calendar.DAY_OF_MONTH);
//			}
//			case "month" :{
//				month = calendar.get(Calendar.MONTH);
//			}
//			case "year":{
//				year = calendar.get(Calendar.YEAR);
//			}
//			}
//			
//			String key = null;
//			if(season != null) {
//				if(season.contains(this.getSeason(month))) {
//					key = year +((month != null)?"," + String.format("%02d", month+1):"")+((day != null)?"," + String.format("%02d", day):"");
//				}
//			} else {
//				key = year +((month != null)?"," + String.format("%02d", month+1):"")+((day != null)?"," + String.format("%02d", day):"");
//			} 
//			if(key != null) {
//				List<Event> eList = eventMap.get(key);
//				if (eList == null) {
//					eList = new ArrayList<>();
//				}
//				eList.add(e);
//				eventMap.put(key,eList);
//			}
//		}
//		for (Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
//			String key = eventEntry.getKey();
//			String[] keyArray = key.split(",");
//			Index index = new Index();
//			Integer year = (keyArray.length>0)?Integer.parseInt(keyArray[0]):null;
//			Integer month = (keyArray.length>1)?Integer.parseInt(keyArray[1]):null;
//			Integer day = (keyArray.length>2)?Integer.parseInt(keyArray[2]):null;
//			index.startCalendar = new GregorianCalendar(year, (month != null)?month - 1:0, (day !=null)?day:0, 0, 0, 0);
//			List<Event> eList = eventEntry.getValue();
//			this.initPointMatrix(eList);
//			List<Tile> tileList = this.getTileList(this.coordinateMatrix,this.speedMatrix);
//			double sum = 0;
//			for (Tile tile : tileList) {
//				switch (variable) {
//				case "Average": {
//					sum += tile.value;
//					break;
//				}
//				}
//			}
//			index.value = sum / tileList.size();
//			indexList.add(index);
//		}
//
//	}
//	System.out.println("getIndexList("+variable+","+eventList.size()+") indexList="+indexList);
//	return indexList;
//}
//@Override
//public List<Plot> getPlotList() throws Exception {
//	List<Plot> plotList = new ArrayList<>();
//	String regression = this.query.map.get("regression");
//	String average = this.query.map.get("average");
//	String season =  this.query.map.get("season");
//	int[] range = this.getRange(this.query.map.get("range"));
//	for (Entry<String, Boolean> variable : this.variableMap.entrySet()) {
//		String variableKey = variable.getKey();
//		Boolean variableLoad = variable.getValue();
//		if (variableLoad) {
//			Plot plot = null;
//			List<Index> indexList = null;
//			String x = "Time";
//			String y = null;
//			switch (variableKey) {
//			case "Average": {
//				indexList = this.getIndexList(season,average,"Average", this.eventList);
//				y = "Average Speed (m/s)";
//				break;
//			}
//			}
//			if (indexList != null) {
//				List<List<Index>> blackIndexMatrix = new ArrayList<>();
//				List<List<Index>> colorPointMatrix = new ArrayList<>();
//				blackIndexMatrix.add((indexList));
//				colorPointMatrix = getRegression(regression, indexList);
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackIndexMatrix, colorPointMatrix);
//				if(range.length == 2) {
//					plot.setYMin(range[0]);
//					plot.setYMax(range[1]);
//				}
//				plot.setTitle(this.name + " " + variableKey);
//				plot.setXLabel(x);
//				plot.setYLabel(y);
//				plotList.add(plot);
//			}
//		}
//	}
//	return plotList;
//}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.add(sourceUUID);
//	}
//}

//@Override
//public void init() throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	Object[] objectArray = (Object[]) this.data.get(sourceUUID, this.query);
//	this.eventList = (List<Event>) objectArray[0];
//	this.regionList = (List<Region>) objectArray[1];
//	if (this.eventList != null) {
//		if (this.query.getTime() == null) {
//			for (int i = 0; i < this.eventList.size(); i++) {
//				CycloneEvent event = (CycloneEvent) this.eventList.get(i);
//				this.coordinateList = event.coordinateList;
//				if (event.containsCalendar(calendar)) {
//					this.setCalendarCoordinateList(calendar, this.coordinateList);
//				} 
//			}
//		}
//		this.setEventList(this.eventList);
//	}
//}
//public Speed(List<CycloneSource> sourceList) {
//	for(CycloneSource s: sourceList) {
//		this.init(s);
//	}
//}
