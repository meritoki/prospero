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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereWMO extends Source {
	static Logger logger = LoggerFactory.getLogger(VolcanicNOAA.class.getName());

	public AtmosphereWMO() {
		super();
		this.setRelativePath("WMO"+seperator+ "Station"+seperator);
	}

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("stationList",this.read());
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}
	


	public List<Station> read() {
		File directory = new File(this.getPath());
		List<Station> stationList = new ArrayList<>();
		for (File subdirectory : directory.listFiles()) {
			if (subdirectory.isDirectory()) {
				Station station = new Station();
				double latitude = 0;
				double longitude = 0;
				String name;
				String country;
				File textFile = new File(subdirectory.getAbsolutePath() + File.separatorChar + "Median.txt");
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(textFile));
					String line;
					int index = 0;
					while ((line = br.readLine()) != null) {
						if (line.charAt(0) == '#') {
							switch(index) {
							case 1: {
//								System.out.println(line);								
								break;
							}
							case 2: {
								line = line.replace("# coordinates: ", "");
								String tmpLatitude = line.substring(0, line.indexOf("N")+2);
								line = line.replace(tmpLatitude, "");
								String tmpLongitude = line.substring(0, line.indexOf("E")+2);
								tmpLatitude = tmpLatitude.trim();
								tmpLongitude = tmpLongitude.trim();
								tmpLatitude = tmpLatitude.replace("N,", "");
								tmpLongitude = tmpLongitude.replace("E,", "");
								latitude = Double.parseDouble(tmpLatitude);
								longitude = Double.parseDouble(tmpLongitude);
								break;
							}
							case 3: {
//								System.out.println(line);
								break;
							}
							}
							index++;
						} else {
							String[] spaceArray = line.split(" ");
							List<String> dataList = new ArrayList<>();
							for(String s: spaceArray) {
								if(!s.isEmpty()) {
									dataList.add(s);
								}
							}
							int year = Integer.parseInt(dataList.remove(0));
							for(int i = 0; i< dataList.size(); i++) {
								Coordinate coordinate = new Coordinate();
								Calendar calendar = new GregorianCalendar(year,i,1);
								coordinate.attribute.put("temperature",Double.parseDouble(dataList.get(i)));
								coordinate.calendar = calendar;
//								System.out.println(coordinate.calendar.getTime());
								coordinate.latitude = latitude;
								coordinate.longitude = longitude;
								station.coordinateList.add(coordinate);
							}
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stationList.add(station);
			}
		}
		return stationList;
	}
}
//public String path = basePath+"prospero-data"+seperator+"WMO"+seperator+ "Station"+seperator;
//public static void main(String[] args) {
//	AtmosphereWMO a = new AtmosphereWMO();
//	a.read();
//}
//@Override
//public Object get() {
//	if (this.stationList == null) {
//		this.stationList = read();
//	}
//	return this.stationList;
//}
