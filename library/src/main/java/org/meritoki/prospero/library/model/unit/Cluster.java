package org.meritoki.prospero.library.model.unit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Cluster {
	
	public String id;
	public List<Tile> tileList = new ArrayList<>();
	
	public boolean contains(Tile tile) {
		boolean flag = false;
		for(Tile t: this.tileList) {
			if(t.equals(tile)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	public boolean setTile(Tile tile) {
		boolean flag = false;
		for(Tile t: this.tileList) {
			if(t.equals(tile)) {
				flag = true;
				t.value = tile.value;
				break;
			}
		}
		return flag;
	}
	
	public double getAverageValue() {
		StandardDeviation standardDeviation = new StandardDeviation();
		Mean mean = new Mean();
		for (Tile tile : this.tileList) {
			standardDeviation.increment(tile.value);
			mean.increment(tile.value);
		}
		double value = mean.getResult();
		return value;
	}

}
