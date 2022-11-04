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
package org.meritoki.prospero.library.model.solar.star.sun;

import java.awt.Color;
import java.awt.Graphics;

import org.meritoki.prospero.library.model.helios.Helios;
import org.meritoki.prospero.library.model.solar.planet.earth.Earth;
import org.meritoki.prospero.library.model.solar.planet.jupiter.Jupiter;
import org.meritoki.prospero.library.model.solar.planet.mars.Mars;
import org.meritoki.prospero.library.model.solar.planet.mercury.Mercury;
import org.meritoki.prospero.library.model.solar.planet.neptune.Neptune;
import org.meritoki.prospero.library.model.solar.planet.saturn.Saturn;
import org.meritoki.prospero.library.model.solar.planet.uranus.Uranus;
import org.meritoki.prospero.library.model.solar.planet.venus.Venus;
import org.meritoki.prospero.library.model.solar.star.Star;
import org.meritoki.prospero.library.model.terra.cartography.Globe;
import org.meritoki.prospero.library.model.terra.cartography.Projection;
import org.meritoki.prospero.library.model.unit.Space;


/**
 * 
 * <a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet/sunfact.html">Reference</a>
 *
 */
public class Sun extends Star {
	
	public Helios helios = new Helios();
	
	public Sun(){
		super("Sun");
        this.mass = 1.9891e30;//Kilograms
        this.radius = 696342;// kilometers
        this.a = this.radius;//Kilometers
        this.b = this.a;//Kilometers
        this.c = this.a;//Kilometers
        this.color = Color.YELLOW;
        this.helios.color = this.color;
        this.defaultScale = 50000.0;
        this.helios.defaultScale = this.defaultScale;
        this.angularVelocity = 2.865329607243705e-06;
        this.rotation = 0.564263323;//Kilometers/Second
        this.setProjection(new Globe(this.a, this.b, this.c));
        this.addChild(helios);
        this.addChild(new Earth());
        this.addChild(new Jupiter());
        this.addChild(new Mars());
        this.addChild(new Mercury());
        this.addChild(new Neptune());
        this.addChild(new Saturn());
        this.addChild(new Uranus());
        this.addChild(new Venus());
        
	}
	
	@Override
	public void updateSpace() {
		super.updateSpace();
		this.helios.setProjection(this.projection);
	}
	
	@Override
	public void setCenter(Space center) {
		super.setCenter(center);
		this.helios.setProjection(this.projection);
	}
	
	@Override
	public void setProjection(Projection projection) {
		super.setProjection(projection);
		this.helios.setProjection(projection);
	}
	
	@Override
	public void setScale(double scale) {
		super.setScale(scale);
		this.helios.setScale(scale);
	}
	
	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
	}
}
//public Helios helios = new Helios();
//this.addChildren(this.helios.getChildren());
//public Sun(Calendar calendar) {
//this.name = "sun";
//this.mass = 1.9891e30;
//this.radius = 6.96342e5;
//this.color = Color.YELLOW;
//this.position = new Vector3D(0, 0, 0);
//this.calendar = calendar;
//this.angularVelocity = 2.865329607243705e-06;
////Sunspot sunspot = null;
////for (String[] s : CSVParser.getData(dailySunspotFileName)) {
////  sunspot = new Sunspot();
////  sunspot.setDaily(s);
////  this.dailySunspotList.add(sunspot);
////}
////for (String[] s : CSVParser.getData(monthlySunspotFileName)) {
////  sunspot = new Sunspot();
////  sunspot.setMonthly(s);
////  this.monthlySunspotList.add(sunspot);
////}
////for (String[] s : CSVParser.getData(monthlySmoothedSunspotFileName)) {
////  sunspot = new Sunspot();
////  sunspot.setMonthlySmoothed(s);
////  this.monthlySmoothedSunspotList.add(sunspot);
////}
//}
