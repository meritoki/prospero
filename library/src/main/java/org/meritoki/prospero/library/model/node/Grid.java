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
package org.meritoki.prospero.library.model.node;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.meritoki.prospero.library.model.node.color.Chroma;
import org.meritoki.prospero.library.model.node.color.Scheme;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.unit.Analysis;
import org.meritoki.prospero.library.model.unit.Band;
import org.meritoki.prospero.library.model.unit.Cluster;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Frame;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Legend;
import org.meritoki.prospero.library.model.unit.Link;
import org.meritoki.prospero.library.model.unit.Meter;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Series;
import org.meritoki.prospero.library.model.unit.Space;
import org.meritoki.prospero.library.model.unit.Station;
import org.meritoki.prospero.library.model.unit.Table;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Grid extends Spheroid {

	static Logger logger = LoggerFactory.getLogger(Grid.class.getName());
	public int latitude = 180;// 20230417, 20230621
	public int longitude = 360;
	public double resolution = 1;// Need to Clean Dimension Resolution Implementation
	public double dimension = 1;
	public int[] monthArray;
	public double max;
	public double min;
	public Double significance;
	public Scheme scheme = Scheme.MAGMA;
	public Chroma chroma = new Chroma(scheme);
	public int[][][] coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
	public float[][][] dataMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
	public List<Region> regionList = new ArrayList<>();
	public List<String> idList = new ArrayList<>();
	public List<Band> bandList = new ArrayList<>();
	public List<Tile> tileList = new ArrayList<>();
	public List<Frame> frameList = new ArrayList<>();
	public List<NetCDF> netCDFList = new ArrayList<>();
	public List<Coordinate> coordinateList = new ArrayList<>();
	public List<Event> eventList = Collections.synchronizedList(new ArrayList<>());
	public List<Time> timeList = new ArrayList<>();
	public List<Station> stationList = new ArrayList<>();
	public List<Table> tableList = new ArrayList<>();
	public List<Index> indexList = new ArrayList<>();
	public List<Cluster> clusterList = new ArrayList<>();
	public HashMap<Time, List<Event>> eventMap = new HashMap<>();
	public HashMap<Region, List<Event>> regionMap = new HashMap<>();
	public Map<Integer, List<Tile>> tileListMap = new TreeMap<>();
	public Map<Integer, List<Band>> bandListMap = new HashMap<>();
	public Map<Integer, int[][][]> coordinateMatrixMap = new HashMap<>();
	public Map<Integer, float[][][]> dataMatrixMap = new HashMap<>();
	public Map<Integer, Integer> yearMap = new HashMap<>();
//	public Map<String, Series> seriesMap = new TreeMap<>();
	public Map<String, Plot> plotMap = new TreeMap<>();
	public Map<Time, List<Tile>> timeTileMap = new HashMap<>();
	protected Cluster cluster;
	public Analysis analysis;
	protected Region region;
	protected Double[] meter;
	protected Calendar[] window;
	public double[] range;
	public double interval = 0.5;
	public double increment;
	public String regression;
	public String season;
	public String group;
	public boolean averageFlag;
	public boolean sumFlag;
	public boolean stackFlag;
	public boolean trajectoryFlag;
	public boolean bandFlag;
	public boolean cubeFlag;
	public boolean tileFlag;
	public boolean monthFlag;
	public boolean yearFlag;
	public boolean clearFlag;
	public boolean histogramFlag;

	public Grid(String name) {
		super(name);
	}

	/**
	 * For all primary object representation, re-initialize
	 */
	public void reset() {
		super.reset();
		this.tileList = new ArrayList<>();
		this.regionList = new ArrayList<>();
		this.region = null;
		this.bandList = new ArrayList<>();
		this.frameList = new ArrayList<>();
		this.coordinateList = new ArrayList<>();
		this.eventList = Collections.synchronizedList(new ArrayList<>());
		this.stationList = new ArrayList<>();
		this.plotList = new ArrayList<>();
		this.clusterList = new ArrayList<>();
	}

	@Override
	public void load(Result result) {
		super.load(result);
	}

	@Override
	public void complete() {
		super.complete();
	}

	/**
	 * Function Set Event & Point Flag to False Flag Set to True If Event And/Or
	 * Point Satisfies Query Parameters
	 * 
	 * @param eventList
	 * @return List<Event>
	 */
	public void reset(List<Event> eventList) {
		if (eventList != null) {
			for (Event e : eventList) {
				e.flag = false;
				for (Coordinate c : e.coordinateList) {
					c.flag = false;
				}
			}
		}
	}

	/**
	 * Precondition EventList truth is known. Easiest way to know is to do a reset.
	 * Truth can change for an event by changing filter parameters and reapplying
	 * filter For example, applying regions.
	 * 
	 * @param eventList
	 * @return
	 * @throws Exception
	 */
	public void filter(List<Event> eventList) throws Exception {
		if (!Thread.interrupted()) {
			if (eventList != null & eventList.size() > 0) {
				if (this.idList.size() > 0) {
					for (Event e : eventList) {
						boolean eventFlag = false;
						for (String id : this.idList) {
							if (e.id.equals(id)) {
								eventFlag = true;
								break;
							}
						}
						if (eventFlag) {
							e.flag = true;
						} else {
							e.flag = false;
						}
					}
				} else {
					boolean regionFlag = false;
					for (Event e : eventList) {
						for (Coordinate c : e.coordinateList) {
							regionFlag = false;
							if (this.region != null) {
								if (this.region.contains(c)) {
									regionFlag = true;
								}
							} else if (this.regionList != null && this.regionList.size() > 0) {
								for (Region r : this.regionList) {
									if (r.contains(c)) {
										regionFlag = true;
									}
								}
							} else {
								regionFlag = true;
							}
							c.flag = regionFlag;
						}
						e.flag = e.hasCoordinate();
					}
				}
			}
		} else {
			throw new InterruptedException();
		}
	}

	/**
	 * 20230621 Recommended only to use on list copies
	 * 
	 * @param eventList
	 */
	public void prune(List<Event> eventList) {
		Iterator<Event> eventIterator = eventList.iterator();
		while (eventIterator.hasNext()) {
			Event e = eventIterator.next();
			if (!e.flag || !e.hasCoordinate()) {
				eventIterator.remove();
			}
		}
	}

	@Override
	public void process() throws Exception {
		super.process();
	}

	/**
	 * Variables that must be reset even if the same Query result can be used
	 */
	@Override
	public void init() {
		super.init();
		try {
			
			this.idList = this.query.getIDList();
			this.regression = this.query.getRegression();
			this.significance = this.query.getSignificance();
			this.group = this.query.getGroup();
			this.eventMap = new HashMap<>();
			this.analysis = this.query.getAnalysis();
			this.sumFlag = this.query.getSum();
			this.averageFlag = this.query.getAverage();
			this.bandFlag = this.query.getBand();
			this.stackFlag = this.query.getStack();
			this.trajectoryFlag = this.query.getTrajectory();
			this.clearFlag = this.query.getClear();
			this.regionList = this.query.getRegionList();
			this.dimension = (this.query.getDimension() != null) ? this.query.getDimension() : 1;
			this.resolution = (this.query.getResolution() != null) ? this.query.getResolution() : 1;
			this.meter = this.query.getMeter();
			this.interval = this.query.getInterval();
			this.window = this.query.getWindow();
			if (this.window != null && this.window.length == 2) {
				this.startCalendar = this.window[0];
				this.endCalendar = this.window[1];
			}
			this.range = this.query.getRange();
			this.scheme = (this.query.getScheme() != null) ? this.query.getScheme() : this.scheme;
			this.seriesMap = new TreeMap<>();
			this.timeList = new ArrayList<>();
			this.histogramFlag = this.query.getHistogram();
			this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
			this.dataMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
		} catch (Exception e) {
			logger.error("init() exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void initCoordinateListMinMax(String variable, Double nullValue) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		boolean flag = true;
		for (Coordinate c : this.coordinateList) {
			double value = (double) c.attribute.get(variable);
			if (nullValue != null && Math.abs(value) == nullValue) {
				flag = false;
			} else {
				flag = true;
			}
			if (flag) {
				if (value > max) {
					max = value;
				}
				if (value < min) {
					min = value;
				}
			}
		}
		this.min = min;
		this.max = max;
	}

	/**
		 * 
		 */
	public void initMonthArray(List<Time> timeList) {
		this.monthArray = new int[12];
		if (timeList != null) {
			for (Time time : timeList) {
				if (time != null) {
					int month = time.month;
					if (month != -1) {
						this.monthArray[month - 1]++;
					}
				}
			}
			logger.debug("initMonthArray(" + timeList.size() + ") this.monthArray=" + Arrays.toString(this.monthArray));
		}
	}

	public Map<Integer, Integer> initYearMap(List<Time> timeList) {
		this.yearMap = new HashMap<>();
		if (timeList != null) {
			for (Time time : timeList) {
				if (time != null) {
					int year = time.year;
					Integer count = this.yearMap.get(year);
					if (count == null) {
						count = 1;
					} else {
						count++;
					}
					this.yearMap.put(year, count);
				}
			}
			logger.debug("initYearMap(" + timeList.size() + ") this.yearMap=" + this.yearMap);
		}
		return this.yearMap;
	}

	public void initTileMinMax() {
		this.initTileMinMax(this.tileList, true);
	}

	public void initTileMinMax(List<Tile> tileList, boolean reset) {
		double min = this.min;
		double max = this.max;
		if (reset) {
			min = Double.POSITIVE_INFINITY;
			max = Double.NEGATIVE_INFINITY;
		}
		if (tileList != null) {
			java.util.Iterator<Tile> iterator = tileList.iterator();
			while (iterator.hasNext()) {
				Tile t = new Tile(iterator.next());
				if (t.value > max) {
					max = t.value;
				}
				if (t.value < min) {
					min = t.value;
				}
			}
		}
		this.max = max;
		this.min = min;
		logger.debug(
				"initTileMinMax(" + tileList.size() + "," + reset + ") this.min=" + this.min + " this.max=" + this.max);
	}

	public void initBandMinMax() {
		this.initBandMinMax(this.bandList, true);
	}

	public void initBandMinMax(List<Band> bandList, boolean reset) {
		double min = this.min;
		double max = this.max;
		if (reset) {
			min = Double.POSITIVE_INFINITY;
			max = Double.NEGATIVE_INFINITY;
		}
		if (bandList != null & bandList.size() > 0) {
			java.util.Iterator<Band> iterator = bandList.iterator();
			while (iterator.hasNext()) {
				Band t = iterator.next();
				if (t.value > max) {
					max = t.value;
				}
				if (t.value < min) {
					min = t.value;
				}
			}
		}
		this.max = max;
		this.min = min;
		logger.debug(
				"initBandMinMax(" + bandList.size() + "," + reset + ") this.min=" + this.min + " this.max=" + this.max);
	}

	@Override
	public void updateSpace() {
		super.updateSpace();
	}

	public List<Tile> getTileList() {
		return this.getTileList(this.coordinateMatrix, this.dataMatrix);
	}

	public List<Tile> getTileList(List<Region> regionList, double value) {
		logger.info("getTileList(" + regionList.size() + ", " + value + ")");
		List<Tile> tileList = new ArrayList<>();
		Tile tile;
		for (int i = 0; i < this.latitude; i += dimension) {
			for (int j = 0; j < this.longitude; j += dimension) {
				tile = new Tile((i - this.latitude / 2) / this.resolution, (j - (this.longitude / 2)) / this.resolution,
						dimension, value);
				if (regionList != null && regionList.size() > 0) {
					for (Region region : regionList) {
						if (region.contains(tile)) {
							tileList.add(tile);
							break;
						}
					}
				} else {
					tileList.add(tile);
				}
			}
		}
		return tileList;
	}

	/**
	 * 
	 * @param coordinateMatrix
	 * @return
	 */
	public List<Tile> getTileList(int[][][] coordinateMatrix) {
		List<Tile> tileList = new ArrayList<>();
		int monthCount = this.getMonthCount();
		Tile tile;
		int count;
		int data;
		double weight;
		double weightedData;
		double quotient;
		double quotientSum = 0;
		double value;
		// cycle through each tile
		for (int i = 0; i < coordinateMatrix.length; i += dimension) {
			for (int j = 0; j < coordinateMatrix[i].length; j += dimension) {
				quotientSum = 0;
				for (int m = 0; m < 12; m++) {// for each month
					data = 0;// each tile in a given month has a density;
					int x = (int) (i + dimension);
					int y = (int) (j + dimension);
					// We do not want the following nested for loop to fail
					// We must check if x and y with dimension added will succeed
					// If x or y go over the possible values of the Coordinate Matrix (0 <= x <=
					// latitude, 0 <= y <= longitude)
					// The nested for loop can sum what it has without a problem, especially with
					// Odd dimensions
					for (int a = i; a < x; a++) {
						for (int b = j; b < y; b++) {
							if (a < this.latitude && b < this.longitude) {
								data += coordinateMatrix[a][b][m];// density is the sum of count within tile
							}
						}
					}
					weight = Coordinate.getArea((i - (this.latitude / 2)) / this.resolution,
							(j - (this.longitude / 2)) / this.resolution, dimension);
//					weight = Coordinate.getArea(i - this.latitude / 2, j - this.longitude / 2, dimension);
					weightedData = (weight > 0) ? data / weight : data;
					count = this.monthArray[m];
					quotient = (count > 0) ? weightedData / count : weightedData;
					quotientSum += quotient;
				}
				value = quotientSum;
				// Correct Solution Applied 2022/12/07
				value /= (monthCount > 0) ? monthCount : 1;
				tile = new Tile((i - (this.latitude / 2)) / this.resolution,
						(j - (this.longitude / 2)) / this.resolution, dimension, value);

				if (this.region != null) {
					if (this.region.contains(tile)) {
						tileList.add(tile);
					}
				} else if (this.regionList != null) {
					for (Region region : this.regionList) {
						if (region.contains(tile)) {
							tileList.add(tile);
							break;
						}
					}
				} else {
					tileList.add(tile);
				}
			}
		}
		return tileList;
	}

	/**
	 * 
	 * @param coordinateMatrix
	 * @param dataMatrix
	 * @return
	 */
	public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] dataMatrix) {
		List<Tile> tileList = new ArrayList<>();
		int monthCount = this.getMonthCount();
		Tile tile;
		int count;
		float data;
		float dataMean;
		float mean;
		float meanSum;
		float value;
		// Nested For Loops Use Dimension to Iterate
		// Over 2D Coordinate and Data Matrices
		// Each Iteration of Both Loops Represents a Unique Tile
		for (int i = 0; i < coordinateMatrix.length; i += this.dimension) {
//			logger.info("getTileList(...) i="+i);
			for (int j = 0; j < coordinateMatrix[i].length; j += this.dimension) {
//				logger.info("getTileList(...) j="+j);
				meanSum = 0;// Reset Mean Sum to Zero
				// We Iterate Over All 12 Months for a Tile
				// Each Month Dimension Contains 1 or More Years of Data
				for (int m = 0; m < 12; m++) {
					// Reset Count and Duration of Zero
					count = 0;
					data = 0;
					// We Iterate over all Coordinate and Data Matrix Cells that correspond to a
					// Tile using a and b as indices
					for (int a = i; a < (i + this.dimension); a++) {
						for (int b = j; b < (j + this.dimension); b++) {
							if (a < this.latitude * this.resolution && b < this.longitude * this.resolution) {
								// Each Tile is like a bucket where we retain a Count and Sum of Unique
								// Coordinates and Data, i.e. Duration, respectively,
								// In Some Cases a Month or Tile may not contain any measurements
								// This is possible with Seasonal Queries.
								// In which case the Mean Sum will only contain those months, i.e. DJF, JJA,
								// MAN, SON
								count += coordinateMatrix[a][b][m];// Count Sum of Months From All Years Queried
								data += dataMatrix[a][b][m];// Data Sum of Months From All Years Queried
							}
						}
					}
					// After Summing the Data, i.e. Duration, and Count of a Tile For a Given Month
					// Divide the Duration by Count when Count > 0, to obtain a Mean for the Tile in
					// a Specific Month
					// No Matter How Many Unique Years and Months are queried, i.e. 1979/01-2019/12
					// Mean officially represents the Average for that Month
					dataMean = (count > 0) ? data / count : data;
					mean = dataMean;
					// Sum every Mean, One for Each Month
					// Mean Sum can contain up to 12 Unique Month Means
					// In Long Queries with Many Unique Years and Data for All 12 Months,
					// The Mean is already the Average for All Possible Years for the Query Months,
					// i.e. DJF or January
					meanSum += mean;

				}
				// Mean Sum is typically the Sum of All 12 Monthly Means for a Given Tile
				// In Some cases the Mean Sum will be the Sum of Less Unique Months, i.e. a
				// Season 3 Months.
				// In Most cases, the Mean Sum of 12 Unique Months is equivalent to the Mean Sum
				// for One Unique Year
				// If we have more than One Unique Year, i.e. 2001,2002,2003
				value = meanSum;// Value is Assigned the Mean Sum
				// Correct Solution Applied 2022/12/07
				value /= (monthCount > 0) ? monthCount : 1;
//				if (this.monthFlag) {// If We Want a Monthly Average, We Divide by Unique Months
//					// value /= monthCount;// Define 0 < monthCount <= 12, commonly has a Value of 3
//				} else if (this.yearFlag) {// If We Want a Yearly Average, we Divide by Unique Years
				// We do this because even though we have the Sum of Averages for Each Month
				// Deprecated 2022/12/07
//					value /= yearCount;// Define 0 < yearCount <= N, where N is a Positive
				// Deprecated
//					value /= ((double)monthTotal/(double)yearCount);
				// Defect Corrected 2022/10/14
				// I thought I had to divide the Mean Sum by Unique Months & then Unique Years
				// This produced incorrect Mean Values for the YEARLY Mean of Tiles
				// The fix applied coincides with the addition of Band Support
				// Detected because Yearly Averages for Lifetime seemed too small/low
//				}
				tile = new Tile((i - (this.latitude * this.resolution / 2)) / this.resolution,
						(j - (this.longitude * this.resolution / 2)) / this.resolution, this.dimension/this.resolution, value);
				if (this.regionList != null) {
					for (Region region : this.regionList) {
						if (region.contains(tile)) {
							tileList.add(tile);
							break;
						}
					}
				} else {
					tileList.add(tile);
				}
			}
		}
		return tileList;
	}

	public List<Double> getTileLatitudeList(List<Tile> tileList) {
		List<Double> tileLatitudeList = new ArrayList<>();
		for (Tile t : tileList) {
			if (!tileLatitudeList.contains(t.coordinate.latitude)) {
				tileLatitudeList.add(t.coordinate.latitude);
			}
		}
		return tileLatitudeList;
	}

	public int getMonthCount() {
		int monthCount = 0;
		for (int month : this.monthArray) {
			if (month > 0) {
				monthCount++;
			}
		}

		return monthCount;
	}

	public int getMonthTotal() {
		int monthTotal = 0;
		for (int month : this.monthArray) {
			if (month > 0) {
				monthTotal += month;
			}
		}
		logger.debug("getMonthTotal() monthTotal=" + monthTotal);
		return monthTotal;
	}

	public int getYearCount() {
		int yearCount = 0;
		for (Map.Entry<Integer, Integer> entry : this.yearMap.entrySet()) {
			if (entry.getValue() > 0) {
				yearCount++;
			}
		}
		// if (print && detail)
		// logger.info("getYearCount() yearCount=" + yearCount);
		return yearCount;
	}

	public double getMax() {
		if (this.meter.length >= 2) {
			return this.meter[1];
		}
		return this.max;
	}

	public double getMin() {
		if (this.meter.length >= 1) {
			return this.meter[0];
		}
		return this.min;
	}

	public double getIncrement() {
		if (this.meter.length == 3) {
			this.increment = this.meter[2];
			return increment;
		}
		return this.increment;
	}

	@Override
	public void setCenter(Space center) {
		super.setCenter(center);
	}

	public void setCluster(Cluster cluster) {
		logger.debug("setCluster(" + cluster + ")");
		this.cluster = cluster;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public void paintStack(Graphics graphics) {
		logger.debug(this + ".paintStack(...) tileListMap.size() = " + this.tileListMap.size());
		this.scheme = Scheme.VIRIDIS;
		this.chroma = new Chroma(this.scheme);
		if (this.tileListMap.size() > 0) {
			int size = this.tileListMap.size();
//			double interval = this.interval;
			int index = (size / 2);
			this.tileListMap = new TreeMap<Integer, List<Tile>>(this.tileListMap).descendingMap();

			for (Entry<Integer, List<Tile>> entry : this.tileListMap.entrySet()) {
				List<Tile> tileList = entry.getValue();
				this.initTileMinMax(tileList, false);
			}

			for (Entry<Integer, List<Tile>> entry : this.tileListMap.entrySet()) {
				((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//				int level = entry.getKey();
				List<Tile> tileList = entry.getValue();
//				this.initTileMinMax(tileList, false);
				double vertical = index * this.interval;
//				if (!this.getProjection().verticalList.contains(vertical)) {
//					this.getProjection().verticalList.add(vertical);
//				}
				if (tileList != null && tileList.size() > 0) {
					for (Tile t : tileList) {
						Point a = this.getProjection().getPoint(vertical, t.coordinate.latitude,
								t.coordinate.longitude);
						Point b = this.getProjection().getPoint(vertical, t.coordinate.latitude + t.dimension,
								t.coordinate.longitude);
						Point c = this.getProjection().getPoint(vertical, t.coordinate.latitude + t.dimension,
								t.coordinate.longitude + t.dimension);
						Point d = this.getProjection().getPoint(vertical, t.coordinate.latitude,
								t.coordinate.longitude + t.dimension);
						if (a != null && b != null && c != null && d != null) {
							int xpoints[] = { (int) (a.x * this.getProjection().scale),
									(int) (b.x * this.getProjection().scale), (int) (c.x * this.getProjection().scale),
									(int) (d.x * this.getProjection().scale) };
							int ypoints[] = { (int) (a.y * this.getProjection().scale),
									(int) (b.y * this.getProjection().scale), (int) (c.y * this.getProjection().scale),
									(int) (d.y * this.getProjection().scale) };
							int npoints = 4;
							if (this.clearFlag) {
								if (t.value != 0) {
									Color color = null;
//									if (this.level) {
//										color = this.chroma.getColor(this.getMeters(level), this.getMin(),
//												this.getMax());
//									} else {
									color = this.chroma.getColor(t.value, this.getMin(), this.getMax());
//									}
									if (color != null) {
										graphics.setColor(color);
										graphics.fillPolygon(xpoints, ypoints, npoints);
									}
								}
							} else {
								Color color = this.chroma.getColor(t.value, this.getMin(), this.getMax());
								graphics.setColor(color);
								graphics.fillPolygon(xpoints, ypoints, npoints);
							}
						}
					}
				}
				graphics.setColor(Color.BLACK);
				List<Point> pointList = this.getProjection().getGridPointList(vertical, 15, 30);
				for (Point p : pointList) {
					graphics.drawLine((int) ((p.x) * this.getProjection().scale),
							(int) ((p.y) * this.getProjection().scale), (int) ((p.x) * this.getProjection().scale),
							(int) ((p.y) * this.getProjection().scale));
				}
				index--;
			}

			if (this.getProjection().scale >= this.defaultScale) {
				Meter meter = new Meter(0.9, (int) (this.getProjection().xMax * this.getProjection().scale),
						this.getMax(), this.getMin(), this.unit, this.getIncrement(), this.format);
				meter.setChroma(this.chroma);
				meter.paint(graphics);
			}
		}
	}

	public void paintBand(Graphics graphics) {
		this.chroma = new Chroma(this.scheme);
		Point a;
		Point b;
		Point c;
		Point d;

		this.initBandMinMax();
		for (Band band : this.bandList) {
			for (Tile t : band.tileList) {
				a = this.getProjection().getPoint(0, t.coordinate.latitude, t.coordinate.longitude);
				b = this.getProjection().getPoint(0, t.coordinate.latitude + t.dimension, t.coordinate.longitude);
				c = this.getProjection().getPoint(0, t.coordinate.latitude + t.dimension,
						t.coordinate.longitude + t.dimension);
				d = this.getProjection().getPoint(0, t.coordinate.latitude, t.coordinate.longitude + t.dimension);
				if (a != null && b != null && c != null && d != null) {
					int xpoints[] = { (int) (a.x * this.getProjection().scale),
							(int) (b.x * this.getProjection().scale), (int) (c.x * this.getProjection().scale),
							(int) (d.x * this.getProjection().scale) };
					int ypoints[] = { (int) (a.y * this.getProjection().scale),
							(int) (b.y * this.getProjection().scale), (int) (c.y * this.getProjection().scale),
							(int) (d.y * this.getProjection().scale) };
					int npoints = 4;
					graphics.setColor(this.chroma.getColor(band.value, this.getMin(), this.getMax()));
					graphics.fillPolygon(xpoints, ypoints, npoints);
				}
			}
		}
		if (projection.scale >= this.defaultScale) {
			Meter meter = new Meter(0.9, (int) (projection.xMax * this.getProjection().scale), this.getMax(),
					this.getMin(), this.unit, this.getIncrement(), this.format);
			meter.setChroma(this.chroma);
			meter.paint(graphics);
		}
	}

	public void paintEvent(Graphics graphics) {
//		logger.info(this + ".paintEvent(...) eventList.size() = " + this.eventList.size());
		this.chroma = new Chroma();

		List<Event> eventList = Event.getSelectedEventList(this.eventList, this.calendar);
		int size = eventList.size();
		for (int i = 0; i < eventList.size(); i++) {
			Event event = (Event) eventList.get(i);
			Color eventColor = this.chroma.getColor(i, 0, size);
			event.attribute.put("color", eventColor);
			List<Coordinate> coordinateList = new ArrayList<>();
			Map<String, List<Coordinate>> timeCoordinateMap = event.getTimeCoordinateMap();
			for (Map.Entry<String, List<Coordinate>> entry : timeCoordinateMap.entrySet()) {
				String key = entry.getKey();
				List<Coordinate> cList = timeCoordinateMap.get(key);
				Calendar calendar = Time.getCalendar(Time.defaultFormat, key);// 20230624 Defect Fix, Event Time Trails
																				// not showing
				if (calendar.before(this.calendar)) {
					Coordinate c = event.getAverageCoordinate(cList, calendar);
					c.flag = true;
					coordinateList.add(c);
				}
			}
//			202304 Test Implementation - Not Viable, Commented Out
//			logger.info(this + ".paintEvent(...) event=" + event.id);
//			Calendar calendar = Time.getCalendar("YYYY/MM/dd HH:mm:ss",this.query.getTime());
//			this.setCalendarCoordinateList(this.calendar,event.coordinateList);
//			202304281739 Code Review - Possible Incorrect Code/Implementation
//			Next Two Lines. Set Calendar Coordinate List is supposed to set Coordinate Flag True where Calendar Equals Coordinate Calendar
			event.setCalendarCoordinateList(this.calendar);
//			Code In Question is getAverageCoordinateList
			Coordinate c = event.getAverageCoordinate(event.getCoordinateList(), this.calendar);
			c.attribute.put("color", Color.BLACK);
			coordinateList.add(c);
			List<Point> pointList = this.getProjection().getCoordinateList(0, coordinateList);
			if (pointList != null) {
				for (int j = 0; j < pointList.size(); j++) {
					Point p = pointList.get(j);
					if (p.flag) {
						graphics.setColor(eventColor);
						double radius = 8;
						double x = (p.x * this.getProjection().scale) - (radius / 2);
						double y = (p.y * this.getProjection().scale) - (radius / 2);
						graphics.fillOval((int) x, (int) y, (int) radius, (int) radius);
//						int unitWidth = graphics.getFontMetrics().stringWidth(event.id);
						Object color = p.attribute.get("color");
						if (color instanceof Color) {
							graphics.setColor((Color) color);
							radius = 4;
							x = (p.x * this.getProjection().scale) - (radius / 2);
							y = (p.y * this.getProjection().scale) - (radius / 2);
							graphics.fillOval((int) x, (int) y, (int) radius, (int) radius);
						}
//						graphics.drawString(event.id, (int)(x - (unitWidth / 2)), (int)(y + 8));
					}
				}
			}
		}
		if (this.getProjection().scale >= this.defaultScale) {
			Legend legend = new Legend(((int) -(this.getProjection().xMax * this.getProjection().scale) - 128));
			legend.setKeyMap(eventList);
			legend.paint(graphics);
		}
	}

	/**
	 * 
	 * @param graphics
	 */
	public void paintTrajectory(Graphics graphics) {
		logger.debug("paintTrajectory(" + (graphics != null) + ") this.eventList.size()=" + this.eventList.size());
		Graphics2D g2d = (Graphics2D) graphics;
		Point aPoint;
		Point bPoint;
		Coordinate aCoordinate;
		Coordinate bCoordinate;
		int thickness = 2;
		Stroke old = g2d.getStroke();
		g2d.setStroke(new BasicStroke(thickness));
		List<Event> eventList = Event.getSelectedEventList(this.eventList, this.calendar);
		logger.debug("paintTrajectory(" + (graphics != null) + ") eventList.size()=" + eventList.size());
		for (Event event : eventList) {
			if (event instanceof CycloneEvent) {
				Map<Integer, List<Coordinate>> pressureCoordinateMap = ((CycloneEvent) event)
						.getPressureCoordinateListMap();
				List<String> timeList = event.getTimeList();
				int index = 1;
				for (Map.Entry<Integer, List<Coordinate>> entry : pressureCoordinateMap.entrySet()) {
					Integer pressure = entry.getKey();
					List<Coordinate> coordinateList = pressureCoordinateMap.get(pressure);
					((CycloneEvent) event).setPointColor(coordinateList);
					double vertical = index * this.interval;
					for (int i = 0; i < coordinateList.size(); i++) {
						if (i + 1 < coordinateList.size()) {
							aCoordinate = coordinateList.get(i);
							bCoordinate = coordinateList.get(i + 1);
							if (aCoordinate instanceof Link && bCoordinate instanceof Link) {
								Link linkA = (Link) aCoordinate;
								Link linkB = (Link) bCoordinate;
								if (linkA.type == Link.START && linkB.type == Link.STOP) {
									aPoint = this.getProjection().getPoint(vertical, aCoordinate.latitude,
											(aCoordinate.longitude));
									bPoint = this.getProjection().getPoint(vertical, bCoordinate.latitude,
											(bCoordinate.longitude));
									graphics.setColor(aCoordinate.getColor());
									if (aPoint != null && bPoint != null) {
										graphics.drawLine((int) ((aPoint.x) * this.getProjection().scale),
												(int) ((aPoint.y) * this.getProjection().scale),
												(int) ((bPoint.x) * this.getProjection().scale),
												(int) ((bPoint.y) * this.getProjection().scale));
									}
								}
							} else if (aCoordinate instanceof Link) {
								Link linkA = (Link) aCoordinate;
								if (linkA.type == Link.START) {
									aPoint = this.getProjection().getPoint(vertical, aCoordinate.latitude,
											(aCoordinate.longitude));
									bPoint = this.getProjection().getPoint(vertical, bCoordinate.latitude,
											(bCoordinate.longitude));
									graphics.setColor(aCoordinate.getColor());
									if (aPoint != null && bPoint != null) {
										graphics.drawLine((int) ((aPoint.x) * this.getProjection().scale),
												(int) ((aPoint.y) * this.getProjection().scale),
												(int) ((bPoint.x) * this.getProjection().scale),
												(int) ((bPoint.y) * this.getProjection().scale));
									}
								}
							} else if (bCoordinate instanceof Link) {
								Link linkB = (Link) bCoordinate;
								if (linkB.type == Link.STOP) {
									aPoint = this.getProjection().getPoint(vertical, aCoordinate.latitude,
											(aCoordinate.longitude));
									bPoint = this.getProjection().getPoint(vertical, bCoordinate.latitude,
											(bCoordinate.longitude));
									graphics.setColor(aCoordinate.getColor());
									if (aPoint != null && bPoint != null) {
										graphics.drawLine((int) ((aPoint.x) * this.getProjection().scale),
												(int) ((aPoint.y) * this.getProjection().scale),
												(int) ((bPoint.x) * this.getProjection().scale),
												(int) ((bPoint.y) * this.getProjection().scale));
									}
								}
							} else {
								int aIndex = timeList.indexOf(aCoordinate.getDateTime());
								int bIndex = timeList.indexOf(bCoordinate.getDateTime());
								if ((aIndex + 1) == bIndex) {
									aPoint = this.getProjection().getPoint(vertical, aCoordinate.latitude,
											(aCoordinate.longitude));
									bPoint = this.getProjection().getPoint(vertical, bCoordinate.latitude,
											(bCoordinate.longitude));
									graphics.setColor(aCoordinate.getColor());
									if (aPoint != null && bPoint != null) {
										graphics.drawLine((int) ((aPoint.x) * this.getProjection().scale),
												(int) ((aPoint.y) * this.getProjection().scale),
												(int) ((bPoint.x) * this.getProjection().scale),
												(int) ((bPoint.y) * this.getProjection().scale));
									}
								}
							}
						}
					}
					g2d.setStroke(old);
					graphics.setColor(Color.LIGHT_GRAY);
					List<Point> pointList = this.getProjection().getGridPointList(vertical, 15, 30);
					for (Point p : pointList) {
						graphics.drawLine((int) ((p.x) * this.getProjection().scale),
								(int) ((p.y) * this.getProjection().scale), (int) ((p.x) * this.getProjection().scale),
								(int) ((p.y) * this.getProjection().scale));
					}
					g2d.setStroke(new BasicStroke(thickness));
					index++;
				}
			}
		}
		g2d.setStroke(old);

	}

	public void paintCluster(Graphics graphics) throws Exception {
		Graphics2D g2d = (Graphics2D) graphics;
		java.util.Iterator<Tile> iterator = this.cluster.tileList.iterator();
		Point a;
		Point b;
		Point c;
		Point d;
		while (iterator.hasNext()) {
			Tile t = new Tile(iterator.next());
			if (t != null) {
				a = this.getProjection().getPoint(0, t.coordinate.latitude, t.coordinate.longitude);
				b = this.getProjection().getPoint(0, t.coordinate.latitude + t.dimension, t.coordinate.longitude);
				c = this.getProjection().getPoint(0, t.coordinate.latitude + t.dimension,
						t.coordinate.longitude + t.dimension);
				d = this.getProjection().getPoint(0, t.coordinate.latitude, t.coordinate.longitude + t.dimension);
				if (a != null && b != null && c != null && d != null) {
					int xpoints[] = { (int) (a.x * this.getProjection().scale),
							(int) (b.x * this.getProjection().scale), (int) (c.x * this.getProjection().scale),
							(int) (d.x * this.getProjection().scale) };
					int ypoints[] = { (int) (a.y * this.getProjection().scale),
							(int) (b.y * this.getProjection().scale), (int) (c.y * this.getProjection().scale),
							(int) (d.y * this.getProjection().scale) };
					int npoints = 4;
					graphics.setColor(Tile.getCorrelationColor(t.getCorrelation()));
					g2d.fillPolygon(xpoints, ypoints, npoints);
					if (t.flag) {
						int thickness = 3;
						Stroke old = g2d.getStroke();
						g2d.setColor(Color.MAGENTA);
						g2d.setStroke(new BasicStroke(thickness));
						g2d.drawPolygon(xpoints, ypoints, npoints);
						g2d.setStroke(old);
					}
					Coordinate center = t.getCenter();
					Point s = this.getProjection().getPoint(0, center.latitude, center.longitude);
					double radius = 4;
					double x = (s.x * this.getProjection().scale) - (radius / 2);
					double y = (s.y * this.getProjection().scale) - (radius / 2);
					graphics.setColor(Tile.getSignificanceColor(t, this.significance));// t.getSignificance()));
					graphics.fillOval((int) x, (int) y, (int) radius, (int) radius);
				}
			}
		}
	}

	public void paintTile(Graphics graphics) {
//		logger.info(this + ".paintTile(...) tileList.size() = " + this.tileList.size());
		this.chroma = new Chroma(this.scheme);
		Point a;
		Point b;
		Point c;
		Point d;
		java.util.Iterator<Tile> iterator = this.tileList.iterator();
		this.initTileMinMax(this.tileList, true);
		while (iterator.hasNext()) {
			Tile t = new Tile(iterator.next());
			if (t != null) {
				a = this.getProjection().getPoint(0, t.coordinate.latitude, t.coordinate.longitude);
				b = this.getProjection().getPoint(0, t.coordinate.latitude + t.dimension, t.coordinate.longitude);
				c = this.getProjection().getPoint(0, t.coordinate.latitude + t.dimension,
						t.coordinate.longitude + t.dimension);
				d = this.getProjection().getPoint(0, t.coordinate.latitude, t.coordinate.longitude + t.dimension);
				if (a != null && b != null && c != null && d != null) {
					int xpoints[] = { (int) (a.x * this.getProjection().scale),
							(int) (b.x * this.getProjection().scale), (int) (c.x * this.getProjection().scale),
							(int) (d.x * this.getProjection().scale) };
					int ypoints[] = { (int) (a.y * this.getProjection().scale),
							(int) (b.y * this.getProjection().scale), (int) (c.y * this.getProjection().scale),
							(int) (d.y * this.getProjection().scale) };
					int npoints = 4;
					if (this.clearFlag) {
						if (t.value != 0) {
							Color color = null;
							color = this.chroma.getColor(t.value, this.getMin(), this.getMax());
							if (color != null) {
								graphics.setColor(color);
								graphics.fillPolygon(xpoints, ypoints, npoints);
							}
						}
					} else {
						Color color = this.chroma.getColor(t.value, this.getMin(), this.getMax());
						graphics.setColor(color);
						graphics.fillPolygon(xpoints, ypoints, npoints);
					}
				}
				Double significance = t.getSignificance();
				if (significance != null) {
					if (significance >= this.significance) {
						graphics.setColor(Color.BLACK);
					} else {
						graphics.setColor(Color.WHITE);
					}
					Coordinate center = t.getCenter();
					Point s = this.getProjection().getPoint(0, center.latitude, center.longitude);
					double radius = 4;
					double x = (s.x * this.getProjection().scale) - (radius / 2);
					double y = (s.y * this.getProjection().scale) - (radius / 2);
					graphics.fillOval((int) x, (int) y, (int) radius, (int) radius);
				}
			}
		}
		if (this.getProjection().scale >= this.defaultScale) {
			Meter meter = new Meter(0.9, (int) (this.getProjection().xMax * this.getProjection().scale), this.getMax(),
					this.getMin(), this.unit, this.getIncrement(), this.format);
			meter.setChroma(this.chroma);
			meter.paint(graphics);
		}
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		if (this.load) {
			if (this.stackFlag) {
				this.paintStack(graphics);
			} else if (this.bandFlag) {
				this.paintBand(graphics);
			} else {
				if (this.cluster != null) {// && !this.cluster.isEmpty()) {
					this.paintCluster(graphics);
				} else if (this.tileFlag && this.tileList != null && this.tileList.size() > 0) {
					this.paintTile(graphics);
				} else if (this.eventList != null && this.eventList.size() > 0) {
					if (this.trajectoryFlag) {
						this.paintTrajectory(graphics);
						this.paintEvent(graphics);
					} else {
						this.paintEvent(graphics);
					}
				}
			}
		}
		super.paint(graphics);
	}
}
//public List<Plot> plotList = new ArrayList<>();
//public int latitude = 90;// Eventually need to implement with 180
//this.initBandMinMax(this.bandList, false);
//if (!this.getProjection().verticalList.contains(vertical)) {
//this.getProjection().verticalList.add(vertical);
//}
//logger.info("paintTrajectory("+(graphics != null)+") vertical="+vertical);
//logger.info("paintTrajectory("+(graphics != null)+") this.getProjection().verticalList="+this.getProjection().verticalList);
//Original
//graphics.drawLine((int) ((c.x) * this.getProjection().scale),
//		(int) ((c.y) * this.getProjection().scale),
//		(int) ((c.x) * this.getProjection().scale),
//		(int) ((c.y) * this.getProjection().scale));
//New
//Time time = new Time("hour", calendar);
//if (time.lessThan(currentTime)) {
//public Color getCorrelationColor(Double correlation) {
//// logger.info(this + ".getCorrelatinColor(" + correlation + ")");
//Color color = Color.BLACK;
//if (correlation != null) {
//	double hue = 0;
//	// if(correlation < 0) {
//	// hue = 0.3;
//	// } else {
//	// hue = 240;
//	// }
//	double saturation = 0;
//	double brightness = Math.abs(correlation) * 100;
//	color = Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
//}
//return color;
//}

//public Color getSignificanceColor(Double significance) {
//if (significance != null && significance <= this.significance) {
//	return Color.GREEN;
//}
//return Color.RED;
//}
//public List<Coordinate> calendarCoordinateList(Calendar calendar, List<Coordinate> coordinateList) {
//List<Coordinate> cList = new ArrayList<>();
//for (Coordinate c : coordinateList) {
//	// System.out.println(calendar.getTime()+":"+c.calendar.getTime());
//	if (c.containsCalendar(calendar)) {
//		cList.add(c);
//	}
//}
//return cList;
//}
//public void setCalendarEventList(Calendar calendar, List<Event> eventList) {
//for (Event c : eventList) {
//	if (c.containsCalendar(calendar)) {
//		c.flag = true;
//	} else {
//		c.flag = false;
//	}
//}
//}

//public void setCalendarCoordinateList(Calendar calendar, List<Coordinate> coordinateList) {
//for (Coordinate c : coordinateList) {
//	if (c.containsCalendar(calendar)) {
//		c.flag = true;
//	} else {
//		c.flag = false;
//	}
//}
//}

//public List<Coordinate> getCoordinateList(Event event) {
//List<Coordinate> coordinateList = new ArrayList<>();
//for (Coordinate c : event.coordinateList) {
//	if (c.flag) {
//		coordinateList.add(c);
//	}
//}
//return coordinateList;
//}
//
//public List<Frame> getCalendarFrameList(Calendar calendar, List<Frame> frameList) {
//	List<Frame> fList = new ArrayList<>();
//	for (Frame f : frameList) {
//		if (f.containsCalendar(calendar)) {
//			fList.add(f);
//		}
//	}
//	return fList;
//}
//public double getArea(double dimension) {
//return dimension * dimension;
//}
//
//public double getArea(double latitude, double longitude, double dimension) {
//return Math.cos(Math.abs(Math.toRadians(this.getCenterLatitude(latitude, dimension))));
//}
//
//public double getCenterLatitude(double latitude, double dimension) {
//return latitude + (dimension / 2);
//}
//
//public double getCenterLongitude(double longitude, double dimension) {
//return longitude + (dimension / 2);
//}
//public double getMeters(int level) {
//double p = (double) level;
//double p0 = 1013.25;
//double T = 15;
//double meters = ((Math.pow(p0 / p, 1 / 5.257) - 1) * (T + 273.15)) / 0.0065;
//// logger.info("getMeters("+level+") meters="+meters);
//return meters;
//}
//if("month".equals(group)) {
//
//} else if("year".equals(group)) {
//this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][1];
//this.dataMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][1];
//}
//graphics.setColor(this.chroma.getColor(t.value, this.getMin(), this.getMax()));
//graphics.fillPolygon(xpoints, ypoints, npoints);
//Set<Integer> levelSet = this.tileListMap.keySet();
//int minLevel = Collections.min(levelSet);
//int maxLevel = Collections.max(levelSet);
//if (this.level) {
//	this.min = this.getMeters(maxLevel);
//	this.max = this.getMeters(minLevel);
//}
//this.initTileMinMax();
//double min = Double.POSITIVE_INFINITY;
//double max = Double.NEGATIVE_INFINITY;
//if (this.tileList != null) {
//	java.util.Iterator<Tile> iterator = this.tileList.iterator();
//	while (iterator.hasNext()) {
//		Tile t = new Tile(iterator.next());
//		if (t.value > max) {
//			max = t.value;
//		}
//		if (t.value < min) {
//			min = t.value;
//		}
//	}
//}
//this.max = max;
//this.min = min;
//logger.debug("initTileMinMax() this.min=" + this.min + " this.max=" + this.max);
// if (print && detail)
// logger.info("getMonthCount() monthCount=" + monthCount);
//this.increment = 1 / this.clusterList.size();
//this.scheme = null;
//this.unit = "id";
//this.paintTile(graphics);
//public boolean level = true;
//Point a;
//Point b;
//Point c;
//Point d;
//logger.info(this + ".paintStack(...) level=" + level);
//logger.info(this + ".paintStack(...) tileList.size()=" + tileList.size());
//if (index == 0) {
//	index--;
//}
//if(count == this.tileListMap.size()) {
//projection.showMap(true);
//} else {
//projection.showMap(false);
//}
//if(mapFlag) {
//projection.showMap(mapFlag);
//mapFlag = false;
//} else {
//projection.showMap(mapFlag);
//}
//projection.paint(graphics, vertical);
//count++;
//this.chroma = new Chroma(null);
//Collections.sort(this.clusterList, new Comparator<Cluster>() {
//	@Override
//	public int compare(Cluster o1, Cluster o2) {
//		return (o1.tileList.size()) - (o2.tileList.size());
//	}
//});
//for (Cluster cluster : this.clusterList) {
//	Graphics2D g2d = (Graphics2D) graphics;
//	Coordinate a;
//	Coordinate b;
//	Coordinate c;
//	Coordinate d;
//	this.tileList.addAll(cluster.getTileList());
//	java.util.Iterator<Tile> iterator = cluster.tileList.iterator();
//	while (iterator.hasNext()) {
//		Tile t = new Tile(iterator.next());
//		if (t != null) {
//			a = this.getProjection().getCoordinate(0, t.latitude, t.longitude);
//			b = this.getProjection().getCoordinate(0, t.latitude + t.dimension, t.longitude);
//			c = this.getProjection().getCoordinate(0, t.latitude + t.dimension, t.longitude + t.dimension);
//			d = this.getProjection().getCoordinate(0, t.latitude, t.longitude + t.dimension);
//			if (a != null && b != null && c != null && d != null) {
//				int xpoints[] = { (int) (a.point.x * this.getProjection().scale), (int) (b.point.x * this.getProjection().scale),
//						(int) (c.point.x * this.getProjection().scale), (int) (d.point.x * this.getProjection().scale) };
//				int ypoints[] = { (int) (a.point.y * this.getProjection().scale), (int) (b.point.y * this.getProjection().scale),
//						(int) (c.point.y * this.getProjection().scale), (int) (d.point.y * this.getProjection().scale) };
//				int npoints = 4;
//				g2d.setColor(this.chroma.getColor(cluster.getID(), 0, this.clusterList.size()));
//				g2d.fillPolygon(xpoints, ypoints, npoints);
//			}
//		}
//	}
//}
//if (this.getProjection().scale >= this.defaultScale) {
//Meter meter = new Meter(0.9, (int) (this.getProjection().xMax * this.getProjection().scale), this.clusterList.size(), 0, "id", 1);
//meter.setChroma(this.chroma);
//meter.paint(graphics);
//}
//boolean mapFlag = true;
//int count = 1;

//private boolean print = false;
//private boolean detail = false;
////logger.info(this.name+".updateSpace()");
//this.space = new Space();
//this.buffer = this.space;
//this.projection.setSpace(this.buffer);
//List<Variable> nodeList = this.getChildren();
//for (Variable n : nodeList) {
//	n.paint(graphics);
//}
//public Map<Integer, Integer> initYearMap() {
//this.yearMap = new HashMap<>();
//for (String sample : this.dateList) {
//	if (sample != null) {
//		String[] array = sample.split("-");
//		int year = Integer.parseInt(array[0]);
//		Integer count = this.yearMap.get(year);
//		if (count == null) {
//			count = 1;
//		} else {
//			count++;
//		}
//		this.yearMap.put(year, count);
//	}
//}
//if (print && detail)
//	logger.info("initYearMap() this.yearMap=" + yearMap);
//return this.yearMap;
//}
//public int getMonth(long milliseconds) {
//Date date = new Date(milliseconds);
//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//cal.setTime(date);
//int month = cal.get(Calendar.MONTH) + 1;
//return month;
//}

/**
 * Function converts Milliseconds to Year
 * 
 * @param milliseconds
 * @return
 */
//public int getYear(long milliseconds) {
//Date date = new Date(milliseconds);
//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//cal.setTime(date);
//int year = cal.get(Calendar.YEAR);
//return year;
//}

///**
//* 
//*/
//public void initMonthArray() {
//this.monthArray = new int[12];
//for (String date : this.dateList) {
//	if (date != null) {
//		String[] array = date.split("-");
//		int month = Integer.parseInt(array[1]);
//		this.monthArray[month - 1]++;
//	}
//}
//if (print && detail)
//	logger.info("initMonthArray() this.monthArray=" + Arrays.toString(this.monthArray));
//}
//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
//	public void paint(Graphics graphics) throws Exception {
//		if (this.load) {
//			switch(this.analysis) {
//			case CLUSTER : {
//				if(this.clusterList != null && !this.clusterList.isEmpty()) {
//					int count = 0;
////					this.chroma = new Chroma(Scheme.MAGMA);
//					Collections.sort(this.clusterList, new Comparator<Cluster>() {
//					    @Override
//					    public int compare(Cluster o1, Cluster o2) {
//					        return (o1.tileList.size())-(o2.tileList.size());
//					    }
//					});
//					for(Cluster cluster: this.clusterList) {
//						Graphics2D g2d = (Graphics2D) graphics;
////						g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
//						Coordinate a;
//						Coordinate b;
//						Coordinate c;
//						Coordinate d;
//						java.util.Iterator<Tile> iterator = cluster.tileList.iterator();
//						while (iterator.hasNext()) {
//							Tile t = new Tile(iterator.next());
//							if (t != null) {
//								a = this.getProjection().getCoordinate(0, t.latitude, t.longitude);
//								b = this.getProjection().getCoordinate(0, t.latitude + t.dimension, t.longitude);
//								c = this.getProjection().getCoordinate(0, t.latitude + t.dimension, t.longitude + t.dimension);
//								d = this.getProjection().getCoordinate(0, t.latitude, t.longitude + t.dimension);
//								if (a != null && b != null && c != null && d != null) {
//									int xpoints[] = { (int) (a.point.x * this.getProjection().scale),
//											(int) (b.point.x * this.getProjection().scale), (int) (c.point.x * this.getProjection().scale),
//											(int) (d.point.x * this.getProjection().scale) };
//									int ypoints[] = { (int) (a.point.y * this.getProjection().scale),
//											(int) (b.point.y * this.getProjection().scale), (int) (c.point.y * this.getProjection().scale),
//											(int) (d.point.y * this.getProjection().scale) };
//									int npoints = 4;
//									g2d.setColor(this.chroma.getColor(count, 0, this.clusterList.size()));
//									g2d.fillPolygon(xpoints, ypoints, npoints);
//								}
//							}
//						}
//						count++;
//					}
//				}
//				break;
//			}
//			case SIGNIFICANCE :{ 
//				if(this.regionList != null) {
//					Graphics2D g2d = (Graphics2D) graphics;
////					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
//					Coordinate a;
//					Coordinate b;
//					Coordinate c;
//					Coordinate d;
//					Iterator<Region> regionIterator = this.regionList.iterator();
//					Chroma significanceChroma = new Chroma(Scheme.MAGMA);
//					while(regionIterator.hasNext()) {
//						Region region = new Region(regionIterator.next());
//						if(region != null) {
//							Double significance = null;
//							Series series = this.seriesMap.get(region.toString());
//							if(series != null) {
//								List<Regression> regressionList = series.regressionMap.get("all");
//								if(regressionList != null && regressionList.size() > 0) {
//									significance = regressionList.get(0).map.get("significance");
//								}
//							}
//							if (this.tileList != null && this.tileList.size() > 0) {
//								java.util.Iterator<Tile> iterator = this.tileList.iterator();
//								while (iterator.hasNext()) {
//									Tile t = new Tile(iterator.next());
//									if (t != null && region.contains(t)) {
//										a = this.getProjection().getCoordinate(0, t.latitude, t.longitude);
//										b = this.getProjection().getCoordinate(0, t.latitude + t.dimension, t.longitude);
//										c = this.getProjection().getCoordinate(0, t.latitude + t.dimension, t.longitude + t.dimension);
//										d = this.getProjection().getCoordinate(0, t.latitude, t.longitude + t.dimension);
//										if (a != null && b != null && c != null && d != null) {
//											int xpoints[] = { (int) (a.point.x * this.getProjection().scale),
//													(int) (b.point.x * this.getProjection().scale), (int) (c.point.x * this.getProjection().scale),
//													(int) (d.point.x * this.getProjection().scale) };
//											int ypoints[] = { (int) (a.point.y * this.getProjection().scale),
//													(int) (b.point.y * this.getProjection().scale), (int) (c.point.y * this.getProjection().scale),
//													(int) (d.point.y * this.getProjection().scale) };
//											int npoints = 4;
//											if(significance != null) {
//												g2d.setColor(significanceChroma.getColor(significance, 0, 1));
//												g2d.fillPolygon(xpoints, ypoints, npoints);
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//				double increment = 0;
//				Meter meter = new Meter(0.9, (int)(projection.xMax * this.getProjection().scale), 1, 0,null,increment);
//				meter.setChroma(this.chroma);
//				meter.paint(graphics);
//				break;
//			}
//			default: {
//				if (this.tileList != null && this.tileList.size() > 0) {
//					logger.debug(this + ".paint(...) tileList.size() = " + this.tileList.size());
//					Graphics2D g2d = (Graphics2D) graphics;
////					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
//					Coordinate a;
//					Coordinate b;
//					Coordinate c;
//					Coordinate d;
//					java.util.Iterator<Tile> iterator = this.tileList.iterator();
//					while (iterator.hasNext()) {
//						Tile t = new Tile(iterator.next());
//						if (t != null) {
//							a = this.getProjection().getCoordinate(0, t.latitude, t.longitude);
//							b = this.getProjection().getCoordinate(0, t.latitude + t.dimension, t.longitude);
//							c = this.getProjection().getCoordinate(0, t.latitude + t.dimension, t.longitude + t.dimension);
//							d = this.getProjection().getCoordinate(0, t.latitude, t.longitude + t.dimension);
//							if (a != null && b != null && c != null && d != null) {
//								int xpoints[] = { (int) (a.point.x * this.getProjection().scale),
//										(int) (b.point.x * this.getProjection().scale), (int) (c.point.x * this.getProjection().scale),
//										(int) (d.point.x * this.getProjection().scale) };
//								int ypoints[] = { (int) (a.point.y * this.getProjection().scale),
//										(int) (b.point.y * this.getProjection().scale), (int) (c.point.y * this.getProjection().scale),
//										(int) (d.point.y * this.getProjection().scale) };
//								int npoints = 4;
//								g2d.setColor(this.chroma.getColor(t.value, this.getMin(), this.getMax()));
//								g2d.fillPolygon(xpoints, ypoints, npoints);
//							}
//						}
//					}
//					Meter meter = new Meter(0.9, (int)(projection.xMax * this.getProjection().scale), this.getMax(), this.getMin(),this.unit,this.getIncrement());
//					meter.setChroma(this.chroma);
//					meter.paint(graphics);
//				} else if (this.eventList != null && this.eventList.size() > 0) {
//					logger.debug(this + ".paint(...) eventList.size() = " + this.eventList.size());
//					for (int i = 0; i < this.eventList.size(); i++) {
//						Event event = (Event) this.eventList.get(i);
//						if (event.flag) {
//							logger.debug(this + ".paint(...) event=" + event.id);
//							List<Coordinate> coordinateList = this.projection.getCoordinateList(0, event.coordinateList);
//							if (coordinateList != null) {
//								for (Coordinate c : coordinateList) {
//									if (c != null && c.flag) {
//										logger.debug(this + ".paint(...) coordinate=" + c);
//										graphics.setColor(this.chroma.getColor(i, 0, this.eventList.size()));
//										graphics.drawLine((int) ((c.point.x) * this.projection.scale),
//												(int) ((c.point.y) * this.projection.scale),
//												(int) ((c.point.x) * this.projection.scale),
//												(int) ((c.point.y) * this.projection.scale));
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//			}
//		}
//
//		List<Variable> nodeList = this.getChildren();
//		for (Variable n : nodeList) {
//			n.paint(graphics);
//		}
//	}

//public void initDateList(List<Frame> frameList) {
//String sample;
//for (Frame f : frameList) {
//	if (f.flag) {
//		for (Long milliseconds : f.millisecondList) {
//			sample = this.getYear(milliseconds) + "-" + this.getMonth(milliseconds);
//			if (!this.dateList.contains(sample)) {
//				this.dateList.add(sample);
//			}
//		}
//	}
//}
//}
///**
//* Used by Ocean
//* @param dataType
//* @param frameList
//* @param dimension
//*/
//public void setFrameList(DataType dataType, List<Frame> frameList, double dimension) {
//	if (frameList != null) {
//		this.initDateList(frameList);
//		int latitude = (int) (this.latitude);// * this.resolution);
//		int longitude = (int) (this.longitude);// * this.resolution);
//		List<Data> dataList;
//		for (int i = 0; i < latitude; i += dimension) {
//			for (int j = 0; j < longitude; j += dimension) {
//				for (int a = i; a < (i + dimension); a++) {
//					for (int b = j; b < (j + dimension); b++) {
//						for (Frame f : frameList) {
//							dataList = f.data.get(a + "," + b);
//							for (Data d : dataList) {
//								if (d.type == dataType) {
//									dataMatrix[a][b] = d.value;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//	this.tileList = new ArrayList<>();
//	for (int i = 0; i < latitude; i += dimension) {
//		for (int j = 0; j < longitude; j += dimension) {
//			float value = dataMatrix[i][j];
//			int lat = (int) ((i - this.latitude));
//			int lon;
//			if (j < 180) {
//				lon = j;
//			} else {
//				lon = j - 360;
//			}
//			Tile tile = new Tile(lat, lon, dimension, value);
//			this.tileList.add(tile);
//		}
//	}
//	this.initTileMinMax();
//}
//@Override
//public void plot(Graphics graphics) throws Exception {
//	super.plot(graphics);
//	logger.info(this + ".plot(" + (graphics != null) + ", " + panel + ") this.load="+this.load);
//	logger.info(this + ".plot(" + (graphics != null) + ", " + panel + ") this.plot="+this.plot);
//	if (this.load) {// && this.plot) {
//		logger.info(this + ".plot(" + (graphics != null) + ", " + panel + ")");
//		panel.removeAll();
//		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//		List<Plot> plotList = this.getPlotList();
//		for (Plot plot : plotList) {
//			plot.setPanelHeight(256);
//			plot.setPanelWidth(panel.getWidth());
//			plot.repaint();
//			panel.add(plot);
//			panel.revalidate();
////			panel.repaint();
//		}
//		this.plot = false;
//	}
//	List<Variable> nodeList = this.getChildren();
//	for (Variable n : nodeList) {
//		n.plot(graphics,panel);
//	}
//}
//this.min = Double.POSITIVE_INFINITY;
//this.max = Double.NEGATIVE_INFINITY;
//this.indexList = new ArrayList<>();
//this.season = this.query.getSeason();
//List<Tile> tileList = new ArrayList<>();
//Tile tile;
//// cycle through each tile
//for (int i = 0; i < coordinateMatrix.length; i += dimension) {
//	for (int j = 0; j < coordinateMatrix[i].length; j += dimension) {
//		int lat = (int) ((i - this.latitude));
//		int lon;
//		if (j < 180) {
//			lon = j;
//		} else {
//			lon = j - 360;
//		}
//		tile = new Tile(lat, lon, dimension, 0);
////		tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution, dimension, 0);
//		tileList.add(tile);
//	}
//}
//public void paint(Graphics graphics) {
//projection.cubeFlag = this.cubeFlag;
//Coordinate a;
//Coordinate b;
//Coordinate c;
//Coordinate d;
//if (this.stackFlag) {
//	int size = this.tileListMap.size();
//	double interval = 1;
//	int index = (size/2);
//	this.tileListMap = new TreeMap<Integer, List<Tile>>(this.tileListMap).descendingMap();
//	for(Entry<Integer, List<Tile>> entry:this.tileListMap.entrySet()) {
//		List<Tile> tileList = entry.getValue();
//		if(index == 0) {
//			index--;
//		}
//		double vertical = index*interval;
//		for (Tile t : tileList) {
//			a = projection.getCoordinate(vertical,t.latitude, t.longitude);
//			b = projection.getCoordinate(vertical,t.latitude + t.dimension, t.longitude);
//			c = projection.getCoordinate(vertical,t.latitude + t.dimension, t.longitude + t.dimension);
//			d = projection.getCoordinate(vertical,t.latitude, t.longitude + t.dimension);
//			if (a != null && b != null && c != null && d != null) {
//				int xpoints[] = { (int) (a.point.x * projection.scale), (int) (b.point.x * projection.scale),
//						(int) (c.point.x * projection.scale), (int) (d.point.x * projection.scale) };
//				int ypoints[] = { (int) (a.point.y * projection.scale), (int) (b.point.y * projection.scale),
//						(int) (c.point.y * projection.scale), (int) (d.point.y * projection.scale) };
//				int npoints = 4;
//				graphics.setColor(this.chroma.getColor(t.value, this.getMin(), this.getMax()));
////				graphics.setColor(t.getColor(0.8, this.getMax(), this.getMin()));
//				graphics.fillPolygon(xpoints, ypoints, npoints);
//			}
//		}
////		projection.paint(graphics, vertical);
//		index--;
//	}
//} else {
//	if(this.bandFlag) {
//		for(Band band: this.bandList) {
//			for (Tile t : band.tileList) {
//				a = projection.getCoordinate(0,t.latitude, t.longitude);
//				b = projection.getCoordinate(0,t.latitude + t.dimension, t.longitude);
//				c = projection.getCoordinate(0,t.latitude + t.dimension, t.longitude + t.dimension);
//				d = projection.getCoordinate(0,t.latitude, t.longitude + t.dimension);
//				if (a != null && b != null && c != null && d != null) {
//					int xpoints[] = { (int) (a.point.x * projection.scale), (int) (b.point.x * projection.scale),
//							(int) (c.point.x * projection.scale), (int) (d.point.x * projection.scale) };
//					int ypoints[] = { (int) (a.point.y * projection.scale), (int) (b.point.y * projection.scale),
//							(int) (c.point.y * projection.scale), (int) (d.point.y * projection.scale) };
//					int npoints = 4;
//					graphics.setColor(this.chroma.getColor(band.value, this.getMin(), this.getMax()));
//					graphics.fillPolygon(xpoints, ypoints, npoints);
//				}
//			}
//		}
//	} else {
//		for (Tile t : this.tileList) {
//			a = projection.getCoordinate(0,t.latitude, t.longitude);
//			b = projection.getCoordinate(0,t.latitude + t.dimension, t.longitude);
//			c = projection.getCoordinate(0,t.latitude + t.dimension, t.longitude + t.dimension);
//			d = projection.getCoordinate(0,t.latitude, t.longitude + t.dimension);
//			if (a != null && b != null && c != null && d != null) {
//				int xpoints[] = { (int) (a.point.x * projection.scale), (int) (b.point.x * projection.scale),
//						(int) (c.point.x * projection.scale), (int) (d.point.x * projection.scale) };
//				int ypoints[] = { (int) (a.point.y * projection.scale), (int) (b.point.y * projection.scale),
//						(int) (c.point.y * projection.scale), (int) (d.point.y * projection.scale) };
//				int npoints = 4;
//				graphics.setColor(this.chroma.getColor(t.value, this.getMin(), this.getMax()));
////					graphics.setColor(t.getColor(0.8, this.getMax(), this.getMin()));
//				graphics.fillPolygon(xpoints, ypoints, npoints);
//			}
//		}
//	}
//}
//}