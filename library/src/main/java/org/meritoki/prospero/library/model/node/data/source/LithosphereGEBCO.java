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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayShort;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class LithosphereGEBCO extends Source {
	static Logger logger = LoggerFactory.getLogger(LithosphereGEBCO.class.getName());
	public static int longitudeCount = 0;
	public static int latitudeCount = 0;
	public static int elevationCount = 0;
	public static int timeCount = 0;
	public static int uCount = 0;
	public static int vCount = 0;
	public static String startDate = null;
//	public static String fileName = basePath+"prospero-data/GEBCO/RN-8098_1510354754870/GEBCO_2014_2D.nc";
	public static NetcdfFileWriter netCDFFile;
//	public List<Coordinate> list;//Collections.synchronizedList(new ArrayList<>());
//	public Map<Tile> map;
	public Thread thread;
	public int latitudeInterval = 120;
	public int longitudeInterval = 120;

	public LithosphereGEBCO() {
		super();
		this.setRelativePath("GEBCO"+seperator+"RN-8098_1510354754870"+seperator);
		this.setFileName("GEBCO_2014_2D.nc");
		// this.list = this.box(-90,-180, 90, 180);
	}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("coordinateList",this.box(-90,-180, 90, 180));
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}

	public List<Coordinate> box(double latA, double lonA, double latB, double lonB) {
		NetcdfFile dataFile = null;
		List<Coordinate> cList = new LinkedList<Coordinate>();
		List<Tile> tileList = new ArrayList<>();
		DecimalFormat df = new DecimalFormat("#.#");
		try {
			dataFile = NetcdfFile.open(fileName, null);
			Variable latVar = dataFile.findVariable("lat");
			Variable lonVar = dataFile.findVariable("lon");
			Variable elevationVar = dataFile.findVariable("elevation");
			longitudeCount = (int) lonVar.getSize();
			latitudeCount = (int) latVar.getSize();
			elevationCount = (int) elevationVar.getSize();
			ArrayDouble.D1 latArray = (ArrayDouble.D1) latVar.read();
			ArrayDouble.D1 lonArray = (ArrayDouble.D1) lonVar.read();
			ArrayShort.D2 elevationArray = (ArrayShort.D2) elevationVar.read();
			double latitude = 0;
			double longitude = 0;
			double elevation = 0;
			int latInt = 0;
			int lonInt = 0;
			double latRemainder = 0;
			double lonRemainder = 0;
			double range = 0.;
			Coordinate c = null;
			System.out.println(latitudeCount);
			System.out.println(longitudeCount);
			for (int j = 0; j < latitudeCount; j+=this.latitudeInterval) {
				latitude = latArray.get(j);
//					System.out.println("latitude="+latitude);
				for (int i = 0; i < longitudeCount; i+=this.longitudeInterval) {
					longitude = lonArray.get(i);
//						System.out.println("longitude="+longitude);
					elevation = elevationArray.get(j, i);
					if (latA < latitude && lonA < longitude && latitude < latB && longitude < lonB) {
//						latInt = (int) latitude;
//							lonInt = (int)longitude;
//						latRemainder = (Double.isFinite(latitude%latInt))?new BigDecimal(Math.abs(latitude % latInt)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue():0;;// ();
//							lonRemainder = (Double.isFinite(longitude%lonInt))?new BigDecimal(Math.abs(longitude%lonInt)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue():0;//Math.abs(longitude%lonInt);
//							System.out.println("latR"+latRemainder);
//							System.out.println("lonR"+lonRemainder);
						// Not reading all the data.
//						latRemainder /= 0.0083;

//						if ((int) latRemainder == 0) {//(int) latRemainder == 120 || 
//						if ((int) latRemainder == 120 || (int) latRemainder == 90 || (int) latRemainder == 60
//								|| (int) latRemainder == 30 || (int) latRemainder == 0) {// dimension needs to be 0.0083
//								Tile tile = new Tile(latitude, longitude, 0.0083, elevation);
//								this.list.add(tile);

							c = new Coordinate();
							c.latitude = latitude;
							c.longitude = longitude;
							c.attribute.put("elevation", elevation);
							cList.add(c);
//							Thread.sleep(100);

//						}
//							Tile tile = new Tile(latitude, longitude, 0.0083, elevation);
//							tileList.add(tile);
					}
				}

			}
			dataFile.close();
		} catch (java.io.IOException e) {
			logger.error("IOException " + e.getMessage());

		} 
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		finally {
			if (dataFile != null) {
				try {
					dataFile.close();
				} catch (IOException ioe) {
					logger.error("IOException " + ioe.getMessage());
				}
			}
		}
		return cList;
	}
}
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
