package org.meritoki.prospero.library.model.data.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Station;

public class AtmosphereWMO extends Source {
	static Logger logger = LogManager.getLogger(VolcanicNOAA.class.getName());
	public String path = this.basePath+"prospero-data/WMO/Station/";
//	public List<Station> stationList;

	public static void main(String[] args) {
		AtmosphereWMO a = new AtmosphereWMO();
		a.read();
	}

	public AtmosphereWMO() {
	}

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("stationList",this.read());
		result.mode = Mode.LOAD;
		query.outputList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.outputList.add(result);
	}
	


	public List<Station> read() {
		File directory = new File(this.path);
		List<Station> stationList = new ArrayList<>();
		for (File subdirectory : directory.listFiles()) {
			if (subdirectory.isDirectory()) {
//				System.out.println(subdirectory);
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
//@Override
//public Object get() {
//	if (this.stationList == null) {
//		this.stationList = read();
//	}
//	return this.stationList;
//}
