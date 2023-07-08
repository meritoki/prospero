package org.meritoki.prospero.library.model.node.data.source;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.ERA5Event;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.utn.library.stacker.model.Collection;
import org.utn.library.stacker.model.Dot;
import org.utn.library.stacker.model.Stack;
import org.utn.library.stacker.model.Track;

import com.meritoki.library.controller.memory.MemoryController;
import com.meritoki.library.controller.node.NodeController;

public class UTNStackerEvent extends Source {
	
	static Logger logger = LoggerFactory.getLogger(CycloneUTNERA5.class.getName());
	private final String merged = "F339A7D11BBFA9F1EF71B466A94895F6";
	private final int startYear = 1979;
	private final int endYear = 2019;
	private final Time startTime = new Time(1979,1,1,-1,-1,-1);
	private final Time endTime = new Time(2019,12,31,-1,-1,-1);
	public static String extension = "json";
	public String defaultTimeFormat = "yyyy-MM-dd HH:mm:ss";

	public UTNStackerEvent() {
		super();
//		this.single = true;
//		this.setPressureArray(ERA5Event.pressureArray);
//		this.setPrefix(merged);
//		this.setRelativePath("UTN" + seperator + "File" + seperator + "Data" + seperator + "Cyclone" + seperator + "202103");
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

//	public List<Event> read(int year, int month) throws Exception {
//		logger.info("read(" + year + "," + month + ")");
////		String yearMonth = year + "" + String.format("%02d", month) + "01";
////		if("m".equals(this.order)) {
////			this.setPrefix(merged);
////		} else {
////			this.setPrefix(this.getPressureString());
////		}
////		List<Event> eventList = this.read(new File(this.getFilePath(yearMonth + seperator + "stack" + seperator
////				+ "collection" + seperator + this.getPrefix() + "-" + yearMonth + "." + extension)));
////		Calendar calendar;
////		Integer m = null;
////		Integer y = null;
////		ListIterator<Event> eventIterator = eventList.listIterator();
////		while (eventIterator.hasNext()) {
////			Event e = eventIterator.next();
////			calendar = e.getStartCalendar();
////			m = calendar.get(Calendar.MONTH) + 1;
////			y = calendar.get(Calendar.YEAR);
////			if (year != y || month != m) {
//////				logger.info("read(" + year + "," + month + ") y="+year+" m="+m);
////				eventIterator.remove();
////			}
////		}
//		return eventList;
//	}

	public List<Event> read(File file) throws Exception {
		if (logger.isDebugEnabled()) {
			MemoryController.log();
			logger.debug("read(" + file + ")");
		}
		List<Event> eventList = new ArrayList<>();
		Object object = NodeController.openJson(file, Collection.class);
		if (object != null) {
			Collection collection = (Collection) object;
			Map<Integer, Stack> stackMap = collection.stackMap;
			Map<Integer, Track> trackMap;
			Map<Integer, Dot> dotMap;
			for (Entry<Integer, Stack> stackEntry : stackMap.entrySet()) {
				if (!Thread.interrupted()) {
					int id = stackEntry.getValue().id;
//					String date = stackEntry.getValue().getFirstDot().startDate;
					List<Coordinate> coordinateList = new ArrayList<Coordinate>();
					trackMap = stackEntry.getValue().trackMap;
					for (Entry<Integer, Track> trackEntry : trackMap.entrySet()) {
						if (!Thread.interrupted()) {
							dotMap = trackEntry.getValue().dotMap;
							for (Entry<Integer, Dot> dotEntry : dotMap.entrySet()) {
								if (!Thread.interrupted()) {
									Coordinate point = new Coordinate();
									double latitude = dotEntry.getValue().lat;
									double longitude = dotEntry.getValue().lon;
									point.calendar = this.getCalendar(dotEntry.getValue().startDate,
											dotEntry.getValue().frame);
									int pressure = dotEntry.getValue().gph;
									float vorticity = (float) (dotEntry.getValue().module * Math.pow(10.0, -5.0) * -1);
									point.latitude = (float) latitude;
									if (longitude < 180) {
										point.longitude = (float) longitude;
									} else {
										point.longitude = (float) (longitude - 360);
									}
									point.attribute.put("pressure", pressure);
									point.attribute.put("vorticity", vorticity);
									coordinateList.add(point);
								} else {
									throw new InterruptedException();
								}
							}
						} else {
							throw new InterruptedException();
						}
					}
					CycloneEvent event = new ERA5Event(coordinateList);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					event.id = sdf.format(event.getStartCalendar().getTime()) + "-"
							+ sdf.format(event.getEndCalendar().getTime()) + id;
					eventList.add(event);
				} else {
					throw new InterruptedException();
				}
			}

		}
		return eventList;
	}

	public Calendar getCalendar(String time, int index) {
		Date dateB = Time.getDate(time, "yyyyMMdd");
		Calendar calendar = this.getCalendar(dateB);
		int product = index * 6;
		calendar.add(Calendar.HOUR_OF_DAY, (product));
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
//		System.out.println(hour);
//		if(hour %2 == 0) {
//			hour++;
//		}
		calendar.set(Calendar.HOUR_OF_DAY, hour);
//		System.out.println("getCalendar("+time+","+index+") calendar="+calendar.getTime());
		return calendar;
	}

	public Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(this.timeZone));
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}

}
