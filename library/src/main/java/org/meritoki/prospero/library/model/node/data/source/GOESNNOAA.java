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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.solar.planet.earth.Earth;
import org.meritoki.prospero.library.model.solar.satellite.Satellite;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class GOESNNOAA extends GOESNOAA {

	static Logger logger = LoggerFactory.getLogger(GOESNNOAA.class.getName());
	public static String prefix = "OR_ABI-L2-MCMIPF-M6_G16_";
//	protected Map<String, List<NetCDF>> netCDFMap = new HashMap<>();
	public ArrayFloat.D2 latitudeArray;
	public ArrayFloat.D2 longitudeArray;
	public ArrayFloat.D2 dataMatrix;
	public ArrayDouble.D1 timeArray;
	public Earth earth = new Earth();
	public double longitude = -75.2;
	public double height = 35786023.0 + (earth.radius * 1000);
	public DataType dataType = DataType.CMI;
	public String variable = "CMI_C11";
	private final int startYear = 1979;
	private final int endYear = 2019;
	private final Time startTime = new Time(2019,6,1,0,-1,-1);
	private final Time endTime = new Time(2019,9,30,0,-1,-1);

	public GOESNNOAA() {
		super();
	}
	
	@Override
	public int getStartYear() {
		return this.startYear;
	}

	@Override
	public int getEndYear() {
		return this.endYear;
	}
	
	@Override
	public Time getStartTime() {
		return this.startTime;
	}

	@Override
	public Time getEndTime() {
		return this.endTime;
	}

	@Override
	public void query(Query query) throws Exception {
		this.intervalList = query.getIntervalList(this.getStartTime(), this.getEndTime());
		this.variable = (query.getChannel()!= null)?query.getChannel():"CMI_C08";
		if (this.intervalList != null) {
			for (Interval i : this.intervalList) {
				this.load(query, i);
			}
			query.objectListAdd(new Result(Mode.COMPLETE));
		}
	}

	public void load(Query query, Interval interval) throws Exception {
		List<Time> timeList = Time.getTimeList(2,interval);
		List<NetCDF> loadList;
		for (Time time : timeList) {
			if (!Thread.interrupted()) {
				loadList = this.read(time);
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("netCDFList", new ArrayList<NetCDF>((loadList)));
				query.objectList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
	}

	public List<NetCDF> read(Time time) throws Exception {
//		logger.info("read(" + time + ")");
		List<NetCDF> netCDFList = new ArrayList<>();
		String days = (time.day != -1) ? String.format("%03d", Time.getDayOfYear(time.year, time.month, time.day))
				: "001";
		String hours = (time.hour != -1) ? String.format("%02d", time.hour) : null;
		String minutes = (time.minute != -1) ? String.format("%02d", time.minute) : null;
		String seconds = (time.second != -1) ? String.format("%02d", time.second) : null;
		String fileName = "";
		if (dataType == DataType.CMI) {
			this.setRelativePath("NOAA" + seperator + "GOES" + seperator + "16");
			fileName = prefix + "s" + time.year + days + ((hours != null) ? hours : "00")
					+ ((minutes != null) ? minutes : "") + ((seconds != null && !seconds.equals("00")) ? seconds : "");
		} else if (dataType == DataType.BAND_4) {
			this.setRelativePath("NOAA" + seperator + "GOES" + seperator + "13");
			fileName = "goes13." + time.year + "." + days + "." + ((hours != null) ? hours : "00")
					+ ((minutes != null) ? minutes : "") + ((seconds != null && !seconds.equals("00")) ? seconds : "");
		}
		String pattern = "glob:{" + fileName + "}*.{nc}";
		logger.info("read(" + time + ") pattern=" + pattern);
//		List<NetCDF> list = this.netCDFMap.get(pattern);
//		if(list != null) {
//			netCDFList = list;
//		} else {
			List<String> matchList = this.getWildCardFileList(Paths.get(this.getPath()), pattern);
			for (String m : matchList) {
				netCDFList.addAll(this.read(this.getPath() + m));
			}
//			if(netCDFList.size() > 0) {
//				this.netCDFMap.put(pattern,netCDFList);
//			}
//		}
		return netCDFList;
	}

	public List<NetCDF> read(String fileName) {
		logger.info("read(" + fileName + ")");
		List<NetCDF> netCDFList = new ArrayList<>();
		if (this.dataType == DataType.BAND_4) {
			netCDFList = this.read13(fileName);
		} else if (this.dataType == DataType.CMI) {
			netCDFList = this.read16(fileName);
		}
		return netCDFList;
	}

	public List<NetCDF> read16(String fileName) {
		logger.info("read16(" + fileName + ")");
		List<NetCDF> netCDFList = new ArrayList<>();
		NetcdfFile dataFile = null;
		try {
			dataFile = NetcdfFile.open(fileName, null);
			Variable xVariable = dataFile.findVariable("x");
			Variable yVariable = dataFile.findVariable("y");
			Variable dataVar = dataFile.findVariable(variable);
			Variable timeVar = dataFile.findVariable("t");
			Variable nominalSatelliteSubpointLonVariable = dataFile.findVariable("nominal_satellite_subpoint_lon");
			Variable nominalSatelliteHeightVariable = dataFile.findVariable("nominal_satellite_height");
			Attribute xAddOffsetAttribute = xVariable.findAttribute("add_offset");
			Attribute xScaleFactorAttribute = xVariable.findAttribute("scale_factor");
			Attribute yAddOffsetAttribute = yVariable.findAttribute("add_offset");
			Attribute yScaleFactorAttribute = yVariable.findAttribute("scale_factor");
			Attribute dataAddOffsetAttribute = dataVar.findAttribute("add_offset");
			Attribute dataScaleFactorAttribute = dataVar.findAttribute("scale_factor");
			Float xScaleFactor = (Float) xScaleFactorAttribute.getNumericValue();
			Float xAddOffset = (Float) xAddOffsetAttribute.getNumericValue();
			Float yScaleFactor = (Float) yScaleFactorAttribute.getNumericValue();
			Float yAddOffset = (Float) yAddOffsetAttribute.getNumericValue();
			Float dataScaleFactor = (Float) dataScaleFactorAttribute.getNumericValue();
			Float dataAddOffset = (Float) dataAddOffsetAttribute.getNumericValue();
			ArrayShort.D1 xArray = (ArrayShort.D1) xVariable.read();
			ArrayShort.D1 yArray = (ArrayShort.D1) yVariable.read();
			ArrayShort.D2 dataArray = (ArrayShort.D2) dataVar.read();
			double t = (double) timeVar.readScalarDouble();
			float nominalSatelliteSubpointLon = nominalSatelliteSubpointLonVariable.readScalarFloat();
			float nominalSatelliteHeight = nominalSatelliteHeightVariable.readScalarFloat();
			int xSize = (int) xVariable.getSize();
			int ySize = (int) yVariable.getSize();
//			logger.info("read16(...) xSize=" + xSize);
//			logger.info("read16(...) ySize=" + ySize);
//			logger.info("read16(...) xAddOffset=" + xAddOffset);
//			logger.info("read16(...) xScaleFactor=" + xScaleFactor);
//			logger.info("read16(...) yAddOffset=" + yAddOffset);
//			logger.info("read16(...) yScaleFactor=" + yScaleFactor);
//			logger.info("read16(...) nominalSatelliteSubpointLon=" + nominalSatelliteSubpointLon);
//			logger.info("read16(...) nominalSatelliteHeight=" + nominalSatelliteHeight);
			this.latitudeArray = new ArrayFloat.D2(xSize,ySize);
			this.longitudeArray = new ArrayFloat.D2(xSize,ySize);
			this.dataMatrix = new ArrayFloat.D2(xSize, ySize);
			this.timeArray = new ArrayDouble.D1(1);
			this.timeArray.set(0, t);
			this.height = (double) (nominalSatelliteHeight * 1000) + (earth.radius * 1000);
			this.longitude = (double) nominalSatelliteSubpointLon;
//			logger.info("read16(...) height=" + height);
			NetCDF netCDF = new NetCDF();
			netCDF.type = DataType.CMI;
			for (int x = 0; x < xSize; x++) {
				short xShort = xArray.get(x);
				float X = (xShort * xScaleFactor) + xAddOffset;
				for (int y = 0; y < ySize; y++) {
					short yShort = yArray.get(y);
					float Y = (yShort * yScaleFactor) + yAddOffset;
					Coordinate c = Satellite.getCoordinate(earth, longitude, -Y, -X, height);
					if (c.is()) {
						this.latitudeArray.set(x, y, (float) c.latitude);
						this.longitudeArray.set(x, y, (float) c.longitude);
						short dataShort = dataArray.get(x, y);
						Float data = (Float) ((dataShort * dataScaleFactor) + dataAddOffset);
						this.dataMatrix.set(x, y, data);
					} else {
						this.latitudeArray.set(x, y, 0f);
						this.longitudeArray.set(x, y, 0f);
						this.dataMatrix.set(x, y, 0f);
					}
				}
			}
			netCDF.latMatrix = this.latitudeArray;
			netCDF.lonMatrix = this.longitudeArray;
			netCDF.timeDoubleArray = this.timeArray;
			netCDF.variableMatrix = this.dataMatrix;
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
					e.printStackTrace();
					logger.error("IOException " + e.getMessage());
				}
			}
		}
		return netCDFList;
	}

	public List<NetCDF> read13(String fileName) {
		logger.info("read13(" + fileName + ")");
		List<NetCDF> netCDFList = new ArrayList<>();
//		String fileName = this.getPath() + "goes13.2011.017.114519.BAND_04.nc";
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
//fileName = this.getPath()+"OR_ABI-L2-CMIPF-M3C01_G16_s20190011600366_e20190011611133_c20190011611205.nc";
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