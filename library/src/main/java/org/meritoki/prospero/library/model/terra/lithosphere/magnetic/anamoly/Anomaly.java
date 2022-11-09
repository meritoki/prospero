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
package org.meritoki.prospero.library.model.terra.lithosphere.magnetic.anamoly;

import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.lithosphere.Lithosphere;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;

public class Anomaly extends Lithosphere {
	
	static Logger logger = LogManager.getLogger(Anomaly.class.getName());
	
	public Anomaly() {
		super("Anomaly");
		this.sourceMap.put("NOAA EMAG", "3580b76c-b70d-4cdd-9a80-4feb57c72c77");
	}
	


	@SuppressWarnings("unchecked")
	@Override
	public void paint(Graphics graphics) throws Exception {
		if (this.load) {

			if (this.coordinateList != null) {
				this.initCoordinateMinMax("z",99999.0);
				List<Point> coordinateList = this.getProjection().getCoordinateList(0, this.coordinateList);
				if (coordinateList != null) {
					for (Point c : coordinateList) {
						if (c != null) {
							if (c.attribute.get("z") != null) {
								graphics.setColor(this.chroma.getColor((double) c.attribute.get("z"),
										this.min, this.max));
							}
							graphics.fillOval((int) ((c.x) * this.getProjection().scale),
									(int) ((c.y) * this.getProjection().scale), (int) 3, (int) 3);
						}
					}
				}
			}
		}
	}

	@Override
	public void initCoordinateMinMax(String variable, Double nullValue) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (Coordinate c : this.coordinateList) {
			double elevation = (double) c.attribute.get(variable);
			if(Math.abs(elevation) != nullValue && Math.abs(elevation) < 100) {
				if (elevation > max) {
					max = elevation;
				}
				if (elevation < min) {
					min = elevation;
				}
			}
		}
		this.min = min;
		this.max = max;
	}
}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.coordinateList = (List<Coordinate>) this.data.query(sourceUUID, this.query);
