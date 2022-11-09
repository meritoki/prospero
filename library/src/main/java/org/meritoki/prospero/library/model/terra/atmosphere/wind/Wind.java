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
package org.meritoki.prospero.library.model.terra.atmosphere.wind;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.atmosphere.Atmosphere;
import org.meritoki.prospero.library.model.terra.atmosphere.wind.jetstream.Jetstream;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Frame;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;

public class Wind extends Atmosphere {

	static Logger logger = LogManager.getLogger(Wind.class.getName());


	public Wind() {
		super("Wind");
		this.addChild(new Jetstream());
	}
	
	public Wind(String name) {
		super(name);
	}
	
	@Override
	public void reset() {
		super.reset();
	}
	
	@Override
	public void init() {
		this.dimension = 1;
		this.latitude = 180;
		this.longitude = 360;
		super.init();
	}
	
	@Override
	public List<Tile> getTileList() {
		return this.getTileList(this.coordinateMatrix,this.dataMatrix);
	}
	
	public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] dataMatrix) {
		List<Tile> tileList = new ArrayList<>();
		int yearCount = this.getYearCount();
		int monthCount = this.getMonthCount();
		Tile tile;
		int coordinate;
		float data;
		float dataMean;
		float dataMeanSum;
		float value;
		for (int i = 0; i < coordinateMatrix.length; i += this.dimension) {
			for (int j = 0; j < coordinateMatrix[i].length; j += this.dimension) {
				dataMeanSum = 0;
				for (int m = 0; m < 12; m++) {
					coordinate = 0;
					data = 0;
					for (int a = i; a < (i + this.dimension); a++) {
						for (int b = j; b < (j + this.dimension); b++) {
							if (a < this.latitude && b < this.longitude) {
								coordinate += coordinateMatrix[a][b][m];
								data += dataMatrix[a][b][m];
							}
						}
					}
					dataMean = (coordinate > 0) ? data / coordinate : 0;
					dataMeanSum += dataMean;
				}
				value = dataMeanSum;
				if (this.monthFlag) {
					value /= monthCount;
				} else if (this.yearFlag) {
					value /= ((double) this.getMonthCount() / (double) yearCount);
				}
				tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution,
						this.dimension, value);
				if (this.region != null) {
					if (this.region.contains(tile)) {
						tileList.add(tile);
					}
				} else if (this.regionList != null) {
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
	public void load(Result result) {
		super.load(result);
		List<Frame> frameList = result.getFrameList();
		try {
			this.process(frameList);
		} catch (Exception e) {
			logger.error("load(" + (result != null) + ") exception=" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void process(List<Frame> frameList) throws Exception {
		this.setMatrix(frameList);
		this.tileList = this.getTileList();
		this.initTileMinMax();
	}
	
	public List<Time> setCoordinateAndDataMatrix(int[][][] coordinateMatrix, float[][][] dataMatrix, List<Frame> frameList) {
		List<Time> timeList = null;
		if (frameList != null) {
			timeList = new ArrayList<>();
			for (Frame f : frameList) {
				if (f.flag) {
					for (Coordinate c : f.coordinateList) {
						if (c.flag) {
							int x = (int) (c.latitude);//((c.latitude + this.latitude) * this.resolution);
							int y = (int) (c.longitude);//((c.longitude + this.longitude / 2) * this.resolution) % this.longitude;
							int z = c.getMonth()-1;
//							System.out.println("coordinate:"+c.latitude+","+c.longitude+","+c.getMonth());
//							System.out.println("index:"+x+","+y+","+z);
							dataMatrix[x][y][z] += (float)c.attribute.get(DataType.INTENSITY.toString());
							coordinateMatrix[x][y][z]++;
							Time time = new Time(c.getYear(), c.getMonth(), -1, -1, -1, -1);
							if (!timeList.contains(time)) {
								timeList.add(time);
							}
						}
					}
				}
			}
		}
		return timeList;
	}
	
	public void setMatrix(List<Frame> frameList) {
		List<Time> timeList = this.setCoordinateAndDataMatrix(this.coordinateMatrix, this.dataMatrix, frameList);
		for(Time t: timeList) {
			if(!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
	}
}
//public void setMatrix(List<Frame> frameList) {
//System.out.println("setMatrix("+netCDFList.size()+")");
//if (frameList != null) {
//	this.initDateList(frameList);
//	int latitude = (int) (this.latitude);// * this.resolution);
//	int longitude = (int) (this.longitude);// * this.resolution);
//	List<Data> dataList;
//	for (int i = 0; i < latitude; i += dimension) {
//		for (int j = 0; j < longitude; j += dimension) {
//			for (int a = i; a < (i + dimension); a++) {
//				for (int b = j; b < (j + dimension); b++) {
//					for (Frame f : frameList) {
//						dataList = f.data.get(a + "," + b);
//						for (Data d : dataList) {
//							if (d.type == DataType.INTENSITY) {
//								this.counterMatrix[a][b]++;
//								this.intensityMatrix[a][b] += d.value;
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//}
//}
//public List<Tile> getTileList() {
//List<Tile> tileList = new ArrayList<>();
//for (int i = 0; i < latitude; i += dimension) {
//	for (int j = 0; j < longitude; j += dimension) {
//		float temperatureMean = (counterMatrix[i][j] > 0) ? intensityMatrix[i][j] / counterMatrix[i][j] : 0;
//		float value = temperatureMean;
//		int lat = (int) ((i - this.latitude));
//		int lon;
//		if (j < 180) {
//			lon = j;
//		} else {
//			lon = j - 360;
//		}
//		Tile tile = new Tile(lat, lon, dimension, value);
//		if (regionList != null) {
//			for (Region region : regionList) {
//				if (region.contains(tile)) {
//					tileList.add(tile);
//					break;
//				}
//			}
//		} else {
//			tileList.add(tile);
//		}
//	}
//}
//
//return tileList;
//}
//public int[][] counterMatrix = new int[(int) latitude][(int) longitude];
//public float[][] intensityMatrix = new float[(int) latitude][(int) longitude];