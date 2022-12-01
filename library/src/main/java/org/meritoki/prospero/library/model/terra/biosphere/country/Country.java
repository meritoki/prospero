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
package org.meritoki.prospero.library.model.terra.biosphere.country;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.MultiPolygon;
import org.meritoki.prospero.library.model.terra.biosphere.Biosphere;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Polygon;
import org.meritoki.prospero.library.model.unit.Result;

public class Country extends Biosphere {

	static Logger logger = LogManager.getLogger(Country.class.getName());
	public Color color = Color.BLACK;
	private List<MultiPolygon> multiPolygonList;

	public Country() {
		super("Country");
		this.sourceMap.put("Natural Earth", "d6eb88d6-100c-4948-8fd1-5300b724ec2d");
	}

	@Override
	public void load(Result result) {
		Object object = result.map.get("multiPolygonList");
		if (object != null) {
			this.multiPolygonList = (List<MultiPolygon>) object;
			if (this.multiPolygonList.size() == 0) {
				logger.warn("load(...) this.multiPolygonList.size() == 0");
			}
		}
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
		if (this.load) {
			if (this.multiPolygonList != null) {
				graphics.setColor(this.color);
				if (this.getProjection().verticalList.size() > 0) {
					for (Double vertical : this.getProjection().verticalList) {
						List<Polygon> polygonList = this.getProjection().getPolygonList(vertical, this.multiPolygonList);
						for (Polygon p : polygonList) {
							if (p.pointList.size() > 0) {
								for (int i = 0; i < p.pointList.size(); i++) {
									Point c = p.pointList.get(i);
									graphics.drawLine((int) ((c.x) * this.getProjection().scale),
											(int) ((c.y) * this.getProjection().scale),
											(int) ((c.x) * this.getProjection().scale),
											(int) ((c.y) * this.getProjection().scale));
								}
							}
						}
					}
				} else {
					List<Polygon> polygonList = this.getProjection().getPolygonList(0, this.multiPolygonList);
					for (Polygon p : polygonList) {
						if (p.pointList.size() > 0) {
							for (int i = 0; i < p.pointList.size(); i++) {
								Point c = p.pointList.get(i);
								graphics.drawLine((int) ((c.x) * this.getProjection().scale),
										(int) ((c.y) * this.getProjection().scale),
										(int) ((c.x) * this.getProjection().scale),
										(int) ((c.y) * this.getProjection().scale));
							}
						}
					}
				}
			}
		}
	}
}
//for (Coordinate c : coordinateList) {
//graphics.drawLine((int) ((c.point.x) * this.projection.scale),
//		(int) ((c.point.y) * this.projection.scale), (int) ((c.point.x) * this.projection.scale),
//		(int) ((c.point.y) * this.projection.scale));
//}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.multiPolygonList = (List<MultiPolygon>) this.data.query(sourceUUID, this.query);
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
