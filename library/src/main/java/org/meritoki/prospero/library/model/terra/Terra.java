/*
  * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meritoki.prospero.library.model.terra;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.solar.moon.luna.Luna;
import org.meritoki.prospero.library.model.solar.planet.earth.Earth;
import org.meritoki.prospero.library.model.solar.star.sun.Sun;
import org.meritoki.prospero.library.model.terra.atmosphere.Atmosphere;
import org.meritoki.prospero.library.model.terra.biosphere.Biosphere;
import org.meritoki.prospero.library.model.terra.hydrosphere.Hydrosphere;
import org.meritoki.prospero.library.model.terra.lithosphere.Lithosphere;
import org.meritoki.prospero.library.model.unit.Coordinate;

public class Terra extends Earth {
	static Logger logger = LogManager.getLogger(Terra.class.getName());
//	public static final double DEFAULT_SCALE = 256;
//	public int latitudeInterval = 15;
//	public int longitudeInterval = 30;
	
	public Terra() {
		super("Terra");
		this.paint = true;
//		this.addChild(new Lithosphere());
//		this.addChild(new Hydrosphere());
//		this.addChild(new Atmosphere());
//		this.addChild(new Biosphere());
		this.addChild(new Luna());
	}
	
	public Terra(String name) {
		super(name);
	}
	
	public List<Variable> getList() {
		return null;
	}
	
	@Override
	public void paint(Graphics graphics) throws Exception {		
		if(this.paint) {
			super.paint(graphics);
		}
	}
}
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