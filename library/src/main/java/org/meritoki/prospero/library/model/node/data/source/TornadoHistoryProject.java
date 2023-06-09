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
import java.util.LinkedList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;

/**
 * Fix Time representation loaded into coordinate
 * @author jorodriguez
 *
 */
public class TornadoHistoryProject extends Source {

//	public List<Event> eventList;
	
	public TornadoHistoryProject() {
		super();
		this.setRelativePath("TornadoHistoryProject");
		
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
		Time time;
		if (fileArray != null) {
			for (File f : fileArray) {
				if (f.isFile()) {
					try {
						BufferedReader inputStream = null;
						inputStream = new BufferedReader(new FileReader(f));
						String line;
						String labels = inputStream.readLine();
						String[] labelArray = labels.split(",");
						String[] values = null;
						Event event;
						Coordinate coordinate;
						while ((line = inputStream.readLine()) != null) {
							values = line.split(",");
							if (!values[27].equals("-") && !values[28].equals("-")) {
								event = new Event();
								coordinate = new Coordinate();
								coordinate.latitude = Double.parseDouble(values[27]);
								coordinate.longitude = Double.parseDouble(values[28]);
//								time = new Time(values[2] + " " + values[3]);
//								coordinate.calendar = time.calendar;
								event.coordinateList.add(coordinate);
								if (!values[29].equals("-") && !values[30].equals("-")) {
									coordinate = new Coordinate();
									coordinate.latitude = Double.parseDouble(values[29]);
									coordinate.longitude = Double.parseDouble(values[30]);
//									time = new Time(values[2] + " " + values[3]);
//									coordinate.calendar = time.calendar;
									event.coordinateList.add(coordinate);
								}
								event.attribute.put(labelArray[8], values[8]);
								event.attribute.put(labelArray[9], values[9]);
								event.attribute.put(labelArray[10], values[10]);
								event.attribute.put(labelArray[11], values[11]);
								event.attribute.put(labelArray[12], values[12]);
								event.attribute.put(labelArray[13], values[13]);
								event.attribute.put(labelArray[25], values[25]);
								eventList.add(event);
							}
						}
						inputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
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
//String path = basePath+"prospero-data/TornadoHistoryProject";
//@Override
//public Object get() {
//	if(this.eventList == null) {
//		this.eventList = read();
//	}
//	return this.eventList;
//}

//@Override
//public Object get() {
//	return this.eventList;
//}
