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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class GravitationERA5Geopotential extends Source {

	protected Map<String, List<NetCDF>> netCDFMap = new HashMap<>();
//	public float earthRadius = 6371;
//	public static String path = basePath+"prospero-data" + seperator + "ECMWF" + seperator + "File" + seperator
//			+ "Data" + seperator + "ERA" + seperator +"5"+seperator +"Geopotential";
	public static String prefix = "geopotential_";
	public static String suffix = "_925_F128";
	public static String extension = "nc";
	public int startYear = 1979;
	public int endYear = 2019;
	public boolean test = false;
	public ArrayFloat.D3 zArray;
	
	public GravitationERA5Geopotential() {
		super();
		this.setRelativePath("ECMWF"+seperator+"File"+seperator+"Data"+seperator+"ERA"+seperator+"Geopotential"+seperator);
		
	}

	
	@Override
	public void query(Query query) throws Exception {
		this.intervalList = query.getIntervalList(this.getStartYear(), this.getEndYear());
		if (this.intervalList != null) {
			for (Interval i : this.intervalList) {
				this.load(query, i);
			}
			query.objectListAdd(new Result(Mode.COMPLETE));
		}
	}
	
	public void load(Query query, Interval interval) throws Exception {
		List<Time> timeList = Time.getTimeList(interval);
		List<NetCDF> loadList;
		for(Time time: timeList) {
			if (!Thread.interrupted()) {
				loadList = this.read(time.year, time.month);
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("netCDFList", new ArrayList<NetCDF>((loadList)));
				query.objectList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
	}
	
	public List<NetCDF> read(int year, int month) throws Exception {
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
			Variable zVar = dataFile.findVariable("z");
			Attribute scaleFactorAttribute = zVar.findAttribute("scale_factor");
			Attribute addOffsetAttribute = zVar.findAttribute("add_offset");
			double scaleFactor = (Double) scaleFactorAttribute.getNumericValue();
			double addOffset = (Double) addOffsetAttribute.getNumericValue();
			int longitudeCount = (int) longitudeVar.getSize();
			int latitudeCount = (int) latitudeVar.getSize();
			int timeCount = (int) timeVar.getSize();
			ArrayFloat.D1 latArray = (ArrayFloat.D1) latitudeVar.read();
			ArrayFloat.D1 lonArray = (ArrayFloat.D1) longitudeVar.read();
			ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
			ArrayShort.D3 zArray = (ArrayShort.D3) zVar.read();
			this.zArray = this.getZArray(zArray, timeCount, latitudeCount, longitudeCount, scaleFactor,
					addOffset);
			NetCDF netCDF = new NetCDF();
//			netCDF.continent = this.getContinent(scaleFactor, addOffset);
			netCDF.type = DataType.GEOPOTENTIAL;
			netCDF.latArray = latArray;
			netCDF.lonArray = lonArray;
			netCDF.timeArray = timeArray;
			netCDF.variableArray = this.zArray;
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
	
	public ArrayFloat.D3 getZArray(ArrayShort.D3 zArray, int timeCount, int latitudeCount, int longitudeCount,
			double scaleFactor, double addOffset) {
		ArrayFloat.D3 newMSLArray = new ArrayFloat.D3(timeCount, latitudeCount, longitudeCount);
		for (int t = 0; t < timeCount; t++) {
			for (int lat = 0; lat < latitudeCount; lat++) {
				for (int lon = 0; lon < longitudeCount; lon++) {
					float z = zArray.get(t, lat, lon);
					z *= scaleFactor;
					z += addOffset;
					newMSLArray.set(t, lat, lon, z);
				}
			}
		}
		return newMSLArray;
	}
}
