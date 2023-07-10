package org.meritoki.prospero.library.model.node.data.source;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.ERA5Event;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.utn.library.stacker.model.Collection;
import org.utn.library.stacker.model.Dot;
import org.utn.library.stacker.model.Stack;
import org.utn.library.stacker.model.Track;

import com.meritoki.library.controller.memory.MemoryController;
import com.meritoki.library.controller.node.NodeController;

public class UTNTrack extends Source {
	
	static Logger logger = LoggerFactory.getLogger(CycloneUTNERA5.class.getName());
	public String prefix;
	public String suffix;
	public String extension = "json";
	private Time startTime = new Time(1979,1,1,-1,-1,-1);
	private Time endTime = new Time(2019,12,31,-1,-1,-1);
	public List<Integer> pressureList = new ArrayList<>();
	public String defaultTimeFormat = "yyyy-MM-dd HH:mm:ss";

	public UTNTrack() {
		super();
		this.prefix = "138-128_";
		this.suffix = "_F128";
	}

	
	@Override
	public Time getStartTime() {
		return this.startTime;
	}

	@Override
	public Time getEndTime() {
		return this.endTime;
	}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query("+query+")");
		if(query.getBasePath() != null) {
			this.setBasePath(query.getBasePath());
		}
		if(query.getRelativePath() != null) {
			this.setRelativePath(query.getRelativePath());
		}
		this.intervalList = query.getIntervalList(this.getStartTime(), this.getEndTime());
		this.pressureList = query.getPressureList();
		if (this.intervalList != null) {
			for (Interval i : this.intervalList) {
				this.load(query, i);
			}
			query.objectListAdd(new Result(Mode.COMPLETE));
		}
	}
	
	public void load(Query query, Interval interval) throws Exception {
		List<Time> timeList = Time.getTimeList(2, interval);
		List<Event> loadList;
		for (Time time : timeList) {
			if (!Thread.interrupted()) {
				loadList = this.read(time);
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("eventList", new ArrayList<Event>((loadList)));
				query.objectList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
	}

	public List<Event> read(Time time) throws Exception {
		List<Event> eventList = new ArrayList<>();
		String start = time.year + String.format("%02d", time.month) + String.format("%02d", 1);
		String end = time.year + String.format("%02d", time.month)
				+ String.format("%02d", this.getYearMonthDays(time.year, time.month));
		String path = start + seperator + prefix + start + "-" + end + "_}*{" + suffix + seperator + "collection" + seperator;
		String fileName = "}*{" + "-"+start;
		if (this.pressureList.size() > 0) {
			for (int pressure : this.pressureList) {
				eventList.addAll(this.read(
						this.getFilePath(start + seperator + prefix + start + "-" + end + "_" + pressure + suffix + seperator + "collection" + seperator + pressure + "-" + start + "." + extension)));
			}
		} else {
			String pattern = "glob:{" + path + fileName + "}*.{"+extension+"}";
			logger.info("read(" + time + ") pattern=" + pattern);
			List<String> matchList = this.getWildCardFileList(Paths.get(this.getPath()), pattern);
			for (String m : matchList) {
				eventList.addAll(this.read(this.getPath() + m));
			}
		}

		return eventList;
	}

	public List<Event> read(String fileName) throws Exception {
		if (logger.isDebugEnabled()) {
			MemoryController.log();
			logger.debug("read(" + fileName + ")");
		}
		List<Event> eventList = new ArrayList<>();
		Object object = NodeController.openJson(new File(fileName), Collection.class);
		if (object != null) {
			Collection collection = (Collection) object;
			Map<Integer, Stack> stackMap = collection.stackMap;
			Map<Integer, Track> trackMap;
			Map<Integer, Dot> dotMap;
			for (Entry<Integer, Stack> stackEntry : stackMap.entrySet()) {
				if (!Thread.interrupted()) {
					int id = stackEntry.getValue().id;
					List<Coordinate> coordinateList = new ArrayList<Coordinate>();
					trackMap = stackEntry.getValue().trackMap;
					for (Entry<Integer, Track> trackEntry : trackMap.entrySet()) {
						if (!Thread.interrupted()) {
							dotMap = trackEntry.getValue().dotMap;
							for (Entry<Integer, Dot> dotEntry : dotMap.entrySet()) {
								if (!Thread.interrupted()) {
									Coordinate coordinate = new Coordinate();
									double latitude = dotEntry.getValue().lat;
									double longitude = dotEntry.getValue().lon;
									coordinate.calendar = this.getCalendar(dotEntry.getValue().startDate,
											dotEntry.getValue().frame);
									int pressure = dotEntry.getValue().gph;
									float vorticity = (float) (dotEntry.getValue().module * Math.pow(10.0, -5.0) * -1);
									coordinate.latitude = (float) latitude;
									if (longitude < 180) {
										coordinate.longitude = (float) longitude;
									} else {
										coordinate.longitude = (float) (longitude - 360);
									}
									coordinate.attribute.put("pressure", pressure);
									coordinate.attribute.put("vorticity", vorticity);
									coordinateList.add(coordinate);
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
		calendar.set(Calendar.HOUR_OF_DAY, hour);
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
