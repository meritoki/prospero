
package org.meritoki.prospero.library.model.data.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;

public class VolcanicNOAA extends Source {

	static Logger logger = LogManager.getLogger(VolcanicNOAA.class.getName());
	public String path = basePath+"prospero-data/NOAA/VOLCANIC/";


	public VolcanicNOAA() {}
	
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
		File file = new File(this.path);
		File[] fileArray = file.listFiles();
		List<Event> eventList = new LinkedList<Event>();
		if (fileArray != null) {
			for (File f : fileArray) {
				if (f.isFile()) {
					try {
						BufferedReader inputStream = new BufferedReader(new FileReader(f));
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
								latitude = Double.parseDouble(values[8]);
								longitude = Double.parseDouble(values[9]);
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
								year = Integer.parseInt(values[0]);
								month = Integer.parseInt(values[1]);
								day = Integer.parseInt(values[2]);
								hour = 0;
								minute = 0;
								second = 0;
								coordinate.calendar = new GregorianCalendar(year, month - 1, day, hour, minute, second);
							} catch (NumberFormatException e) {

							}
							event.coordinateList.add(coordinate);
							event.attribute.put(labels[9], values[9]);
							event.attribute.put(labels[10], values[10]);
							event.attribute.put(labels[11], values[11]);
							event.attribute.put(labels[12], values[12]);
							event.attribute.put(labels[13], values[13]);
							eventList.add(event);
						}
						inputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			logger.info("read() success!");
		} else {
			logger.error("read() failure");
		}
		return eventList;
	}
}
//public List<Event> eventList;
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
