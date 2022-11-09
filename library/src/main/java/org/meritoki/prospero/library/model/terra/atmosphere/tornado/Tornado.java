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
package org.meritoki.prospero.library.model.terra.atmosphere.tornado;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.atmosphere.Atmosphere;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Result;

public class Tornado extends Atmosphere {
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
