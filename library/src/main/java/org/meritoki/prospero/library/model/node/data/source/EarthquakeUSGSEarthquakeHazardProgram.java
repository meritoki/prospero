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
import java.io.FileReader;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;

public class EarthquakeUSGSEarthquakeHazardProgram extends Source {

//	public String path = this.basePath+"prospero-data/USGS/EarthquakeHazardsProgram/";
	public List<Event> eventList;

	public EarthquakeUSGSEarthquakeHazardProgram() {
		super();
		this.setRelativePath("USGS"+seperator+"EarthquakeHazardsProgram"+seperator);
	}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("eventList",this.read());
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}
	


	public List<Event> read() {
		File file = new File(this.getPath());
		File[] fileArray = file.listFiles();
		List<Event> eventList = new LinkedList<Event>();
		if (fileArray != null) {
			for (File f : fileArray) {
				if (f.isFile()) {
					BufferedReader inputStream = null;
					try {
						inputStream = new BufferedReader(new FileReader(f));
						String line;
						line = inputStream.readLine();
						String[] labels = line.split(",");
						String[] values = null;
						Event event;
						Coordinate coordinate;
						while ((line = inputStream.readLine()) != null) {
							values = line.split(",");
							event = new Event();
							coordinate = new Coordinate();
							double latitude = 0;
							double longitude = 0;
							try {
								latitude = Double.parseDouble(values[20]);
								longitude = Double.parseDouble(values[21]);
							} catch (NumberFormatException e) {

							}
							if (latitude > 0 && longitude > 0) {
								coordinate.latitude = latitude;
								coordinate.longitude = longitude;
							}
							int year = 0;
							int month = 0;
							int day = 0;
							int hour = 0;
							int minute = 0;
							int second = 0;
							try {
								year = Integer.parseInt(values[2]);
								month = Integer.parseInt(values[3]);
								day = Integer.parseInt(values[4]);
								hour = Integer.parseInt(values[5]);
								minute = Integer.parseInt(values[6]);
								second = Integer.parseInt(values[7]);
								coordinate.calendar = new GregorianCalendar(year, month - 1, day, hour, minute, second);
							} catch (NumberFormatException e) {

							}
							event.coordinateList.add(coordinate);
							event.attribute.put(labels[9], values[9]);
							event.attribute.put(labels[10], values[10]);
							event.attribute.put(labels[11], values[11]);
							event.attribute.put(labels[12], values[12]);
							event.attribute.put(labels[13], values[13]);
							event.attribute.put(labels[16], values[16]);
							eventList.add(event);
						}
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
//			logger.info("read() success!");
		} else {
//			logger.error("read() failure");
		}
		return eventList;
	}

}
