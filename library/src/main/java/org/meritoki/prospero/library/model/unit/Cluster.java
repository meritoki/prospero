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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.table.TableModel;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Cluster {
	
	public String uuid;
	public Integer id;
	public List<Tile> tileList = new ArrayList<>();//Tile List Could Use Flag to Determine which Tiles Belong to Cluster
	
	public Cluster() {
		this.uuid = UUID.randomUUID().toString();
	}
	
	public static TableModel getTableModel(List<Cluster> clusterList) {
		Object[] objectArray = getObjectArray(clusterList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	public static Object[] getObjectArray(List<Cluster> clusterList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
	
	public int getID() {
		return (id != null)?id:0;
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
	
	public void addTilePoint(double average) {
		for(Tile tile: this.tileList) {
			tile.addPoint(new Point(tile.value,average));
		}
	}
	
	public List<Tile> getTileList() {
		for(Tile t: this.tileList) {
			t.value = Float.valueOf(this.id);
		}
		return this.tileList;
	}
}
