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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.table.TableModel;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Cluster {
	
	static Logger logger = LogManager.getLogger(Cluster.class.getName());
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	@JsonProperty
	public String uuid;
	@JsonProperty
	public Integer id;
	public List<Tile> tileList = new ArrayList<>();//Tile List Could Use Flag to Determine which Tiles Belong to Cluster
	
	public Cluster() {
		this.uuid = UUID.randomUUID().toString();
	}
	
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

	/**
	 * Proper Function Requires that Tile Flag True indicates a Tile belongs to a Cluster
	 * @return
	 */
	@JsonIgnore
	public double getAverageValue() {
		StandardDeviation standardDeviation = new StandardDeviation();
		Mean mean = new Mean();
		for (Tile tile : this.tileList) {
			if(tile.flag) {
//				logger.info("getAverageValue() tile="+tile);
				standardDeviation.increment(tile.value);
				mean.increment(tile.value);
			}
		}
		double value = mean.getResult();
		return value;
	}

	@JsonIgnore
	public static TableModel getTableModel(List<Cluster> clusterList) {
		Object[] objectArray = getObjectArray(clusterList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	@JsonIgnore
	public static Object[] getObjectArray(List<Cluster> clusterList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		
		if (clusterList != null) {
			if (clusterList.size() > 0) {
				for (int i = 0; i < clusterList.size(); i++) {
					Cluster e = clusterList.get(i);
					if (e instanceof Cluster) {
						Cluster cluster = (Cluster) e;
						if (i == 0) {
							columnArray = Table.getColumnNames(3).toArray();
							dataMatrix = new Object[clusterList.size() + 1][3];
							dataMatrix[i][0] = "uuid";
							dataMatrix[i][1] = "id";
							dataMatrix[i][2] = "tileCount";
						}
						dataMatrix[i + 1][0] = cluster.uuid;
						dataMatrix[i + 1][1] = cluster.id;
						dataMatrix[i + 1][2] = cluster.tileList.size();
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}
	
	@JsonIgnore
	public int getID() {
		return (id != null)?id:0;
	}

	@JsonIgnore
	public List<Tile> getTileList() {
		for(Tile t: this.tileList) {
			t.value = Float.valueOf(this.id);
		}
		return this.tileList;
	}

	/**
	 * Something is Wrong Here
	 * Tile List Represents One Moment in Time, i.e. One Month Average
	 * @param tileList
	 * @return
	 */
	@JsonIgnore
	public void setTileList(List<Tile> tileList) {
		for(Tile tile: tileList) {// All Tiles
			if(!this.tileList.contains(tile)) {
				this.tileList.add(new Tile(tile));
			}
		}
		for(Tile tile: tileList) {
			for(Tile t: this.tileList) {
				if(t.equals(tile)) {
					t.value = tile.value;
					break;
				}
			}
		}
	}
	
	/**
	 * Tile List must be correct already
	 * @param average
	 */
	@JsonIgnore
	public void addTilePoint(double average) {
//		logger.info("addTilePoint("+average+")");
		for(Tile tile: this.tileList) {
			tile.addPoint(new Point(tile.value,average));
		}
	}
	
	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}
//public boolean setTile(Tile tile) {
//boolean flag = false;
//for(Tile t: this.tileList) {
//	if(t.equals(tile)) {
//		t.value = tile.value;
//		flag = true;
//		break;
//	}
//}
//return flag;
//}
