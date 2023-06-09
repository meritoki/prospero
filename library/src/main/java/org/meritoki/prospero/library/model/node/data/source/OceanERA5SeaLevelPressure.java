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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class OceanERA5SeaLevelPressure extends OceanSource {

	static Logger logger = LoggerFactory.getLogger(OceanERAInterimZeroTwoFive.class.getName());
//	public static String path = basePath + "prospero-data" + seperator + "ECMWF" + seperator + "File" + seperator
//			+ "Data" + seperator + "ERA" + seperator + "5" + seperator + "SeaLevelPressure";
	public static String prefix = "mean_sea_level_pressure_";
	public static String suffix = "_F128";
	public static String extension = "nc";
	public int startYear = 2001;
	public int endYear = 2017;
	public boolean test = false;
	public ArrayFloat.D3 mslArray;

	public OceanERA5SeaLevelPressure() {
		super();
		this.setRelativePath("ECMWF" + seperator + "File" + seperator + "Data" + seperator + "ERA" + seperator + "5"
				+ seperator + "SeaLevelPressure" + seperator);
	}

	public List<NetCDF> read(int year, int month) {
		logger.info("read(" + year + "," + month + ")");
		MemoryController.log();
		List<NetCDF> netCDFList = new ArrayList<>();
		String start = year + String.format("%02d", month) + String.format("%02d", 1);
		String stop = year + String.format("%02d", month) + String.format("%02d", this.getYearMonthDays(year, month));
		String fileName = this.getFilePath(prefix + start + "-" + stop + suffix + "." + extension);
		NetcdfFile dataFile = null;
		try {
			dataFile = NetcdfFile.open(fileName, null);
			Variable latitudeVar = dataFile.findVariable("latitude");
			Variable longitudeVar = dataFile.findVariable("longitude");
			Variable timeVar = dataFile.findVariable("time");
			Variable mslVar = dataFile.findVariable("msl");
			Attribute scaleFactorAttribute = mslVar.findAttribute("scale_factor");
			Attribute addOffsetAttribute = mslVar.findAttribute("add_offset");
			double scaleFactor = (Double) scaleFactorAttribute.getNumericValue();
			double addOffset = (Double) addOffsetAttribute.getNumericValue();
			int longitudeCount = (int) longitudeVar.getSize();
			int latitudeCount = (int) latitudeVar.getSize();
			int timeCount = (int) timeVar.getSize();
			ArrayFloat.D1 latArray = (ArrayFloat.D1) latitudeVar.read();
			ArrayFloat.D1 lonArray = (ArrayFloat.D1) longitudeVar.read();
			ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
			ArrayShort.D3 mslArray = (ArrayShort.D3) mslVar.read();
			this.mslArray = this.getMSLArray(mslArray, timeCount, latitudeCount, longitudeCount, scaleFactor,
					addOffset);
			NetCDF netCDF = new NetCDF();
			netCDF.continent = this.getContinent(scaleFactor, addOffset);
			netCDF.type = DataType.MSL;
			netCDF.latArray = latArray;
			netCDF.lonArray = lonArray;
			netCDF.timeArray = timeArray;
			netCDF.variableArray = this.mslArray;
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

}
