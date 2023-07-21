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
package org.meritoki.prospero.library.model.node.data.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.utn.app.command.era.Five;
import org.utn.app.command.era.model.five.Batch;
import org.utn.app.command.era.model.five.Form;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class SeaSurfaceTemperatureERA5 extends OceanERA {

	static Logger logger = LoggerFactory.getLogger(SeaSurfaceTemperatureERA5.class.getName());
	private Form form = new Form();

	public SeaSurfaceTemperatureERA5() {
		super();
		this.variable = "sst";
		this.prefix = "sea_surface_temperature_";
		this.suffix = "_0-25-0-25";
		this.startTime = new Time(1979, 1, 1, 0, -1, -1);
		this.endTime = new Time(2019, 12, 31, 24, -1, -1);
		this.setRelativePath("ECMWF" + seperator + "File" + seperator + "Data" + seperator + "ERA" + seperator + "5"
				+ seperator + "SST");
		this.form.grid.add("0.25/0.25");
		this.form.time.add("00:00");
		this.form.time.add("06:00");
		this.form.time.add("12:00");
		this.form.time.add("18:00");
		this.form.path = "reanalysis-era5-single-levels";
		for (int i = 1; i <= 31; i++) {
			this.form.day.add(String.format("%02d", i));
		}
		this.form.variable.add("sea_surface_temperature");
	}

	public void form(Time time) {
		super.form(time);
		if (!this.form.year.contains(String.valueOf(time.year))) {
			this.form.year.add(String.valueOf(time.year));
		}
		String month = String.format("%02d", time.month);
		if (!this.form.month.contains(month)) {
			this.form.month.add(month);
		}
	}

	@Override
	public List<NetCDF> read(Time time) throws Exception {
		List<NetCDF> netCDFList = new ArrayList<>();
		String start = time.year + String.format("%02d", time.month) + String.format("%02d", 1);
		String stop = time.year + String.format("%02d", time.month)
				+ String.format("%02d", Time.getYearMonthDays(time.year, time.month));
		this.setFileName(prefix + start + "-" + stop + suffix + "." + extension);
		if (this.fileExists()) {
			netCDFList.addAll(this.read(this.getFilePath()));
		} else {
			this.form(time);
		}
		return netCDFList;
	}

	@Override
	public void download(Query query) {
		super.download(query);
		this.form.outputPath = this.getPath();
		Batch batch = new Batch(this.form);
		String batchPath = this.getPath() + batch.uuid + ".json";
		Five.executeBatch(batchPath, new Batch(this.form));
	}

	public List<NetCDF> read(String fileName) {
		logger.info("read(" + fileName + ")");
		List<NetCDF> netCDFList = this.netCDFMap.get(fileName);
		if (netCDFList == null) {
			MemoryController.log();
			netCDFList = new ArrayList<>();
			NetcdfFile dataFile = null;
			try {
				dataFile = NetcdfFile.open(fileName, null);
				Variable latitudeVar = dataFile.findVariable("latitude");
				Variable longitudeVar = dataFile.findVariable("longitude");
				Variable timeVar = dataFile.findVariable("time");
				Variable sstVar = dataFile.findVariable("sst");
				Attribute scaleFactorAttribute = sstVar.findAttribute("scale_factor");
				Attribute addOffsetAttribute = sstVar.findAttribute("add_offset");
				double scaleFactor = (Double) scaleFactorAttribute.getNumericValue();
				double addOffset = (Double) addOffsetAttribute.getNumericValue();
				int longitudeCount = (int) longitudeVar.getSize();
				int latitudeCount = (int) latitudeVar.getSize();
				int timeCount = (int) timeVar.getSize();
				ArrayFloat.D1 latitudeArray = (ArrayFloat.D1) latitudeVar.read();
				ArrayFloat.D1 longitudeArray = (ArrayFloat.D1) longitudeVar.read();
				ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
				ArrayShort.D3 sstArray = (ArrayShort.D3) sstVar.read();
				
				NetCDF sstNetCDF = new NetCDF();
//				netCDF.continent = this.getContinent(scaleFactor, addOffset);
				sstNetCDF.type = DataType.SST;
				sstNetCDF.latArray = latitudeArray;
				sstNetCDF.lonArray = longitudeArray;
				sstNetCDF.timeArray = timeArray;
				sstNetCDF.variableCube = this.getSSTArray(sstArray, timeCount, latitudeCount, longitudeCount, scaleFactor,
						addOffset);
				dataFile.close();
				System.gc();
				netCDFList.add(sstNetCDF);
				// MODULUS
				NetCDF netCDF = new NetCDF();
//				netCDF.continent = this.getContinent(scaleFactor, addOffset);
				netCDF.type = DataType.MODULUS;
				netCDF.latArray = latitudeArray;
				netCDF.lonArray = longitudeArray;
				netCDF.timeArray = timeArray;
				netCDF.variableCube = new ArrayFloat.D3(timeCount, latitudeCount, longitudeCount);
				float continent = this.getContinent(scaleFactor, addOffset);
				boolean latitudeCentralFlag = false;
				boolean latitudeBackwardFlag = false;
				boolean latitudeForwardFlag = false;
				boolean longitudeCentralFlag = false;
				boolean longitudeBackwardFlag = false;
				boolean longitudeForwardFlag = false;
				boolean dPhiFlag = false;
				boolean dThetaFlag = false;
				for (int time = 0; time < timeCount; time++) {
					for (int lat = 0; lat < latitudeCount; lat++) {
						float latitude = latitudeArray.get(lat);
						float latitudeA = 0;
						float latitudeB = 0;
						latitudeCentralFlag = false;
						latitudeForwardFlag = false;
						latitudeBackwardFlag = false;
						if ((lat) == 0) {
							latitudeForwardFlag = true;
							latitudeA = latitudeArray.get(lat);
							latitudeB = latitudeArray.get(lat + 1);
						} else if ((lat + 1) == latitudeCount) {
							latitudeBackwardFlag = true;
							latitudeA = latitudeArray.get(lat - 1);
							latitudeB = latitudeArray.get(lat);
						} else if ((lat - 1) >= 0 && (lat + 1) < latitudeCount) {
							latitudeCentralFlag = true;
							latitudeA = latitudeArray.get(lat - 1);
							latitudeB = latitudeArray.get(lat + 1);
						}

						float phi = 90 - latitude;
						float phiA = 90 - latitudeA;
						float phiB = 90 - latitudeB;
						phi = (float) Math.toRadians(phi);
						phiA = (float) Math.toRadians(phiA);
						phiB = (float) Math.toRadians(phiB);
						latitude += 90;
						for (int lon = 0; lon < longitudeCount; lon++) {
							float longitude = longitudeArray.get(lon);
							float longitudeA = 0;
							float longitudeB = 0;
							longitudeCentralFlag = false;
							longitudeForwardFlag = false;
							longitudeBackwardFlag = false;
							if (lon == 0) {
								longitudeForwardFlag = true;
								longitudeA = longitudeArray.get(lon);
								longitudeB = longitudeArray.get(lon + 1);
							} else if (lon + 1 == longitudeCount) {
								longitudeBackwardFlag = true;
								longitudeA = longitudeArray.get(lon - 1);
								longitudeB = longitudeArray.get(lon);
							} else if ((lon - 1) >= 0 && (lon + 1) < longitudeCount) {
								longitudeCentralFlag = true;
								longitudeA = longitudeArray.get(lon - 1);
								longitudeB = longitudeArray.get(lon + 1);
							}
							double theta = longitude;
							double thetaA = longitudeA;
							double thetaB = longitudeB;
							theta = Math.toRadians(theta);
							thetaA = Math.toRadians(thetaA);
							thetaB = Math.toRadians(thetaB);
							float sst = sstNetCDF.variableCube.get(time, lat, lon);
							float sstPhiA;
							float sstPhiB;
							float sstThetaA;
							float sstThetaB;
							float modulus = 0;
							double dPhi = -1;
							double dTheta = -1;
							if (sst != continent) {
								dPhiFlag = false;
								if (latitudeForwardFlag) {
									sstPhiA = sstNetCDF.variableCube.get(time, lat, lon);
									sstPhiB = sstNetCDF.variableCube.get(time, lat + 1, lon);
									if (sstPhiA != continent && sstPhiB != continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("forward", sstPhiA, sstPhiB, phiA, phiB);
									}
								} else if (latitudeBackwardFlag) {
									sstPhiA = sstNetCDF.variableCube.get(time, lat - 1, lon);
									sstPhiB = sstNetCDF.variableCube.get(time, lat, lon);
									if (sstPhiA != continent && sstPhiB != continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("backward", sstPhiA, sstPhiB, phiA, phiB);
									}
								} else if (latitudeCentralFlag) {
									sstPhiA = sstNetCDF.variableCube.get(time, lat - 1, lon);
									sstPhiB = sstNetCDF.variableCube.get(time, lat + 1, lon);
									if (sstPhiA != continent && sstPhiB != continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("central", sstPhiA, sstPhiB, phiA, phiB);
									}
								}
								dThetaFlag = false;
								if (longitudeForwardFlag) {
									sstThetaA = sstNetCDF.variableCube.get(time, lat, lon);
									sstThetaB = sstNetCDF.variableCube.get(time, lat, lon + 1);
									if (sstThetaA != continent && sstThetaB != continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("forward", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								} else if (longitudeBackwardFlag) {
									sstThetaA = sstNetCDF.variableCube.get(time, lat, lon - 1);
									sstThetaB = sstNetCDF.variableCube.get(time, lat, lon);
									if (sstThetaA != continent && sstThetaB != continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("backward", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								} else if (longitudeCentralFlag) {
									sstThetaA = sstNetCDF.variableCube.get(time, lat, lon - 1);
									sstThetaB = sstNetCDF.variableCube.get(time, lat, lon + 1);
									if (sstThetaA != continent && sstThetaB != continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("central", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								}
								if (dPhiFlag && dThetaFlag) {
									modulus = (float) this.getModulus(this.earthRadius, phi, theta, 0, dPhi, dTheta);
								} else {
									modulus = 0;
								}
								netCDF.variableCube.set(time, lat, lon, modulus);
							} else {
								netCDF.variableCube.set(time, lat, lon, 0);
							}
						}
					}
				}

				netCDFList.add(netCDF);
				// MODULUS
				this.netCDFMap.put(fileName, netCDFList);
			} catch (java.io.IOException e) {
				logger.error("IOException " + e.getMessage());

			} finally {
				if (dataFile != null) {
					try {
						dataFile.close();
					} catch (IOException e) {
						logger.error("IOException " + e.getMessage());
					}
				}
			}
//			String outputFileName = this.getFilePath("modulus" + "_" + fileName);
//			File file = new File(fileName);
//			if (!file.exists()) {
//				ModulusSSTERA5Generator generator = new ModulusSSTERA5Generator();
//				generator.write(fileName, outputFileName);
//			}
//			try {
//				dataFile = NetcdfFile.open(outputFileName, null);
//				Variable latitudeVar = dataFile.findVariable("latitude");
//				Variable longitudeVar = dataFile.findVariable("longitude");
//				Variable timeVar = dataFile.findVariable("time");
//				Variable modulusVar = dataFile.findVariable("modulus");
//				ArrayFloat.D1 latArray = (ArrayFloat.D1) latitudeVar.read();
//				ArrayFloat.D1 lonArray = (ArrayFloat.D1) longitudeVar.read();
//				ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
//				ArrayFloat.D3 modulusArray = (ArrayFloat.D3) modulusVar.read();
//				NetCDF netCDF = new NetCDF();
//				netCDF.continent = 0;
//				netCDF.type = DataType.MODULUS;
//				netCDF.latArray = latArray;
//				netCDF.lonArray = lonArray;
//				netCDF.timeArray = timeArray;
//				netCDF.variableArray = modulusArray;
//				dataFile.close();
//				System.gc();
//				netCDFList.add(netCDF);
//			} catch (java.io.IOException e) {
//				logger.error("IOException " + e.getMessage());
//
//			} finally {
//				if (dataFile != null) {
//					try {
//						dataFile.close();
//					} catch (IOException e) {
//						logger.error("IOException " + e.getMessage());
//					}
//				}
//			}
//			MemoryController.log();
		}
		return netCDFList;
	}

	public int getResolution(String resolution) throws Exception {
		int r = 1;
		if (resolution != null && !resolution.isEmpty()) {
			boolean valid = true;
			try {
				r = Integer.parseInt(resolution);
			} catch (NumberFormatException e) {
				valid = false;
			}
			if (r != 1 && r != 100) {
				valid = false;
			}
			if (!valid) {
				throw new Exception("invalid resolution format: " + resolution);
			}
		}
		return r;
	}

//	public static void main(String[] args) {
//		ERAInterimZeroTwoFiveNetCDFSource source = new ERAInterimZeroTwoFiveNetCDFSource();
//		Query query = new Query();
//		query.time = "2001-2017";
//		query.resolution = "100";
//		query.dimension = "0.25";
//		SeaSurfaceTemperature sst;
//		MemoryController.log();
//		try {
//			source.query(query);
//			MemoryController.log();
//			sst = new SeaSurfaceTemperature(source.dimension, source.resolution);
//			for (Interval i : source.intervalList) {
//				List<OceanPlot> oceanList = new ArrayList<>();
//				oceanList.add(sst);
//				source.setNetCDFList(i, oceanList);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
//public int startYear = 1979;
//public int endYear = 2019;
//public boolean test = false;
//@Override
//public int getStartYear() {
//	return this.startYear;
//}
//
//@Override
//public int getEndYear() {
//	return this.endYear;
//}
//public static String path = "." + seperator + "data" + seperator + "hydrosphere" + seperator + "ocean" + seperator
//+ "sst";
//public static String path = basePath+"prospero-data" + seperator + "ECMWF" + seperator + "File" + seperator
//+ "Data" + seperator + "ERA" + seperator +"5"+seperator +"SST";
//public static String prefix = 
//public static String suffix = 
//public static String extension = "nc";
//public void query(Query query) throws Exception {
//logger.info("query(" + query + ")");
//this.unsupportedException("level", query.level);
//this.unsupportedException("duration", query.duration);
//this.unsupportedException("family", query.familyList);
//this.unsupportedException("class", query.classList);
//this.intervalList = this.getIntervalList(query.time);
//this.regionList = this.getRegionList(query.region);
//this.dimension = this.getDimension(query.dimension);
//this.resolution = this.getResolution(query.resolution);
//if (this.dimension == 0.25 && this.resolution == 100) {
//	logger.info("valid dimension and resolution");
//} else {
//	throw new Exception("invalid dimension and resolution");
//}
//}

//public void setNetCDFList(Interval i, List<OceanPlot> oceanList) {
//DataType dataType = null;
//List<NetCDF> frameList = new ArrayList<>();
//int startYear = (i.startYear == -1) ? this.startYear : i.startYear;
//int startMonth = (i.startMonth == -1) ? 1 : i.startMonth;
//int endYear = (i.endYear == -1) ? this.endYear : i.endYear;
//int endMonth = (i.endMonth == -1) ? 12 : i.endMonth;
//if (i.startYear == -1 && i.endYear == -1) {
//	if (startMonth <= endMonth) {
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for (OceanPlot o : oceanList) {
//					if (o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if (o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					o.addNetCDFList(frameList, dataType);
//				}
//			}
//		}
//	} else {
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= 12; m++) {
//				frameList = this.read(y, m);
//				for (OceanPlot o : oceanList) {
//					if (o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if (o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					o.addNetCDFList(frameList, dataType);
//				}
//			}
//			for (int m = 1; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for (OceanPlot o : oceanList) {
//					if (o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if (o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					o.addNetCDFList(frameList, dataType);
//				}
//			}
//		}
//	}
//} else {
//	int yearDifference = endYear - startYear - 1;
//	if (yearDifference == -1) {// same year
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for (OceanPlot o : oceanList) {
//					if (o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if (o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					o.addNetCDFList(frameList, dataType);
//				}
//			}
//		}
//	} else if (yearDifference > -1) {
//		for (int m = startMonth; m <= 12; m++) {
//			frameList = this.read(startYear, m);
//			for (OceanPlot o : oceanList) {
//				if (o instanceof SeaSurfaceTemperature) {
//					dataType = DataType.SST;
//				} else if (o instanceof Modulus) {
//					dataType = DataType.MODULUS;
//				}
//				o.addNetCDFList(frameList, dataType);
//			}
//		}
//		for (int y = startYear + 1; y <= endYear - 1; y++) {// skipped is years are consecutive
//			for (int m = 1; m <= 12; m++) {
//				frameList = this.read(y, m);
//				for (OceanPlot o : oceanList) {
//					if (o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if (o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					o.addNetCDFList(frameList, dataType);
//				}
//			}
//		}
//		for (int m = 1; m <= endMonth; m++) {
//			frameList = this.read(endYear, m);
//			for (OceanPlot o : oceanList) {
//				if (o instanceof SeaSurfaceTemperature) {
//					dataType = DataType.SST;
//				} else if (o instanceof Modulus) {
//					dataType = DataType.MODULUS;
//				}
//				o.addNetCDFList(frameList, dataType);
//			}
//		}
//	}
//}
//for (OceanPlot o : oceanList) {
//	o.finalize(this.regionList, this.month, this.year);
//}
//}

//if (frame.dataMap.get(latitudeIndex + "," + longitudeIndex) == null) {
//frame.dataMap.put(latitudeIndex + "," + longitudeIndex, new BigList<>());
//}
//if (frame.countMap.get(
//	DataType.MODULUS.toString() + latitudeIndex + "," + longitudeIndex) == null) {
//frame.countMap
//		.put(DataType.MODULUS.toString() + latitudeIndex + "," + longitudeIndex, 0);
//}
//if (frame.countMap
//	.get(DataType.SST.toString() + latitudeIndex + "," + longitudeIndex) == null) {
//frame.countMap.put(DataType.SST.toString() + latitudeIndex + "," + longitudeIndex,
//		0);
//}
//
//if (frame.sumMap.get(
//	DataType.MODULUS.toString() + latitudeIndex + "," + longitudeIndex) == null) {
//frame.sumMap.put(DataType.MODULUS.toString() + latitudeIndex + "," + longitudeIndex,
//		(float) 0);
//}
//if (frame.sumMap
//	.get(DataType.SST.toString() + latitudeIndex + "," + longitudeIndex) == null) {
//frame.sumMap.put(DataType.SST.toString() + latitudeIndex + "," + longitudeIndex,
//		(float) 0);
//}
////count
//int count = frame.countMap
//	.get(DataType.MODULUS.toString() + latitudeIndex + "," + longitudeIndex);
//count++;
//frame.countMap.put(DataType.MODULUS.toString() + latitudeIndex + "," + longitudeIndex,
//	count);
//count = frame.countMap
//	.get(DataType.SST.toString() + latitudeIndex + "," + longitudeIndex);
//count++;
//frame.countMap.put(DataType.SST.toString() + latitudeIndex + "," + longitudeIndex,
//	count);
////sum
//float sum = frame.sumMap
//	.get(DataType.MODULUS.toString() + latitudeIndex + "," + longitudeIndex);
//sum += modulus;
//frame.sumMap.put(DataType.MODULUS.toString() + latitudeIndex + "," + longitudeIndex,
//	sum);
//sum = frame.sumMap.get(DataType.SST.toString() + latitudeIndex + "," + longitudeIndex);
//sum += sst;
//frame.sumMap.put(DataType.SST.toString() + latitudeIndex + "," + longitudeIndex, sum);
