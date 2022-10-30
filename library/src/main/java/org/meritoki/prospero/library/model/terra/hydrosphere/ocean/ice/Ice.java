package org.meritoki.prospero.library.model.terra.hydrosphere.ocean.ice;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.Ocean;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Frame;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;

public class Ice extends Ocean {

	static Logger logger = LogManager.getLogger(Ice.class.getName());
	
	public Ice() {
		super("Ice");
		this.sourceMap.put("Toth","8b2215c6-945b-4109-bfb4-6c764636e390");
	}
	
	@Override
	public void reset() {
		super.reset();
	}
	
	@Override
	public void init() {
		this.dimension = 1;
//		this.latitude = 180;
//		this.longitude = 360;
		super.init();
	}
	
	@Override
	public List<Tile> getTileList() {
		return this.getTileList(this.coordinateMatrix,this.dataMatrix);
	}
	
	public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] dataMatrix) {
		List<Tile> tileList = new ArrayList<>();
		int yearCount = this.getYearCount();
		int monthCount = this.getMonthCount();
		Tile tile;
		int coordinate;
		float data;
		float dataMean;
		float dataMeanSum;
		float value;
		for (int i = 0; i < coordinateMatrix.length; i += this.dimension) {
			for (int j = 0; j < coordinateMatrix[i].length; j += this.dimension) {
				dataMeanSum = 0;
				for (int m = 0; m < 12; m++) {
					coordinate = 0;
					data = 0;
					for (int a = i; a < (i + this.dimension); a++) {
						for (int b = j; b < (j + this.dimension); b++) {
							if (a < this.latitude && b < this.longitude) {
								coordinate += coordinateMatrix[a][b][m];
								data += dataMatrix[a][b][m];
							}
						}
					}
					dataMean = (coordinate > 0) ? data / coordinate : 0;
					dataMeanSum += dataMean;
				}
				value = dataMeanSum;
				if (this.monthFlag) {
					value /= monthCount;
				} else if (this.yearFlag) {
					value /= ((double) this.getMonthCount() / (double) yearCount);
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
	public void load(Result result) {
		super.load(result);
		List<Frame> frameList = result.getFrameList();
		try {
//			this.process(frameList);
		} catch (Exception e) {
			logger.error("load(" + (result != null) + ") exception=" + e.getMessage());
			e.printStackTrace();
		}
	}
	
//	public void process(List<Frame> frameList) throws Exception {
//		this.setMatrix(frameList);
//		this.tileList = this.getTileList();
//		this.initTileMinMax();
//	}
//	
//	public List<Time> setCoordinateAndDataMatrix(int[][][] coordinateMatrix, float[][][] dataMatrix, List<Frame> frameList) {
//		List<Time> timeList = null;
//		if (frameList != null) {
//			timeList = new ArrayList<>();
//			for (Frame f : frameList) {
//				if (f.flag) {
//					for (Coordinate c : f.coordinateList) {
//						if (c.flag) {
//							int x = (int) ((c.latitude + this.latitude) * this.resolution);
//							int y = (int) ((c.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//							int z = c.getMonth()-1;
//							dataMatrix[x][y][z] += (float)((double)c.attribute.get("density"));
//							coordinateMatrix[x][y][z]++;
//							Time time = new Time(c.getYear(), c.getMonth(), -1, -1, -1, -1);
//							if (!timeList.contains(time)) {
//								timeList.add(time);
//							}
//						}
//					}
//				}
//			}
//		}
//		return timeList;
//	}
	
//	public void setMatrix(List<Frame> frameList) {
//		logger.info("setMatrix("+frameList.size()+")");
//		List<Time> timeList = this.setCoordinateAndDataMatrix(this.coordinateMatrix, this.dataMatrix, frameList);
//		for(Time t: timeList) {
//			if(!this.timeList.contains(t)) {
//				this.timeList.add(t);
//			}
//		}
//		this.initMonthArray(this.timeList);
//		this.initYearMap(this.timeList);
//	}
	
//	@Override
//	public void load(Result result) {
//		Object object = result.map.get("stationList");
//		if(object != null) {
//			this.stationList = (List<Station>)object;
//		}
//	}
//	
//	public void process(List<Station> stationList) {
//		
//	}
//	
//	@Override
//	public void paint(Graphics graphics) throws Exception {
//		if(this.load) { 
//			if (this.stationList != null) {
//				List<Coordinate> coordinateList = new ArrayList<>();
//				for (Station s : this.stationList) {
//					Coordinate c = s.getDefaultCoordinate();
//					coordinateList.add(c);
//					c = this.projection.getCoordinate(0,c.latitude,c.longitude);
//					graphics.setColor(this.chroma.getColor(s.getAverageDensity(), 0, 100));
//					graphics.fillOval((int) ((c.point.x) * this.projection.scale),
//							(int) ((c.point.y) * this.projection.scale), (int) 4, (int) 4);
//				}
//			}
//		}
//	}
}
