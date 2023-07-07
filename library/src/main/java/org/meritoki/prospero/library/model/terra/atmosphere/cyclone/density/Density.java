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
package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.density;

import java.util.List;

import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Density extends Cyclone {

	static Logger logger = LoggerFactory.getLogger(Density.class.getName());

	public Density() {
		super("Density");
		this.format = "##.##E0";
		this.tileFlag = true;
	}

	public Density(String name) {
		super(name);
		this.format = "##.##E0";
		this.tileFlag = true;
	}

	@Override
	public List<Tile> getTileList() {
		return this.getTileList(this.coordinateMatrix);
	}

	@Override
	public Index getIndex(Time key, List<Event> eventList) {
		int[][][] coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
		Index index = null;
		List<Time> timeList = this.setCoordinateMatrix(coordinateMatrix, eventList);
		this.initMonthArray(timeList);
		this.initYearMap(timeList);
		List<Tile> tileList = this.getTileList(coordinateMatrix);
		this.timeTileMap.put(key,tileList);//All Tiles at Moments in Time
		if (averageFlag) {
			index = Tile.getAverage(key, tileList);
		} else if (sumFlag) {
			index = Tile.getSum(key, tileList);
		} else {
			index = super.getIndex(key, eventList);
		}
		return index;
	}
}
//public Index getIndex(List<Tile> tileList) {
//Index index = null;
//if (average) {
//	StandardDeviation standardDeviation = new StandardDeviation();
//	Mean mean = new Mean();
//	for (Tile tile : tileList) {
//		standardDeviation.increment(tile.value);
//		mean.increment(tile.value);
//	}
//	double value = mean.getResult();
//	if (!Double.isNaN(value) && value != 0) {
//		index = key.getIndex();
//		index.value = value;
//		index.map.put("N", standardDeviation.getN());
//		index.map.put("standardDeviation", standardDeviation.getResult());
//	}
//} else if (sum) {
//	double sum = 0;
//	for (Tile tile : tileList) {
//		sum += tile.value;
//	}
//	index = key.getIndex();
//	index.value = sum;
//} else {
//	index = super.getIndex(key, eventList);
//}
//}
//public List<Tile> getTileList(int[][][] coordinateMatrix) {
//List<Tile> tileList = new ArrayList<>();
//int yearCount = this.getYearCount();
//int monthCount = this.getMonthCount();
//Tile tile;
//int count;
//int density;
//double weight;
//double weightedDensity;
//double weightedDensityQuotient;
//double weightedDensityQuotientSum = 0;
//double value;
//// cycle through each tile
//for (int i = 0; i < coordinateMatrix.length; i += dimension) {
//	for (int j = 0; j < coordinateMatrix[i].length; j += dimension) {
//		weightedDensityQuotientSum = 0;
//		for (int m = 0; m < 12; m++) {// for each month
//			density = 0;// each tile in a given month has a density;
//			int x = (int) (i + dimension);
//			int y = (int) (j + dimension);
//			//We do not want the following nested for loop to fail
//			//We must check if x and y with dimension added will succeed
//			//If x or y go over the possible values of the Coordinate Matrix (0 <= x <= latitude, 0 <= y <= longitude)
//			//The nested for loop can sum what it has without a problem, especially with Odd dimensions
////			if (x >= latitude) {
////				x = (int) (latitude);
////			}
////			if (y >= longitude) {
////				y = (int) (longitude);
////			}
//			for (int a = i; a < x; a++) {
//				for (int b = j; b < y; b++) {
//					if (a < this.latitude && b < this.longitude) {
//					density += coordinateMatrix[a][b][m];// density is the sum of count within tile for a given
//					// month;
//					}
//				}
//			}
//			weight = this.getArea(i - this.latitude, j - this.longitude / 2, dimension); // this.getArea(dimension);
//			weightedDensity = (weight > 0) ? density / weight : 0;
//			count = this.monthArray[m];
//			weightedDensityQuotient = (count > 0) ? weightedDensity / count : weightedDensity;
//			weightedDensityQuotientSum += weightedDensityQuotient;
//		}
//		value = weightedDensityQuotientSum;
//		if (this.monthFlag) {
//			value /= monthCount;
//		} else if (this.yearFlag) {
////			value /= yearCount;
//		}
//		tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution,
//				dimension, value);
//
//		if (this.region != null) {
//			if (this.region.contains(tile)) {
//				tileList.add(tile);
//			}
//		} else if (this.regionList != null) {
//			for (Region region : this.regionList) {
//				if (region.contains(tile)) {
//					tileList.add(tile);
//					break;
//				}
//			}
//		} else {
//			tileList.add(tile);
//		}
//	}
//}
//return tileList;
//}
//@Override
//public void setIndexList(Series series, Map<Time, List<Event>> eventMap, boolean reset) {
////	logger.info("setIndexList(" +series+","+ eventMap.size() + ","+reset+")");
//	if(reset) {
//		series.indexList = new ArrayList<>();		
//	}
//	if (eventMap.size() > 0) {
//		int[][][] bufferCoordinateMatrix = this.coordinateMatrix;
//		List<String> bufferDateList = this.dateList;
//		for (Entry<Time, List<Event>> eventEntry : eventMap.entrySet()) {
//			Time key = eventEntry.getKey();
////			String[] keyArray = key.split(",");
////			Index index = new Index();
////			Integer year = (keyArray.length > 0) ? Integer.parseInt(keyArray[0]) : null;
////			Integer month = (keyArray.length > 1) ? Integer.parseInt(keyArray[1]) : null;
////			Integer day = (keyArray.length > 2) ? Integer.parseInt(keyArray[2]) : null;
//			Index index = new Index();
//			Integer year = (key.year != -1) ? key.year : null;
//			Integer month = (key.month != -1) ? key.month : null;
//			Integer day = (key.day != -1) ? key.day : null;
////			logger.info("setIndexList("+eventMap.size()+") year="+year+" month="+month+" day="+day);
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
//			List<Event> eList = eventEntry.getValue();
////			this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude
////					* resolution)][monthCount];
////			this.dateList = new ArrayList<>();
//			this.setEventList(eList,true);
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
////				logger.info("getIndexList("+eventMap.size()+") value="+value);
//				if (!Double.isNaN(value)) {
//					index.value = value;
//					index.map.put("N", standardDeviation.getN());
//					index.map.put("standardDeviation", standardDeviation.getResult());
//					if (index.value > 0 || index.value < 0) {
////						series.indexList.add(index);
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
//				if(index.value>0) {
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
//		this.dateList = bufferDateList;
//	}
////	logger.info("setIndexList(" +series+","+ eventMap.size() + ","+reset+")");
////	return indexList;
//}
//@Override
//public Plot getPlot(List<Index> indexList) throws Exception {
//	Plot plot = null;
//	if (indexList != null) {
//		List<List<Index>> blackIndexMatrix = new ArrayList<>();
//		List<List<Index>> colorPointMatrix = new ArrayList<>();
//		blackIndexMatrix.add((indexList));
//		List<Regression> regressionList = getRegression(indexList);
//		colorPointMatrix = this.getMatrix(regressionList);
//		plot = new TimePlot(this.startCalendar, this.endCalendar, blackIndexMatrix, colorPointMatrix);
//		if (range != null && range.length == 2) {
//			plot.setYMin(range[0]);
//			plot.setYMax(range[1]);
//		}
//		String title = this.name + " " + ((this.average)?" Average":"")+ ((this.sum)?" Sum":"");
//		plot.setTitle(title);
//		plot.setXLabel("Time");
//		plot.tableList.add(new Table(this.name + " Regression", Regression.getTableModel(regressionList)));
//		plot.tableList.add(new Table(title, Index.getTableModel(indexList)));
//	}
//	return plot;
//}
//public List<Index> getIndexList(String season, String average, String variable, List<Event> eventList) {
//List<Index> indexList = null;
//if (eventList != null) {
//	indexList = new ArrayList<>();
//	Map<String, List<Event>> eventMap = new TreeMap<>();
//	for (Event e : eventList) {
//		Calendar calendar = e.getStartCalendar();
//		Integer day = null;
//		Integer month = null;
//		Integer year = null;
//		if(average == null) {
//			average = "day";
//		}
//		switch(average) {
//		case "day": {
//			day = calendar.get(Calendar.DAY_OF_MONTH);
//		}
//		case "month" :{
//			month = calendar.get(Calendar.MONTH);
//		}
//		case "year":{
//			year = calendar.get(Calendar.YEAR);
//		}
//		}
//		
//		String key = null;
//		if(season != null) {
//			if(season.contains(this.getSeason(month))) {
//				key = year +((month != null)?"," + String.format("%02d", month+1):"")+((day != null)?"," + String.format("%02d", day):"");
//			}
//		} else {
//			key = year +((month != null)?"," + String.format("%02d", month+1):"")+((day != null)?"," + String.format("%02d", day):"");
//		} 
//		if(key != null) {
//			List<Event> eList = eventMap.get(key);
//			if (eList == null) {
//				eList = new ArrayList<>();
//			}
//			eList.add(e);
//			eventMap.put(key,eList);
//		}
//	}
//	for (Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
//		String key = eventEntry.getKey();
//		String[] keyArray = key.split(",");
//		Index index = new Index();
//		Integer year = (keyArray.length>0)?Integer.parseInt(keyArray[0]):null;
//		Integer month = (keyArray.length>1)?Integer.parseInt(keyArray[1]):null;
//		Integer day = (keyArray.length>2)?Integer.parseInt(keyArray[2]):null;
//		index.startCalendar = new GregorianCalendar(year, (month != null)?month - 1:0, (day !=null)?day:0, 0, 0, 0);
//		List<Event> eList = eventEntry.getValue();
//		this.initCoordinateMatrix(eList);
//		List<Tile> tileList = this.getTileList(this.coordinateMatrix);
//		double sum = 0;
//		for (Tile tile : tileList) {
//			switch (variable) {
//			case "Average": {
//				sum += tile.value;
//				break;
//			}
//			}
//		}
//		index.value = sum / tileList.size();
//		indexList.add(index);
//	}
//	System.out.println("getIndexList("+variable+","+eventList.size()+") indexList="+indexList);
//}
//
//return indexList;
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
//				y = "Average Density";
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
//public void initCoordinateMatrix(List<Event> eventList) {
////System.out.println("initPointMatrix(" + eventList.size() + ")");
//coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//dateList = new ArrayList<>();
//
//
//}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.add(sourceUUID);
//	}
//}

//Can be called by another function to set event list based on the query
//public void init(Calendar calendar) throws Exception {
//@Override
//public void init() throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	Query query = null;
//	if(this.queryStack.size() > 0) {
//		query = this.queryStack.poll();
//	}
//	if(!this.query.equals(query)) {
//		Object object = this.data.get(sourceUUID, this.query);
//		this.eventList = (List<Event>) object;
//		if (this.eventList != null) {
//			this.queryStack.push(this.query);
//			if(this.query.getTime() == null) {
//				for (int i = 0; i < this.eventList.size(); i++) {
//					CycloneEvent event = (CycloneEvent)this.eventList.get(i);
//					this.coordinateList = event.coordinateList;
//					if (event.containsCalendar(calendar)) {
//						this.setCalendarCoordinateList(calendar,this.coordinateList);
//					} else {
//						event.flag = false;
//					}
//				}
//			}
//			this.setEventList(this.eventList);
//		}
//	}
//}
//

//List<Coordinate> coordinateList = this.projection.getCoordinateList(0,
//this.calendarCoordinateList(this.calendar, event.coordinateList));
//if (coordinateList != null) {
//for (Coordinate c : coordinateList) {
//if (c != null && c.flag) {
//	graphics.setColor(this.chroma.getColor(i, 0, this.eventList.size()));
//	graphics.fillOval((int) ((c.point.x) * this.projection.scale),
//			(int) ((c.point.y) * this.projection.scale), (int) 4, (int) 4);
//}
//}
//}

//public void setEventList(List<CycloneEvent> eventList) {
//	String date;
//	if (this.stackFlag) {
//		List<Integer> levelList = this.getEventLevelList(eventList);
//		for (Integer level : levelList) {
//			this.pointMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//			for (CycloneEvent e : eventList) {
//				for (Coordinate p : e.coordinateList) {
//					if (p.flag && ((Integer) p.attribute.map.get("pressure")).equals(level)) {
//						int x = (int) ((p.latitude + this.latitude) * this.resolution);
//						int y = (int) ((p.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//						int z = p.getMonth() - 1;
//						this.pointMatrix[x][y][z]++;
//						date = p.getYear() + "-" + p.getMonth();
//						if (!this.dateList.contains(date)) {
//							this.dateList.add(date);
//						}
//
//					}
//				}
//			}
//			this.pointMatrixMap.put(level, this.pointMatrix);
//		}
//	} else {
//		for (CycloneEvent e : eventList) {
//			for (Coordinate p : e.coordinateList) {
//				if (p.flag) {
//					int x = (int) ((p.latitude + this.latitude) * this.resolution);
//					int y = (int) ((p.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//					int z = p.getMonth() - 1;
//					pointMatrix[x][y][z]++;
//					date = p.getYear() + "-" + p.getMonth();
//					if (!this.dateList.contains(date)) {
//						this.dateList.add(date);
//					}
//				}
//			}
//		}
//	}
//	this.initMonthArray();
//	this.initYearMap();
//}

//if (eventList != null) {
//	this.regionList = cycloneSource.regionList;
//	this.dimension = cycloneSource.dimension;
//	this.monthFlag = cycloneSource.month;
//	this.yearFlag = cycloneSource.year;
//	this.stackFlag = cycloneSource.stack;
//	this.bandFlag = cycloneSource.band;
//	this.cubeFlag = cycloneSource.cube;
//	this.setEventList(eventList);
//	if (this.stackFlag) {
//		System.out.println("init(...) this.stack=true");
//		for (Entry<Integer, int[][][]> entry : pointMatrixMap.entrySet()) {
//			int key = entry.getKey();
//			int[][][] pointMatrix = entry.getValue();
//			List<Tile> tileList = this.getTileList(pointMatrix);
//			this.tileListMap.put(key, tileList);
//		}
//		System.out.println("init(...) this.tileListMap.size()=" + this.tileListMap.size());
//	} else {
//		this.tileList = this.getTileList(this.pointMatrix);
//		if (this.bandFlag) {
//			List<Double> tileLatitudeList = this.getTileLatitudeList(this.tileList);
//			for (Double latitude : tileLatitudeList) {
//				List<Tile> bandTileList = new ArrayList<>();
//				for (Tile t : this.tileList) {
//					if (latitude.equals(t.latitude)) {
//						bandTileList.add(t);
//					}
//				}
//				Band band = new Band(bandTileList, latitude);
//				this.bandList.add(band);
//			}
//		}
//	}
//	this.initMax();
//}

//@Override
//public void paint(Graphics graphics) throws Exception {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.eventList = (List<CycloneEvent>) this.data.get(sourceUUID, this.filter);
//		if (this.eventList != null) {
//			for (int i = 0; i < this.eventList.size(); i++) {
//				CycloneEvent event = this.eventList.get(i);
//				this.coordinateList = event.coordinateList;
//				if (event.containsCalendar(this.calendar)) {
//					this.setCalendarCoordinateList(this.calendar,this.coordinateList);
//				} else {
//					event.flag = false;
//				}
//			}
//			this.setEventList(this.eventList);
//			super.paint(graphics);
//		}
//	}
//}