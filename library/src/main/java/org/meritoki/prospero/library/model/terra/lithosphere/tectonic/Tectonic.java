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
/**
 * https://github.com/fraxen/tectonicplates
 */
package org.meritoki.prospero.library.model.terra.lithosphere.tectonic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.locationtech.jts.geom.MultiLineString;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.terra.lithosphere.Lithosphere;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tectonic extends Lithosphere {

	static Logger logger = LoggerFactory.getLogger(Tectonic.class.getName());
	public Color color = Color.GRAY;
	private List<MultiLineString> multiLineStringList;

	public Tectonic() {
		super("Tectonic");
		this.sourceMap.put("Peter Bird", "8f6ef7b8-b8d1-452c-944a-c77d2e971db2");
	}
	
	@Override
	public void load(Result result) {
		this.multiLineStringList = result.getMultiLineStringList();
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
		if(this.load) { 
			if (this.multiLineStringList != null) {
				graphics.setColor(this.color);
				List<Point> coordinateList = this.getProjection().getMultiLineStringList(0, multiLineStringList);
				for (Point c : coordinateList) {
					graphics.fillOval((int) ((c.x) * this.getProjection().scale),
							(int) ((c.y) * this.getProjection().scale), (int) 2, (int) 2);
				}
			}
		}
	}
}
//Object object = result.map.get("multiLineStringList");
//if(object != null) {
//	this.multiLineStringList = (List<MultiLineString>)object;
//	if (this.multiLineStringList.size() == 0) {
//		logger.warn("load(...) this.multiPolygonList.size() == 0");
//	}
//}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.multiLineStringList = (List<MultiLineString>) this.data.query(sourceUUID, this.query);
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
