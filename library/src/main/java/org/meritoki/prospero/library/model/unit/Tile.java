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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Tile {

	static Logger logger = LogManager.getLogger(Tile.class.getName());
	@JsonIgnore
	public List<Point> pointList = new ArrayList<>();
	@JsonProperty
	public Coordinate coordinate = new Coordinate();
	@JsonProperty
	public double dimension;
	@JsonProperty
	public float value;
	@JsonProperty
	public boolean flag;

	public Tile(Tile tile) {
		if (tile != null) {
			this.pointList = new ArrayList<>(tile.pointList);
			this.coordinate = new Coordinate(tile.coordinate);
			this.dimension = tile.dimension;
			this.value = tile.value;
			this.flag = tile.flag;
		}
	}

	public Tile(double latitude, double longitude, double dimension) {
//		logger.debug("Tile(" + latitude + "," + longitude + "," + dimension + "," + value + ")");
		this.coordinate.latitude = latitude;
		this.coordinate.longitude = longitude;
		this.dimension = dimension;
	}

	public Tile(double latitude, double longitude, double dimension, double value) {
//		logger.info("Tile(" + latitude + "," + longitude + "," + dimension + "," + value + ")");
		this.coordinate.latitude = latitude;
		this.coordinate.longitude = longitude;
		this.dimension = dimension;
		this.value = (float) value;// (float) Math.abs(value);
	}

	/**
	 * Need to Use Coordiante Equals
	 */
	@JsonIgnore
	public boolean equals(Object o) {
		if (o instanceof Tile) {
			Tile t = (Tile) o;
			return t.coordinate.latitude == this.coordinate.latitude && t.coordinate.longitude == this.coordinate.longitude && t.dimension == this.dimension;
		}
		return false;
	}
	
	@JsonIgnore
	public Coordinate getCenter() {
		Coordinate center = new Coordinate();
		center.latitude = this.coordinate.latitude+(this.dimension/2);
		center.longitude = this.coordinate.longitude+(this.dimension/2);
		return center;
	}

	@JsonIgnore
	public static TableModel getTableModel(List<Tile> tileList) {
		Object[] objectArray = getObjectArray(tileList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	@JsonIgnore
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
					dataMatrix[i + 1][0] = tile.coordinate.latitude;
					dataMatrix[i + 1][1] = tile.coordinate.longitude;
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

	@JsonIgnore
	public Map<String, Double> getRegressionMap() {
		return Regression.getRegression(this.pointList);
	}

	@JsonIgnore
	public void addPoint(Point point) {
		this.pointList.add(point);
	}

	@JsonIgnore
	public Double getSignificance() {
		return this.getRegressionMap().get("significance");
	}
	
	@JsonIgnore
	public Double getCorrelation() {
		return this.getRegressionMap().get("r");
	}

	@JsonIgnore
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

	@JsonIgnore
	public static Index getSum(Time key, List<Tile> tileList) {
		double sum = 0;
		for (Tile tile : tileList) {
			sum += tile.value;
		}
		Index index = key.getIndex();
		index.value = sum;
		return index;
	}

//	public String toString() {
//		return "latitude=" + this.coordinate.latitude + ", longitude=" + this.coordinate.longitude + ", dimension=" + dimension;
//		// + ", value=" + value;
//	}
	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();//.withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}
//public double latitude;
//public double longitude;
//this.latitude = tile.latitude;
//this.longitude = tile.longitude;
//public Map<String, Double> regressionMap = null;
//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
