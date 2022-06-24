package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.lifetime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;

public class Lifetime extends Cyclone {

	public float[][][] durationMatrix = new float[(int) (latitude * resolution)][(int) (longitude
			* resolution)][12];
	public Map<Integer, float[][][]> durationMatrixMap = new HashMap<>();
	
	public Lifetime() {
		super("Lifetime");
		this.unit = "days";
	}
	
	public void init() {
		super.init();
		durationMatrix = new float[(int) (latitude * resolution)][(int) (longitude
				* resolution)][12];
	}
	
	@Override
	public List<Tile> getTileList() {
		return this.getTileList(coordinateMatrix, durationMatrix);
	}
	
	public List<Tile> getTileList(int[][][] pointMatrix, float[][][] durationMatrix) {
		List<Tile> tileList = new ArrayList<>();
		int yearCount = this.getYearCount();
		int monthCount = this.getMonthCount();
		Tile tile;
		short point;
		float duration;
		float durationMean;
		float durationMeanSum;
		float value;
		for (int i = 0; i < pointMatrix.length; i += this.dimension) {
			for (int j = 0; j < pointMatrix[i].length; j += this.dimension) {
				durationMeanSum = 0;
				for (int m = 0; m < 12; m++) {
					point = 0;
					duration = 0;
					for (int a = i; a < (i + this.dimension); a++) {
						for (int b = j; b < (j + this.dimension); b++) {
							if (a < this.latitude && b < this.longitude) {
								point += pointMatrix[a][b][m];
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
					value /= ((double)this.getMonthCount()/(double)yearCount);
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
		List<Time> timeList = this.setDurationCoordinateMatrix(this.durationMatrix, this.coordinateMatrix, eventList);
		for(Time t: timeList) {
			if(!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
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
		if (average) {
			StandardDeviation standardDeviation = new StandardDeviation();
			Mean mean = new Mean();
			for (Tile tile : tileList) {
				if(tile.value != 0) {
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
