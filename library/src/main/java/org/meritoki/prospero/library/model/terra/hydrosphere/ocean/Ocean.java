package org.meritoki.prospero.library.model.terra.hydrosphere.ocean;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.time.TimePlot;
import org.meritoki.prospero.library.model.terra.hydrosphere.Hydrosphere;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.ice.Ice;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.modulus.Modulus;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.temperature.SurfaceTemperature;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Series;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ocean extends Hydrosphere {

	static Logger logger = LoggerFactory.getLogger(Ocean.class.getName());
	public DataType dataType;

	public Ocean() {
		super("Ocean");
		this.sourceMap.put("NOAA ENSO", "162baa09-9ad1-4556-9a9f-a967ee37e514");
		this.sourceMap.put("NOAA El Nino 12", "36e6219d-4867-4c94-84ba-538fd40e63e1");
		this.sourceMap.put("NOAA El Nino 3", "19cf287a-5d24-4f60-9dd7-e5c2a545a1a5");
		this.sourceMap.put("NOAA El Nino 3.4", "1973a943-de01-46c2-b376-60384645bea8");
		this.sourceMap.put("NOAA El Nino 4", "8f2bc89d-7ba3-46b2-9dde-7717d992a61e");
		this.sourceMap.put("NOAA PDO", "671b1a22-53e4-47b1-b148-5ba83420b0cd");
		this.sourceMap.put("NOAA SAM", "49c5c583-3ab3-4b71-b655-7c63f79cd19e");
		this.sourceMap.put("NOAA AMO","9002e9df-10c9-417a-a12e-c29da659577b");
		this.sourceMap.put("NOAA IOD","c6cda394-0243-4ed5-a6db-9add2a837490");
		this.addChild(new SurfaceTemperature());
		this.addChild(new Modulus());
		this.addChild(new Ice());
		this.tileFlag = true;
	}

	public Ocean(String name) {
		super(name);
	}

	@Override
	public void init() {
		this.dimension = 1;
		super.init();
	}

	@Override
	public void load(Result result) {
		super.load(result);
		List<NetCDF> netCDFList = result.getNetCDFList();
		List<Index> indexList = result.getIndexList();
		if (netCDFList.size()>0) {
			this.netCDFList.addAll(netCDFList);
			try {
				this.process(this.netCDFList);
			} catch (Exception e) {
				logger.error("load(" + (result != null) + ") exception=" + e.getMessage());
				e.printStackTrace();
			}
		} else if(indexList.size() > 0) {
			this.regionList = result.getRegionList();
			Series series = this.newSeries();
			series.addIndexList(indexList);
			this.seriesMap.put(this.query.getSource(), series);
		}
	}
	
	@Override
	public void complete() {
		super.complete();
		this.initPlotList(this.seriesMap);
		this.initTileList(this.seriesMap);
	}
	
	@Override
	public List<Plot> getPlotList() throws Exception {
		return this.plotList;
	}

	public Plot getPlot(Series series) throws Exception {
		Plot plot = null;
		if (series.indexList != null && series.indexList.size() > 0) {
			series.map.put("startCalendar", this.startCalendar);
			series.map.put("endCalendar", this.endCalendar);
			series.setRegression(this.regression);
			plot = new TimePlot(series);
		}
		return plot;
	}
	
	public void initPlotList(Map<String, Series> seriesMap) {
		List<Plot> plotList = new ArrayList<>();
		for (Series series : new ArrayList<Series>(seriesMap.values())) {
			try {
				Plot plot = this.getPlot(series);
				if (plot != null) {
					plotList.add(plot);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("initPlotList(" + seriesMap.size() + ") e=" + e);
				e.printStackTrace();
			}
		}
		this.plotList = plotList;
	}
	
	public void initTileList(Map<String, Series> seriesMap) {
		for (Series series : new ArrayList<Series>(seriesMap.values())) {
			this.tileList = this.getTileList((List<Region>)series.map.get("regionList"),series.getIndex(this.calendar).value);
		}
	}
	
	public Series newSeries() {
		Series series = new Series();
		series.map.put("startCalendar", this.startCalendar);
		series.map.put("endCalendar", this.endCalendar);
		series.map.put("name", this.name);
		series.map.put("average", this.averageFlag);
		series.map.put("sum", this.sumFlag);
		series.map.put("regression", this.regression);
		series.map.put("region", "");
		series.map.put("regionList", this.regionList);
		series.map.put("family", this.query.getFamily());
		series.map.put("class", this.query.getClassification());
		series.map.put("group", this.query.getGroup());
		series.map.put("variable", this.query.getVariable());
		series.map.put("window", this.window);
		series.map.put("range", this.range);
		Query q = new Query(this.query);
		q.map.put("region", "");//region.toString());
		series.map.put("query", q);
		return series;
	}

	/**
	 * Reviewed 202112160852 Good
	 */
	@Override
	public void process() throws Exception {
		super.process();
		try {
			this.process(this.netCDFList);
			this.complete();
		} catch (Exception e) {
			logger.error("process() exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void process(Object object) throws Exception { // List<NetCDF> netCDFList) throws Exception {
		this.setMatrix(object);
		this.tileList = this.getTileList();
		this.tileFlag = true;
		this.initTileMinMax();
	}

	public void setMatrix(Object object) { // List<NetCDF> netCDFList) {
		List<Time> timeList = this.setCoordinateAndDataMatrix(this.coordinateMatrix, this.dataMatrix, object);
		for (Time t : timeList) {
			if (!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
	}

	public List<Time> setCoordinateAndDataMatrix(int[][][] coordinateMatrix, float[][][] dataMatrix, Object object) {
		List<Time> timeList = new ArrayList<>();
		if (object != null) {
			List<NetCDF> netCDFList = (List<NetCDF>) object;
			for (NetCDF netCDF : netCDFList) {
				if (netCDF.type == this.dataType) {
					long timeSize = netCDF.timeArray.getSize();
					long latSize = netCDF.latArray.getSize();
					long lonSize = netCDF.lonArray.getSize();
					for (int t = 0; t < timeSize; t++) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(Time.getNineteenHundredJanuaryFirstDate(netCDF.timeArray.get(t)));
						boolean flag = true;
						if (this.query.isDateTime()) {
							if (!this.calendar.equals(calendar)) {
								flag = false;
							}
						}
						if (flag) {
							for (int lat = 0; lat < latSize; lat++) {
								float latitude = netCDF.latArray.get(lat);
								for (int lon = 0; lon < lonSize; lon++) {
									float longitude = netCDF.lonArray.get(lon);
									int x = (int) (((latitude + (this.latitude * this.resolution) / 2)) % (this.latitude * this.resolution));
									int y = (int) (((longitude + (this.longitude * this.resolution) / 2)) % (this.longitude * this.resolution));
									int z = calendar.get(Calendar.MONTH);
									dataMatrix[x][y][z] += netCDF.variableCube.get(t, lat, lon);
									coordinateMatrix[x][y][z]++;
									Time time = new Time(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
											-1, -1, -1, -1);
									if (!timeList.contains(time)) {
										timeList.add(time);
									}
								}
							}
						}
					}
				}
			}
		}
		return timeList;
	}
}
//public int[][] countMatrix;
//public float[][] sumMatrix;
//public boolean[][] continentMatrix;
//@Override
//public List<Tile> getTileList() {
//	return this.getTileList(this.coordinateMatrix,this.dataMatrix);
//}
//
//public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] dataMatrix) {
//	List<Tile> tileList = new ArrayList<>();
//	int yearCount = this.getYearCount();
//	int monthCount = this.getMonthCount();
//	Tile tile;
//	int coordinate;
//	float data;
//	float dataMean;
//	float dataMeanSum;
//	float value;
//	for (int i = 0; i < coordinateMatrix.length; i += this.dimension) {
//		for (int j = 0; j < coordinateMatrix[i].length; j += this.dimension) {
//			dataMeanSum = 0;
//			for (int m = 0; m < 12; m++) {
//				coordinate = 0;
//				data = 0;
//				for (int a = i; a < (i + this.dimension); a++) {
//					for (int b = j; b < (j + this.dimension); b++) {
//						if (a < this.latitude && b < this.longitude) {
//							coordinate += coordinateMatrix[a][b][m];
//							data += dataMatrix[a][b][m];
//						}
//					}
//				}
//				dataMean = (coordinate > 0) ? data / coordinate : 0;
//				dataMeanSum += dataMean;
//			}
//			value = dataMeanSum;
//			if (this.monthFlag) {
//				value /= monthCount;
//			} else if (this.yearFlag) {
//				value /= ((double) this.getMonthCount() / (double) yearCount);
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
//	return tileList;
//}
//public List<Tile> getTileList() {
//List<Tile> tileList = new ArrayList<>();
//for (int i = 0; i < latitude; i++) {
//	for (int j = 0; j < longitude; j++) {
//		double latitude = 90 - (i * this.dimension);
//		if (latitude <= 0) {
//			if (!continentMatrix[i][j]) {
//				float temperatureMean = (countMatrix[i][j] > 0) ? sumMatrix[i][j] / countMatrix[i][j] : 0;
//				double value = temperatureMean;
//				double lat = latitude;
//				double lon;
//				if ((j * this.dimension) < 180) {
//					lon = (j * this.dimension);
//				} else {
//					lon = (j * this.dimension) - 360;
//				}
//				Tile tile = new Tile(lat, lon, this.dimension, value);
//				if (regionList != null) {
//					for (Region region : regionList) {
//						if (region.contains(tile)) {
//							tileList.add(tile);
//							break;
//						}
//					}
//				} else {
//					tileList.add(tile);
//				}
//			}
//		}
//	}
//}
//return tileList;
//}
//public List<Frame> getCalendarFrameList(List<Frame> frameList) {
//List<Frame> fList = new ArrayList<>();
//for(Frame f: frameList) {
//	if(f.containsCalendar(this.calendar)) {
//		fList.add(f);
//	}
//}
//return fList;
//}
