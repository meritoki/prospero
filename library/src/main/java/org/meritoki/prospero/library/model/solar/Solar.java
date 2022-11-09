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
package org.meritoki.prospero.library.model.solar;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.function.Cosine;
import org.meritoki.prospero.library.model.function.Sine;
import org.meritoki.prospero.library.model.node.Energy;
import org.meritoki.prospero.library.model.node.Grid;
import org.meritoki.prospero.library.model.node.Orbital;
import org.meritoki.prospero.library.model.node.Spheroid;
import org.meritoki.prospero.library.model.node.Triangle;
import org.meritoki.prospero.library.model.node.Tunnel;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.node.cartography.Projection;
import org.meritoki.prospero.library.model.solar.planet.earth.Earth;
import org.meritoki.prospero.library.model.solar.star.sun.Sun;
import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Space;
import org.meritoki.prospero.library.model.unit.Table;

/**
 * Citation
 * <ol type="A">
 * <li><a href="https://nssdc.gsfc.nasa.gov/planetary/factsheet.html">https://nssdc.gsfc.nasa.gov/planetary/factsheet.html</a></li>
 * </ol>
 *
 */
public class Solar extends Grid {

	static Logger logger = LogManager.getLogger(Solar.class.getName());
	public Color color = Color.YELLOW;
	private List<String> planetOrder = Arrays.asList("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus",
			"Neptune");
	private List<Tunnel> tunnelList = null;
	public Sun sun = new Sun();
	public Variable tunnel = new Variable("Tunnel");
	
	//
	private Map<String, List<Index>> allAnglePlanetsIndexListMap;
	private Map<String, List<Index>> allDistancePlanetsIndexListMap;
	private Map<String, List<Index>> allGravityForcePlanetsIndexListMap;
	private List<Index> anglePlanetsIndexList;
	private List<Index> distancePlanetsIndexList;
	private List<Index> gravityForcePlanetsIndexList;

	public Solar() {
		super("Solar");
		this.addChild(this.sun);
		this.tunnelList = this.getTunnelList();
		for(Tunnel t: this.tunnelList) {
			this.tunnel.addChild(t);
		}
		this.addChild(this.tunnel);
		this.defaultScale = 16;
		this.setScale(this.defaultScale);
		this.getProjection().setRadius(39.5);// Astronomical Unit
		this.getProjection().setUnit(1);
		this.setProjection(this.projection);
		
	}
	
	public Solar(String name) {
		super(name);
	}
	
//	@Override
//	public void setProjection(Projection projection) {
//		super.setProjection(projection);
////		List<Variable> nodeList = this.getChildren();
////		for (Variable n : nodeList) {
////			if (n instanceof Spheroid) {
////				((Spheroid) n).setProjection(projection);
////			}
////		}
//	}
	
	@Override
	public void setProjection(Projection projection) {
		super.setProjection(projection);
		List<Variable> nodeList = this.tunnel.getChildren();
		for (Variable n : nodeList) {
			if (n instanceof Tunnel) {
				((Tunnel) n).setProjection(projection);
			}
		}
	}
	
	public void setScale(double scale) {
		super.setScale(scale);
		List<Variable> nodeList = this.tunnel.getChildren();
		for (Variable n : nodeList) {
			if (n instanceof Tunnel) {
				((Tunnel) n).setScale(scale);
			}
		}
	}

	@Override
	public void initVariableMap() {
		super.initVariableMap();
		this.variableMap.put("Sun Angle Planets", false);
		this.variableMap.put("Sun Test", false);
		this.variableMap.put("Earth Test", false);
		this.variableMap.put("Solar Cycle", false);
	}

	public Sun getSun() {
		return this.sun;
	}

	@Override
	public List<Table> getTableList() throws Exception {
		return this.tableList;
	}
	////////////////////////////

	public List<Index> getAllGravityForceIndexList(String name) throws Exception {
		List<Index> allIndexList = new ArrayList<>();
		Map<String, List<Index>> indexListMap = new HashMap<>();
		List<String> dateList = getDateList(this.period, Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Energy energy = (Energy) this.getVariable(name);
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			this.setCalendar(c);
			Index index;
			for (Energy e : this.getEnergyList()) {
				if (!e.name.equals(name)) {
					System.out.println(e.name);
					double gravityForce = energy.getGravityForce(e);
					List<Index> list = indexListMap.get(e.toString());
					if (list == null) {
						list = new ArrayList<>();
					}
					index = new Index();
					index.value = gravityForce;
					index.startCalendar = c;
					list.add(index);
					indexListMap.put(e.toString(), list);
				}
			}
		}
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			Index index = new Index();
			index.startCalendar = c;
			index.value = 0;
			allIndexList.add(index);
		}

		for (Energy e : this.getEnergyList()) {
			List<Index> list = indexListMap.get(e.toString());
			if (list != null) {
				for (int i = 0; i < dateList.size(); i++) {
					Index index = allIndexList.get(i);
					index.value += (list.get(i).value);
					allIndexList.set(i, index);
				}
			}
		}
		return allIndexList;
	}

	/**
	 * Convert
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<Index> getAllAnglePlanetsIndexList(String name) throws Exception {
		List<Index> indexList = new ArrayList<>();
		Map<String, List<Index>> indexListMap = new HashMap<>();
		List<String> dateList = getDateList(this.period, Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			this.setCalendar(c);
			List<Triangle> triangleList = this.getTriangleList(name);
			Index index;
			for (Triangle t : triangleList) {
				double angle = t.A;
				List<Index> list = indexListMap.get(t.toString());
				if (list == null) {
					list = new ArrayList<>();
				}
				index = new Index();
				if (angle > 90) {
					angle -= 180;
					angle = Math.abs(angle);
				}
				index.value = Math.toRadians(angle);
				index.startCalendar = c;
				list.add(index);
				indexListMap.put(t.toString(), list);
			}
		}

		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			Index index = new Index();
			index.startCalendar = c;
			index.value = 0;
			indexList.add(index);
		}
		for (Triangle t : this.getTriangleList(name)) {
			List<Index> list = indexListMap.get(t.toString());
			if (list != null) {
				for (int i = 0; i < dateList.size(); i++) {
					Index index = indexList.get(i);
					double value = list.get(i).value;
					index.value += (value);
					indexList.set(i, index);
				}
			}
		}
		for (int i = 0; i < dateList.size(); i++) {
			Index index = indexList.get(i);
			index.value = index.value / (Math.PI);
//			index.value = index.value/(2*Math.PI);
			index.value = Math.toDegrees(index.value);
			indexList.set(i, index);
		}
		return indexList;
	}

	public List<Index> getTestIndexList(String name) throws Exception {
		List<Index> indexList = new ArrayList<>();
		Map<String, List<Index>> indexListMap = new HashMap<>();
		List<String> dateList = getDateList(this.period, Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			this.setCalendar(c);
			List<Triangle> triangleList = this.getTriangleList(name);
			Index index;
			for (Triangle t : triangleList) {
//					if(t.contains("Jupiter") && t.contains("Saturn")
//					   || t.contains("Jupiter") && t.contains("Neptune")
//					   || t.contains("Jupiter") && t.contains("Uranus")
//					   || t.contains("Saturn") && t.contains("Neptune")
//					   || t.contains("Uranus") && t.contains("Neptune")
//					   || t.contains("Saturn") && t.contains("Uranus")) {
				double distance = t.getDistance();
				double angle = t.A;
				if (angle > 90) {
					angle -= 180;
					angle = Math.abs(angle);
				}
				List<Index> list = indexListMap.get(t.toString());
				if (list == null) {
					list = new ArrayList<>();
				}
				index = new Index();
//					index.value = Math.toRadians(angle) * t.getGravityForce();//(t.j.mass+t.k.mass);
				index.value = Math.toRadians(angle) * t.getMassSum() / t.getDistanceSum();// (t.j.mass+t.k.mass);
				index.startCalendar = c;
				list.add(index);
				indexListMap.put(t.toString(), list);
//					}
			}
		}
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			Index index = new Index();
			index.startCalendar = c;
			index.value = 0;
			indexList.add(index);
		}
		for (Triangle t : this.getTriangleList(name)) {
			List<Index> list = indexListMap.get(t.toString());
			if (list != null) {
				for (int i = 0; i < dateList.size(); i++) {
					Index index = indexList.get(i);
					double value = list.get(i).value;
					index.value += (value);
					indexList.set(i, index);
				}
			}
		}
		for (int i = 0; i < dateList.size(); i++) {
			Index index = indexList.get(i);
			index.value = index.value / (Math.PI);
			// index.value = index.value/(2*Math.PI);
			index.value = Math.toDegrees(index.value);
			indexList.set(i, index);
		}
		return indexList;
	}

	public Map<String, List<Index>> getAnglePlanetsIndexListMap() throws Exception {
		Map<String, List<Index>> indexListMap = new HashMap<>();
		List<String> dateList = getDateList(this.period, Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			this.setCalendar(c);
			List<Triangle> triangleList = this.getTriangleList("Sun");
			Index index;
			for (Triangle t : triangleList) {
				double angle = t.A;
				List<Index> list = indexListMap.get(t.toString());
				if (list == null) {
					list = new ArrayList<>();
				}
				index = new Index();
				index.value = angle;
				index.startCalendar = c;
				list.add(index);
				indexListMap.put(t.toString(), list);
			}
		}
		return indexListMap;
	}

	public Map<String, List<Index>> getDistancePlanetsIndexListMap(String name) throws Exception {
		Map<String, List<Index>> indexListMap = new HashMap<>();
		List<String> dateList = getDateList(this.period, Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Energy energy = (Energy) this.getVariable(name);
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			this.setCalendar(c);
			Index index;
			for (Energy e : this.getEnergyList()) {
				if (!e.name.equals(name)) {
					double distance = energy.getRectangularDistance(e);
					List<Index> list = indexListMap.get(e.name);
					if (list == null) {
						list = new ArrayList<>();
					}
					index = new Index();
					index.value = distance;
					index.startCalendar = c;
					list.add(index);
					indexListMap.put(e.name, list);
				}
			}
		}
		return indexListMap;
	}

	public Map<String, List<Index>> getGravityForcePlanetsIndexListMap(String name) throws Exception {
		Map<String, List<Index>> indexListMap = new HashMap<>();
		List<String> dateList = getDateList(this.period, Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Energy energy = (Energy) this.getVariable(name);
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			this.setCalendar(c);
			Index index;
			for (Energy e : this.getEnergyList()) {
				if (!e.name.equals(name)) {
					double distance = energy.getGravityForce(e);
					List<Index> list = indexListMap.get(e.name);
					if (list == null) {
						list = new ArrayList<>();
					}
					index = new Index();
					index.value = distance;
					index.startCalendar = c;
					list.add(index);
					indexListMap.put(e.name, list);
				}
			}
		}
		return indexListMap;
	}

	public List<Index> getSinePointList(Sine sine) throws Exception {
		List<Index> pointList = new ArrayList<>();
		List<String> dateList = getDateList("18000101-20200101", Calendar.DATE);
		Calendar calendar;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (String date : dateList) {
			calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			Index p = new Index();
			p.startCalendar = calendar;
			p.value = sine.getY(calendar.getTimeInMillis());
			pointList.add(p);
		}
		return pointList;
	}

	public List<Index> getCosinePointList(Cosine sine) throws Exception {
		List<Index> pointList = new ArrayList<>();
		List<String> dateList = getDateList("18000101-20200101", Calendar.DATE);
		Calendar calendar;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (String date : dateList) {
			calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			Index p = new Index();
			p.startCalendar = calendar;
			p.value = sine.getY(calendar.getTimeInMillis());
			pointList.add(p);
		}
		return pointList;
	}

	public static double getMin(List<Index> list) {
		double min = Double.MAX_VALUE;
		for (Index d : list) {
			if (d.value < min) {
				min = d.value;
			}
		}
		System.out.println("getMin(...) min=" + min);
		return min;
	}

	public static double getMax(List<Index> list) {
		double max = Double.MIN_VALUE;
		for (Index d : list) {
			if (d.value > max) {
				max = d.value;
			}
		}
		System.out.println("getMax(...) max=" + max);
		return max;
	}

	public static double getZero(double min, double max) {
		double zero = ((max - min) / 2) + min;
		System.out.println("getZero(...) zero=" + zero);
		return zero;
	}

	public static List<Index> getZeroList(List<Index> list) {
//		System.out.println("getZeroList(...,"+mass+")");
		double zero = getZero(getMin(list), getMax(list));
		List<Index> zeroList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Index d = list.get(i);
			d.value -= zero;
			zeroList.add(d);
		}
		return zeroList;
	}

	public static double getVolume(double radius) {
		return (4 / 3) * Math.PI * Math.pow(radius, 3);
	}

	public List<Coordinate> getCoordinateList(String name) {// Imagine energy is Earth.
		List<Coordinate> coordinateList = new LinkedList<Coordinate>();
		Coordinate coordinate = null;
		Vector3D prime = null;
		Vector3D out = null;
		double alpha;
		double delta;
		double obliquity;
		double distance;
		DecimalFormat df = new DecimalFormat("#.##");
		Energy energy = null;
		for (Energy e : this.getEnergyList()) {
			if (e.name.equals(name)) {
				energy = e;
				break;
			}
		}
		if (energy != null) {
			for (Energy e : this.getEnergyList()) {
				if (!e.name.equals(energy.name)) {
					prime = e.space.rectangular.subtract(energy.space.rectangular);
					distance = e.space.rectangular.distance(energy.space.rectangular);
					obliquity = Math.toRadians(((Orbital) energy).obliquity);
					out = new Vector3D(prime.getX(),
							((prime.getY() * Math.cos(obliquity)) - (prime.getZ() * Math.sin(obliquity))),
							((prime.getY() * Math.sin(obliquity)) + (prime.getZ() * Math.cos(obliquity))));
					alpha = Math.atan(out.getY() / out.getX());
					delta = Math.atan(out.getZ() / Math.sqrt(out.getX() * out.getX() + out.getY() * out.getY()));
					if (out.getX() < 0) {
						alpha = alpha + Math.PI;
					}
					if (out.getX() > 0 && out.getY() < 0) {
						alpha = alpha + (2 * Math.PI);
					}
					alpha = Math.toDegrees(alpha);
					delta = Math.toDegrees(delta);
					double angle = ((Earth) energy).getRotationCorrection(energy.calendar);
					alpha = alpha + angle;
					while (alpha > 180) {
						alpha = alpha - 360;
					}
					while (delta > 90) {
						delta -= 180;
					}
					coordinate = new Coordinate();
					coordinate.latitude = delta;
					coordinate.longitude = alpha;
					coordinate.attribute.put("label", e.name);
					coordinate.attribute.put("distance", df.format(distance));
					coordinateList.add(coordinate);
				}
			}
		}
		return coordinateList;
	}

	public ArrayList<Energy> getEnergyList(List<Triangle> triangleList) {
		ArrayList<Energy> energyList = new ArrayList<Energy>();
		for (Triangle t : triangleList) {
			if (!energyList.contains(t.i)) {
				energyList.add(t.i);
			}
			if (!energyList.contains(t.j)) {
				energyList.add(t.j);
			}
			if (!energyList.contains(t.k)) {
				energyList.add(t.k);
			}
		}
		return energyList;
	}

	public double getKineticEnergy() {
		double kineticEnergy = 0;
		for (Energy e : this.getEnergyList()) {
			if (e instanceof Spheroid)
				kineticEnergy += 0.5 * e.mass * Math.pow(((Orbital) e).angularVelocity, 2)
						* Math.pow(((Spheroid) e).radius, 2);
		}
		return kineticEnergy;
	}

	public Vector3D getCenterOfMass() {
		double totalMass = 0;
		double totalX = 0;
		double totalY = 0;
		double totalZ = 0;
		for (Energy e : this.getEnergyList()) {
			if (!(e instanceof Sun)) {
				totalMass += e.mass;
				totalX += e.space.rectangular.getX() * e.mass;
				totalY += e.space.rectangular.getY() * e.mass;
				totalZ += e.space.rectangular.getZ() * e.mass;
			}
		}
		return new Vector3D(totalX / totalMass, totalY / totalMass, totalZ / totalMass);
	}

	public Vector3D getCenterOfMass(Energy e1, Energy e2) {
		double totalMass = 0;
		double totalX = 0;
		double totalY = 0;
		double totalZ = 0;
		List<Energy> eList = new LinkedList<Energy>();
		eList.add(e1);
		eList.add(e2);
		for (Energy e : eList) {
			if (!(e instanceof Sun)) {
				totalMass += e.mass;
				totalX += e.space.rectangular.getX() * e.mass;
				totalY += e.space.rectangular.getY() * e.mass;
				totalZ += e.space.rectangular.getZ() * e.mass;
			}
		}
		return new Vector3D(totalX / totalMass, totalY / totalMass, totalZ / totalMass);
	}

	public Vector3D getCenterOfMass(Energy e1, Energy e2, List<Energy> list) {
		double totalMass = 0;
		double totalX = 0;
		double totalY = 0;
		double totalZ = 0;
		List<Energy> eList = new LinkedList<Energy>();
		eList.add(e1);
		eList.add(e2);
		eList.addAll(list);
		for (Energy e : eList) {
			if (!(e instanceof Sun)) {
				totalMass += e.mass;
				totalX += e.space.rectangular.getX() * e.mass;
				totalY += e.space.rectangular.getY() * e.mass;
				totalZ += e.space.rectangular.getZ() * e.mass;
			}
		}
		return new Vector3D(totalX / totalMass, totalY / totalMass, totalZ / totalMass);
	}

	public Vector3D getCenterOfMass(List<Energy> eList) {
		double totalMass = 0;
		double totalX = 0;
		double totalY = 0;
		double totalZ = 0;
		for (Energy e : eList) {
			if (!(e instanceof Sun)) {
				totalMass += e.mass;
				totalX += e.space.rectangular.getX() * e.mass;
				totalY += e.space.rectangular.getY() * e.mass;
				totalZ += e.space.rectangular.getZ() * e.mass;
			}
		}
		return new Vector3D(totalX / totalMass, totalY / totalMass, totalZ / totalMass);
	}

	public Calendar getCalendar() {
		return this.calendar;
	}

	public int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

	public double calculateMean(List<Double> doubleList) {
		double sum = 0;
		for (Double d : doubleList) {
			sum += d;
		}
		return sum / doubleList.size();
	}

	public double calculateSum(List<Double> doubleList) {
		double sum = 0;
		for (Double d : doubleList) {
			sum += d;
		}
		return sum;
	}

	public List<Triangle> filterTriangleList(List<Triangle> list) {
		List<Triangle> tList = new ArrayList<>();
		for (Triangle t : list) {
			int index = this.planetOrder.indexOf(t.j.name);
			int minus = index - 1;
			int plus = index + 1;
			if ((minus >= 0 && t.k.name.equals(this.planetOrder.get(minus)))
					|| (plus < this.planetOrder.size() && t.k.name.equals(this.planetOrder.get(plus)))) {
				tList.add(t);
			}
		}
		return tList;
	}

	public List<Triangle> matchTriangleList(List<Triangle> aList, List<Triangle> bList, double threshold) {
		List<Triangle> triangleList = new LinkedList<Triangle>();
		for (Triangle a : aList) {
			for (Triangle b : bList) {
				if (a.j.name.equals(b.j.name) && a.k.name.equals(b.k.name)) {
					if (b.thresholdA(a.A, threshold)) {
						triangleList.add(a);
					}
				}
			}
		}
		return triangleList;
	}

	public Space compareSpace(List<Triangle> aList, List<Triangle> bList, double threshold) {
		List<Triangle> triangleList = this.matchTriangleList(aList, bList, threshold);
		double total = aList.size();
		double count = triangleList.size();
		Space space = new Space();
//		space.triangleList = triangleList;
//		space.match = count / total;
		return space;
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
		this.initVariableMap();
//		Variable tunnelNode = this.getVariable("Tunnel");
//		for(Variable t: tunnelNode.getChildren()) {
//			((Tunnel)t).paint(graphics);
//		}
	}
}
//Variable triangleNode = new Variable("Triangle");
//this.triangleList = this.getTriangleList("Sun");
//for(Triangle t: this.triangleList) {
//	triangleNode.addChild(t);
//}
//this.addChild(triangleNode);
//this.setScale(defaultScale);
//Variable triangleNode = this.getVariable("Triangle");
//for(Variable t: triangleNode.getChildren()) {
//	((Triangle)t).paint(graphics);
//}
//if(this.model.node != null && !(this.model.node instanceof Solar) && !(this.model.node instanceof Model)) {
//if(this.model.node instanceof Energy) {
//	Energy e = (Energy)this.model.node;
//	this.setCenter(e.space);
//}
//}
//Variable tunnelNode = this.getVariable("Tunnel");
//for(Variable t: tunnelNode.getChildren()) {
//	((Tunnel)t).paint(graphics);
//}
//
//Variable triangleNode = this.getVariable("Triangle");
//
//for(Variable t: triangleNode.getChildren()) {
//	((Triangle)t).paint(graphics);
//}
//List<Coordinate> coordinateList = this.projection.getGridCoordinateList(0, 15, 30);
//graphics.setColor(this.color);
//for (Coordinate c : coordinateList) {
//	graphics.drawLine((int) ((c.point.x) * this.projection.scale), (int) ((c.point.y) * this.projection.scale), (int) ((c.point.x) * this.projection.scale), (int) ((c.point.y) * this.projection.scale));
//}

//List<Variable> nodeList = this.getChildren();
//for (Variable n : nodeList) {
//	if (n instanceof Energy) {
//		n.paint(graphics);
//	}
//}
//this.projection.setNear((float)this.projection.getRadius(1));
//this.projection.setNearToObject((float)this.projection.getRadius(1)+1);
//Variable tunnelNode = new Variable("Tunnel");
//this.tunnelList = this.getTunnelList();
//for(Tunnel t: this.tunnelList) {
//	tunnelNode.addChild(t);
//}
//Variable triangleNode = new Variable("Triangle");
//this.triangleList = this.getTriangleList("Helios");
//for(Triangle t: this.triangleList) {
//	triangleNode.addChild(t);
//}
//this.addChild(tunnelNode);
//this.addChild(triangleNode);
//this.setScale(DEFAULT_SCALE);
//Point point = this.projection.getPoint(this.space.getPoint());
//double x = point.x * this.projection.scale;
//double y = point.y * this.projection.scale;
//graphics.setColor(this.color);
//double radius = 5;
//x = x - (radius / 2);
//y = y - (radius / 2);
//graphics.fillOval((int) x, (int) y, (int) radius, (int) radius);
//	@Override
//public List<Plot> getPlotList() throws Exception {
//List<Plot> plotList = super.getPlotList();
//for (Entry<String, Boolean> variable : this.variableMap.entrySet()) {
//	String variableKey = variable.getKey();
//	Boolean variableLoad = variable.getValue();
//	if (variableLoad) {
//		TimePlot plot = null;
//		switch (variableKey) {
//		case "Solar Cycle": {
//			Cosine sine = new Cosine(new GregorianCalendar(1816, 0, 1, 0, 0, 0), Long.valueOf("348666451258"));
//			List<Index> indexList = this.getCosinePointList(sine);
//			if (indexList != null) {
//				List<List<Index>> blackPointMatrix = new ArrayList<>();
//				blackPointMatrix.add((indexList));
//				TimePlot cPlot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//				cPlot.setTitle(variableKey);
//				cPlot.setXLabel("Time");
//				cPlot.setYLabel("Distance");
//				plotList.add(cPlot);
//			}
//			break;
//		}
//		case "Sun Angle Planets": {
//			if (this.anglePlanetsIndexList == null)
//				anglePlanetsIndexList = this.getAllAnglePlanetsIndexList("Sun");
//			if (anglePlanetsIndexList != null) {
//				List<List<Index>> blackPointMatrix = new ArrayList<>();
//				blackPointMatrix.add((anglePlanetsIndexList));
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//				plot.setTitle(variableKey);
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			break;
//		}
//		case "Earth Angle Planets": {
//			if (this.anglePlanetsIndexList == null)
//				anglePlanetsIndexList = this.getAllAnglePlanetsIndexList("Earth");
//			if (anglePlanetsIndexList != null) {
//				List<List<Index>> blackPointMatrix = new ArrayList<>();
//				blackPointMatrix.add((anglePlanetsIndexList));
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//				plot.setTitle(variableKey);
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			break;
//		}
//
//		case "Angle Planets": {
//			if (this.allAnglePlanetsIndexListMap == null)
//				allAnglePlanetsIndexListMap = getAnglePlanetsIndexListMap();
//			for (Entry<String, List<Index>> entry : allAnglePlanetsIndexListMap.entrySet()) {
//				String key = entry.getKey();
//				List<List<Index>> blackIndexMatrix = new ArrayList<>();
//				List<List<Index>> colorIndexMatrix = new ArrayList<>();
//				List<Index> indexList = entry.getValue();
//				blackIndexMatrix.add(indexList);
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackIndexMatrix, colorIndexMatrix);
//				plot.setTitle(key + " Angle");
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			break;
//		}
//		case "Distance Planets": {
//			if (this.allDistancePlanetsIndexListMap == null)
//				allDistancePlanetsIndexListMap = getDistancePlanetsIndexListMap("Sun");
//			for (Entry<String, List<Index>> entry : allDistancePlanetsIndexListMap.entrySet()) {
//				String key = entry.getKey();
//				List<List<Index>> blackIndexMatrix = new ArrayList<>();
//				List<List<Index>> colorIndexMatrix = new ArrayList<>();
//				List<Index> indexList = entry.getValue();
//				blackIndexMatrix.add(indexList);
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackIndexMatrix, colorIndexMatrix);
//				plot.setTitle("Sun," + key + " Distance");
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			break;
//		}
//		case "Gravity Force Planets": {
//			if (this.allGravityForcePlanetsIndexListMap == null)
//				allGravityForcePlanetsIndexListMap = getGravityForcePlanetsIndexListMap("Sun");
//			for (Entry<String, List<Index>> entry : allGravityForcePlanetsIndexListMap.entrySet()) {
//				String key = entry.getKey();
//				List<List<Index>> blackIndexMatrix = new ArrayList<>();
//				List<List<Index>> colorIndexMatrix = new ArrayList<>();
//				List<Index> indexList = entry.getValue();
//				blackIndexMatrix.add(indexList);
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackIndexMatrix, colorIndexMatrix);
//				plot.setTitle("Sun," + key + " Gravity Force");
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			break;
//		}
//
//		case "Earth Gravity Force": {
//			List<Index> indexList = this.getAllGravityForceIndexList("Earth");
//			if (indexList != null) {
//				List<List<Index>> blackPointMatrix = new ArrayList<>();
//				blackPointMatrix.add((indexList));
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//				plot.setTitle(variableKey);
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			break;
//		}
//
//		case "Jupiter Gravity Force": {
//			List<Index> indexList = this.getAllGravityForceIndexList("Jupiter");
//			if (indexList != null) {
//				List<List<Index>> blackPointMatrix = new ArrayList<>();
//				blackPointMatrix.add((indexList));
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//				plot.setTitle(variableKey);
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			break;
//		}
//		case "Earth Test": {
//			List<Index> indexList = this.getTestIndexList("Earth");
//			if (indexList != null) {
//				List<List<Index>> blackPointMatrix = new ArrayList<>();
//				blackPointMatrix.add((indexList));
//				List<List<Index>> colorPointMatrix = null;
//				// colorPointMatrix.add(this.getAnglePlanetsIndexList());
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, colorPointMatrix);
//				plot.setTitle(variableKey);
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			break;
//		}
//		case "Sun Test": {
//			List<Index> indexList = this.getTestIndexList("Sun");
//			if (indexList != null) {
//				List<List<Index>> blackPointMatrix = new ArrayList<>();
//				blackPointMatrix.add((indexList));
//				List<List<Index>> colorPointMatrix = null;
//				// colorPointMatrix.add(this.getAnglePlanetsIndexList());
//				plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, colorPointMatrix);
//				plot.setTitle(variableKey);
//				plot.setXLabel("Time");
//				plot.setYLabel("Distance");
//				plotList.add(plot);
//			}
//			// List<Point> indexList = this.getTestIndexList("Sun");
//			// if (indexList != null) {
//			// List<List<Point>> blackPointMatrix = new ArrayList<>();
//			// blackPointMatrix.add((indexList));
//			// CartesianPlot cPlot = new CartesianPlot(blackPointMatrix, null);
//			// cPlot.setTitle(variableKey);
//			// cPlot.setXLabel("Time");
//			// cPlot.setYLabel("Distance");
//			// plotList.add(cPlot);
//			// }
//			break;
//		}
//		}
//	}
//}
//return plotList;
//}
//public void setScale(double scale) {
//this.projection.scale = scale;
//for (Variable e : this.getList()) {
//	if (e instanceof Energy) {
//		((Energy) e).projection.scale = scale;
//	}
//}
//}

//public void getGravity() {
//for (Energy energy : this.getEnergyList()) {
//	for (Energy e : this.getEnergyList()) {
//		if (energy != e) {
//			if (energy.mass != 1 && e.mass != 1) {
//				Vector3D force = e.getGravity(energy);
//				energy.force = energy.force.subtract(force);
//				e.force = e.force.add(force);
//			}
//		}
//	}
//}
//
//}

//public void drawDate(Graphics g, Calendar c) {
//g.setColor(Color.WHITE);
//String calendar = this.getCalendarString(null, c);
//int text = g.getFontMetrics().stringWidth(calendar);
//g.drawString(calendar, 0 - text / 2, -360);
//}

//public Space compareSpace(Calendar time, double threshold) {
//List<Energy> energyList = this.getEnergyList(time);
//List<Triangle> triangleListA = this.getTriangleList(energyList);
//List<Triangle> triangleListB = this.getTriangleList(this.energyList);
//return this.compareSpace(triangleListA, triangleListB, threshold);
//}
//
//public Space compareSpace(Calendar timeA, Calendar timeB, double threshold) {
//List<Triangle> triangleListA = this.getTriangleList(this.getEnergyList(timeA));
//List<Triangle> triangleListB = this.getTriangleList(this.getEnergyList(timeB));
//return this.compareSpace(triangleListA, triangleListB, threshold);
//}
/////////////////////////////

//public void setAzimuth(int azimuth) {
////	if(print)System.out.println("setAzimuth("+azimuth+")");
//	this.azimuth = azimuth;
//	for (Energy e : this.getEnergyList()) {
//		e.azimuth = azimuth;
//	}
//}
//
//public void setElevation(int elevation) {
////	if(print)System.out.println("setElevation("+elevation+")");
//	this.elevation = elevation;
//	for (Energy e : this.getEnergyList()) {
//		e.elevation = elevation;
//	}
//}
//public List<Index> getTestIndexList(String name) throws Exception {
//List<Index> indexList = new ArrayList<>();
//Map<String, List<Index>> indexListMap = new HashMap<>();
//List<String> dateList = getDateList("18000101-20200101", Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	this.setCalendar(c);
//	List<Triangle> triangleList = this.getTriangleList("Sun");
//	Index index;
//	for (Triangle t : triangleList) {
//		double distance = t.a;
//		List<Index> list = indexListMap.get(t.toString());
//		if (list == null) {
//			list = new ArrayList<>();
//		}
//		index = new Index();
//		index.value = distance * t.getMass();
//		index.startCalendar = c;
//		list.add(index);
//		indexListMap.put(t.toString(), list);
//	}
//}
//
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	Index index = new Index();
//	index.startCalendar = c;
//	index.value = 0;
//	indexList.add(index);
//}
//for (Triangle t : this.getTriangleList("Sun")) {
//	List<Index> list = indexListMap.get(t.toString());
//	for (int i = 0; i < dateList.size(); i++) {
//		Index index = indexList.get(i);
//		double value = list.get(i).value;
//		index.value += (value);
//		indexList.set(i, index);
//	}
//}
//return indexList;
//}

//public static void main(String[] args) {
//Solar solar = new Solar();
//solar.setCalendar(new GregorianCalendar(1990, 3, 19, 0, 0, 0));
//Sun sun = (Sun) solar.getVariable("Sun");
//List<Energy> energyList = solar.getEnergyList();
//for (Energy e : energyList) {
//	if (!(e instanceof Sun)) {
//		System.out.println(e.name + ": " + sun.getElipticDistance(e));
//		System.out.println(e.name + ": " + sun.getRectangularDistance(e));
//	}
//}
//}
//public void setCalendar(String time) {
//DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//try {
//	Date date = formatter.parse(time);
//	Calendar calendar = Calendar.getInstance();
//	calendar.setTime(date);
//	this.setCalendar(calendar);
//} catch (ParseException e) {
//	e.printStackTrace();
//}
//
//}

//public void setCalendar(Calendar time) {
//this.calendar = time;
//for (Energy energy : energyList) {
//	energy.setCalendar(time);
//}
//}
//public ArrayList<Energy> getEnergyList(String time) {
//return this.getEnergyList(this.getCalendar("yyyy-MM-dd", time));
//}

//public ArrayList<Energy> getEnergyList(Calendar calendar) {
//Sun sun = new Sun(calendar);
//Earth earth = new Earth(calendar, sun);
//Jupiter jupiter = new Jupiter(calendar, sun);
//Mars mars = new Mars(calendar, sun);
//Mercury mercury = new Mercury(calendar, sun);
//Neptune neptune = new Neptune(calendar, sun);
//Saturn saturn = new Saturn(calendar, sun);
//Venus venus = new Venus(calendar, sun);
//Uranus uranus = new Uranus(calendar, sun);
//Luna luna = new Luna(calendar, earth);
//ArrayList<Energy> energyList = new ArrayList<Energy>();
//energyList.add(earth);
//energyList.add(jupiter);
//energyList.add(mars);
//energyList.add(mercury);
//energyList.add(neptune);
//energyList.add(saturn);
//energyList.add(venus);
//energyList.add(uranus);
//energyList.add(sun);
//energyList.add(luna);
//
//return energyList;
//}

//public void paint(Graphics g) {
//if (this.calendar != null) {
//	drawDate(g, this.calendar);
//}
////for (Node e : this.getList()) {
////	if (e instanceof Earth) {
////		Earth earth = (Earth) e;
////		earth.draw(g);
////	} else if (e instanceof Luna) {
////		Luna luna = (Luna) e;
////		luna.draw(g);
////	} else if (e instanceof Jupiter) {
////		Jupiter jupiter = (Jupiter) e;
////		jupiter.draw(g);
////	} else if (e instanceof Venus) {
////		Venus venus = (Venus) e;
////		venus.draw(g);
////	} else if (e instanceof Mars) {
////		Mars mars = (Mars) e;
////		mars.draw(g);
////	} else if (e instanceof Mercury) {
////		Mercury mercury = (Mercury) e;
////		mercury.draw(g);
////	} else if (e instanceof Neptune) {
////		Neptune neptune = (Neptune) e;
////		neptune.draw(g);
////	} else if (e instanceof Saturn) {
////		Saturn saturn = (Saturn) e;
////		saturn.draw(g);
////	} else if (e instanceof Uranus) {
////		Uranus uranus = (Uranus) e;
////		uranus.draw(g);
////	} else if (e instanceof Sun) {
////		Sun sun = (Sun) e;
////		sun.draw(g);
////	}
////}
//}

//public Map<String, List<Space>> searchSpace(String startDate, String endDate, double threshold, double match)
//	throws ParseException {
//Map<String, List<Space>> timeMap = new HashMap<String, List<Space>>();
//Date start = this.getDate(null, startDate);
//Date end = this.getDate(null, endDate);
//GregorianCalendar timeline = this.getCalendar(start);
//List<Space> spaceList = null;
//while (!timeline.getTime().after(end)) {
//	String timelineDate = this.getCalendarString(null, timeline);
//	spaceList = this.searchSpace(timelineDate, startDate, endDate, threshold, match);
//	timeMap.put(timelineDate, spaceList);
//	timeline.add(defaultRate, 1);
//}
//return timeMap;
//}

//public List<Space> searchSpace(String indexDate, String startDate, String endDate, double threshold, double match) {
//GregorianCalendar array = this.getCalendar(null, startDate);
//GregorianCalendar index = this.getCalendar(null, indexDate);
//Date end = this.getDate(null, endDate);
//List<Space> spaceList = new LinkedList<Space>();
//while (!array.getTime().after(end)) {
//	Space space = this.compareSpace(index, array, threshold);
//	if (space.match >= match)
//		spaceList.add(space);
//	array.add(defaultRate, 1);
//}
//return spaceList;
//}

//public List<Index> getAllDistanceIndexList(String name) throws Exception {
//List<Index> allIndexList = new ArrayList<>();
//Map<String, List<Index>> indexListMap = new HashMap<>();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	this.setCalendar(c);
//	Index index;
//	for (Energy e : this.getEnergyList()) {
//		if (!e.name.equals(name)) {
//			double distance = energy.getRectangularDistance(e);
//			List<Index> list = indexListMap.get(e.toString());
//			if (list == null) {
//				list = new ArrayList<>();
//			}
//			index = new Index();
//			index.value = distance;// * ((Spheroid)e).getVolume();
//			index.startCalendar = c;
//			list.add(index);
//			indexListMap.put(e.toString(), list);
//		}
//	}
//}
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	Index index = new Index();
//	index.startCalendar = c;
//	index.value = 0;
//	allIndexList.add(index);
//}
//
//for (Energy e : this.getEnergyList()) {
//	if (!e.name.equals(name)) {
//		List<Index> list = indexListMap.get(e.toString());
//		for (int i = 0; i < dateList.size(); i++) {
//			Index index = allIndexList.get(i);
//			index.value += (list.get(i).value);
//			allIndexList.set(i, index);
//		}
//	}
//}
//return allIndexList;
//}
