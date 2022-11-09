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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class GOESNNOAA extends Source {
	
	public static String path = basePath+"prospero-data" + seperator + "NOAA" + seperator +"GOES"+seperator+"13";
	
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
				loadList = this.read();
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("netCDFList", new ArrayList<NetCDF>((loadList)));
				query.objectList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
	}
	
	public List<NetCDF> read() {
		List<NetCDF> netCDFList = new ArrayList<>();
		String fileName = path + seperator + "goes13.2011.017.114519.BAND_04.nc";
		NetcdfFile dataFile = null;
		try {
			dataFile = NetcdfFile.open(fileName, null);
			Dimension xcDimension = dataFile.findDimension("xc");
			Dimension ycDimension = dataFile.findDimension("yc");
			Dimension timeDimension = dataFile.findDimension("time");
			Variable timeVar = dataFile.findVariable("time");
			Variable latVar = dataFile.findVariable("lat");
			Variable lonVar = dataFile.findVariable("lon");
			Variable dataVar = dataFile.findVariable("data");
			ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
			ArrayFloat.D2 latArray = (ArrayFloat.D2) latVar.read();
			ArrayFloat.D2 lonArray = (ArrayFloat.D2) lonVar.read();
			ArrayFloat.D3 dataArray = (ArrayFloat.D3) dataVar.read();
			NetCDF netCDF = new NetCDF();
			netCDF.type = DataType.BAND_4;
			netCDF.xDimension = xcDimension;
			netCDF.yDimension = ycDimension;
			netCDF.latMatrix = latArray;
			netCDF.lonMatrix = lonArray;
			netCDF.timeArray = timeArray;
			netCDF.variableArray = dataArray;
			dataFile.close();
			System.gc();
			netCDFList.add(netCDF);
		} catch (java.io.IOException e) {
			e.printStackTrace();
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
		return netCDFList;
	}
}
//Object[] objectArray = this.getDataArray(latArray,lonArray,dataArray,timeDimension.getLength(),xcDimension.getLength(),ycDimension.getLength());
//public Object[] getDataArray(ArrayFloat.D2 latitudeArray, ArrayFloat.D2 longitudeArray, ArrayFloat.D3 dataArray, int timeCount, int xCount, int yCount) {
//logger.info("getDataArray(...,...,...,"+timeCount+","+xCount+","+yCount+")");
//Object[] objectArray = new Object[3];
//ArrayFloat.D1 latArray = new ArrayFloat.D1(xCount*yCount);
//ArrayFloat.D1 lonArray = new ArrayFloat.D1(xCount*yCount);
//ArrayFloat.D3 dArray = new ArrayFloat.D3(timeCount, xCount*yCount, xCount*yCount);
//for (int t = 0; t < timeCount; t++) {
//	for (int x = 0; x < xCount; x++) {
//		for (int y = 0; y < yCount; y++) {
//			int i = (x * xCount)+y;
//			float latitude = latitudeArray.get(x,y);
//			float longitude = longitudeArray.get(x,y);
//			float data = dataArray.get(t, x, y);
//			latArray.set(i,latitude);
//			lonArray.set(i,longitude);
//			dArray.set(t, i, i, data);
//		}
//	}
//}
//objectArray[0]=latArray;
//objectArray[1]=lonArray;
//objectArray[2]=dArray;
//return objectArray;
//}