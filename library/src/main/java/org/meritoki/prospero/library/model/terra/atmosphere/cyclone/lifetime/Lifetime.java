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
	public void setMatrix(List<Event> eventList) {
		List<Time> timeList = this.setDurationCoordinateMatrix(this.dataMatrix, this.coordinateMatrix, eventList);
		for (Time t : timeList) {
			if (!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
		this.tileList = this.getTileList();
		this.bandList = this.getBandList(this.tileList);
		this.initTileMinMax();
	}

	public List<Time> setDurationCoordinateMatrix(float[][][] durationMatrix, int[][][] coordinateMatrix,
			List<Event> eventList) {
		List<Time> timeList = null;
		Time startTime = new Time("month", this.startCalendar);
		Time endTime = new Time("month", this.endCalendar);
		if (eventList != null) {
			timeList = new ArrayList<>();
			for (Event e : eventList) {
				if (e.flag) {
					for (Coordinate c : e.coordinateList) {
						if (c.flag) {
							Time time = new Time(c.getYear(), c.getMonth(), -1, -1, -1, -1);
							if (startTime.lessThan(time) && time.lessThan(endTime)) {
								int x = (int) ((c.latitude + this.latitude) * this.resolution);
								int y = (int) ((c.longitude + this.longitude / 2) * this.resolution) % this.longitude;
								int z = c.getMonth() - 1;
								coordinateMatrix[x][y][z]++;
								durationMatrix[x][y][z] += e.getDuration().days;

								if (!timeList.contains(time)) {
									timeList.add(time);
								}
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
		if (this.averageFlag) {
			index = Tile.getAverage(key, tileList);
		} else if (this.sumFlag) {
			index = Tile.getSum(key, tileList);
		} else {
			index = super.getIndex(key, eventList);
		}
		return index;
	}
}
//@Override
//public List<Tile> getTileList() {
//	return this.getTileList(this.coordinateMatrix, this.dataMatrix);
//}

//public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] dataMatrix) {
//	List<Tile> tileList = new ArrayList<>();
//	int yearCount = this.getYearCount();
//	int monthCount = this.getMonthCount();
//	int monthTotal = this.getMonthTotal();
//	Tile tile;
//	int count;
//	float data;
//	float mean;
//	float meanSum;
//	float value;
//	// Nested For Loops Use Dimension to Increment over 2D Coordinate and Data
//	// Matrices
//	// Each Iteration of Both Loops Represents a Unique Tile
//	for (int i = 0; i < coordinateMatrix.length; i += this.dimension) {
//		for (int j = 0; j < coordinateMatrix[i].length; j += this.dimension) {
//			meanSum = 0;// Reset Mean Sum to Zero
//			// Each Month the Mean Sum will Have a Monthly Mean for any Number of Years
//			// Added to it
//			// We Iterate Over All 12 Months for a Tile
//			for (int m = 0; m < 12; m++) {
//				// Reset Count and Duration of Zero
//				count = 0;
//				data = 0;
//				// We Iterate over all Coordinate and Data Matrix Cells that correspond to a
//				// Tile using a and b as indices
//				for (int a = i; a < (i + this.dimension); a++) {
//					for (int b = j; b < (j + this.dimension); b++) {
//						if (a < this.latitude && b < this.longitude) {
//							// Each Tile is like a bucket where we retain a Count and Sum of Unique
//							// Coordinates and Data, i.e. Duration, respectively,
//							// In Some Cases a Month or Tile may not contain any measurements
//							// This is possible with Seasonal Queries.
//							// In which case the Mean Sum will only contain those months, i.e. DJF, JJA,
//							// MAN, SON
//							count += coordinateMatrix[a][b][m];// Count Sum of Months From All Years Queried
//							data += dataMatrix[a][b][m];// Data Sum of Months From All Years Queried
//						}
//					}
//				}
//				// After Summing the Data, i.e. Duration, and Count of a Tile For a Given Month
//				// Divide the Duration by Count when Count > 0, to obtain a Mean for the Tile in
//				// a Specific Month
//				// No Matter How Many Unique Years and Months are queried, i.e. 1979/01-2019/12
//				// Mean officially represents the Average for that Month
//				mean = (count > 0) ? data / count : 0;
//				count = this.monthArray[m];
//				mean = (count > 0) ? mean / count : mean;
//				// Sum every Mean, One for Each Month
//				// Mean Sum can contain up to 12 Unique Month Means
//				// In Long Queries with Many Unique Years and Data for All 12 Months,
//				// The Mean is already the Average for All Possible Query Months, i.e. DJF or
//				// January
//				meanSum += mean;
//			}
//			// Mean Sum is typically the Sum of All 12 Monthly Means for a Given Tile
//			// In Some cases the Mean Sum will be the Sum of Less Unique Months, i.e. a
//			// Season 3 Months.
//			// In Most cases, the Mean Sum of 12 Unique Months is equivalent to the Mean Sum
//			// for One Unique Year
//			// If we have more than One Unique Year, i.e. 2001,2002,2003
//			value = meanSum;// Value is Assigned the Mean Sum
//			if (this.monthFlag) {// If We Want a Monthly Average, We Divide by Unique Months
//				value /= monthCount;// Define 0 < monthCount <= 12, commonly has a Value of 3 or 1 as well, season
//									// and unique month
//			} else if (this.yearFlag) {// If We Want a Yearly Average, we Divide by Unique Years
//				// We do this because even though we have the Sum of Averages for Each Month
//				// value /= yearCount;// Define 0 < yearCount <= N, where N is a Positive Integer
//				// value /= monthCount;
//				// Deprecated
//				// value /= ((double)monthTotal/(double)yearCount);
//				// Defect Corrected 2022/10/14
//				// I thought I had to divide the Mean Sum by Unique Months & then Unique Years
//				// This produced incorrect Mean Values for the YEARLY Mean of Tiles
//				// The fix applied coincides with the addition of Band Support
//				// Detected because Yearly Averages for Lifetime seemed too small/low
//			}
//			// With the Value Corrected to Provide
//			tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution,
//					this.dimension, value);
//			if (this.regionList != null) {
//				for (Region region : this.regionList) {
//					if (region.contains(tile)) {
//						tileList.add(tile);
//						break;
//					}
//				}
//			} else {
//				tileList.add(tile);
//			}
//		}
//	}
//	return tileList;
//}
//public float[][][] durationMatrix = new float[(int) (latitude * resolution)][(int) (longitude
//* resolution)][12];
//public Map<Integer, float[][][]> durationMatrixMap = new HashMap<>();
