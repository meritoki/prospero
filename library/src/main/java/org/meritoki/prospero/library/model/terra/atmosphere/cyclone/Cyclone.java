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
package org.meritoki.prospero.library.model.terra.atmosphere.cyclone;

import java.awt.Graphics;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.histogram.Histogram;
import org.meritoki.prospero.library.model.plot.time.TimePlot;
import org.meritoki.prospero.library.model.terra.atmosphere.Atmosphere;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.density.Density;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.genesis.Genesis;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.lifetime.Lifetime;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.lysis.Lysis;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.speed.InstantaneousSpeed;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.speed.Speed;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.Classification;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.Family;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.vorticity.Vorticity;
import org.meritoki.prospero.library.model.unit.Analysis;
import org.meritoki.prospero.library.model.unit.Band;
import org.meritoki.prospero.library.model.unit.Bar;
import org.meritoki.prospero.library.model.unit.Cluster;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Count;
import org.meritoki.prospero.library.model.unit.Duration;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Regression;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Series;
import org.meritoki.prospero.library.model.unit.Table;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;

import com.meritoki.library.controller.node.Exit;
import com.meritoki.library.controller.node.NodeController;

public class Cyclone extends Atmosphere {

	static Logger logger = LogManager.getLogger(Cyclone.class.getName());
	public String title = this.name;
	public List<Integer> pressureList;
	public List<Duration> durationList;
	public List<Family> familyList;
	public List<Classification> classificationList;
	public Count count;

	public Cyclone() {
		super("Cyclone");
		this.addChild(new Density());
		this.addChild(new Genesis());
		this.addChild(new Lysis());
		this.addChild(new Vorticity());
		this.addChild(new Speed());
		this.addChild(new InstantaneousSpeed());
		this.addChild(new Lifetime());
		this.sourceMap.put("UTN ERA INTERIM", "2d611935-9786-4c28-9dcf-f18cf3e99a3a");
		this.sourceMap.put("UTN ERA INTERIM TEST", "cc7b89a0-ccc4-4a76-a79b-c0e04b9dd45a");
		this.sourceMap.put("UTN ERA 5", "281cbf52-7014-4229-bffd-35c8ba41bcb5");
	}

	public Cyclone(String name) {
		super(name);
		this.sourceMap.put("UTN ERA INTERIM", "2d611935-9786-4c28-9dcf-f18cf3e99a3a");
		this.sourceMap.put("UTN ERA 5", "281cbf52-7014-4229-bffd-35c8ba41bcb5");
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public void init() {
		super.init();
		try {
			this.familyList = query.getFamilyList();
			this.classificationList = query.getClassificationList();
			this.durationList = query.getDurationList();
			this.pressureList = query.getPressureList();
			this.count = query.getCount();
		} catch (Exception e) {
			logger.error("init() exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Method is used to set flags in Event and Coordinate to true or false
	 * depending on query parameters
	 */
	@Override
	public void filter(List<Event> eventList) throws Exception {
		super.filter(eventList);
		if (!Thread.interrupted()) {
			if (eventList != null) {
				boolean levelFlag = false;
				boolean durationFlag = false;
				boolean familyFlag = false;
				boolean classFlag = false;
				for (Event e : eventList) {
					durationFlag = false;
					familyFlag = false;
					classFlag = false;
					for (Coordinate c : e.coordinateList) {
						levelFlag = false;
						if (this.pressureList != null && this.pressureList.size() > 0) {
							for (Integer l : this.pressureList) {
								int level = (int) c.attribute.get("pressure");
								if (l == level) {
									levelFlag = true;
								}
							}
						} else {
							levelFlag = true;
						}
						c.flag = levelFlag;
					}
					if (this.durationList != null && this.durationList.size() > 0) {
						for (Duration d : this.durationList) {
							if (d.contains(e.getDuration())) {
								durationFlag = true;
								break;
							}
						}
					} else {
						durationFlag = true;
					}
					if (this.familyList != null && this.familyList.size() > 0) {
						for (Family family : this.familyList) {
							if (((CycloneEvent) e).family != null && family == ((CycloneEvent) e).family) {
								familyFlag = true;
								break;
							}
						}
					} else {
						familyFlag = true;
					}
					if (this.classificationList != null && this.classificationList.size() > 0) {
						for (Classification classification : classificationList) {
							if (classification == ((CycloneEvent) e).classification) {
								classFlag = true;
								break;
							}
						}
					} else {
						classFlag = true;
					}
					e.flag = durationFlag && familyFlag && classFlag;
				}
				this.setCountEventList(eventList, this.count);
			}
		} else {
			throw new InterruptedException();
		}
	}

	/**
	 * Function Sets Cyclone Event Flag to False Where Count Condition is Not Satisfied.
	 * @param eventList
	 * @param count
	 */
	public void setCountEventList(List<Event> eventList, Count count) {
		if (count != null) {
			Iterator<Event> eventIterator = eventList.iterator();
			while (eventIterator.hasNext()) {
				Event e = eventIterator.next();
				if (e instanceof CycloneEvent) {
					CycloneEvent event = (CycloneEvent) e;
					switch (count.operator) {
					case '>': {
						if (event.getPressureCount() <= count.value) {
							event.flag = false;
						}
						break;
					}
					case '<': {
						if (event.getPressureCount() >= count.value) {
							event.flag = false;
						}
						break;
					}
					case '=': {
						if (event.getPressureCount() < count.value || event.getPressureCount() > count.value) {
							event.flag = false;
						}
						break;
					}
					}
				}
			}
		}
	}

	/**
	 * Reviewed 202112160851 Good
	 */
	@Override
	public void load(Result result) {
		super.load(result);
		List<Event> eventList = result.getEventList();
		this.eventList.addAll(new ArrayList<>(eventList));
		try {
			this.process(eventList);
//			this.addRootObject(new Result(Mode.PAINT));
		} catch (Exception e) {
			logger.error("load(" + (result != null) + ") exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Reviewed 202112160852 Good
	 */
	@Override
	public void process() throws Exception {
		super.process();// this.init();
		try {
			this.process(new ArrayList<>(this.eventList));
			this.complete();
		} catch (Exception e) {
			logger.error("process() exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Process is a singular function that is used to add new events to
	 * 
	 * @param eventList
	 */
	public void process(List<Event> eventList) throws Exception {
		this.region = null;
		this.reset(eventList);
		this.filter(eventList);
		this.prune(eventList);
		List<Time> timeList = this.setEventMap(this.eventMap, eventList);// what we have
		for (Time time : timeList) {
			if (!this.timeList.contains(time)) {
				time.flag = true;
				this.timeList.add(time);// what we have seen
			} else {
				int index = this.timeList.indexOf(time);
				this.timeList.get(index).flag = true;
			}
		}
		this.setMatrix(eventList);
		if (this.regionList != null && this.regionList.size() > 0) {
			for (Time time : this.timeList) {
				if (!time.flag) {
					try {
						eventList = this.eventMap.get(time);
						if (eventList != null) {
							for (Region region : this.regionList) {
								this.region = region;
								Series series = this.seriesMap.get(region.toString());
								if (series == null) {
									series = this.newSeries();
								}
								super.filter(eventList);
								series.addIndex(this.getIndex(time, eventList));
								this.seriesMap.put(region.toString(), series);
							}
							this.initPlotList(this.seriesMap, null);
//							this.initTableList(eventList, null, null, null);
							this.eventMap.remove(time);
						}
					} catch (Exception e) {
						logger.error("complete() exception=" + e.getMessage());
						e.printStackTrace();
					}
				} else {
					time.flag = false;
				}
			}
		}
	}

	@Override
	public void complete() {
		super.complete();
		if (this.regionList != null && this.regionList.size() > 0) {//
			for (Time time : this.timeList) {
				try {
					List<Event> eventList = this.eventMap.get(time);
					if (eventList != null) {
						for (Region region : this.regionList) {
							this.region = region;
							Series series = this.seriesMap.get(region.toString());
							if (series == null) {
								series = this.newSeries();
							}
							super.filter(eventList);
							series.addIndex(this.getIndex(time, eventList));
							this.seriesMap.put(region.toString(), series);
						}
						this.eventMap.remove(time);
					}
				} catch (Exception e) {
					logger.error("complete() exception=" + e.getMessage());
					e.printStackTrace();
				}
			}

		}
		if (this.analysis == Analysis.CLUSTER) {
			this.cluster();
		}
		this.initPlotList(this.seriesMap, this.eventList);
		this.initTableList(this.eventList, this.tileList, this.bandList, this.clusterList);
		this.addRootObject(new Result(Mode.PAINT));
	}

	/**
	 * 
	 */
	public void cluster() {
		// Time List Contains All Moments in Time Queried
		// Time Tile Map Retains All Tiles at Moments in Time (Key)
		// All Tile Lists Have Same Tile Order
		// Constructing String for File w/ New Lines
		// Each Column is a Tile Latitude and Longitude
		// Each Row is a moment in Time
		// Must be Monthly Averages
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.timeList.size(); i++) {
			Time time = this.timeList.get(i);
			List<Tile> tileList = this.timeTileMap.get(time);
			if (tileList != null) {
				if (i == 0) {
					sb.append("\"\"");
					for (Tile tile : tileList) {
						sb.append(",\"" + String.valueOf(tile.coordinate.latitude).replace("-", "N") + "_"
								+ String.valueOf(tile.coordinate.longitude).replace("-", "N") + "\"");
					}
					sb.append("\n");
				}
				sb.append("\"" + (i + 1) + "\"");
				for (Tile tile : tileList) {
					sb.append(",\"" + tile.value + "\"");
				}
				sb.append("\n");
			}
		}
		// Save String to CSV File
		Date dateTime = Calendar.getInstance().getTime();
		String date = new SimpleDateFormat("yyyyMMdd").format(dateTime);
		String uuid = UUID.randomUUID().toString();
		String path = "." + File.separatorChar + "output" + File.separatorChar + date + File.separatorChar + name;
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		NodeController.saveText(path, uuid + ".csv", sb);
		// Prepare R Command w/ Parameters
		// R Script
		// Parameter One: CSV File w/ Path
		// Parameter Two: Start Year
		// Parameter Three: Start Month
		// Parameter Four: End Year
		// Parameter Five: End Month
		// Parameter Six: Output CSV File w/ Path
		String input = path + File.separatorChar + uuid + ".csv";
		String output = path + File.separatorChar + "output-" + uuid + ".csv";
		Time a = timeList.get(0);
		Time b = timeList.get(timeList.size() - 1);
		String rCommand = "Rscript comparison.R " + input;
		rCommand += " " + a.year + " " + a.month + " " + b.year + " " + b.month;
		rCommand += " " + output;
		Exit exit;
		try {
			exit = NodeController.executeCommand(rCommand, 1440 * 64);
			if (exit.value != 0) {
				throw new Exception("Non-Zero Exit Value: " + exit.value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Read Output CSV File
		// CSV Contains Tile Latitude and Longitude and what Cluster it belongs to w/ ID
		// Must Use ID Returned by Algorithm to Create Lists of Tiles
		List<String[]> outputList = NodeController.openCsv(output);
		outputList = outputList.subList(2, outputList.size() - 1);
		List<Tile> tileList = new ArrayList<Tile>();
		Map<Integer, List<Tile>> tileListMap = new HashMap<>();
		for (String[] stringArray : outputList) {
			String coordinate = stringArray[0];
			coordinate = coordinate.replace("\"", "");
			String[] coordinateArray = coordinate.split("_");
			double latitude = Double.parseDouble(coordinateArray[0].replace("N", "-"));
			double longitude = Double.parseDouble(coordinateArray[1].replace("N", "-"));
			Integer id = Integer.parseInt(stringArray[1]);
			tileList = tileListMap.get(id);
			if (tileList == null) {
				tileList = new ArrayList<>();
			}
			Tile tile = new Tile(latitude, longitude, this.dimension);
			tile.flag = true;
			tileList.add(tile);
			tileListMap.put(id, tileList);
		}
		// Lists of Tiles are Converted to Cluster Objects
		// Cluster Object Tile List provides the Latitude, Longitude, and Dimension
		// corresponding to the Cluster
		// The Tile List does not retain any
		List<Cluster> clusterList = new ArrayList<>();
		Cluster cluster;
		for (Entry<Integer, List<Tile>> entry : tileListMap.entrySet()) {
			cluster = new Cluster();
			cluster.id = entry.getKey();
			cluster.tileList = new ArrayList<>(entry.getValue());// Defect Was Here, Uses Copy Constructor and Important
																	// Variables where Not Transferred in Copy
			clusterList.add(cluster);
		}
		this.clusterList = clusterList;
		this.getClusterPlots(this.clusterList);

	}

	/**
	 * Defect 20221129. Cluster Has Tile List w/ Flag True Within this method
	 * Cluster must be given all Tiles
	 * 
	 * @param clusterList
	 */
	public void getClusterPlots(List<Cluster> clusterList) {
//		logger.info("getClusterPlots(" + clusterList + ")");
		Map<String, Series> seriesMap = new HashMap<>();
		// Time Tile Map Retains All Tiles at Moments in Time
		// The Main For Loop Iterates Over a Map w/ Time Key and Tile List Value
		// Each Cluster Contains a Tile List Describing the Position
		// Each Tile Can Have a Temporary Real Number Value
		// The Time Tile List is Loaded into a Cluster Tile List
		for (Entry<Time, List<Tile>> entry : this.timeTileMap.entrySet()) {
			Time time = entry.getKey();
			List<Tile> tileList = entry.getValue();
			// Given that Each Cluster has a Persistent and Unique ID
			// We Get a Series from the Series Map by Cluster ID
			// We Add and Average of the Cluster Tile List Values to an Index
			// We Add the Index to the Series Building a Series of Averages for all Tiles
			// Belonging to a Cluster
			for (Cluster cluster : clusterList) {
				Series series = seriesMap.get(cluster.uuid);
				if (series == null) {
					series = this.newSeries();
					series.map.put("cluster", cluster.id);
				}
				cluster.setTileList(tileList);// Problem
				double average = cluster.getAverageValue();
				cluster.addTilePoint(average);
				Index index = time.getIndex();
				index.value = average;
				series.addIndex(index);
				seriesMap.put(cluster.uuid, series);
			}
		}
		this.seriesMap.putAll(seriesMap);
	}

	public Series newSeries() {
		Series series = new Series();
		series.map.put("startCalendar", this.startCalendar);
		series.map.put("endCalendar", this.endCalendar);
		series.map.put("name", this.name);
		series.map.put("average", this.averageFlag);
		series.map.put("sum", this.sumFlag);
		series.map.put("regression", this.regression);
		series.map.put("region", region.toString());
		series.map.put("family", this.query.getFamily());
		series.map.put("class", this.query.getClassification());
		series.map.put("group", this.query.getGroup());
		series.map.put("variable", this.query.getVariable());
		series.map.put("window", this.window);
		series.map.put("range", this.range);
		Query q = new Query(this.query);
		q.map.put("region", region.toString());
		series.map.put("query", q);
		return series;
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

	public List<Time> setEventMap(HashMap<Time, List<Event>> eventMap, List<Event> eventList) {
		String[] groupArray = this.group.split(",");
		List<Time> timeList = new ArrayList<>();
		if (eventList != null) {
			for (Event event : eventList) {
				for (String group : groupArray) {
					Time key = this.getTime(group, event);
					if (key != null) {
						if (!timeList.contains(key)) {
							timeList.add(key);
						}
						List<Event> eList = eventMap.get(key);
						if (eList == null) {
							eList = new ArrayList<>();
						}
						eList.add(event);
						eventMap.put(key, eList);
					}
				}
			}
		}
//	logger.info("setEventMap("+eventMap.size()+", "+eventList.size()+") timeList="+timeList);
		return timeList;
	}

	public Index getIndex(Time key, List<Event> eventList) {
		Index index = null;
		int value = 0;
		for (Event e : eventList) {
			if (e.flag) {
				value++;
			}
		}
		if (value > 0) {
			index = key.getIndex();
			index.value = value;
		}
		return index;
	}

	public void initPlotList(Map<String, Series> seriesMap, List<Event> eventList) {
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
		if (eventList != null) {
			plotList.add(this.getEventTotalDurationCountHistogram(eventList));
			plotList.add(this.getEventTotalDistanceCountHistogram(eventList));
			plotList.add(this.getEventTotalMeanVelocityCountHistogram(eventList));
			plotList.add(this.getEventTotalMeanVorticityCountHistogram(eventList));
//			plotList.add(this.getEventLevelCountHistogram(eventList));
			plotList.add(this.getEventLowermostLevelCountHistogram(eventList));
			plotList.add(this.getEventTotalLevelCountHistogram(eventList));
			plotList.add(this.getEventUppermostLevelCountHistogram(eventList));
			plotList.add(this.getGenesisLowermostLevelCountHistogram(eventList));
			plotList.add(this.getGenesisUppermostLevelCountHistrogram(eventList));
			plotList.add(this.getLysisLowermostLevelCountHistogram(eventList));
			plotList.add(this.getLysisUppermostLevelCountHistogram(eventList));
		}
		this.plotList = plotList;
	}

	public void initTableList(List<Event> eventList, List<Tile> tileList, List<Band> bandList,
			List<Cluster> clusterList) {
		List<Table> tableList = new ArrayList<>();
		if (eventList != null && eventList.size() > 0) {
			tableList.add(new Table("Cyclone Event(s)", CycloneEvent.getTableModel(eventList,this.calendar)));
		}
		if (eventList != null && eventList.size() > 0) {
			tableList.add(new Table("Cyclone Event Time", CycloneEvent.getTimeTableModel(eventList, this.calendar)));
		}
		if (tileList != null && tileList.size() > 0) {
			tableList.add(new Table("Tiles(s)", Tile.getTableModel(tileList)));
		}
		if (bandList != null && bandList.size() > 0) {
			tableList.add(new Table("Band(s)", Band.getTableModel(bandList)));
		}
		if (clusterList != null && clusterList.size() > 0) {
			tableList.add(new Table("Cluster(s)", Cluster.getTableModel(clusterList)));
		}
		this.tableList = tableList;
	}

	@Override
	public List<Table> getTableList() throws Exception {
		return this.tableList;
	}

	@Override
	public List<Plot> getPlotList() throws Exception {
		return this.plotList;
	}

	public void setMatrix(List<Event> eventList) {
		List<Time> timeList = this.setCoordinateMatrix(this.coordinateMatrix, eventList);
		for (Time t : timeList) {
			if (!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
		this.tileFlag = !this.name.equals("Cyclone");
		this.tileList = this.getTileList();
		this.bandList = this.getBandList(this.tileList);
		if (this.stackFlag) {
			List<Integer> levelList = this.getEventPressureList(eventList);
			for (Integer level : levelList) {
				this.coordinateMatrix = this.coordinateMatrixMap.get(level);
				List<Tile> tileList = this.getTileList();
				this.tileListMap.put(level, tileList);
			}
		}
	}

	public List<Band> getBandList(List<Tile> tileList) {
		List<Band> bandList = new ArrayList<>();
		if (tileList != null && tileList.size() > 0) {
			List<Double> tileLatitudeList = this.getTileLatitudeList(tileList);
			for (Double latitude : tileLatitudeList) {
				List<Tile> bandTileList = new ArrayList<>();
				for (Tile t : tileList) {
					if (latitude.equals(t.coordinate.latitude)) {
						bandTileList.add(t);
					}
				}
				Band band = new Band(bandTileList, latitude);
				bandList.add(band);
			}
		}
		return bandList;
	}

	public List<Time> setCoordinateMatrix(int[][][] coordinateMatrix, List<Event> eventList) {
		List<Time> timeList = null;
		Time startTime = new Time("month", this.startCalendar);
		Time endTime = new Time("month", this.endCalendar);
		if (eventList != null) {
			timeList = new ArrayList<>();
			if (this.stackFlag) {
				List<Integer> pressureList = this.getEventPressureList(eventList);
				for (Integer pressure : pressureList) {
					coordinateMatrix = this.coordinateMatrixMap.get(pressure);
					if (coordinateMatrix == null)
						coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
					for (Event e : eventList) {
						for (Coordinate p : e.coordinateList) {
							if (p.flag && ((Integer) p.attribute.get("pressure")).equals(pressure)) {
								Time time = new Time(p.getYear(), p.getMonth(), -1, -1, -1, -1);
								if (startTime.lessThan(time) && time.lessThan(endTime)) {
									int x = (int) ((p.latitude + this.latitude) * this.resolution);
									int y = (int) ((p.longitude + this.longitude / 2) * this.resolution)
											% this.longitude;
									int z = p.getMonth() - 1;
									coordinateMatrix[x][y][z]++;
									if (!timeList.contains(time)) {
										timeList.add(time);
									}
								}
							}
						}
					}
					this.coordinateMatrixMap.put(pressure, coordinateMatrix);
				}
			} else {
				for (Event e : eventList) {
					if (e.flag) {
						for (Coordinate c : e.coordinateList) {
							if (c.flag) {
								Time time = new Time(c.getYear(), c.getMonth(), -1, -1, -1, -1);
								if (startTime.lessThan(time) && time.lessThan(endTime)) {
									int x = (int) ((c.latitude + this.latitude) * this.resolution);
									int y = (int) ((c.longitude + this.longitude / 2) * this.resolution)
											% this.longitude;
									int z = c.getMonth() - 1;
									coordinateMatrix[x][y][z]++;
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

	public Time getTime(String value, Event e) {
		Calendar calendar = e.getStartCalendar();
		int year = -1;
		int month = -1;
		int day = -1;
		int hour = -1;
		int minute = -1;
		int second = -1;
		if (value != null) {
			switch (value) {
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
				this.monthFlag = true;
				month = calendar.get(Calendar.MONTH) + 1;
				year = calendar.get(Calendar.YEAR);
				break;
			}
			case "year": {
				this.yearFlag = true;
				year = calendar.get(Calendar.YEAR);
				break;
			}
			case "djf": {
				if (value.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
					month = calendar.get(Calendar.MONTH) + 1;
					year = calendar.get(Calendar.YEAR);
				}
				break;
			}
			case "mam": {
				if (value.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
					month = calendar.get(Calendar.MONTH) + 1;
					year = calendar.get(Calendar.YEAR);
				}
				break;
			}
			case "jja": {
				if (value.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
					month = calendar.get(Calendar.MONTH) + 1;
					year = calendar.get(Calendar.YEAR);
				}
				break;
			}
			case "son": {
				if (value.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
					month = calendar.get(Calendar.MONTH) + 1;
					year = calendar.get(Calendar.YEAR);
				}
				break;
			}
			}
		}
		Time time = new Time(year, month, day, hour, minute, second);
		return time;
	}

	public List<List<Index>> getMatrix(List<Regression> regressionList) {
		List<List<Index>> matrix = new ArrayList<>();
		if (regressionList != null) {
			for (Regression r : regressionList) {
				if (r.getIndexList().size() > 0) {
					matrix.add(r.getIndexList());
				}
			}
		}
		return matrix;
	}

	public List<Integer> getEventPressureList(List<Event> eventList) {
		List<Integer> levelList = new ArrayList<>();
		for (Event e : eventList) {
			if (e.flag) {
				List<Integer> lList = ((CycloneEvent) e).getSelectedPressureList();
				for (Integer i : lList) {
					if (!levelList.contains(i)) {
						levelList.add(i);
					}
				}
			}
		}
		Collections.sort(levelList);
		return levelList;
	}

	public Histogram getEventTotalMeanVelocityCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "event-total-mean-velocity-count-histogram";
		Map<Integer, Integer> countMap = new HashMap<>();
		histogram.setTitle("Mean Velocity Count");
		histogram.setXLabel("Event Mean Velocity (m/s)");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			int velocity;
			for (Event e : eventList) {
				if (e.flag) {
					velocity = (int) (((CycloneEvent) e).getMeanSpeed());
					count = countMap.get(velocity);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(velocity, count);
				}
			}
			countMap = new TreeMap<Integer, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<Integer, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), String.valueOf(entry.getKey()));
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getEventTotalMeanVorticityCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "event-total-mean-vorticity-count-histogram";
		Map<String, Integer> countMap = new HashMap<>();
		histogram.setTitle("Mean Vorticity Count");
		histogram.setXLabel("Event Mean Vorticity");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			String velocity;
			NumberFormat formatter = new DecimalFormat("0E0");
			for (Event e : eventList) {
				if (e.flag) {
					velocity = formatter.format(((CycloneEvent) e).getMeanVorticity());
					count = countMap.get(velocity);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(velocity, count);
				}
			}
			countMap = new TreeMap<String, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<String, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), String.valueOf(entry.getKey()));
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getEventTotalDurationCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "event-total-duration-count-histogram";
		Map<Integer, Integer> countMap = new HashMap<>();
		histogram.setTitle("Duration Count");
		histogram.setXLabel("Event Duration (Days)");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			int days;
			for (Event e : eventList) {
				if (e.flag) {
					days = (int) (((CycloneEvent) e).getDuration()).days;
					count = countMap.get(days);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(days, count);
				}
			}
			countMap = new TreeMap<Integer, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<Integer, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), String.valueOf(entry.getKey()));
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getEventTotalDistanceCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "event-total-distance-count-histogram";
		Map<Integer, Integer> countMap = new HashMap<>();
		histogram.setTitle("Distance Count");
		histogram.setXLabel("Event Distance (1000 Kilometers)");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			int distance;
			for (Event e : eventList) {
				if (e.flag) {
					distance = (int) (((CycloneEvent) e).getDistance());// Meters
					int range = distance / 1000;// Kilometers
					range /= 1000;
					count = countMap.get(range);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(range, count);
				}
			}
			countMap = new TreeMap<Integer, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<Integer, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), String.valueOf(entry.getKey()));
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getEventLevelCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "event-level-count-histogram";
		Map<String, Integer> countMap = new HashMap<>();
		histogram.setTitle("Pressure Percentage (%)");
		histogram.setXLabel("Event Pressure");
		histogram.setYLabel("Event Percentage (%)");
		if (eventList != null) {
			Integer count = 0;
			String level;
			for (Event e : eventList) {
				if (e.flag) {
					for (Integer l : ((CycloneEvent) e).getPressureList()) {
						level = String.valueOf(l);
						count = countMap.get(level);
						if (count == null) {
							count = 0;
						}
						count++;
						countMap.put(level, count);
					}
				}
			}
			countMap = new TreeMap<String, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<String, Integer> entry : countMap.entrySet()) {
				double percentage = (double) entry.getValue() / (double) eventList.size() * 100;
				bar = new Bar(percentage, entry.getKey());
				histogram.addBar(bar);
			}
			histogram.setYMax(100);
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getEventLowermostLevelCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		Map<String, Integer> countMap = new HashMap<>();
		histogram.data = "event-lowermost-level-count-histogram";
		histogram.setTitle("Lowermost Level Count");
		histogram.setXLabel("Event Level");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			String level;
			for (Event e : eventList) {
				if (e.flag) {
					level = String.valueOf(((CycloneEvent) e).getLowerMostLevel());
					count = countMap.get(level);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(level, count);
				}
			}
			countMap = new TreeMap<String, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<String, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), entry.getKey());
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getEventTotalLevelCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "event-total-level-count-histogram";
		Map<Integer, Integer> countMap = new HashMap<>();
		histogram.setTitle("Level Count");
		histogram.setXLabel("Event Level");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			int level;
			for (Event e : eventList) {
				if (e.flag) {
					level = (((CycloneEvent) e).getPressureCount());
					count = countMap.get(level);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(level, count);
				}
			}
			countMap = new TreeMap<Integer, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<Integer, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), String.valueOf(entry.getKey()));
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getEventUppermostLevelCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "event-uppermost-level-count-histogram";
		Map<String, Integer> countMap = new HashMap<>();
		histogram.setTitle("Uppermost Level Count");
		histogram.setXLabel("Event Level");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			String level;
			for (Event e : eventList) {
				if (e.flag) {
					level = String.valueOf(((CycloneEvent) e).getUpperMostLevel());
					count = countMap.get(level);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(level, count);
				}
			}
			countMap = new TreeMap<String, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<String, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), entry.getKey());
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getGenesisLowermostLevelCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "genesis-lowermost-level-count-histogram";
		Map<String, Integer> countMap = new HashMap<>();
		histogram.setTitle("Genesis Lowermost Level Count");
		histogram.setXLabel("Event Level");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			String level;
			for (Event e : eventList) {
				if (e.flag) {
					level = String.valueOf(((CycloneEvent) e).getGenesisLowermostLevel());
					count = countMap.get(level);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(level, count);
				}
			}
			countMap = new TreeMap<String, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<String, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), entry.getKey());
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getGenesisUppermostLevelCountHistrogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		Map<String, Integer> countMap = new HashMap<>();
		histogram.data = "genesis-uppermost-level-count-histogram";
		histogram.setTitle("Genesis Uppermost Level Count");
		histogram.setXLabel("Event Level");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			String level;
			for (Event e : eventList) {
				if (e.flag) {
					level = String.valueOf(((CycloneEvent) e).getGenesisUppermostLevel());
					count = countMap.get(level);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(level, count);
				}
			}
			countMap = new TreeMap<String, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<String, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), entry.getKey());
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getLysisLowermostLevelCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "lysis-lowermost-level-count-histogram";
		Map<String, Integer> countMap = new HashMap<>();
		histogram.setTitle("Lysis Lowermost Level Count");
		histogram.setXLabel("Event Level");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			String level;
			for (Event e : eventList) {
				if (e.flag) {
					level = String.valueOf(((CycloneEvent) e).getLysisLowermostLevel());
					count = countMap.get(level);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(level, count);
				}
			}
			countMap = new TreeMap<String, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<String, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), entry.getKey());
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	public Histogram getLysisUppermostLevelCountHistogram(List<Event> eventList) {
		Histogram histogram = new Histogram();
		histogram.data = "lysis-uppermost-level-count-histogram";
		Map<String, Integer> countMap = new HashMap<>();
		histogram.setTitle("Lysis Uppermost Level Count");
		histogram.setXLabel("Event Level");
		histogram.setYLabel("Event Count");
		if (eventList != null) {
			Integer count = 0;
			String level;
			for (Event e : eventList) {
				if (e.flag) {
					level = String.valueOf(((CycloneEvent) e).getLysisUppermostLevel());
					count = countMap.get(level);
					if (count == null) {
						count = 0;
					}
					count++;
					countMap.put(level, count);
				}
			}
			countMap = new TreeMap<String, Integer>(countMap).descendingMap();
			Bar bar;
			for (Entry<String, Integer> entry : countMap.entrySet()) {
				bar = new Bar(entry.getValue(), entry.getKey());
				histogram.addBar(bar);
			}
			histogram.initYMax();
		}
		histogram.initTableList();
		return histogram;
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
	}
}

//@JsonIgnore
//@Override
//public void setCalendar(Calendar calendar) {
//	this.calendar = calendar;
//	this.query();
//	List<Variable> nodeList = this.getChildren();
//	for (Variable n : nodeList) {
//		n.setCalendar(calendar);
//	}
//}
//public List<Cluster> getClusterList(List<Tile> tileList) {
//List<Cluster> clusterList = new ArrayList<>();
//List<TileWrapper> clusterInput = new ArrayList<TileWrapper>(tileList.size());
//for (Tile tile : tileList) {
//	clusterInput.add(new TileWrapper(tile));
//}
//KMeansPlusPlusClusterer<TileWrapper> clusterer = new KMeansPlusPlusClusterer<TileWrapper>(10, 10000);
////MultiKMeansPlusPlusClusterer mClusterer = new MultiKMeansPlusPlusClusterer(clusterer,10);
//List<CentroidCluster<TileWrapper>> clusterResults = clusterer.cluster(clusterInput);
//for (int i = 0; i < clusterResults.size(); i++) {
//	Cluster cluster = new Cluster();
//	for (TileWrapper tileWrapper : clusterResults.get(i).getPoints()) {
//		cluster.tileList.add(tileWrapper.getTile());
//	}
////    logger.info("getClusterList("+tileList.size()+") cluster.tileList.size()="+cluster.tileList.size());
//	clusterList.add(cluster);
//}
//return clusterList;
//}
//Collections.sort(this.clusterList, new Comparator<Cluster>() {
//@Override
//public int compare(Cluster o1, Cluster o2) {
//	return (o1.tileList.size()) - (o2.tileList.size());
//}
//});
//this.tileList = new ArrayList<>();
//for (Cluster c : this.clusterList) {
//this.tileList.addAll(c.getTileList());
//}
//for (Tile t : tileList) {
//for (Cluster cluster : clusterList) {
//	if (cluster.contains(t)) {
//		cluster.setTile(t);
//	}
//}
//}
//this.initPlotList(this.seriesMap, null);
//List<Variable> nodeList = this.getChildren();
//for (Variable n : nodeList) {
//	n.paint(graphics);
//}
//if (this.load) {
//	super.paint(graphics);
//}
//else if(this.bandFlag) {
//List<Double> tileLatitudeList = this.getTileLatitudeList(this.tileList);
//this.bandList = new ArrayList<>();
//for (Double latitude : tileLatitudeList) {
//	List<Tile> bandTileList = new ArrayList<>();
//	for (Tile t : this.tileList) {
//		if (latitude.equals(t.latitude)) {
//			bandTileList.add(t);
//		}
//	}
//	Band band = new Band(bandTileList, latitude);
//	this.bandList.add(band);
//}
//}
///**
//* 
//* @param eventList
//* @param reset
//*/
//public void setEventList(List<Event> eventList, boolean reset) {
////	logger.debug("setEventList(" + eventList.size() + "," + reset + ")");
//	if (reset) {
//		this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
//		this.dateList = new ArrayList<>();
//	}
//	if (eventList != null) {
//		for (Event e : eventList) {
//			if (e.flag) {
//				for (Coordinate c : e.coordinateList) {
//					if (c.flag) {
//						int x = (int) ((c.latitude + this.latitude) * this.resolution);
//						int y = (int) ((c.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//						int z = c.getMonth() - 1;
//						this.coordinateMatrix[x][y][z]++;
//						String date = c.getYear() + "-" + c.getMonth();
//						if (!this.dateList.contains(date)) {
//							this.dateList.add(date);
//						}
//					}
//				}
//			}
//		}
//	}
//}
// Uncomment to Restore
//if (this.regionList != null && this.regionList.size() == 1) {//
//	for (Region region : this.regionList) {
//		this.region = region;
//		Series series = this.seriesMap.get(region.toString());
//		if (series == null) {
//			series = this.newSeries();
//		}
//		for (Time time: timeList) {
//			eventList = super.filter(new ArrayList<>(this.eventMap.get(time)));//Bottleneck is filtering by region
//			//TRY stop trying to use the filter
//			this.prune(eventList);
//			this.addSeriesIndex(series, time, eventList);
//		}
//		this.seriesMap.put(region.toString(), series);
//	}
//	this.initPlotList(this.seriesMap);
//}
//}
//time.year = year;
//time.month = month;
//time.day = day;
//time.hour = hour;
//public HashMap<Time, List<Event>> initEventMap(HashMap<Time, List<Event>> eventMap, List<Event> eventList) {
//String[] groupArray = this.group.split(",");
//for (Event event : eventList) {
//	for (String group : groupArray) {
//		Time key = this.getTime(group, event);
//		if (key != null) {
//			List<Event> eList = eventMap.get(key);
//			if (eList == null) {
//				eList = new ArrayList<>();
//			}
//			eList.add(event);
//			eventMap.put(key, eList);
//		}
//	}
//}
//return eventMap;
//}

//@Override
//public void complete() {
//	super.complete();
//	if (this.regionList != null && this.regionList.size() > 0) {//
//		for (Region region : this.regionList) {
//			MemoryController.log();
//			this.region = region;
//			Series series = this.seriesMap.get(region.toString());
//			if (series == null) {
//				series = this.newSeries();
//			}
//			for (Time time:this.timeList) {
//				try {
//					this.eventList = super.filter(new ArrayList<>(this.eventMap.get(time)));//Bottleneck is filtering by region
//					this.prune(this.eventList);
//					this.addSeriesIndex(series, time, this.eventList);
//				} catch (Exception e) {
//					logger.error("complete() exception=" + e.getMessage());
//					e.printStackTrace();
//				}
//			}
//			this.seriesMap.put(region.toString(), series);
//			this.initPlotList(this.seriesMap);
//		}
//	}
//}
//How do we know when an eventMap entry is complete?
//3 states NULL, PENDING, COMPLETE?
//NULL, no entry 
//boolean flag = false;
//for(Time time: timeList) {
//	if(!this.timeList.contains(time)) {
//		this.timeList.add(time);
//		flag = true;
//	}
//}
//If we have multiple Regions to SHOW
//Hold off generating seriesMap from eventMap
//Otherwise DO it, to show loading in real-time
///**
//* 
//* @param eventMap
//* @return
//* @throws Exception
//*/
//public Map<String, List<Event>> filter(Map<String, List<Event>> eventMap) throws Exception {
//	logger.info("filter(" + eventMap.size() + ")");
//	for (List<Event> eventList : eventMap.values()) {
//		this.resetFlags(eventList);
//		eventList = this.filter(eventList);
//	}
//	return eventMap;
//}
//public void setIndexList(Series series, Map<Time, List<Event>> eventMap, boolean reset) {
////logger.info("getIndexList(" + series + "," + eventMap.size() + "," + reset + ")");
//if (reset) {
//series.indexList = new ArrayList<>();
//}
//for (Entry<Time, List<Event>> eventEntry : eventMap.entrySet()) {
//Time key = eventEntry.getKey();
////logger.debug(this + ".getIndexList(" + eventMap.size() + ") key=" + key);
////String[] keyArray = key.split(",");
////Index index = new Index();
////Integer year = (keyArray.length > 0) ? Integer.parseInt(keyArray[0]) : null;
////Integer month = (keyArray.length > 1) ? Integer.parseInt(keyArray[1]) : null;
////Integer day = (keyArray.length > 2) ? Integer.parseInt(keyArray[2]) : null;
//Index index = new Index();
//Integer year = (key.year != -1) ? key.year : null;
//Integer month = (key.month != -1) ? key.month : null;
//Integer day = (key.day != -1) ? key.day : null;
////logger.debug("getIndexList(" + eventMap.size() + ") year=" + year + " month=" + month + " day=" + day);
//index.startCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0, (day != null) ? day : 0,
//		24, 0, 0);
//if (year != null) {
//	if (month != null) {
//		if (day != null) {
//			index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//					(day != null) ? day : 0, 24, 0, 0);
//		} else {
//			// handle to end of month
//			YearMonth yearMonthObject = YearMonth.of(year, month);
//			day = yearMonthObject.lengthOfMonth();
//		}
//		index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//				(day != null) ? day : 0, 24, 0, 0);
//	} else {
//		// handle to end of year
//		month = 12;
//		YearMonth yearMonthObject = YearMonth.of(year, month);
//		day = yearMonthObject.lengthOfMonth();
//		index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//				(day != null) ? day : 0, 24, 0, 0);
//	}
//}
//List<Event> eList = eventEntry.getValue();
//index.value = eList.size();
//series.addIndex(index);
//// if (!this.indexList.contains(index)) {
//// this.indexList.add(index);
//// } else {
//// int x = this.indexList.indexOf(index);
//// Index i = this.indexList.get(x);
//// if (i.value != index.value) {
//// i.value += index.value;
//// }
//// }
//}
////logger.info("getIndexList(" + eventMap.size() + ") indexList.size()=" + indexList.size());
//// return this.indexList;
//}

//public Map<Time, List<Event>> group(List<Event> eventList, boolean reset) {
//// logger.info("group(" + eventList.size() + "," + reset + ")");
//if (reset) {
//this.eventMap = new HashMap<>();
//}
//String[] groupArray = this.group.split(",");
//for (Event e : eventList) {
//if (e.flag) {
//	for (String value : groupArray) {
//		Time key = this.getTime(value, e);
//		if (key != null) {
//			List<Event> eList = this.eventMap.get(key);
//			if (eList == null) {
//				eList = new ArrayList<>();
//			}
//			eList.add(e);
//			this.eventMap.put(key, eList);
//		}
//	}
//}
//}
//return this.eventMap;
//}

//public void setIndexList(Series series, Map<Time, List<Event>> eventMap, boolean reset) {
////logger.info("getIndexList(" + series + "," + eventMap.size() + "," + reset + ")");
//if (reset) {
//series.indexList = new ArrayList<>();
//}
//for (Entry<Time, List<Event>> eventEntry : eventMap.entrySet()) {
//Time key = eventEntry.getKey();
////	logger.debug(this + ".getIndexList(" + eventMap.size() + ") key=" + key);
////	String[] keyArray = key.split(",");
////	Index index = new Index();
////	Integer year = (keyArray.length > 0) ? Integer.parseInt(keyArray[0]) : null;
////	Integer month = (keyArray.length > 1) ? Integer.parseInt(keyArray[1]) : null;
////	Integer day = (keyArray.length > 2) ? Integer.parseInt(keyArray[2]) : null;
//Index index = new Index();
//Integer year = (key.year != -1) ? key.year : null;
//Integer month = (key.month != -1) ? key.month : null;
//Integer day = (key.day != -1) ? key.day : null;
////	logger.debug("getIndexList(" + eventMap.size() + ") year=" + year + " month=" + month + " day=" + day);
//index.startCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0, (day != null) ? day : 0,
//		24, 0, 0);
//if (year != null) {
//	if (month != null) {
//		if (day != null) {
//			index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//					(day != null) ? day : 0, 24, 0, 0);
//		} else {
//			// handle to end of month
//			YearMonth yearMonthObject = YearMonth.of(year, month);
//			day = yearMonthObject.lengthOfMonth();
//		}
//		index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//				(day != null) ? day : 0, 24, 0, 0);
//	} else {
//		// handle to end of year
//		month = 12;
//		YearMonth yearMonthObject = YearMonth.of(year, month);
//		day = yearMonthObject.lengthOfMonth();
//		index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//				(day != null) ? day : 0, 24, 0, 0);
//	}
//}
//List<Event> eList = eventEntry.getValue();
//index.value = eList.size();
//series.addIndex(index);
//// if (!this.indexList.contains(index)) {
//// this.indexList.add(index);
//// } else {
//// int x = this.indexList.indexOf(index);
//// Index i = this.indexList.get(x);
//// if (i.value != index.value) {
//// i.value += index.value;
//// }
//// }
//}
////logger.info("getIndexList(" + eventMap.size() + ") indexList.size()=" + indexList.size());
//// return this.indexList;
//}
//public String getGroup(String value, Event e) {
//Calendar calendar = e.getStartCalendar();
//Integer day = null;
//Integer month = null;
//Integer year = null;
//Integer hour = null;
//if (value != null) {
//	switch (value) {
//	case "hour": {
//		hour = calendar.get(Calendar.HOUR_OF_DAY);
//		day = calendar.get(Calendar.DAY_OF_MONTH);
//		month = calendar.get(Calendar.MONTH);
//		year = calendar.get(Calendar.YEAR);
//		break;
//	}
//	case "day": {
//		day = calendar.get(Calendar.DAY_OF_MONTH);
//		month = calendar.get(Calendar.MONTH);
//		year = calendar.get(Calendar.YEAR);
//		break;
//	}
//	case "month": {
//		this.monthFlag = true;
//		month = calendar.get(Calendar.MONTH);
//		year = calendar.get(Calendar.YEAR);
//		break;
//	}
//	case "year": {
//		this.yearFlag = true;
//		year = calendar.get(Calendar.YEAR);
//		break;
//	}
//	case "djf": {
//		if (value.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
//			month = calendar.get(Calendar.MONTH);
//			year = calendar.get(Calendar.YEAR);
//		}
//		break;
//	}
//	case "mam": {
//		if (value.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
//			month = calendar.get(Calendar.MONTH);
//			year = calendar.get(Calendar.YEAR);
//		}
//		break;
//	}
//	case "jja": {
//		if (value.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
//			month = calendar.get(Calendar.MONTH);
//			year = calendar.get(Calendar.YEAR);
//		}
//		break;
//	}
//	case "son": {
//		if (value.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
//			month = calendar.get(Calendar.MONTH);
//			year = calendar.get(Calendar.YEAR);
//		}
//		break;
//	}
//	}
//}
//return this.getDateString(year, month, day, hour);
//}

///**
//* Convert String to String Array 
//* @param year
//* @param month
//* @param day
//* @param hour
//* @return
//*/
//public String getDateString(Integer year, Integer month, Integer day, Integer hour) {
////logger.info("getDateString("+year+","+month+","+day+","+hour+")");
//return ((year != null) ? year+"," : "") + ((month != null) ? String.format("%02d", month + 1)+"," : "")
//		+ ((day != null) ? String.format("%02d", day)+"," : "")
//		+ ((hour != null) ? String.format("%02d", hour) : "");
//}
//
//public String[] getDateArray(Integer year, Integer month, Integer day, Integer hour) {
////logger.info("getDateString("+year+","+month+","+day+","+hour+")");
//String[] dateArray = new String[4];
//dateArray[0] = String.valueOf(year);
//dateArray[1] = String.format("%02d", month + 1);
//dateArray[2] = String.format("%02d", day);
//dateArray[3] = String.format("%02d", hour);
//return dateArray;
//}
///**
//* Function and accepts List<Event> eventList and boolean reset parameters
//* eventList is used to pass the list of events from the time query reset is
//* used to instantiate Map<String,String> seriesMap
//* 
//* @param eventList
//* @param reset
//*/
//public void initSeriesMap(Region region, List<Event> eventList, boolean reset) {
//	if (reset) {
//		this.seriesMap = new TreeMap<>();
//	}
//	Series series = this.seriesMap.get(region.toString());
//	if (series == null) {
//		series = new Series();
//	}
//	series.map.put("startCalendar", this.startCalendar);
//	series.map.put("endCalendar", this.endCalendar);
//	series.map.put("name", this.name);
//	series.map.put("average", this.average);
//	series.map.put("sum", this.sum);
//	series.map.put("regression", this.regression);
//	series.map.put("region", region.toString());
//	series.map.put("family", this.query.getFamily());
//	series.map.put("class", this.query.getClassification());
//	series.map.put("group", this.query.getGroup());
//	series.map.put("variable", this.query.getVariable());
//	series.map.put("window", this.window);
//	series.map.put("range", this.range);
//	Query q = new Query(this.query);
//	q.map.put("region", region.toString());
//	series.map.put("query", q);
////	this.setIndexList(series, this.group(eventList, true), reset);
//	this.seriesMap.put(region.toString(), series);
//	this.initPlots(this.seriesMap);
//}
//@JsonIgnore
//public List<Regression> getRegression(List<Index> indexList) throws Exception {
//	List<Regression> regressionList = new ArrayList<>();
//	String[] regressionArray = (this.regression != null) ? this.regression.split(",") : new String[0];
//	for (String regression : regressionArray) {
//		List<Regression> rList = this.getRegression(regression, indexList);
//		regressionList.addAll(rList);
//	}
//	return regressionList;
//}

//@JsonIgnore
//public List<Regression> getRegression(String regression, List<Index> indexList) throws Exception {
//	List<Regression> regressionList = new ArrayList<>();
//	if (regression != null && indexList.size() > 0) {
//		List<List<Index>> periodMatrix = new ArrayList<>();
//		switch (regression) {
//		case "all": {
//			List<Index> period = indexList;
//			periodMatrix.add(period);
//			break;
//		}
//		case "season": {
//			String seasonIndex = null;
//			List<Index> period = null;
//			for (Index index : indexList) {
//				Calendar calendar = index.startCalendar;
//				int month = calendar.get(Calendar.MONTH);
//				String season = Regression.getSeason(month);
//				if (!season.equals(seasonIndex)) {
//					if (period != null) {
//						periodMatrix.add(period);
//					}
//					seasonIndex = season;
//					period = new ArrayList<>();
//				}
//				if (period != null) {
//					period.add(index);
//				}
//			}
//			break;
//		}
//		case "year": {// doesn't work if year does not change
//			int yearIndex = -1;
//			List<Index> period = null;
//			for (Index index : indexList) {
//				Calendar calendar = index.startCalendar;
//				int year = calendar.get(Calendar.YEAR);
//				if (yearIndex != year) {
//					if (period != null) {
//						periodMatrix.add(period);
//					}
//					yearIndex = year;
//					period = new ArrayList<>();
//				}
//				if (period != null) {
//					period.add(index);
//				}
//			}
//			break;
//		}
//		case "decade": {
//			int decadeIndex = -1;
//			List<Index> period = null;
//			for (Index index : indexList) {
//				Calendar calendar = index.startCalendar;
//				int year = calendar.get(Calendar.YEAR);
//				int decade = Regression.getDecade(year);
//				if (decadeIndex != decade) {
//					if (period != null) {
//						periodMatrix.add(period);
//					}
//					decadeIndex = decade;
//					period = new ArrayList<>();
//				}
//				if (period != null) {
//					period.add(index);
//				}
//			}
//			break;
//		}
//		case "vicennial": {
//			int decadeIndex = -1;
//			List<Index> period = null;
//			for (Index index : indexList) {
//				Calendar calendar = index.startCalendar;
//				int year = calendar.get(Calendar.YEAR);
//				int decade = Regression.getDecade(year);
//				if (decadeIndex != decade) {
//					if (period != null) {
//						periodMatrix.add(period);
//					}
//					decadeIndex = decade;
//					period = new ArrayList<>();
//				}
//				if (period != null) {
//					period.add(index);
//				}
//			}
//			List<List<Index>> tmpPeriodMatrix = new ArrayList<>();
//			for (int i = 0; i < periodMatrix.size(); i++) {
//				if ((i + 1) < periodMatrix.size()) {
//					List<Index> p = new ArrayList<>();
//					p.addAll(periodMatrix.get(i));
//					p.addAll(periodMatrix.get(i + 1));
//					tmpPeriodMatrix.add(p);
//				}
//			}
//			periodMatrix = tmpPeriodMatrix;
//			break;
//		}
//		case "quinquennial": {// 5-years
//			// Pending
//		}
//		}
//		for (List<Index> period : periodMatrix) {
//			if (period != null && period.size() > 0) {
//				Calendar startCalendar = period.get(0).startCalendar;
//				Calendar endCalendar = period.get(period.size() - 1).endCalendar;
//				List<Point> pointList = new ArrayList<>();
//				for (Index index : period) {
//					Point point = index.getPoint(startCalendar);
//					pointList.add(point);
//				}
//				double[][] data = new double[pointList.size()][2];
//				for (int i = 0; i < pointList.size(); i++) {
//					Point p = pointList.get(i);
//					data[i][0] = p.x;
//					data[i][1] = p.y;
//				}
//				SimpleRegression simpleRegression = new SimpleRegression(true);
//				simpleRegression.addData(data);
//				Regression r = new Regression();
//				Map<String, Double> map = new HashMap();
//				map.put("intercept", simpleRegression.getIntercept());
//				map.put("interceptStdErr", simpleRegression.getInterceptStdErr());
//				map.put("meanSquareError", simpleRegression.getMeanSquareError());
////				map.put("n", simpleRegression.getN());
//				map.put("r", simpleRegression.getR());
//				map.put("regressionSumSquares", simpleRegression.getRegressionSumSquares());
//				map.put("rSquare", simpleRegression.getRSquare());
//				map.put("significance", simpleRegression.getSignificance());
//				map.put("slope", simpleRegression.getSlope());
//				map.put("slopeConfidenceInterval", simpleRegression.getSlopeConfidenceInterval());
//				map.put("slopeStdErr", simpleRegression.getSlopeStdErr());
//				map.put("sumOfCrossProducts", simpleRegression.getSumOfCrossProducts());
//				map.put("sumSquaredErrors", simpleRegression.getSumSquaredErrors());
//				map.put("totalSumSquares", simpleRegression.getTotalSumSquares());
//				map.put("xSumSqaures", simpleRegression.getXSumSquares());
//				r.map = map;
//				r.startCalendar = startCalendar;
//				r.endCalendar = endCalendar;
//				regressionList.add(r);
//			}
//		}
//	}
//	return regressionList;
//}

///**
//* Function and accepts List<Event> eventList and boolean reset parameters
//* eventList is used to pass the list of events from the time query reset is
//* used to instantiate Map<String,String> seriesMap
//* 
//* @param eventList
//* @param reset
//*/
//public void initSeriesMap(Region region, List<Event> eventList, boolean reset) {
//	if (reset) {
//		this.seriesMap = new TreeMap<>();
//	}
//	Series series = this.seriesMap.get(region.toString());
//	if (series == null) {
//		series = new Series();
//	}
//	series.map.put("startCalendar", this.startCalendar);
//	series.map.put("endCalendar", this.endCalendar);
//	series.map.put("name", this.name);
//	series.map.put("average", this.average);
//	series.map.put("sum", this.sum);
//	series.map.put("regression", this.regression);
//	series.map.put("region", region.toString());
//	series.map.put("family", this.query.getFamily());
//	series.map.put("class", this.query.getClassification());
//	series.map.put("group", this.query.getGroup());
//	series.map.put("variable", this.query.getVariable());
//	series.map.put("window", this.window);
//	series.map.put("range", this.range);
//	Query q = new Query(this.query);
//	q.map.put("region", region.toString());
//	series.map.put("query", q);
////	this.setIndexList(series, this.group(eventList, true), reset);
//	this.seriesMap.put(region.toString(), series);
//	this.initPlots(this.seriesMap);
//}
//public Plot getPlot(Series series) throws Exception {
//logger.debug("getPlot(" + series + ")");
//Plot plot = null;
//if (series.indexList != null && series.indexList.size() > 0) {
//	series.setRegression(this.regression);
//	List<List<Index>> blackIndexMatrix = new ArrayList<>();
//	List<List<Index>> colorPointMatrix = new ArrayList<>();
//	blackIndexMatrix.add(series.indexList);
//	
//	for(Entry<String,List<Regression>> entry:series.regressionMap.entrySet()) {
//		colorPointMatrix.addAll(this.getMatrix(entry.getValue()));
//	}
////			if (regressionList.size() > 0) {
////				colorPointMatrix = this.getMatrix(regressionList);
////			}
//	if(this.window != null && this.window.length == 2) {
//		logger.info("getPlot(...) this.window="+this.window.length);
//		logger.info("getPlot(...) this.window[0]="+this.window[0]);
//		logger.info("getPlot(...) this.window[1]="+this.window[1]);
//		plot = new TimePlot(this.window[0], this.window[1], blackIndexMatrix, colorPointMatrix);
//	} else {
//		plot = new TimePlot(this.startCalendar, this.endCalendar, blackIndexMatrix, colorPointMatrix);
//	}
//	if (this.range != null && this.range.length == 2) {
//		plot.setYMin(this.range[0]);
//		plot.setYMax(this.range[1]);
//	}
//	String title = null;
//	String data = null;
//	switch (this.name) {
//	case "Cyclone": {
//		title = this.name + " Count";
//		data = this.name.toLowerCase()+"-count";
//		break;
//	}
//	default: {
//		title = "Cyclone " + this.name + (("month".equals(this.group)) ? " Monthly" : "")
//				+ (("year".equals(this.group)) ? " Yearly" : "") + ((this.average) ? " Average" : "")
//				+ ((this.sum) ? " Sum" : "");
//		data = "cyclone-" + this.name.toLowerCase() + (("month".equals(this.group)) ? "-monthly" : "")
//				+ (("year".equals(this.group)) ? "-yearly" : "") + ((this.average) ? "-average" : "")
//				+ ((this.sum) ? "-sum" : "");
//		break;
//	}
//	}
//	String region = (String)series.map.get("region");
//	//Query was working because OR would generate multiple Queries
//	//where ach query had one value per parameter.
//	//For And (focused on Region) the remainder of the Query must be composed of singular
//	//values or require superposition of multiple variables, i.e. Speed and Density
//	if (this.query != null) {
//		region = (region == null) ? this.query.getRegion() : region;
//		if (region != null) {
//			title += " Region " + region;
//			data += "-region-"+"("+region.replace(",", "_").replace(":", ")-(")+")";
//		}
//		String family = this.query.getFamily();
//		if (family != null) {
//			title += " Family " + family;
//			data += "-family-"+family;
//		}
//		String classification = this.query.getClassification();
//		if (classification != null) {
//			title += " Class " + classification;
//			data += "-class-"+classification;
//		}
//	} else {
//		if (region != null) {
//			title += " Region " + region;
//			data += "-region-"+"("+region.replace(",", "_").replace(":", ")-(")+")";
//		}
//	}
//	plot.setTitle(title);
//	plot.setData(data);
//	plot.setXLabel("Time");
////			if (regressionList.size() > 0) {
////				plot.tableList.add(new Table(this.name + " Regression", Regression.getTableModel(regressionList)));
////			}
//	if(region != null) {
//		plot.tableList.add(new Table("("+region.replace(",", "_").replace(":", ")-(")+")", Index.getTableModel(series.indexList)));
//	} else {
//		plot.tableList.add(new Table("Main", Index.getTableModel(series.indexList)));
//	}
//}
//return plot;
//}
//public void and(List<Event> eventList, boolean reset) {
//logger.info("and("+eventList.size()+", "+reset+")");
//if (reset) {
//	this.seriesMap = new TreeMap<>();
//}
//if (this.regionList != null) {
//	for (Region r : this.regionList) {
//		this.region = r;
//		List<Event> eList = new ArrayList<>();
//		for (Event e : eventList) {
//			if (r.contains(e)) {
//				eList.add(e);
//			}
//		}
//		Series series = this.seriesMap.get(r.toString());
//		if (series == null) {
//			series = new Series();
//		}
//		String region = r.toString();
//		series.map.put("region", region);
//		this.setIndexList(series, (this.group(eList, true)), reset);
//		this.seriesMap.put(region, series);
//	}
//	
//} else {
//	// Need to Test
//	Series series = new Series();
//	this.setIndexList(series, (this.group(eventList, reset)), reset);
//	this.seriesMap.put("main", series);
//}
//this.initPlots(this.seriesMap);
//}

///**
//* 
//* @param reset
//*/
//public void or(List<Event> eventList, boolean reset) {
//logger.info("or("+eventList.size()+", "+reset+")");
//Series series = new Series();
//this.setIndexList(series, this.group(eventList, true), reset);
//this.seriesMap.put("main", series);
//this.initPlots(this.seriesMap);
//}
//public void initTiles(List<Event> eventList, boolean reset) {
//logger.info("initTiles(" + eventList.size() + "," + reset + ")");
//this.setEventList(eventList, reset);
//this.initMonthArray();
//this.initYearMap();
//this.tileList = this.getTileList();
//this.initTileMinMax();
//}
//this.initTiles(eventList, reset);
//switch (this.operator) {
//case AND: {
//	and(eventList, reset);
//	break;
//}
//case OR: {
//	or(eventList, reset);
//	break;
//}
//}
//public String eventMapToString(Map<String, List<Event>> eventMap) {
//StringBuilder sb = new StringBuilder();
//int size = eventMap.size();
//int index = 0;
//sb.append("{");
//for (Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
//	String key = eventEntry.getKey();
//	List<Event> eventList = eventEntry.getValue();
//	if (index < size) {
//		sb.append("{" + key + ":" + eventList.size() + "},");
//	} else {
//		sb.append("{" + key + ":" + eventList.size() + "}");
//	}
//	index++;
//}
//sb.append("}");
//return sb.toString();
//}

//public List<Index> mergeIndexList(List<Index> a, List<Index> b) {
//for (Index i : b) {
//	if (a.contains(i)) {
//		for (Index j : a) {
//			if (j.equals(i)) {
//				j.value += i.value;
//			}
//		}
//	} else {
//		a.add(i);
//	}
//}
//return a;
//}
//
//Calendar calendar = e.getStartCalendar();
//Integer day = null;
//Integer month = null;
//Integer year = null;
//if (group != null) {
//	switch (group) {
//	case "day": {
//		day = calendar.get(Calendar.DAY_OF_MONTH);
//		month = calendar.get(Calendar.MONTH);
//		year = calendar.get(Calendar.YEAR);
//		break;
//	}
//	case "month": {
//		month = calendar.get(Calendar.MONTH);
//		year = calendar.get(Calendar.YEAR);
//		break;
//	}
//	case "year": {
//		year = calendar.get(Calendar.YEAR);
//		break;
//	}
//	case "season": {
//		if (season != null) {
//			if (season.contains(Regression.getSeason(calendar.get(Calendar.MONTH)))) {
//				month = calendar.get(Calendar.MONTH);
//				year = calendar.get(Calendar.YEAR);
//			}
//		}
//		break;
//	}
//	}
//	String key = year + ((month != null) ? "," + String.format("%02d", month + 1) : "")
//			+ ((day != null) ? "," + String.format("%02d", day) : "");
//
//	if (key != null) {
//		List<Event> eList = eventMap.get(key);
//		if (eList == null) {
//			eList = new ArrayList<>();
//		}
//		eList.add(e);
//		this.eventMap.put(key, eList);
//	}
//}
//Variable variable = (Variable)this.getRoot();
//Query q = this.queryStack.peek();
//if(q == null) {
//	q = variable.queryStack.peek();
//}
//if(q != null && !q.getGroup().equals(this.query.getGroup())) {
//	this.eventMap = new HashMap<>();
//} else {
//	if(variable instanceof Cyclone) {
//		Cyclone cyclone = (Cyclone)variable;
//		return cyclone.eventMap;
//	} else {
//		this.eventMap = new HashMap<>();
//	}
//}
//				if (season != null) {
//					if (month != null && season.contains(Regression.getSeason(month))) {
//						key = year + ((month != null) ? "," + String.format("%02d", month + 1) : "")
//								+ ((day != null) ? "," + String.format("%02d", day) : "");
//					}
//				} else {
//					key = year + ((month != null) ? "," + String.format("%02d", month + 1) : "")
//							+ ((day != null) ? "," + String.format("%02d", day) : "");
//				}
//this.initVariableMap();
//@Override
//public void initVariableMap() {
//	this.variableMap.put("Average", false);
//}
//@Override
//protected void defaultState(Object object) {
//	if (object instanceof Result) {
//		Result result = (Result) object;
//		switch (result.mode) {
//		case LOAD: {
//			this.mode = Mode.LOAD;
//			load(result);
//			break;
//		}
//		case COMPLETE: {
//			this.mode = Mode.COMPLETE;
//			logger.info("defaultState(...) this.mode="+this.mode);
//			break;
//		}
//		}
//	}
//	if (this.delayExpired()) {
//		this.query();
//		this.setDelay(this.newDelay(3.0));
//	}
//}
//@Override
//public void init() throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	if (sourceUUID != null && !sourceUUID.equals("null")) {
//		Query query = null;
//		if (this.queryStack.size() > 0) {
//			query = this.queryStack.poll();
//		}
//		if (!this.query.equals(query)) {
//			Object object = this.data.get(sourceUUID, this.query, "cyclone");
//			this.eventList = (List<Event>) object;
//			if (this.eventList != null) {
//				this.queryStack.push(this.query);
//				if (this.query.getTime() == null) {
//					for (int i = 0; i < this.eventList.size(); i++) {
//						CycloneEvent event = (CycloneEvent) this.eventList.get(i);
//						this.coordinateList = event.coordinateList;
//						if (event.containsCalendar(calendar)) {
//							this.setCalendarCoordinateList(calendar, this.coordinateList);
//						}
//					}
//				}
//				this.setEventList(this.eventList);
//			}
//		}
//	}
//}

//public void setEventList(List<Event> eventList) {
//
//}
//if(this.query.map.size() > 0) {
//
//} else {
//try {
//	object = this.data.get(this.sourceUUID, this.calendar);
//	this.eventList = (List<Event>) object;
//	if (this.eventList != null) {
//		
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
//} catch (Exception e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}
//@Override
//public void load(String sourceUUID) {
//	this.load = true;
//	this.start(sourceUUID);
////	if (this.load) {
////		String sourceUUID = this.sourceMap.get(this.sourceKey);
////		this.data.load(sourceUUID);
////		this.initVariableMap();
////	}
//}

//@Override
//public void init() throws Exception {
//	logger.info("init(" + calendar.getTime() + ")");
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	Object object = this.data.get(sourceUUID, this.query);
//	this.eventList = (List<Event>) object;
//	if (this.eventList != null) {
//		if (this.query.getTime() == null) {
//			for (int i = 0; i < this.eventList.size(); i++) {
//				CycloneEvent event = (CycloneEvent) this.eventList.get(i);
//				this.coordinateList = event.coordinateList;
//				if (event.containsCalendar(calendar)) {
//					this.setCalendarCoordinateList(calendar, this.coordinateList);
//				} 
//
//			}
//		}
//	}
//}

//else {
////event.flag = false;
//}

//public Map<String, List<Index>> getIndexListMap() throws Exception {
//Map<String, List<Index>> map = this.indexListMap;
//if (map == null) {
//	map = new HashMap<>();
//	this.indexListMap = map;
//}
//this.mapPut(map, "Average Speed", this.eventList);
//return map;
//}

//public void mapPut(Map<String, List<Index>> map, String key, Object object) throws Exception {
////System.out.println("mapPut("+map.size()+","+key+","+object+")");
//List<Index> indexList = map.get(key);
//if (indexList == null && object != null) {
//	map.put(key, this.getIndexList(key, object));
//}
//}