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
package org.meritoki.prospero.library.model.node;

import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.cartography.Projection;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Space;
import org.meritoki.prospero.library.model.unit.Unit;

import com.fasterxml.jackson.annotation.JsonIgnore;

//
/**
 * 
 * <a href="https://en.wikipedia.org/wiki/Spheroid">Reference</a>
 *
 */
public class Spheroid extends Energy {

	static Logger logger = LogManager.getLogger(Spheroid.class.getName());
	public Projection projection = new Projection();
	public Projection selected = null;
	public double defaultScale = 1;
	public double radius = 1;
	public double a = this.radius;
	public double b = this.radius;
	public double c = this.radius;

	public Spheroid(String name) {
		super(name);
	}

	@Override
	public void updateSpace() {
		super.updateSpace();
		this.projection.setSpace(this.buffer);
	}

	@JsonIgnore
	public void setCenter(Space center) {
		super.setCenter(center);
		this.projection.setSpace(this.buffer);
	}

	public void setProjection(Projection projection) {
//		logger.info(this.name+".setProjection("+projection+")");
		this.projection = projection;
	}
	
	public void setSelectedProjection(Projection projection) {
		logger.info(this.name+".setSelectedProjection("+projection+")");
		this.selected = projection;
	}

	public void setScale(double scale) {
		logger.info(this.name+".setScale("+scale+")");
		this.getProjection().setScale(scale);
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			if (n instanceof Spheroid) {
				((Spheroid) n).setScale(scale);
			}
		}
	}

	public void setAzimuth(double azimuth) {
		this.getProjection().setAzimuth(azimuth);
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			if (n instanceof Spheroid) {
				((Spheroid) n).setAzimuth(azimuth);
			}
		}
	}

	public void setElevation(double azimuth) {
		this.getProjection().setElevation(azimuth);
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			if (n instanceof Spheroid) {
				((Spheroid) n).setElevation(azimuth);
			}
		}
	}

	public double getVolume() {
		return (4 / 3) * Math.PI * this.a * this.b * this.c;// Math.pow(this.radius, 3);
	}

	@Override
	public double getX() {
		return Unit.C;
	}

	/**
	 * Same as Charge Force Ratio b/c doig the same arithmetic
	 * 
	 * @param x
	 * @return
	 */
	public double getXRatio(double x) {
		return x / this.calculateX(x);
	}

	public double getChargeAcceleration() {
		return this.getChargeAcceleration(this.radius);
	}

	public double getChargeForce() {
		return this.getMass() * this.getChargeAcceleration();// this.getGravityForce()/Unit.GRAVITY_CHARGE_CONSTANT;//-Unit.k
																// *
																// ((this.getCharge())/Math.pow(this.radius,2));//this.getChargeForce(this.tunnelList);
	}

	public double getChargeForceRatio(double x) {
		return this.getChargeForce() / this.calculateChargeForce(x);
	}

	public double getGravityForceRatio(double x) {
		return this.calculateGravityForce(x) / this.getGravityForceSum();
	}

	public double getGravityAcceleration() {
		return this.getGravityAcceleration(this.radius) / 1000000;// dividing to get meters
	}

	public double getGravityForce() {
		return this.getMass() * this.getGravityAcceleration();
	}

	public double getChargeForceDifference() {
		return this.getCentroidChargeForce() - this.getChargeForce();
	}

	/**
	 * Gravity Force Sum sums the force of the centroid gravity and surface gravity
	 * 
	 * @return
	 */
	public double getChargeForceSum() {
		return this.getCentroidChargeForce() + this.getChargeForce();
	}

	public double getGravityForceDifference() {
		return this.getCentroidGravityForce() - this.getGravityForce();
	}

	/**
	 * Gravity Force Sum sums the force of the centroid gravity and surface gravity
	 * 
	 * @return
	 */
	public double getGravityForceSum() {
		return this.getCentroidGravityForce() + this.getGravityForce();
	}

	public double getGravityForceQuotient() {
		return this.getCentroidGravityForce() / this.getGravityForce();
	}

	/**
	 * Vital method, shows the Amperes that can traverse the tunnel over the time it
	 * takes to reach the barycenter. The time it takes to reach barycenter, it the
	 * time it takes for a point on the surface of a spheroid to reach the centroid,
	 * where it traverses instantaneously to another object. Therefore, we can
	 * divide it by the Coulombs transfered and by the time it takes to reach the
	 * barycenter to get the Amperes or rate of Coulomb transfer per second.
	 * 
	 * @return
	 */
	public double getCentroidAmperes(double time) {
		return this.getCentroidCoulomb() / time;
	}

	/**
	 * Amperes are calculated by dividing Coulombs by time. Here time is the seconds
	 * it takes a photon to travel from the centroid of the spheriod to the
	 * barycenter. The barycenter is the core of a gravitational anomaly that allows
	 * for instantaneous transfer of data from one Speriod to another.
	 * 
	 * @return
	 */
	public double getAmperes(double time) {
		return Unit.COULOMBS / time;// this.getCoulomb()/time;
	}

	public double getWatts(double time) {
		return this.getJoules() / time;
	}

	public double getTesla(double time, double distance) {
		return this.getVoltage() * time / distance;
	}

	/**
	 * Interesting result; When the voltage of a Spheriod is divided by amperes, we
	 * get the same resistance between any two Spheriods. When we divide voltage
	 * 
	 * @return
	 */
	public double getResistance(double time) {
		double resistance = this.getVoltage() / this.getAmperes(time);
		// resistance = Double.parseDouble(df.format(resistance));
		return resistance;
	}
	
	public Projection getProjection() {
		Projection projection = this.selected;
		if(projection == null) {
			projection = this.projection;
		}
//		logger.info(this.name+".getProjection() projection="+projection);
		return projection;
	}

	/**
	 * Return the quotient of Gravity Force Sum and the Charge Force between all
	 * tunnels
	 * 
	 * @param x
	 * @return
	 */
	public double calculateX(double x) {
		return this.getGravityForceSum() / this.getChargeForceDifference();// this.calculateChargeForce(x);
	}

	public double calculateGravityForce(double x) {
		return this.getChargeForce() * x;
	}

	/**
	 * Really the Charge Force
	 * 
	 * @param x
	 * @return
	 */
	public double calculateChargeForce(double x) {
		return this.getGravityForceSum() / x;
	}

	@Override
	public void print() {
		super.print();
		System.out.println("Mass");
		System.out.println(this.name + " Mass:" + this.getMass());
		System.out.println(this.name + " Centroid Mass:" + this.getCentroidMass());
		System.out.println("X");
		double X = this.getX();
		System.out.println(this.name + " X: " + X);
		System.out.println(this.name + " Calculate X: " + this.calculateX(X));
		System.out.println(this.name + " X Ratio: " + this.getXRatio(X));
		System.out.println("CHARGE");
		System.out.println(this.name + " Coulomb:" + this.getCoulomb());
		System.out.println(this.name + " Centroid Coulomb:" + this.getCentroidCoulomb());
		System.out.println(this.name + " Charge Acceleration: " + this.getChargeAcceleration());
		System.out.println(this.name + " Charge Force: " + this.getChargeForce());
		System.out.println(this.name + " Calculate Charge Force: " + this.calculateChargeForce(X));
		System.out.println(this.name + " Charge Force Ratio: " + this.getChargeForceRatio(X));
		System.out.println("GRAVITY");
		System.out.println(this.name + " Gravity Acceleration: " + this.getGravityAcceleration());
		System.out.println(this.name + " Centroid Gravity Acceleration: " + this.getCentroidGravityAcceleration());
		System.out.println(this.name + " Gravity Force: " + this.getGravityForce());
		System.out.println(this.name + " Centroid Gravity Force: " + this.getCentroidGravityForce());
		System.out.println(this.name + " Gravity Force Quotient: " + this.getGravityForceQuotient());
		System.out.println(this.name + " Gravity Force Difference: " + this.getGravityForceDifference());
		System.out.println(this.name + " Gravity Force Sum: " + this.getGravityForceSum());
		System.out.println(this.name + " Calculate Gravity Force: " + this.calculateGravityForce(X));
		System.out.println(this.name + " Gravity Force Ratio: " + this.getGravityForceRatio(X));
//		System.out.println(this.name+" Gravity Force Ratio Charge Quotient: "+this.getGravityForceRatio(X)/Unit.COULOMBS);
		System.out.println("Voltage");
		System.out.println(this.name + " Voltage:" + this.getVoltage());
//		System.out.println(this.name+" Centroid Voltage:"+this.getCentroidVoltage());
		System.out.println("Joules");
		System.out.println(this.name + " Joules:" + this.getJoules());
//		System.out.println(this.name+" Centroid Joules:"+this.getCentroidJoules());
		System.out.println("RESISTANCE");
		System.out.println(this.name + " Resistance Ratio: " + this.getResistanceRatio());
	}

	@JsonIgnore
	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
		graphics.setColor(this.color);
//		logger.info(this.name+".paint(graphics) this.getProjection()="+this.getProjection()+" xMax="+this.getProjection().xMax);
		List<Coordinate> coordinateList = this.getProjection().getGridCoordinateList(0, 15, 30);
		for (Coordinate c : coordinateList) {
			graphics.drawLine((int) ((c.point.x) * this.getProjection().scale), (int) ((c.point.y) * this.getProjection().scale),
					(int) ((c.point.x) * this.getProjection().scale), (int) ((c.point.y) * this.getProjection().scale));
		}
		
//		logger.info(this.name+".paint(graphics) this.getProjection().space="+this.getProjection().space);
		Point point = this.getProjection().getPoint(this.getProjection().space.getPoint());// this.buffer
		double x = point.x * this.getProjection().scale;
		double y = point.y * this.getProjection().scale;
//		graphics.setColor(this.color);
		double radius = 2;
		x = x - (radius / 2);
		y = y - (radius / 2);
		graphics.fillOval((int) x, (int) y, (int) radius, (int) radius);
//		graphics.setColor(Color.black);
//		graphics.drawString(this.name.substring(0, 1).toUpperCase() + this.name.substring(1) + "", (int) x,
//				(int) y);
	}
}
//this.projection = new Globe(this.a,this.b,this.c);
//this.projection.setSpace(this.space);
//List<Coordinate> coordinateList = this.projection.getGridCoordinateList(0, 15, 30);
//graphics.setColor(this.color);
//for (Coordinate c : coordinateList) {
//	graphics.drawLine((int) ((c.point.x) * this.projection.scale), (int) ((c.point.y) * this.projection.scale), (int) ((c.point.x) * this.projection.scale), (int) ((c.point.y) * this.projection.scale));
//}
//public double getCentroidTesla(double time, double distance) {
//return this.getCentroidVoltage()*time/distance;
//}
//public double getCentroidResistance(double time) {
//double resistance = this.getCentroidVoltage()/this.getCentroidAmperes(time);
////resistance = Double.parseDouble(df.format(resistance));
//return resistance;
//}
//public double getRadius(double unit) {
//return this.radius * unit;
//}
//@Override
//public Map<String,List<Index>> getIndexListMap() throws Exception {
//	Map<String, List<Index>> map = super.getIndexListMap();
//	if(map == null) {
//		map = this.initIndexListMap();
//		this.indexListMap = map;
//	} else {
//		map.putAll(this.initIndexListMap());
//	}
//	return map;
//}
//
//public Map<String,List<Index>> initIndexListMap() throws Exception{
//	Map<String,List<Index>> map = new HashMap<>();
//	Solar solar = (Solar)this.getParent().getParent();
//	List<String> dateList = getDateList(this.period, Calendar.DATE);
//	Calendar calendar = null;
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//	for (String date : dateList) {
//		calendar = Calendar.getInstance();
//		calendar.setTime(sdf.parse(date));
//		solar.setCalendar(calendar);
//		this.mapPut(map, calendar, "X Ratio", this.getXRatio(this.getX()));
//		this.mapPut(map, calendar, "Charge Force Ratio", this.getChargeForceRatio(this.getX()));
//		this.mapPut(map, calendar, "Gravity Force Ratio", this.getGravityForceRatio(this.getX()));
//	}
//	return map;
//}

//public double getCentroidForce() {
//return this.getCentroidJoules()/Unit.ELECTRON_RADIUS;
//}
