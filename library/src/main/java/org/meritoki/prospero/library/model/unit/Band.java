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

import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Band {
	
	@JsonIgnore
	static Logger logger = LoggerFactory.getLogger(Band.class.getName());
	public List<Tile> tileList = new ArrayList<>();
	public double value;
	public double latitude;
	
	public Band(List<Tile> tileList, double latitude) {
		this.latitude = latitude;
		this.tileList = tileList;
		this.value = this.average(this.tileList);
	}
	
	public double average(List<Tile> tileList) {
		double quotient = 0;
		double sum=0;
		double size = 0;
		for(Tile t: tileList) {
			if(t.value != 0) {
				sum += t.value;
				size++;
			}
		}
		quotient = (size>0)?(sum/size):quotient;
//		logger.info("average("+tileList.size()+") quotient="+quotient);
		return quotient;
	}
	
	public static TableModel getTableModel(List<Band> bandList) {
		Object[] objectArray = getObjectArray(bandList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	public static Object[] getObjectArray(List<Band> bandList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (bandList != null) {
			if (bandList.size() > 0) {
				for (int i = 0; i < bandList.size(); i++) {
					Band e = bandList.get(i);
					if (e instanceof Band) {
						Band band = (Band) e;
						if (i == 0) {
							columnArray = Table.getColumnNames(3).toArray();
							dataMatrix = new Object[bandList.size() + 1][3];
							dataMatrix[i][0] = "latitude";
							dataMatrix[i][1] = "dimension";
							dataMatrix[i][2] = "mean";
						}
						dataMatrix[i + 1][0] = band.latitude;
						dataMatrix[i + 1][1] = band.tileList.get(0).dimension;
						dataMatrix[i + 1][2] = band.value;
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}
}
