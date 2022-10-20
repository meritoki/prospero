package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.speed;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;

public class InstantaneousSpeed extends Cyclone {
	
	static Logger logger = LogManager.getLogger(InstantaneousSpeed.class.getName());
//	public float[][][] speedMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
//	public Map<Integer, float[][][]> speedMatrixMap = new HashMap<>();

	public InstantaneousSpeed() {
		super("InstantaneousSpeed");
		this.unit = "m/s";
	}
	
	@Override
	public void init() {
		super.init();
//		this.speedMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
	}
	
	@Override
	public List<Tile> getTileList() {
		return this.getTileList(this.coordinateMatrix, this.dataMatrix);
	}
	
	public List<Tile> getTileList(int[][][] pointMatrix, float[][][] speedMatrix) {
		List<Tile> tileList = new ArrayList<>();
		int yearCount = this.getYearCount();
		int monthCount = this.getMonthCount();
		Tile tile;
		int point;
		float speed;
		float speedMean;
		float speedMeanSum;
		float value;
		for (int i = 0; i < pointMatrix.length; i += this.dimension) {
			for (int j = 0; j < pointMatrix[i].length; j += this.dimension) {
				speedMeanSum = 0;
				for (int m = 0; m < 12; m++) {
					point = 0;
					speed = 0;
					for (int a = i; a < (i + this.dimension); a++) {
						for (int b = j; b < (j + this.dimension); b++) {
							if(a < this.latitude && b < this.longitude) { 
								point += pointMatrix[a][b][m];
								speed += speedMatrix[a][b][m];
							}
						}
					}
					speedMean = (point > 0) ? speed / point : 0;
					speedMeanSum += speedMean;
				}
				value = speedMeanSum;
				if (this.monthFlag) {
					value /= monthCount;
				} else if (this.yearFlag) {
					value /= yearCount;//value /= ((double)this.getMonthCount()/(double)yearCount);
				}
				tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution,
						this.dimension, value);
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
	
	public List<Time> setSpeedCoordinateMatrix(float[][][] speedMatrix, int[][][] coordinateMatrix, List<Event> eventList) {
		List<Time> timeList = null;
		if (eventList != null) {
			timeList = new ArrayList<>();
			for (Event e : eventList) {
				if (e.flag) {
					List<Coordinate> pointList = ((CycloneEvent)e).getSpeedPointList();
					for (Coordinate c : pointList) {
						coordinateMatrix[(int) ((c.latitude + this.latitude)
								* this.resolution)][(int) ((c.longitude + this.longitude / 2) * this.resolution)][c
										.getMonth() - 1]++;
						speedMatrix[(int) ((c.latitude + this.latitude)
								* this.resolution)][(int) ((c.longitude + this.longitude / 2) * this.resolution)][c
										.getMonth() - 1] += ((CycloneEvent) e).getMeanSpeed();
						Time time = new Time(c.getYear(),c.getMonth(),-1,-1,-1,-1);
						if(!timeList.contains(time)) {
							timeList.add(time);
						}
					}
				}
			}
		}
		return timeList;
	}
	
	@Override
	public void setMatrix(List<Event> eventList) {
		List<Time> timeList = this.setSpeedCoordinateMatrix(this.dataMatrix, this.coordinateMatrix, eventList);
		for(Time t: timeList) {
			if(!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
		this.tileList = this.getTileList();
		this.initTileMinMax();
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
