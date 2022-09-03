package org.meritoki.prospero.library.model.unit;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Tile {

	public double latitude;
	public double longitude;
	public double dimension;
	public float value;
	private boolean print = false;

	public Tile(Tile tile) {
		if (tile != null) {
			this.latitude = tile.latitude;
			this.longitude = tile.longitude;
			this.dimension = tile.dimension;
			this.value = tile.value;
		}
	}
	
	public Tile(double latitude, double longitude, double dimension) {
		if (print)
			System.out.println("Tile(" + latitude + "," + longitude + "," + dimension + "," + value + ")");
		this.latitude = latitude;
		this.longitude = longitude;
		this.dimension = dimension;
	}

	public Tile(double latitude, double longitude, double dimension, double value) {
		if (print)
			System.out.println("Tile(" + latitude + "," + longitude + "," + dimension + "," + value + ")");
		this.latitude = latitude;
		this.longitude = longitude;
		this.dimension = dimension;
		this.value = (float) value;// (float) Math.abs(value);
	}
	
	public boolean equals(Object o) {
		if(o instanceof Tile) {
			Tile t = (Tile)o;
			return t.latitude == this.latitude && t.longitude == this.longitude && t.dimension == this.dimension;
		}
		return false;
	}
	
	public static Index getAverage(Time key, List<Tile> tileList) {
		StandardDeviation standardDeviation = new StandardDeviation();
		Mean mean = new Mean();
		for (Tile tile : tileList) {
			standardDeviation.increment(tile.value);
			mean.increment(tile.value);
		}
		double value = mean.getResult();
		Index index = null;
		if (!Double.isNaN(value) && value != 0) {
			index = key.getIndex();
			index.value = value;
			index.map.put("N", standardDeviation.getN());
			index.map.put("standardDeviation", standardDeviation.getResult());
		}
		return index;
	}
	
	public static Index getSum(Time key, List<Tile> tileList) {
		double sum = 0;
		for (Tile tile : tileList) {
			sum += tile.value;
		}
		Index index = key.getIndex();
		index.value = sum;
		return index;
	}

	public String toString() {
		return "latitude=" + this.latitude + ", longitude=" + this.longitude + ", dimension=" + dimension; 
				//+ ", value=" + value;
	}
}
