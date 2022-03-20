package com.meritoki.library.prospero.model.terra.lithosphere.earthquake;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.meritoki.library.prospero.model.data.source.EarthquakeUSGSEarthquakeHazardProgram;
import com.meritoki.library.prospero.model.data.source.Source;
import com.meritoki.library.prospero.model.data.source.VolcanicNOAA;
import com.meritoki.library.prospero.model.grid.Grid;
import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import com.meritoki.library.prospero.model.unit.Coordinate;
import com.meritoki.library.prospero.model.unit.Event;
import com.meritoki.library.prospero.model.unit.Result;

public class Earthquake extends Grid {

	static Logger logger = LogManager.getLogger(Earthquake.class.getName());

	public Earthquake() {
		super("Earthquake");
		this.sourceMap.put("USGS Earthquake Hazard Program", "7cfb5b0a-0f8a-4e38-b0a9-5d50bf64a7b5");
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
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}

//@Override
//public void paint(Graphics graphics) throws Exception {
//	if(this.load) {
////		String sourceUUID = this.sourceMap.get(this.sourceKey);
////		this.eventList = (List<Event>) this.data.query(sourceUUID, this.query);
//		if (this.eventList != null) {
//			for (Event event : this.eventList) {
//				if(event.flag) {//containsCalendar(this.calendar)) {
//					List<Coordinate> coordinateList = this.projection.getCoordinateList(0, event.coordinateList);
//					if (coordinateList != null) {
//						for (Coordinate c : coordinateList) {
//							if (c != null) {
//								graphics.setColor(Color.GREEN);
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
