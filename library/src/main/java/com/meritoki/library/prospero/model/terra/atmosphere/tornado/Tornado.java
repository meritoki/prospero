package com.meritoki.library.prospero.model.terra.atmosphere.tornado;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.meritoki.library.prospero.model.grid.Grid;
import com.meritoki.library.prospero.model.unit.Event;
import com.meritoki.library.prospero.model.unit.Result;

public class Tornado extends Grid {
	static Logger logger = LogManager.getLogger(Tornado.class.getName());

	public Tornado() {
		super("Tornado");
		this.sourceMap.put("Tornado History Project", "ff6e87b8-b8d1-452c-944a-c77d2e971db2");
	}
	
	@Override
	public void load(Result result) {
		List<Event> eventList = (List<Event>) result.map.get("eventList");
		if (eventList.size() == 0) {
			logger.warn("loadResult(...) eventList.size() == 0");
		}
		this.eventList = (eventList);
	}
}
//@Override
//public void paint(Graphics graphics) throws Exception {
//	if(this.load) {
//		if (this.eventList != null) {
//			for (Event event : this.eventList) {
//				if(event.containsCalendar(this.calendar)) {
//					List<Coordinate> coordinateList = this.projection.getCoordinateList(0, event.coordinateList);
//					if (coordinateList != null) {
//						for (Coordinate c : coordinateList) {
//							if (c != null) {
//								graphics.setColor(Color.BLUE);
//								graphics.fillOval((int) ((c.point.x) * this.projection.scale),
//										(int) ((c.point.y) * this.projection.scale), (int) 4, (int) 4);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.eventList = (List<Event>) this.data.query(sourceUUID, this.query);
//public void load() {
//if (this.load) {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.data.load(sourceUUID);
//}
//}
