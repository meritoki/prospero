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
package org.meritoki.prospero.library.model.terra.atmosphere.cloud;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.meritoki.prospero.library.model.terra.atmosphere.Atmosphere;
import org.meritoki.prospero.library.model.terra.atmosphere.cloud.goes.N;
import org.meritoki.prospero.library.model.terra.atmosphere.cloud.goes.R;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayFloat;
import ucar.nc2.Dimension;

public class Cloud extends Atmosphere {

	static Logger logger = LoggerFactory.getLogger(Cloud.class.getName());
	protected DataType dataType;

	public Cloud() {
		super("Cloud");
		this.addChild(new N());
		this.addChild(new R());
	}

	public Cloud(String name) {
		super(name);
		this.tileFlag = true;
	}

	@Override
	public void init() {
		this.dimension = 1;
		super.init();
	}

	@Override
	public void load(Result result) {
		super.load(result);
		List<NetCDF> netCDFList = result.getNetCDFList();
		try {
			this.process(netCDFList);
		} catch (Exception e) {
			logger.error("load(" + (result != null) + ") exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void process() throws Exception {
		super.process();
		try {
//			this.process(this.netCDFList);
			this.complete();
		} catch (Exception e) {
			logger.error("process() exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void process(List<NetCDF> netCDFList) throws Exception {
		this.setMatrix(netCDFList);
		this.tileList = this.getTileList();
		this.tileFlag = true;
		this.initTileMinMax();
	}

	public void setMatrix(List<NetCDF> netCDFList) {
//		logger.info("setMatrix(" + netCDFList.size() + ")");
		List<Time> timeList = this.setCoordinateAndDataMatrix(this.coordinateMatrix, this.dataMatrix, netCDFList);
		for (Time t : timeList) {
			if (!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
	}

	public List<Time> setCoordinateAndDataMatrix(int[][][] coordinateMatrix, float[][][] dataMatrix,
			List<NetCDF> netCDFList) {
		List<Time> timeList = new ArrayList<>();
		for (NetCDF netCDF : netCDFList) {
			if (netCDF.type == DataType.BAND_4) {
				ArrayFloat.D2 latMatrix = netCDF.latMatrix;
				ArrayFloat.D2 lonMatrix = netCDF.lonMatrix;
				ArrayFloat.D3 dataArray = netCDF.variableCube;
				Dimension xDimension = netCDF.xDimension;
				Dimension yDimension = netCDF.yDimension;
				long timeSize = netCDF.timeArray.getSize();
				for (int t = 0; t < timeSize; t++) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(Time.getNineteenHundredJanuaryFirstDate(netCDF.timeArray.get(t)));
					for (int i = 0; i < xDimension.getLength(); i++) {
						for (int j = 0; j < yDimension.getLength(); j++) {
							float latitude = latMatrix.get(j, i);
							float longitude = lonMatrix.get(j, i);
							if ((int) latitude != 2143289344 && (int) longitude != 2143289344 && latitude <= 0) {
								int x = (int) ((latitude + this.latitude) * this.resolution);
								int y = (int) ((longitude + this.longitude / 2) * this.resolution) % this.longitude;
								int z = calendar.get(Calendar.MONTH);
								dataMatrix[x][y][z] += dataArray.get(t, j, i);
								coordinateMatrix[x][y][z]++;
								Time time = new Time(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, -1,
										-1, -1, -1);
								if (!timeList.contains(time)) {
									timeList.add(time);
								}
							}
						}
					}
				}
			} else if (netCDF.type == DataType.CMI) {
//				logger.info("setCoordinateMatrix(...) CMI");
				ArrayFloat.D2 latArray = netCDF.latMatrix;
				ArrayFloat.D2 lonArray = netCDF.lonMatrix;
				ArrayFloat.D2 dataArray = netCDF.variableMatrix;
				int timeSize = (int)netCDF.timeDoubleArray.getSize();
				int latSize = (int)Math.sqrt(latArray.getSize());
				int lonSize = (int)Math.sqrt(lonArray.getSize());
//				logger.info("setCoordinateMatrix(...) latSize="+latSize);
//				logger.info("setCoordinateMatrix(...) lonSize="+lonSize);
				for (int t = 0; t < timeSize; t++) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(Time.getTwoThousandJanuaryFirstDate((int)netCDF.timeDoubleArray.get(t)));
					for (int i = 0; i < latSize; i++) {
//						logger.info("setCoordinateMatrix(...) latitude="+latitude);
						for (int j = 0; j < lonSize; j++) {
							float latitude = latArray.get(i,j);
							float longitude = lonArray.get(i,j);
//							logger.info("setCoordinateMatrix(...) latitude="+latitude);
//							logger.info("setCoordinateMatrix(...) longitude="+longitude);
							if (latitude < 0) {
								int x = (int) ((latitude + this.latitude) * this.resolution);
								int y = (int) ((longitude + this.longitude / 2) * this.resolution) % this.longitude;
								int z = calendar.get(Calendar.MONTH);
								dataMatrix[x][y][z] += dataArray.get(i, j);
								coordinateMatrix[x][y][z]++;
								Time time = new Time(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE),
										calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
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
}
//@Override
//public List<Tile> getTileList() {
//	return this.getTileList(this.coordinateMatrix, this.dataMatrix);
//}
//
//public List<Tile> getTileList(int[][][] coordinateMatrix, float[][][] dataMatrix) {
//	List<Tile> tileList = new ArrayList<>();
//	int yearCount = this.getYearCount();
//	int monthCount = this.getMonthCount();
//	Tile tile;
//	int coordinate;
//	float data;
//	float dataMean;
//	float dataMeanSum;
//	float value;
//	for (int i = 0; i < coordinateMatrix.length; i += this.dimension) {
//		for (int j = 0; j < coordinateMatrix[i].length; j += this.dimension) {
//			dataMeanSum = 0;
//			for (int m = 0; m < 12; m++) {
//				coordinate = 0;
//				data = 0;
//				for (int a = i; a < (i + this.dimension); a++) {
//					for (int b = j; b < (j + this.dimension); b++) {
//						if (a < this.latitude && b < this.longitude) {
//							coordinate += coordinateMatrix[a][b][m];
//							data += dataMatrix[a][b][m];
//						}
//					}
//				}
//				dataMean = (coordinate > 0) ? data / coordinate : 0;
//				dataMeanSum += dataMean;
//			}
//			value = dataMeanSum;
//			if (this.monthFlag) {
//				value /= monthCount;
//			} else if (this.yearFlag) {
//				value /= ((double) this.getMonthCount() / (double) yearCount);
//			}
//			tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution,
//					this.dimension, value);
//			if (this.region != null) {
//				if (this.region.contains(tile)) {
//					tileList.add(tile);
//				}
//			} else if (this.regionList != null) {
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
//this.latitude = 180;
//this.resolution = 100;
//this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
//this.dataMatrix = new float[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
