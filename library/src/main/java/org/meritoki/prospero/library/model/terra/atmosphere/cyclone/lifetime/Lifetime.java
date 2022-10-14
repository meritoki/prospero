package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.lifetime;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;

public class Lifetime extends Cyclone {

	static Logger logger = LogManager.getLogger(Lifetime.class.getName());
	
	public Lifetime() {
		super("Lifetime");
		this.unit = "days";
	}
	
	public void init() {
		super.init();
	}
	
	@Override
	public List<Tile> getTileList() {
		return this.getTileList(this.coordinateMatrix, this.dataMatrix);
	}
	
	public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] durationMatrix) {
		List<Tile> tileList = new ArrayList<>();
		int yearCount = this.getYearCount();
		int monthCount = this.getMonthCount();
		Tile tile;
		short point;
		float duration;
		float durationMean;
		float durationMeanSum;
		float value;
		for (int i = 0; i < coordinateMatrix.length; i += this.dimension) {
			for (int j = 0; j < coordinateMatrix[i].length; j += this.dimension) {
				durationMeanSum = 0;
				for (int m = 0; m < 12; m++) {
					point = 0;
					duration = 0;
					for (int a = i; a < (i + this.dimension); a++) {
						for (int b = j; b < (j + this.dimension); b++) {
							if (a < this.latitude && b < this.longitude) {
								point += coordinateMatrix[a][b][m];
								duration += durationMatrix[a][b][m];
							}
						}
					}
					durationMean = (point > 0) ? duration / point : 0;
					durationMeanSum += durationMean;
				}
				value = durationMeanSum;
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
	
	@Override
	public void setMatrix(List<Event> eventList) {
		List<Time> timeList = this.setDurationCoordinateMatrix(this.dataMatrix, this.coordinateMatrix, eventList);
		for(Time t: timeList) {
			if(!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
		this.tileList = this.getTileList();
		this.bandList = this.getBandList(this.tileList);
		this.initTileMinMax();
	}
	
	public List<Time> setDurationCoordinateMatrix(float[][][] durationMatrix, int[][][] coordinateMatrix, List<Event> eventList) {
		List<Time> timeList = null;
		if (eventList != null) {
			timeList = new ArrayList<>();
			for (Event e : eventList) {
				if(e.flag) {
					for (Coordinate c : e.coordinateList) {
						if (c.flag) {
							int x = (int) ((c.latitude + this.latitude) * this.resolution);
							int y = (int) ((c.longitude + this.longitude / 2) * this.resolution) % this.longitude;
							int z = c.getMonth() - 1;
							coordinateMatrix[x][y][z]++;
							durationMatrix[x][y][z] += e.getDuration().days;
							Time time = new Time(c.getYear(),c.getMonth(),-1,-1,-1,-1);
							if(!timeList.contains(time)) {
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
		float[][][] durationMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
		Index index = null;
		List<Time> timeList = this.setDurationCoordinateMatrix(durationMatrix, coordinateMatrix, eventList);
		this.initMonthArray(timeList);
		this.initYearMap(timeList);
		List<Tile> tileList = this.getTileList(coordinateMatrix, durationMatrix);
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
//public float[][][] durationMatrix = new float[(int) (latitude * resolution)][(int) (longitude
//* resolution)][12];
//public Map<Integer, float[][][]> durationMatrixMap = new HashMap<>();
