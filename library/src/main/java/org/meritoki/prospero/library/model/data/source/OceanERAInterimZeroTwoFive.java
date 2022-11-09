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
package org.meritoki.prospero.library.model.data.source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.data.generator.ModulusZeroTwoFiveNetCDFGenerator;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.NetCDF;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class OceanERAInterimZeroTwoFive extends OceanSource {

	static Logger logger = LogManager.getLogger(OceanERAInterimZeroTwoFive.class.getName());
//	public static String path = "." + seperator + "data" + seperator + "hydrosphere" + seperator + "ocean" + seperator
//			+ "sst";
	public static String path = basePath+"prospero-data" + seperator + "ECMWF" + seperator + "File" + seperator
			+ "Data" + seperator + "ERA" + seperator +"Interim"+seperator +"SST";
	public static String prefix = "34-128_";
	public static String suffix = "_0-25-0-25";
	public static String extension = "nc";
	public int startYear = 2001;
	public int endYear = 2017;
	public boolean test = false;
	public ArrayFloat.D3 sstArray;
	
	public OceanERAInterimZeroTwoFive() {}

	public List<NetCDF> read(int year, int month) {
		logger.info("read(" + year + "," + month + ")");
		MemoryController.log();
		List<NetCDF> netCDFList = new ArrayList<>();
		String start = year + String.format("%02d", month) + String.format("%02d", 1);
		String stop = year + String.format("%02d", month) + String.format("%02d", this.getYearMonthDays(year, month));
		String fileName = path + seperator + prefix + start + "-" + stop + suffix + "." + extension;
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
			ArrayFloat.D1 latArray = (ArrayFloat.D1) latitudeVar.read();
			ArrayFloat.D1 lonArray = (ArrayFloat.D1) longitudeVar.read();
			ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
			ArrayShort.D3 sstArray = (ArrayShort.D3) sstVar.read();
			this.sstArray = this.getSSTArray(sstArray, timeCount, latitudeCount, longitudeCount, scaleFactor,
					addOffset);
			NetCDF netCDF = new NetCDF();
			netCDF.continent = this.getContinent(scaleFactor, addOffset);
			netCDF.type = DataType.SST;
			netCDF.latArray = latArray;
			netCDF.lonArray = lonArray;
			netCDF.timeArray = timeArray;
			netCDF.variableArray = this.sstArray;
			dataFile.close();
			System.gc();
			netCDFList.add(netCDF);
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
		fileName = path + seperator + "modulus"+"_" + start + "-" + stop + suffix + "." + extension;
		File file = new File(fileName);
		if(!file.exists()) {
			ModulusZeroTwoFiveNetCDFGenerator generator = new ModulusZeroTwoFiveNetCDFGenerator();
			generator.write(year,month);
		}
		try {
			dataFile = NetcdfFile.open(fileName, null);
			Variable latitudeVar = dataFile.findVariable("latitude");
			Variable longitudeVar = dataFile.findVariable("longitude");
			Variable timeVar = dataFile.findVariable("time");
			Variable modulusVar = dataFile.findVariable("modulus");
			ArrayFloat.D1 latArray = (ArrayFloat.D1) latitudeVar.read();
			ArrayFloat.D1 lonArray = (ArrayFloat.D1) longitudeVar.read();
			ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
			ArrayFloat.D3 modulusArray = (ArrayFloat.D3) modulusVar.read();
			NetCDF netCDF = new NetCDF();
			netCDF.continent = 0;
			netCDF.type = DataType.MODULUS;
			netCDF.latArray = latArray;
			netCDF.lonArray = lonArray;
			netCDF.timeArray = timeArray;
			netCDF.variableArray = modulusArray;
			dataFile.close();
			System.gc();
			netCDFList.add(netCDF);
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
		logger.info("read(" + year + "," + month + ") complete");
		MemoryController.log();
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
