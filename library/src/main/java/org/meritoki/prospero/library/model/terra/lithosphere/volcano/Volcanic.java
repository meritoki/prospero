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
package org.meritoki.prospero.library.model.terra.lithosphere.volcano;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.Grid;

public class Volcanic extends Grid {

	static Logger logger = LogManager.getLogger(Volcanic.class.getName());

	public Volcanic() {
		super("Volcanic");
		this.sourceMap.put("NOAA", "495ae7cd-9781-4b56-a2d0-cd3f6b2e80e1");
	}
}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
//
//@Override
//public void paint(Graphics graphics) throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.eventList = (List<Event>) this.data.query(sourceUUID, this.query);
//	if (this.eventList != null) {
//		for (Event event : this.eventList) {
//			if (event.containsCalendar(this.calendar)) {
//				System.out.println("Event contains Calendar="+calendar);
//				List<Coordinate> coordinateList = this.projection.getCoordinateList(0, event.coordinateList);
//				if (coordinateList != null) {
//					for (Coordinate c : coordinateList) {
//						if (c != null) {
//							graphics.setColor(Color.RED);
//							graphics.fillOval((int) ((c.point.x) * this.projection.scale),
//									(int) ((c.point.y) * this.projection.scale), (int) 4, (int) 4);
//						}
//					}
//				}
//			}
//		}
//	}
//}