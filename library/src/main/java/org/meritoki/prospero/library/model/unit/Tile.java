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
package org.meritoki.prospero.library.model.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tile {

	static Logger logger = LogManager.getLogger(Tile.class.getName());
	public List<Point> pointList = new ArrayList<>();
	public double latitude;
	public double longitude;
	public double dimension;
	public float value;
	public boolean flag;

	public Tile(Tile tile) {
		if (tile != null) {
			this.latitude = tile.latitude;
			this.longitude = tile.longitude;
			this.dimension = tile.dimension;
			this.value = tile.value;
		}
	}

	public Tile(double latitude, double longitude, double dimension) {
		logger.debug("Tile(" + latitude + "," + longitude + "," + dimension + "," + value + ")");
		this.latitude = latitude;
		this.longitude = longitude;
		this.dimension = dimension;
	}

	public Tile(double latitude, double longitude, double dimension, double value) {
		logger.debug("Tile(" + latitude + "," + longitude + "," + dimension + "," + value + ")");
		this.latitude = latitude;
		this.longitude = longitude;
		this.dimension = dimension;
		this.value = (float) value;// (float) Math.abs(value);
	}

	public boolean equals(Object o) {
		if (o instanceof Tile) {
			Tile t = (Tile) o;
			return t.latitude == this.latitude && t.longitude == this.longitude && t.dimension == this.dimension;
		}
		return false;
	}
	
	public Coordinate getCenter() {
		Coordinate center = new Coordinate();
		center.latitude = this.latitude+(this.dimension/2);
		center.longitude = this.longitude+(this.dimension/2);
		return center;
	}

	public static TableModel getTableModel(List<Tile> tileList) {
		Object[] objectArray = getObjectArray(tileList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	public static Object[] getObjectArray(List<Tile> tileList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		if (tileList != null) {
			if (tileList.size() > 0) {
				for (int i = 0; i < tileList.size(); i++) {
					Tile tile = tileList.get(i);
					if (i == 0) {
						columnArray = Table.getColumnNames(5).toArray();
						dataMatrix = new Object[tileList.size() + 1][5];
						dataMatrix[i][0] = "latitude";
						dataMatrix[i][1] = "longitude";
						dataMatrix[i][2] = "dimension";
						dataMatrix[i][3] = "value";
						dataMatrix[i][4] = "significance";
					}
					dataMatrix[i + 1][0] = tile.latitude;
					dataMatrix[i + 1][1] = tile.longitude;
					dataMatrix[i + 1][2] = tile.dimension;
					dataMatrix[i + 1][3] = tile.value;
					dataMatrix[i + 1][4] = tile.getSignificance();
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}

	public Map<String, Double> getRegressionMap() {
		return Regression.getRegression(this.pointList);
	}

	public void addPoint(Point point) {
		this.pointList.add(point);
	}

	public Double getSignificance() {
		return this.getRegressionMap().get("significance");
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
		// + ", value=" + value;
	}
}
//public Map<String, Double> regressionMap = null;
//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
