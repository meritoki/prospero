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
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayShort;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class LithosphereGEBCO extends Source {
	static Logger logger = LoggerFactory.getLogger(LithosphereGEBCO.class.getName());
	public String downloadURL = "https://www.bodc.ac.uk/data/open_download/gebco/gebco_2023_sub_ice_topo/zip/";
	public DataType dataType = DataType.ELEVATION;

	public LithosphereGEBCO() {
		super();

		this.setRelativePath("GEBCO" + seperator + "RN-8098_1510354754870" + seperator);
		this.setFileName("GEBCO_2014_2D.nc");

	}

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("netCDFList", this.read());
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}

	public List<NetCDF> read() {
		logger.info("read()");
		NetcdfFile dataFile = null;
		List<NetCDF> netCDFList = new ArrayList<>();
		try {
			dataFile = NetcdfFile.open(this.getFilePath(), null);
			Variable latVar = dataFile.findVariable("lat");
			Variable lonVar = dataFile.findVariable("lon");
			Variable elevationVar = dataFile.findVariable("elevation");
			int longitudeCount = (int) lonVar.getSize();
			int latitudeCount = (int) latVar.getSize();
			ArrayDouble.D1 latArray = (ArrayDouble.D1) latVar.read();
			ArrayDouble.D1 lonArray = (ArrayDouble.D1) lonVar.read();
			ArrayShort.D2 elevationArray = (ArrayShort.D2) elevationVar.read();
			NetCDF netCDF = new NetCDF();
			netCDF.type = this.dataType;
			netCDF.latArray = new ArrayFloat.D1(latitudeCount);
			netCDF.lonArray = new ArrayFloat.D1(longitudeCount);
			netCDF.variableMatrix = new ArrayFloat.D2(latitudeCount, longitudeCount);
			for (int i = 0; i < latitudeCount; i++) {
				netCDF.latArray.set(i, (float) latArray.get(i));
				for (int j = 0; j < longitudeCount; j++) {
					netCDF.lonArray.set(j, (float) lonArray.get(j));
					netCDF.variableMatrix.set(i, j, elevationArray.get(i, j));
				}
			}
			netCDFList.add(netCDF);
			dataFile.close();
		} catch (java.io.IOException e) {
			logger.error("IOException " + e.getMessage());

		} finally {
			if (dataFile != null) {
				try {
					dataFile.close();
				} catch (IOException ioe) {
					logger.error("IOException " + ioe.getMessage());
				}
			}
		}
		return netCDFList;
	}
}
// this.list = this.box(-90,-180, 90, 180);
//public static NetcdfFileWriter netCDFFile;
//DecimalFormat df = new DecimalFormat("#.#");
//catch (InterruptedException e) {
//e.printStackTrace();
//}
//List<Coordinate> cList = new LinkedList<Coordinate>();
//List<Tile> tileList = new ArrayList<>();
//int elevationCount = (int) elevationVar.getSize();
//Coordinate c = null;
//System.out.println(latitudeCount);
//System.out.println(longitudeCount);
//public List<Coordinate> list;//Collections.synchronizedList(new ArrayList<>());
//public Map<Tile> map;
//public Thread thread;
//public int latitudeInterval = 120;
//public int longitudeInterval = 120;
//public static int longitudeCount = 0;
//public static int latitudeCount = 0;
//public static int elevationCount = 0;
//public static int timeCount = 0;
//public static int uCount = 0;
//public static int vCount = 0;
//public static String startDate = null;
//public static String fileName = basePath+"prospero-data/GEBCO/RN-8098_1510354754870/GEBCO_2014_2D.nc";
//if (latA < latitude && lonA < longitude && latitude < latB && longitude < lonB) {
//latInt = (int) latitude;
//	lonInt = (int)longitude;
//latRemainder = (Double.isFinite(latitude%latInt))?new BigDecimal(Math.abs(latitude % latInt)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue():0;;// ();
//	lonRemainder = (Double.isFinite(longitude%lonInt))?new BigDecimal(Math.abs(longitude%lonInt)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue():0;//Math.abs(longitude%lonInt);
//	System.out.println("latR"+latRemainder);
//	System.out.println("lonR"+lonRemainder);
// Not reading all the data.
//latRemainder /= 0.0083;

//if ((int) latRemainder == 0) {//(int) latRemainder == 120 || 
//if ((int) latRemainder == 120 || (int) latRemainder == 90 || (int) latRemainder == 60
//		|| (int) latRemainder == 30 || (int) latRemainder == 0) {// dimension needs to be 0.0083
//		Tile tile = new Tile(latitude, longitude, 0.0083, elevation);
//		this.list.add(tile);

//	c = new Coordinate();
//	c.latitude = latitude;
//	c.longitude = longitude;
//	c.attribute.put("elevation", elevation);
//	cList.add(c);
//	Thread.sleep(100);

//}
//	Tile tile = new Tile(latitude, longitude, 0.0083, elevation);
//	tileList.add(tile);
//}
//@Override
//public Object get() {
//	if(list == null) {
//		this.list = this.box(-90,-180, 90, 180);
//	}
//	return this.list;
//}
//
//@Override
//public void run() {
//	this.box(-90, -180, 0, 180);
//}

//@Override
//public Object get() {
//
//	return this.list;//new ArrayList<>(this.list);
//}
