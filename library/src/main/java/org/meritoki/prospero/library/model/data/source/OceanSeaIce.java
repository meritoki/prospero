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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Frame;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;

public class OceanSeaIce extends Source {

	static Logger logger = LogManager.getLogger(OceanSeaIce.class.getName());
	public String path = basePath + "prospero-data" + seperator + "Toth" + seperator + "hielo2020toths.txt";

	private final int startYear = 1979;
	private final int endYear = 2019;

	@Override
	public int getStartYear() {
		return this.startYear;
	}

	@Override
	public int getEndYear() {
		return this.endYear;
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
		List<Frame> loadList;
		for(Time time: timeList) {
			if (!Thread.interrupted()) {
				loadList = this.read(time.year, time.month);
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("frameList", new ArrayList<Frame>((loadList)));
				query.objectList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
	}

	public List<Frame> read(int year, int month) throws Exception {
		logger.info("read("+year+","+month+")");
		List<Frame> frameList = new ArrayList<>();
		FileReader input = new FileReader(path);
		Frame frame = new Frame();
		Calendar calendar = new GregorianCalendar(year, month + 1, 1);
		frame.calendar = calendar;
		frame.flag = true;
		int y = year - 1979;
		int index = (12*y)+(month-1);
		index += 2;
		try (BufferedReader bufferedeReader = new BufferedReader(input)) {
			String line = null;
			while ((line = bufferedeReader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					String[] lineArray = line.split("\\s+");
					if (lineArray.length == 522) {
						double latitude = Double.parseDouble(lineArray[0]);
						double longitude = Double.parseDouble(lineArray[1]);
//						String[] tempArray = IntStream.range(2, lineArray.length).mapToObj(i -> lineArray[i])
//								.toArray(String[]::new);
						Coordinate coordinate = new Coordinate();
						coordinate.calendar = frame.calendar;
						coordinate.attribute.put("density", Double.parseDouble(lineArray[index]));
						coordinate.calendar = calendar;
						coordinate.latitude = latitude;
						coordinate.longitude = longitude;
						coordinate.flag = true;
						frame.coordinateList.add(coordinate);
					}
				}
			}
		}
		frameList.add(frame);
		return frameList;
	}
}

//@Override
//public void query(Query query) throws Exception {
//	logger.info("query(" + query + ")");
//	Result result = new Result();
//	result.map.put("stationList", this.read());
//	result.mode = Mode.LOAD;
//	query.objectList.add(result);
//	result = new Result();
//	result.mode = Mode.COMPLETE;
//	query.objectList.add(result);
//}

//public List<Station> read() throws Exception {
//	List<Station> stationList = new ArrayList<>();
//	FileReader input = new FileReader(path);
//	BufferedReader bufferedeReader = new BufferedReader(input);
//	String line = null;
//	while ((line = bufferedeReader.readLine()) != null) {
//		line = line.trim();
//		if (line.length() > 0) {
//			String[] lineArray = line.split("\\s+");
//			if (lineArray.length == 522) {
//				double latitude = Double.parseDouble(lineArray[0]);
//				double longitude = Double.parseDouble(lineArray[1]);
//				String[] tempArray = IntStream.range(2, lineArray.length).mapToObj(i -> lineArray[i])
//						.toArray(String[]::new);
//				Station station = new Station();
//				int year = 1979;
//				for (int i = 0; i < tempArray.length; i++) {
//					int month = (i % 11);
//					if (month == 0) {
//						year++;
//					}
//					Coordinate coordinate = new Coordinate();
//					Calendar calendar = new GregorianCalendar(year, month + 1, 1);
//					coordinate.attribute.put("density", Double.parseDouble(tempArray[i]));
//					coordinate.calendar = calendar;
//					coordinate.latitude = latitude;
//					coordinate.longitude = longitude;
//					station.coordinateList.add(coordinate);
//				}
//				stationList.add(station);
//			}
//		}
//	}
//	return stationList;
//}
