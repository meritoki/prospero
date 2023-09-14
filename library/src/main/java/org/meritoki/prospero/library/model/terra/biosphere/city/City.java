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
package org.meritoki.prospero.library.model.terra.biosphere.city;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.terra.biosphere.Biosphere;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class City extends Biosphere {

	static Logger logger = LoggerFactory.getLogger(City.class.getName());
	public Color color = Color.BLACK;
	public List<org.locationtech.jts.geom.Point> pointList;

	public City() {
		super("City");
		this.sourceMap.put("Natural Earth", "9bc2dd83-85c9-48fe-818f-f62db97c594a");
	}

	@Override
	public void load(Result result) {
		Object object = result.map.get("pointList");
		if (object != null) {
			this.pointList = (List<org.locationtech.jts.geom.Point>) object;
			if (this.pointList.size() == 0) {
				logger.warn("load(...) this.pointList.size() == 0");
			}
		}
	}
	
	@Override
	public void query(Query query) {
		if (this.mode == Mode.COMPLETE) {
			try {
				this.process();
			} catch (Exception e) {
				logger.warn("query(" + query + ") Exception " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			super.query(query);
		}
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
		if (this.load) {
			if (this.pointList != null) {
				graphics.setColor(Color.black);
				List<Point> coordinateList = this.getProjection().getPointList(0, this.pointList);
				if (coordinateList != null) {
					for (Point c : coordinateList) {
						if (c != null) {
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
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.pointList = (List<Point>) this.data.query(sourceUUID, this.query);
