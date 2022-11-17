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
package org.meritoki.prospero.library.model.terra;

import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.Grid;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.node.cartography.Projection;
import org.meritoki.prospero.library.model.terra.atmosphere.Atmosphere;
import org.meritoki.prospero.library.model.terra.biosphere.Biosphere;
import org.meritoki.prospero.library.model.terra.hydrosphere.Hydrosphere;
import org.meritoki.prospero.library.model.terra.lithosphere.Lithosphere;

public class Terra extends Grid {
	static Logger logger = LogManager.getLogger(Terra.class.getName());
	
	public Terra() {
		super("Terra");
		this.addChild(new Lithosphere());
		this.addChild(new Hydrosphere());
		this.addChild(new Atmosphere());
		this.addChild(new Biosphere());
		this.defaultScale = 7200000.0;
	}
	
	public Terra(String name) {
		super(name);
		this.defaultScale = 7200000.0;
	}
	
	@Override
	public void setProjection(Projection projection) {
		super.setProjection(projection);
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			if (n instanceof Terra) {
				((Terra) n).setProjection(projection);
			}
		}
	}
	
	@Override
	public void setSelectedProjection(Projection projection) {
		super.setSelectedProjection(projection);
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			if (n instanceof Terra) {
				((Terra) n).setSelectedProjection(projection);
			}
		}
	}
	
	@Override
	public void paint(Graphics graphics) throws Exception {	
		super.paint(graphics);
	}
}
//@Override
//public Projection getProjection() {
//	Projection projection = super.projection;
//	logger.info(this.name+".getProjection() projection="+projection);
//	return projection;
//}
//this.defaultScale = 7200000.0;
//Object root = this.getRoot();
//if(this.name.equals("Terra") && root instanceof Earth) {
//	Earth variable = (Earth)root;
//	variable.paint(graphics);
//}
//public List<Variable> getList() {
//return null;
//}
//public static final double DEFAULT_SCALE = 256;
//public int latitudeInterval = 15;
//public int longitudeInterval = 30;
//this.addChild(new Luna());
//this.setScale(DEFAULT_SCALE);
//List<Variable> nodeList = this.getChildren();
//for(Variable n: nodeList) {
//	n.paint(graphics);
//}
//graphics.fillOval((int) ((c.point.x) * this.projection.scale), (int) ((c.point.y) * this.projection.scale), (int) 2, (int) 2);
//coordinateList = this.projection.getCoordinateList(0, this.solar.getCoordinateList("earth"));
//graphics.setColor(Color.BLACK);
//for (Coordinate c : coordinateList) {
//	graphics.drawString((String)c.attribute.map.get("label")+":"+(String)c.attribute.map.get("distance"),(int) ((c.point.x) * this.projection.scale), (int) ((c.point.y) * this.projection.scale));
//	graphics.fillOval((int) ((c.point.x) * this.projection.scale), (int) ((c.point.y) * this.projection.scale), (int) 2, (int) 2);
//}