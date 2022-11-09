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
package org.meritoki.prospero.library.model.solar.planet.earth;

import java.awt.Color;
import java.awt.Graphics;

import org.meritoki.prospero.library.model.node.cartography.Projection;
import org.meritoki.prospero.library.model.solar.moon.luna.Luna;
import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.solar.star.Star;
import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.unit.Space;

/**
 * Citation
 * <ol type="A">
 * <li><a href=
 * "https://nssdc.gsfc.nasa.gov/planetary/factsheet/earthfact.html">https://nssdc.gsfc.nasa.gov/planetary/factsheet/earthfact.html</a></li>
 * </ol>
 *
 */
public class Earth extends Planet {

	public Terra terra = new Terra();

	public Earth() {
		super("Earth");
		this.mass = 5.972e24;// Kilograms
		this.radius = 6378.137;// Kilometers
		this.a = this.radius;// Kilometers
		this.b = this.a;// Kilometers
		this.c = 6357.00;// Kilometers
		this.color = Color.BLUE;
		this.defaultScale = 16;
		this.terra.defaultScale = 7200000.0;
		// N
		this.longitudeOfAscendingNode[0] = 0;// Degrees
		this.longitudeOfAscendingNode[1] = 0;// Degrees
		// i
		this.inclination[0] = 0;// Degrees
		this.inclination[1] = 0;// Degrees
		// w
		this.argumentOfPeriapsis[0] = -282.9404;// Degrees
		this.argumentOfPeriapsis[1] = -4.70935E-5;// Degrees
		// a
		this.semiMajorAxis[0] = 1.000000;// Astronomical Unit
		this.semiMajorAxis[1] = 0;// Astronomical Unit
		// e
		this.eccentricity[0] = -0.016709;// Degrees
		this.eccentricity[1] = 1.151E-9;// Degrees
		// M
		this.meanAnomaly[0] = 356.0470;
		this.meanAnomaly[1] = 0.9856002585;
		this.orbitalPeriod = 365.256363004;//days
		this.angularVelocity = 7.292115053925690e-05;
		this.obliquity = 23.439292;//Degrees
		this.rotation = 23.9345;//Hours
		this.terra.color = this.color;
		this.addChild(this.terra);
		this.addChild(new Luna());
		this.setProjection(new Projection(this.a, this.b, this.c));
	}
	
	@Override
	public void updateSpace() {
		super.updateSpace();
		this.terra.setProjection(this.projection);
	}
	
	@Override
	public void setCenter(Space center) {
		super.setCenter(center);
		this.terra.setProjection(this.projection);
	}
	
	@Override
	public void setProjection(Projection projection) {
		super.setProjection(projection);
		this.terra.setProjection(projection);
	}
	
	@Override
	public void setScale(double scale) {
		super.setScale(scale);
		this.terra.setScale(scale);
	}
	
	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
	}
}
//this.terra.paint(graphics);
//this.terra.paint(graphics);
//List<Variable> nodeList = this.getChildren();
//for (Variable n : nodeList) {
//	if(n instanceof Spheroid) {
//		((Spheroid)n).setProjection(projection);
//	}
//}
//@Override
//public void updateSpace() {
//	super.updateSpace();
//	this.setPr
//	this.terra.projection = projection;
//}
//this.projection = new Projection(this.a,this.b,this.c);

//this.projection = 
//this.terra.projection = this.projection;
//Object root = this.getRoot();
//if (root instanceof Orbital) {
//	this.centroid = (Orbital)root;
//	this.space = this.getSpace(this.calendar, this.centroid);
//	this.space.add(this.center);
//	this.projection.setSpace(this.space);
//}
//this.projection.setScale(0.0000001);
//this.addChild(new Luna());
//@Override
//public void paint(Graphics graphics) throws Exception {
//	this.centroid = (Orbital)this.getRoot();
//	System.out.println(this.centroid);
//	super.paint(graphics);
//}
//public Terra terra = new Terra();
//this.addChildren(this.terra.getChildren());
//this.addChild(this.terra);
//@Override
//@JsonIgnore

//}