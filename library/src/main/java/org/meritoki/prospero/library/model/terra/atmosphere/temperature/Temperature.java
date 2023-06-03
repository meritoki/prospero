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
package org.meritoki.prospero.library.model.terra.atmosphere.temperature;

import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.atmosphere.Atmosphere;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Station;

public class Temperature extends Atmosphere {
	static Logger logger = LogManager.getLogger(Temperature.class.getName());
	
	public Temperature() {
		super("Temperature");
		this.sourceMap.put("WMO","2fb6b1b3-89f3-445f-9ffb-e0e5f9afcd94");
	}

	@Override
	public void load(Result result) {
		List<Station> stationList = result.getStationList();
		this.stationList.addAll(stationList);
	}
	
	@Override
	public void paint(Graphics graphics) throws Exception {
		if(this.load) { 
////			this.init(this.calendar);
//			if (coordinateList != null) {
//				for (Coordinate c : coordinateList) {
//					if (c != null) {
//						graphics.setColor(this.chroma.getColor((double)c.attribute.get("temperature"), this.min, this.max));
//						graphics.fillOval((int) ((c.point.x) * this.projection.scale),
//								(int) ((c.point.y) * this.projection.scale), (int) 4, (int) 4);
//					}
//				}
//			}
		}
	}
}
//public void init(Calendar calendar) throws Exception {
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.stationList = (List<Station>) this.data.query(sourceUUID, this.query);
//if (this.stationList != null) {
//	this.coordinateList = new ArrayList<>();
//	for (int i = 0; i < this.stationList.size(); i++) {
//		Station station = this.stationList.get(i);
//		List<Coordinate> coordinateList = station.coordinateList;
//		coordinateList = this.projection.getCoordinateList(0,
//				this.calendarCoordinateList(calendar,coordinateList));
//		this.coordinateList.addAll(coordinateList);
////		this.setCalendarCoordinateList(calendar,this.coordinateList);
//		this.initCoordinateMinMax("temperature",999.0);
//	}
//}
//}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
