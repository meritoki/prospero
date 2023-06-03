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
package org.meritoki.prospero.library.model.terra.lithosphere;

import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.lithosphere.earthquake.Earthquake;
import org.meritoki.prospero.library.model.terra.lithosphere.tectonic.Tectonic;
import org.meritoki.prospero.library.model.terra.lithosphere.volcano.Volcanic;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Result;

public class Lithosphere extends Terra {
	
	static Logger logger = LogManager.getLogger(Lithosphere.class.getName());
	
	public Lithosphere() {
		super("Lithosphere");
//		this.addChild(new Magnetic());
		this.addChild(new Earthquake());
		this.addChild(new Tectonic());
		this.addChild(new Volcanic());
		this.sourceMap.put("GEBCO", "1aac29c0-e2f6-45e8-9921-c88397957795");
	}
	
	public Lithosphere(String name) {
		super(name);
	}
	
	@Override
	public void load(Result result) {
		Object object = result.map.get("coordinateList");
		if(object != null) {
			this.coordinateList = (List<Coordinate>)object;
			if (this.coordinateList.size() == 0) {
				logger.warn("load(...) this.coordinateList.size() == 0");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
		if (this.load) {
//			String sourceUUID = this.sourceMap.get(this.sourceKey);
//			this.coordinateList = (List<Coordinate>) this.data.get(sourceUUID, this.query);
			if (this.coordinateList != null) {
				this.initCoordinateMinMax("elevation", null);
				List<Point> coordinateList = this.projection.getCoordinateList(0, this.coordinateList);
				if (coordinateList != null) {
					for (Point c : coordinateList) {
						if (c != null) {
							if (c.attribute.get("elevation") != null) {
								graphics.setColor(this.chroma.getColor((double) c.attribute.get("elevation"),
										this.min, this.max));
							}
							graphics.fillOval((int) ((c.x) * this.projection.scale),
									(int) ((c.y) * this.projection.scale), (int) 5, (int) 5);
						}
					}
				}
			}
		}
//		List<Variable> nodeList = this.getChildren();
//		for(Variable n: nodeList) {
//			n.paint(graphics);
//		}
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
//public void init() throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	if (sourceUUID != null && !sourceUUID.equals("null")) {
//		Query query = null;
//		if (this.queryStack.size() > 0) {
//			query = this.queryStack.poll();
//		}
//		if (!this.query.equals(query)) {
//			Object object = this.data.get(sourceUUID, this.query, "cyclone");
//			this.coordinateList = (List<Coordinate>)object;
//		}
//	}
//}
