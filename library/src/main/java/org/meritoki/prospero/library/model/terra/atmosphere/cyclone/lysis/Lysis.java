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
package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.lysis;

import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.density.Density;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Time;

public class Lysis extends Density {

	public Lysis() {
		super("Lysis");
	}
	
	@Override
	public List<Time> setCoordinateMatrix(int[][][] coordinateMatrix, List<Event> eventList) {
		List<Time> timeList = null;
		if (eventList != null) {
			timeList = new ArrayList<>();
			for (Event e : eventList) {
				if (e.flag) {
					Coordinate c = ((CycloneEvent) e).getEndCoordinate();
					if (c.flag) {
						int x = (int) ((c.latitude + this.latitude) * this.resolution);
						int y = (int) ((c.longitude + this.longitude / 2) * this.resolution) % this.longitude;
						int z = c.getMonth() - 1;
						coordinateMatrix[x][y][z]++;
						Time time = new Time(c.getYear(), c.getMonth(), -1, -1, -1, -1);
						if (!timeList.contains(time)) {
							timeList.add(time);
						}
					}
				}
			}
		}
		return timeList;
	}
}
//public void setEventList(List<Event> eventList) {
//for (Event e : eventList) {
//	if (e.flag) {
//		Coordinate p = ((CycloneEvent)e).getEndCoordinate();
//		if (p.flag) {
//			coordinateMatrix[(int) ((p.latitude + this.latitude)
//					* this.resolution)][(int) ((p.longitude + this.longitude / 2) * this.resolution)][p
//							.getMonth() - 1]++;
//			String date = p.getYear() + "-" + p.getMonth();
//			if (!this.dateList.contains(date)) {
//				this.dateList.add(date);
//			}
//		}
//	}
//}
//}

//@Override
//public void initCoordinateMatrix(List<Event> eventList) {
//String date;
//coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//dateList = new ArrayList<>();
//if (this.stackFlag) {
//	List<Integer> levelList = this.getEventLevelList(eventList);
//	for (Integer level : levelList) {
//		this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//		for (Event e : eventList) {
//			Coordinate p = e.getEndCoordinate();
//			if (p.flag && ((Integer) p.attribute.map.get("pressure")).equals(level)) {
//				int x = (int) ((p.latitude + this.latitude) * this.resolution);
//				int y = (int) ((p.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//				int z = p.getMonth() - 1;
//				this.coordinateMatrix[x][y][z]++;
//				date = p.getYear() + "-" + p.getMonth();
//				if (!this.dateList.contains(date)) {
//					this.dateList.add(date);
//				}
//			}
//		}
//		this.pointMatrixMap.put(level, coordinateMatrix);
//	}
//} else {
//	for (Event e : eventList) {
//		if (e.flag) {
//			Coordinate p = ((CycloneEvent)e).getEndCoordinate();
//			if (p.flag) {
//				coordinateMatrix[(int) ((p.latitude + this.latitude)
//						* this.resolution)][(int) ((p.longitude + this.longitude / 2) * this.resolution)][p
//								.getMonth() - 1]++;
//				date = p.getYear() + "-" + p.getMonth();
//				if (!this.dateList.contains(date)) {
//					this.dateList.add(date);
//				}
//			}
//		}
//	}
//}
//this.initMonthArray();
//this.initYearMap();
//}
