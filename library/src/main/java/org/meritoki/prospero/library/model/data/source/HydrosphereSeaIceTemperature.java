package org.meritoki.prospero.library.model.data.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.IntStream;

import org.meritoki.prospero.library.model.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Station;

import com.meritoki.library.controller.memory.MemoryController;

public class HydrosphereSeaIceTemperature extends Source {

	public String path = basePath + "prospero-data" + seperator + "Toth" + seperator + "hielo2020toths.txt";

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("stationList", this.read());
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}

	public List<Station> read() throws Exception {
		System.out.println("read() path=" + path);
		List<Station> stationList = new ArrayList<>();
//		try {
		FileReader input = new FileReader(path);
		BufferedReader bufferedeReader = new BufferedReader(input);
		String line = null;
		while ((line = bufferedeReader.readLine()) != null) {
//			MemoryController.log();
			line = line.trim();
			if (line.length() > 0) {
				String[] lineArray = line.split("\\s+");
//				System.out.println(lineArray.length);
				if (lineArray.length == 522) {
					double latitude = Double.parseDouble(lineArray[0]);
					double longitude = Double.parseDouble(lineArray[1]);
					String[] tempArray = IntStream.range(2, lineArray.length).mapToObj(i -> lineArray[i])
							.toArray(String[]::new);
					Station station = new Station();
					int year = 1979;
					for (int i = 0; i < tempArray.length; i++) {
						int month = (i % 11);
						if (month == 0) {
							year++;
						}
						Coordinate coordinate = new Coordinate();
						Calendar calendar = new GregorianCalendar(year, month + 1, 1);
						coordinate.attribute.put("density", Double.parseDouble(tempArray[i]));
						coordinate.calendar = calendar;
						coordinate.latitude = latitude;
						coordinate.longitude = longitude;
						station.coordinateList.add(coordinate);
					}
					stationList.add(station);
				}
			}
		}
//		} catch (IOException e) {
//			logger.error("read() Exception=" + e);
//			e.printStackTrace();
//		}
		return stationList;
	}

}
