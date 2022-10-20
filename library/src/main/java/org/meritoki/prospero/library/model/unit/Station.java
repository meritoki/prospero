package org.meritoki.prospero.library.model.unit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Station {
	public String name;
	public String country;
	public List<Coordinate> coordinateList = new ArrayList<>();
	public Coordinate defaultCoordinate;
	public boolean flag;
	
	public double getAverageDensity() {
		StandardDeviation standardDeviation = new StandardDeviation();
		Mean mean = new Mean();
		for (Coordinate tile : this.coordinateList) {
			double temp = (double)tile.attribute.get("density");
			if(temp != -8888.0 && temp != -7777) {
				standardDeviation.increment(temp);
				mean.increment(temp);
			}
		}
		return mean.getResult();
	}
	
	public Coordinate getDefaultCoordinate() {
		if(defaultCoordinate == null) {
			defaultCoordinate = this.coordinateList.get(0);
		}
		return defaultCoordinate;
	}
}
