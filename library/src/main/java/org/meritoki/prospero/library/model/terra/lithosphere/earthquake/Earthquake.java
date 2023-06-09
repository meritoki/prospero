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
package org.meritoki.prospero.library.model.terra.lithosphere.earthquake;

import java.util.List;

import org.meritoki.prospero.library.model.terra.lithosphere.Lithosphere;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Earthquake extends Lithosphere {

	static Logger logger = LoggerFactory.getLogger(Earthquake.class.getName());

	public Earthquake() {
		super("Earthquake");
		this.sourceMap.put("USGS Earthquake Hazard Program", "7cfb5b0a-0f8a-4e38-b0a9-5d50bf64a7b5");
	}
	
	@Override
	public void load(Result result) {
		List<Event> eventList = result.getEventList();
		this.eventList.addAll(eventList);
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
