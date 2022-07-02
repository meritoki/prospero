package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.vorticity;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.speed.Speed;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Series;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;

public class Vorticity extends Cyclone {

	static Logger logger = LogManager.getLogger(Vorticity.class.getName());
//	public float[][][] vorticityMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
//	public Map<Integer, float[][][]> vorticityMatrixMap = new HashMap<>();

	public Vorticity() {
		super("Vorticity");
		this.unit = "";
	}

	@Override
	public void init() {
		super.init();
//		this.vorticityMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
	}

	public List<Tile> getTileList() {
		List<Tile> tileList = this.getTileList(this.coordinateMatrix, this.dataMatrix);
		logger.debug("getTileList() tileList.size()=" + tileList.size());
		return tileList;
	}

	public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] vorticityMatrix) {
		List<Tile> tileList = new ArrayList<>();
		int yearCount = this.getYearCount();
		int monthCount = this.getMonthCount();
		Tile tile;
		int point;
		float vorticity;
		float vorticityMean;
		float vorticityMeanSum;
		float value;
		for (int i = 0; i < coordinateMatrix.length; i += dimension) {
			for (int j = 0; j < coordinateMatrix[i].length; j += dimension) {
				vorticityMeanSum = 0;
				for (int m = 0; m < 12; m++) {
					point = 0;
					vorticity = 0;
					for (int a = i; a < (i + dimension); a++) {
						for (int b = j; b < (j + dimension); b++) {
							if (a < this.latitude && b < this.longitude) {
								point += coordinateMatrix[a][b][m];
								vorticity += vorticityMatrix[a][b][m];
							}
						}
					}
					vorticityMean = (point > 0) ? vorticity / point : 0;
					vorticityMeanSum += vorticityMean;
				}
				value = vorticityMeanSum;
				if (this.monthFlag) {
					value /= monthCount;
				} else if (this.yearFlag) {
					value /= yearCount;
				}
				tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution,
						this.dimension, value);
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

	@Override
	public void setMatrix(List<Event> eventList) {
		List<Time> timeList = this.setVorticityCoordinateMatrix(this.dataMatrix, this.coordinateMatrix, eventList);
		for (Time t : timeList) {
			if (!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
	}

	public List<Time> setVorticityCoordinateMatrix(float[][][] vorticityMatrix, int[][][] coordinateMatrix,
			List<Event> eventList) {
		List<Time> timeList = null;
		if (eventList != null) {
			timeList = new ArrayList<>();
			for (Event e : eventList) {
				if (e.flag) {
					for (Coordinate p : e.coordinateList) {
						if (p.flag) {
							int x = (int) ((p.latitude + this.latitude) * this.resolution);
							int y = (int) ((p.longitude + this.longitude / 2) * this.resolution) % this.longitude;
							int z = p.getMonth() - 1;
							coordinateMatrix[x][y][z]++;
							vorticityMatrix[x][y][z] += (float) p.attribute.get("vorticity");
							Time time = new Time(p.getYear(), p.getMonth(), -1, -1, -1, -1);
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
		float[][][] vorticityMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
		Index index = null;
		List<Time> timeList = this.setVorticityCoordinateMatrix(vorticityMatrix, coordinateMatrix, eventList);
		this.initMonthArray(timeList);
		this.initYearMap(timeList);
		List<Tile> tileList = this.getTileList(coordinateMatrix, vorticityMatrix);
		if (average) {
			StandardDeviation standardDeviation = new StandardDeviation();
			Mean mean = new Mean();
			for (Tile tile : tileList) {
				if (tile.value != 0) {
					standardDeviation.increment(tile.value);
					mean.increment(tile.value);
				}
			}
			double value = mean.getResult();
			if (!Double.isNaN(value) && value != 0) {
				index = key.getIndex();
				index.value = value;
				index.map.put("N", standardDeviation.getN());
				index.map.put("standardDeviation", standardDeviation.getResult());
			}
		} else if (sum) {
			double sum = 0;
			for (Tile tile : tileList) {
				sum += tile.value;
			}
			index = key.getIndex();
			index.value = sum;
		} else {
			index = super.getIndex(key, eventList);
		}
		return index;
	}
}
//@Override
//public void initTileMinMax() {
//	double min = Double.POSITIVE_INFINITY;
//	double max = Double.NEGATIVE_INFINITY;
//	if (this.tileList != null) {
//		java.util.Iterator<Tile> iterator = this.tileList.iterator();
//		while (iterator.hasNext()) {
//			Tile t = new Tile(iterator.next());
//			if (t.value > max) {
//				max = t.value;
//			}
//			if (t.value < min) {
//				min = t.value;
//			}
//		}
//	}
//	this.max = -min;
//	this.min = -max;
//	
//}
//@Override
//public void setEventList(List<Event> eventList, boolean reset) {
//	logger.debug("setEventList(" + eventList.size() + "," + reset + ")");
//	if (reset) {
//		this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
//		this.vorticityMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
//		this.dateList = new ArrayList<>();
//	}
//	for (Event e : eventList) {
//		if (e.flag) {
//			for (Coordinate p : e.coordinateList) {
//				if (p.flag) {
//					int x = (int) ((p.latitude + this.latitude) * this.resolution);
//					int y = (int) ((p.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//					int z = p.getMonth() - 1;
//					this.coordinateMatrix[x][y][z]++;
//					this.vorticityMatrix[x][y][z] += (float) p.attribute.get("vorticity");
//					String date = p.getYear() + "-" + p.getMonth();
//					if (!this.dateList.contains(date)) {
//						this.dateList.add(date);
//					}
//				}
//			}
//		}
//	}
//}
//@Override
//public void setIndexList(Series series, Map<String, List<Event>> eventMap, boolean reset) {
////	logger.info("getIndexList(" + eventMap.size() + ","+reset+")");
//	if(reset) {
//		series.indexList = new ArrayList<>();		
//	}
//	if (eventMap.size() > 0) {
//		int[][][] bufferCoordinateMatrix = this.coordinateMatrix;
//		float[][][] bufferSpeedMatrix = this.vorticityMatrix;
//		List<String> bufferDateList = this.dateList;
//		for (Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
//			String key = eventEntry.getKey();
//			String[] keyArray = key.split(",");
//			Index index = new Index();
//			Integer year = (keyArray.length > 0) ? Integer.parseInt(keyArray[0]) : null;
//			Integer month = (keyArray.length > 1) ? Integer.parseInt(keyArray[1]) : null;
//			Integer day = (keyArray.length > 2) ? Integer.parseInt(keyArray[2]) : null;
//			index.startCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//					(day != null) ? day : 0, 24, 0, 0);
//			if (year != null) {
//				if (month != null) {
//					if (day != null) {
//						index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//								(day != null) ? day : 0, 24, 0, 0);
//					} else {
//						// handle to end of month
//						YearMonth yearMonthObject = YearMonth.of(year, month);
//						day = yearMonthObject.lengthOfMonth();
//					}
//					index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//							(day != null) ? day : 0, 24, 0, 0);
//				} else {
//					// handle to end of year
//					month = 12;
//					YearMonth yearMonthObject = YearMonth.of(year, month);
//					day = yearMonthObject.lengthOfMonth();
//					index.endCalendar = new GregorianCalendar(year, (month != null) ? month - 1 : 0,
//							(day != null) ? day : 0, 24, 0, 0);
//				}
//			}
//			List<Event> eList = eventEntry.getValue();
////			this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude
////					* resolution)][monthCount];
////			this.vorticityMatrix = new float[(int) (latitude * resolution)][(int) (longitude
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
////					logger.info("tile.value="+tile.value);
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
//						series.addIndex(index);
//						//							indexList.add(index);
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
//					series.addIndex(index);
////					indexList.add(index);
////					this.addIndex(index);
//				}
//			} else {
//				for (Tile tile : tileList) {
//					Index i = new Index(index);
//					i.value = tile.value;
//					i.map.put("latitude", tile.latitude);
//					i.map.put("longitude", tile.longitude);
//					i.map.put("dimension", tile.dimension);
//					series.addIndex(i);
////					indexList.add(i);
////					this.addIndex(i);
//				}
//			}
//		}
//		this.coordinateMatrix = bufferCoordinateMatrix;
//		this.vorticityMatrix = bufferSpeedMatrix;
//		this.dateList = bufferDateList;
//	}
////	return indexList;
//}
//public void setEventList(List<Event> eventList) {
//
//	if (eventList != null) {
////			this.regionList = cycloneSource.regionList;
////			this.dimension = cycloneSource.dimension;
////			this.monthFlag = cycloneSource.month;
////			this.yearFlag = cycloneSource.year;
////			this.stackFlag = cycloneSource.stack;
////			this.bandFlag = cycloneSource.band;
////			this.cubeFlag = cycloneSource.cube;
//		this.initPointMatrix(eventList);
//		if (this.stackFlag) {
//			List<Integer> levelList = this.getEventLevelList(eventList);
//			for (Integer level : levelList) {
//				this.coordinateMatrix = this.pointMatrixMap.get(level);
//				this.vorticityMatrix = this.vorticityMatrixMap.get(level);
//				List<Tile> tileList = this.getTileList(this.coordinateMatrix, this.vorticityMatrix);
//				this.tileListMap.put(level.toString(), tileList);
//			}
//		} else {
//			this.tileList = this.getTileList(this.coordinateMatrix, this.vorticityMatrix);
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
//public void initPointMatrix(List<Event> eventList) {
//	String date;
//	coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//	vorticityMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//	dateList = new ArrayList<>();
//	if (this.stackFlag) {
//		List<Integer> levelList = this.getEventLevelList(eventList);
//		for (Integer level : levelList) {
//			this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude
//					* resolution)][monthCount];
//			this.vorticityMatrix = new float[(int) (latitude * resolution)][(int) (longitude
//					* resolution)][monthCount];
//			for (Event e : eventList) {
//				for (Coordinate p : e.coordinateList) {
//					if (p.flag && ((Integer) p.attribute.map.get("level")).equals(level)) {
//						int x = (int) ((p.latitude + this.latitude) * this.resolution);
//						int y = (int) ((p.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//						int z = p.getMonth() - 1;
//						this.coordinateMatrix[x][y][z]++;
//						this.vorticityMatrix[x][y][z] += (float) p.attribute.map.get("vorticity");
//						date = p.getYear() + "-" + p.getMonth();
//						if (!this.dateList.contains(date)) {
//							this.dateList.add(date);
//						}
//					}
//				}
//			}
//			this.pointMatrixMap.put(level, this.coordinateMatrix);
//			this.vorticityMatrixMap.put(level, this.vorticityMatrix);
//		}
//	} else {
//		for (Event e : eventList) {
//			if (e.flag) {
//				for (Coordinate p : e.coordinateList) {
//					if (p.flag) {
//						int x = (int) ((p.latitude + this.latitude) * this.resolution);
//						int y = (int) ((p.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//						int z = p.getMonth() - 1;
//						coordinateMatrix[x][y][z]++;
//						vorticityMatrix[x][y][z] += (float) p.attribute.map.get("vorticity");
//						// System.out.println(vorticityMatrix[x][y][z]);
//						date = p.getYear() + "-" + p.getMonth();
//						if (!this.dateList.contains(date)) {
//							this.dateList.add(date);
//						}
//					}
//				}
//			}
//		}
//	}
//	this.initMonthArray();
//	this.initYearMap();
//}
//
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
//			List<Tile> tileList = this.getTileList(this.coordinateMatrix,this.vorticityMatrix);
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
//				y = "Average Vorticity";
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
//public void initTileMinMax() {
//	double max = 0;
//	if (this.stackFlag) {
//		for (Entry<Integer, List<Tile>> entry : this.tileListMap.entrySet()) {
//			List<Tile> tileList = entry.getValue();
//			for (Tile t : tileList) {
//				if (t.value > max) {
//					max = t.value;
//				}
//			}
//		}
//	} else {
//		for (Tile t : this.tileList) {
//			if (t.value > max) {
//				max = t.value;
//			}
//		}
//	}
//	this.max = -max;
//}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.add(sourceUUID);
//	}
//}

//public void init(Calendar calendar) throws Exception {
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
////				else {
////					event.flag = false;
////				}
//			}
//		}
//		this.setEventList(this.eventList);
//	}
//}

//@Override
//public void paint(Graphics graphics) throws Exception {
//	if (this.load) {
//		this.init(this.calendar);
//		super.paint(graphics);
//	}
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
//					this.setCalendarCoordinateList(this.calendar, this.coordinateList);
//				} else {
//					event.flag = false;
//				}
//			}
//			this.setEventList(this.eventList);
//			super.paint(graphics);
//		}
//	}
//}
//@Override
//public void initMax() {
//	double max = 0;
//	for (Tile t : this.tileList) {
//		if (t.value > max) {
//			max = t.value;
//		}
//	}
//	this.max = -max;
//}
//public Vorticity(List<CycloneSource> sourceList) {
//for(CycloneSource s: sourceList) {
//	this.init(s);
//}
//}