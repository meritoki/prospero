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

import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class JetstreamERAInterim extends ERANetCDF {

	static Logger logger = LoggerFactory.getLogger(JetstreamERAInterim.class.getName());

	public JetstreamERAInterim() {
		super();
		this.dataType = DataType.INTENSITY;
		this.prefix = "131-128-132-128_";
		this.suffix = "_200-250-300_F128";
		this.startTime = new Time(1979, 1, 1, 0, -1, -1);
		this.endTime = new Time(2019, 12, 31, 24, -1, -1);
		this.setRelativePath("ECMWF" + seperator + "File" + seperator + "Data" + seperator + "ERA" + seperator + "Interim"
				+ seperator + "UV");
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
		}
		return netCDFList;
	}

	@Override
	public List<NetCDF> read(String fileName) {
		logger.info("read(" + fileName + ")");
		List<NetCDF> netCDFList = this.netCDFMap.get(fileName);
		if (netCDFList == null) {
			MemoryController.log();
			netCDFList = new ArrayList<>();
			NetcdfFile dataFile = null;
			try {
				dataFile = NetcdfFile.open(fileName, null);
				Variable latVar = dataFile.findVariable("latitude");
				Variable lonVar = dataFile.findVariable("longitude");
				Variable level = dataFile.findVariable("level");
				Variable time = dataFile.findVariable("time");
				Variable uVar = dataFile.findVariable("u");
				Variable vVar = dataFile.findVariable("v");
				Attribute uScaleFactorAttribute = uVar.findAttribute("scale_factor");
				Attribute uAddOffsetAttribute = uVar.findAttribute("add_offset");
				Attribute vScaleFactorAttribute = vVar.findAttribute("scale_factor");
				Attribute vAddOffsetAttribute = vVar.findAttribute("add_offset");
				double uScaleFactor = (Double) uScaleFactorAttribute.getNumericValue();
				double uAddOffset = (Double) uAddOffsetAttribute.getNumericValue();
				double vScaleFactor = (Double) vScaleFactorAttribute.getNumericValue();
				double vAddOffset = (Double) vAddOffsetAttribute.getNumericValue();
				int longitudeCount = (int) lonVar.getSize();
				int latitudeCount = (int) latVar.getSize();
				int levelCount = (int) level.getSize();
				int timeCount = (int) time.getSize();
				ArrayFloat.D1 latArray = (ArrayFloat.D1) latVar.read();
				ArrayFloat.D1 lonArray = (ArrayFloat.D1) lonVar.read();
				ArrayInt.D1 timeArray = (ArrayInt.D1) time.read();
				ArrayShort.D4 uArray = (ArrayShort.D4) uVar.read();
				ArrayShort.D4 vArray = (ArrayShort.D4) vVar.read();
				double intensity = 0;
				float intensitySum;
				float intensityAverage;
				float u = 0;
				float v = 0;
				NetCDF netCDF = new NetCDF();
				netCDF.type = this.dataType;
				netCDF.latArray = latArray;
				netCDF.lonArray = lonArray;
				netCDF.timeArray = timeArray;
				netCDF.variableCube = new ArrayFloat.D3(timeCount, latitudeCount, longitudeCount);
				for (int t = 0; t < timeCount; t++) {
					for (int j = 0; j < latitudeCount; j++) {
						for (int i = 0; i < longitudeCount; i++) {
							intensitySum = 0;
							for (int l = 0; l < levelCount; l++) {
								u = uArray.get(t, l, j, i);
								v = vArray.get(t, l, j, i);
								u *= uScaleFactor;
								u += uAddOffset;
								v *= vScaleFactor;
								v += vAddOffset;
								intensity = Math.sqrt((Math.pow(u, 2) + Math.pow(v, 2)));
								intensitySum += intensity;
							}
							intensityAverage = intensitySum / levelCount;
							netCDF.variableCube.set(t, j, i, intensityAverage);
						}
					}
				}
				dataFile.close();
				System.gc();
				netCDFList.add(netCDF);
				if(this.cache)
					this.netCDFMap.put(fileName,netCDFList);
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
		}
		return netCDFList;
	}
}
//frame.flag = true;
//Coordinate coordinate = new Coordinate();
//coordinate.calendar = frame.calendar;
//coordinate.latitude = latitude + 90;
//coordinate.longitude = longitude;
//coordinate.attribute.put(DataType.INTENSITY.toString(), intensityAverage);
//coordinate.flag = true;
//frame.coordinateList.add(coordinate);
//public static char seperator = File.separatorChar;
//public static String path = basePath + "prospero-data" + seperator + "ECMWF" + seperator + "File" + seperator
//		+ "Data" + seperator + "ERA" + seperator + "5" + seperator + "UV";
//short latitudeIndex = (short) latitude;
//short longitudeIndex = (short) longitude;
//if (frame.data.get(latitudeIndex+","+longitudeIndex) == null) {
//	frame.data.put(latitudeIndex+","+longitudeIndex, new Data());
//}
//frame.data.get(latitudeIndex+","+longitudeIndex).map.put(DataType.INTENSITY, intensityAverage);
//@Override
//public List<Frame> frameMapGet(int y, int m) {
//	if (this.frameMap == null)
//		this.frameMap = new HashMap<>();
//	List<Frame> eList = this.frameMap.get(y + "" + m);
//	if (eList == null) {
//		eList = (List<Frame>)this.read(y, m);
//		if (eList != null) {
//			this.frameMap.put(y + "" + m, eList);
//		} else {
//			eList = new ArrayList<>();
//		}
//	}
//	eList = new ArrayList<>(eList);
//	return eList;
//}
//@Override 
//public Object get(Calendar calendar) {
//	return this.frameMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}

//@Override
//public Object get(Calendar calendar) {
//	return this.frameMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}
