/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meritoki.prospero.library.model.solar.star.sun;

import java.awt.Color;

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

/**
 *
 * @author jorodriguez
 */
public class Sun extends Star {

	public Helios helios = new Helios();
	
	public Sun(){
		super("Sun");
        this.mass = 1.9891e30;
        this.radius = 6.96342e5;
        this.color = Color.YELLOW;
        this.angularVelocity = 2.865329607243705e-06;
        this.rotation = 0.564263323;//1.997;//km/s
        this.addChild(new Earth(this));
        this.addChild(new Jupiter(this));
        this.addChild(new Mars(this));
        this.addChild(new Mercury(this));
        this.addChild(new Neptune(this));
        this.addChild(new Saturn(this));
        this.addChild(new Uranus(this));
        this.addChild(new Venus(this));
        this.addChildren(this.helios.getChildren());
	}
}

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
