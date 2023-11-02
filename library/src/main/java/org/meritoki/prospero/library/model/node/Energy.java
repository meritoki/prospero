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

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.table.TableModel;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.time.TimePlot;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Series;
import org.meritoki.prospero.library.model.unit.Space;
import org.meritoki.prospero.library.model.unit.Table;
import org.meritoki.prospero.library.model.unit.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Energy object provides mass parameter used to calculate Joules
 *
 */
public class Energy extends Variable {

	static Logger logger = LoggerFactory.getLogger(Energy.class.getName());
	@JsonProperty
	public double mass;
	@JsonProperty
	public Space space = new Space();// Global X,Y,Z Point
	@JsonProperty
	public Space center = new Space();// Global X,Y,Z Center
	@JsonProperty
	public Space buffer = new Space();// Global X,Y,Z space minus center
	@JsonIgnore
	public Color color = Color.BLACK;
	@JsonIgnore
	public List<Tunnel> tunnelList = new ArrayList<>();
	@JsonIgnore
	public List<Triangle> triangleList = null;
	@JsonIgnore
	public Map<String, Series> seriesMap = null;//new TreeMap<>();
	@JsonIgnore
	public List<Plot> plotList = new ArrayList<>();
	@JsonIgnore
	public List<String> energyList = new ArrayList<>();

	public Energy(String name) {
		super(name);
		this.initVariableMap();
	}


	
	@Override
	public void query() {
		super.query();
		try {
			this.initPlotList();
			this.addModelObject(new Result(Mode.PAINT));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@JsonIgnore
	public void setSpace(Space space) {
		this.space = space;
	}

	@JsonIgnore
	public void updateSpace() {
//		logger.info(this.name+".updateSpace()");
		this.space = new Space();
		this.buffer = new Space(this.space);
	}

	@JsonIgnore
	public void setCenter(Space center) {
//		logger.info(this.name+".setCenter("+center+") this.space="+this.space);
		this.center = center;
		this.buffer = new Space(this.space);
		this.buffer.subtract(this.center);
	}

	public List<Energy> getEnergyList() {
		List<Energy> energyList = new ArrayList<>();
		if (this.energyList != null && this.energyList.size() > 0) {
			for (String string : this.energyList) {
				Object object = this.getVariable(string);
				if (object instanceof Energy) {
					Energy energy = (Energy) object;
					energyList.add(energy);
				}
			}
		}

		return energyList;
	}

	public List<Triangle> getTriangleList(String name) {
		return (this.getTriangleList(name, this.getEnergyList()));
	}

	public List<Triangle> getTriangleList(String name, List<Energy> energyList) {
		List<Triangle> triangleList = new LinkedList<Triangle>();
		Energy center = (Energy) this.getVariable(name);
		Triangle triangle = null;
		List<Energy> energyStack = new LinkedList<Energy>();
		for (Energy a : energyList) {
			if (a instanceof Orbital && !(a.name.equals(name))) {
				for (Energy b : energyList) {
					if (b instanceof Orbital && !(b.name.equals(name)) && a != b && !energyStack.contains(b)) {
						triangle = new Triangle(center, a, b);
						if (!triangleList.contains(triangle))
							triangleList.add(triangle);
					}
				}
				energyStack.add(a);
			}
		}
		return triangleList;
	}

	public List<Variable> getTunnelList() {
		List<Variable> tunnelList = new ArrayList<>();
		List<Energy> energyList = this.getEnergyList();
		for (Energy a : energyList) {
			for (Energy b : energyList) {
				if (a instanceof Orbital && b instanceof Orbital && !a.equals(b)) {
					Tunnel tunnel = new Tunnel((Orbital) a, (Orbital) b);
					if (!tunnelList.contains(tunnel)) {
						tunnelList.add(tunnel);
					}
				}
			}
		}
		return tunnelList;
	}

	public Map<String, Series> initSeriesMap() throws Exception {
		logger.info("initSeriesMap()");
		Map<String, Series> map = new HashMap<>();
		this.seriesMapPut(map, "X", this.tunnelList);
		this.seriesMapPut(map, "Z", this.tunnelList);
		this.seriesMapPut(map, "A", this.tunnelList);
		this.seriesMapPut(map, "B", this.tunnelList);
		this.seriesMapPut(map, "C", this.tunnelList);
		this.seriesMapPut(map, "Distance", this.tunnelList);
		this.seriesMapPut(map, "Gravity Force", this.tunnelList);
		this.seriesMapPut(map, "Charge Force", this.tunnelList);
		this.seriesMapPut(map, "Calculate Gravity Force", this.tunnelList);
		this.seriesMapPut(map, "Calculate Charge Force", this.tunnelList);
		this.seriesMapPut(map, "Charge", this.tunnelList);
		this.seriesMapPut(map, "Resistance", this.tunnelList);
		this.seriesMapPut(map, "Resistance Ratio", this.tunnelList);
		this.seriesMapPut(map, "Tesla", this.tunnelList);
		this.seriesMapPut(map, "Voltage", this.tunnelList);
		this.seriesMapPut(map, "Angle", this.triangleList);
		return map;
	}
	
	public void initTableList() {
		
	}
	

	public Map<String, Series> getSeriesMap() throws Exception {
//		if (this.seriesMap == null) {
			this.seriesMap = this.initSeriesMap();
//		}
		return this.seriesMap;
	}

	public void seriesMapPut(Map<String, Series> seriesMap, String key, Object object) throws Exception {
//		System.out.println("mapPut("+map.size()+","+key+","+object+")");
		Series series = seriesMap.get(key);
		if (series == null && object != null) {
			seriesMap.put(key, this.getSeries(key, object));
		}
	}

//	public void mapPut(Map<String, Series> map, Calendar calendar, String key, double value) {
////		logger.info("mapPut("+map.size()+","+calendar.getTime()+","+key+","+value+")");
//		Index index = new Index();
//		index.startCalendar = calendar;
//		Series indexList = map.get(key);
//		if (indexList == null) {
//			indexList = new Series();
//		}
//		index.value = value;
//		indexList.add(index);
//		map.put(key, indexList);
//	}
	
	public void seriesMapPut(Map<String, Series> seriesMap, String key, Index index) {
//		logger.info("mapPut("+map.size()+","+calendar.getTime()+","+key+","+value+")");
		Series series = seriesMap.get(key);
		if (series == null) {
			series = new Series();
		}
		series.add(index);
		seriesMap.put(key, series);
	}

	public Series getSeries(String key, Object object) throws Exception {
		logger.info(this+".getSeries(" + key + "," + object + ")");
//		List<Index> indexList = new ArrayList<>();
		Series series = new Series();
		if (object instanceof List<?>) {
			List<Object> objectList = (List<Object>) object;
			Series s = new Series();
			// Have to iterate over tunnels
			for (Object o : objectList) {
				if (o instanceof Tunnel) {
					s = ((Tunnel) o).getSeriesMap().get(key);
				} else if (o instanceof Triangle) {
					s = ((Triangle) o).getSeriesMap().get(key);
				}
				if (s != null) {
					for (int x = 0; x < s.indexList.size(); x++) { // Index index:t.getGravityForceList()) {
						Index index = s.indexList.get(x);
						Index i = (series.indexList.size() > x) ? series.indexList.get(x) : new Index();
						i.value += index.value;
						i.startCalendar = index.startCalendar;
						series.add(i);
					}
				}
			}
		}
		return series;
	}
	
	@Override
	public List<Plot> getPlotList() throws Exception {
		return this.plotList;
	}

	@Override
	public void initPlotList() throws Exception {
//		logger.info("initPlotList()");
		List<Plot> plotList = new ArrayList<>();
		for (Entry<String, Boolean> variable : this.variableMap.entrySet()) {
			String variableKey = variable.getKey();
			Boolean variableLoad = variable.getValue();
			if (variableLoad) {
				TimePlot plot = null;
				Series series = null;
				String unit = null;
				switch (variableKey) {
				case "X": {
					series = this.getSeriesMap().get("X");
					unit = "(N/N)";
					break;
				}
				case "Z": {
					series = this.getSeriesMap().get("Z");
					unit = "  ";
					break;
				}
				case "A": {
					series = this.getSeriesMap().get("A");
					unit = "  ";
					break;
				}
				case "B": {
					series = this.getSeriesMap().get("B");
					unit = "  ";
					break;
				}
				case "C": {
					series = this.getSeriesMap().get("C");
					unit = "  ";
					break;
				}
				case "Calculate Distance": {
					series = this.getSeriesMap().get("Calculate Distance");
					unit = "meters(m)";
					break;
				}
				case "Calculate Gravity Force": {
					series = this.getSeriesMap().get("Calculate Gravity Force");
					unit = "Newtons(N)";
					break;
				}
				case "Gravity Force": {
					series = this.getSeriesMap().get("Gravity Force");
					unit = "Newtons(N)";
					break;
				}
				case "Charge Force": {
					series = this.getSeriesMap().get("Charge Force");
					unit = "Newtons(N)";
					break;
				}
				case "Calculate Charge Force": {
					series = this.getSeriesMap().get("Calculate Charge Force");
					unit = "Newtons(N)";
					break;
				}
				case "Charge": {
					series = this.getSeriesMap().get("Charge");
					unit = "C^2";
					break;
				}
				case "Distance": {
					series = this.getSeriesMap().get("Distance");
					unit = "Meters(m)";
					break;
				}
				case "Resistance": {
					series = this.getSeriesMap().get("Resistance");
					unit = "Ohms";
					break;
				}
				case "Resistance Ratio": {
					series = this.getSeriesMap().get("Resistance Ratio");
					unit = "";
					break;
				}
				case "Tesla": {
					series = this.getSeriesMap().get("Tesla");
					unit = "T";
					break;
				}
				case "Angle": {
					series = this.getSeriesMap().get("Angle");
					unit = "Degrees";
					break;
				}
				case "Voltage": {
					series = this.getSeriesMap().get("Voltage");
					unit = "Volts (V)";
					break;
				}
				case "Print": {
					this.print();
					this.variableMap.put(variableKey, false);
					break;
				}
				case "X Ratio": {
					series = this.getSeriesMap().get("X Ratio");
					unit = "";
					break;
				}
				case "Charge Force Ratio": {
					series = this.getSeriesMap().get("Charge Force Ratio");
					unit = "";
					break;
				}
				case "Gravity Force Ratio": {
					series = this.getSeriesMap().get("Gravity Force Ratio");
					unit = "";
					break;
				}
				}
				if (series != null) {
					series.map.put("startCalendar", this.startCalendar);
					series.map.put("endCalendar", this.endCalendar);
					series.map.put("query", new Query());
					series.map.put("region", name);
					plot = new TimePlot(series);// this.startCalendar, this.endCalendar, blackPointMatrix, null);
					plot.setTitle(this.name + " " + variableKey);
					plot.setXLabel("Time");
					plot.setYLabel(unit);
					plotList.add(plot);
				}
			}
		}

		this.plotList = plotList;
	}

	public void addTunnel(Tunnel tunnel) {
		if (this.tunnelList == null) {
			this.tunnelList = new ArrayList<>();
		}
		if (!this.tunnelList.contains(tunnel)) {
			this.tunnelList.add(tunnel);
		}
	}

	public void addTriangle(Triangle triangle) {
		if (!this.triangleList.contains(triangle)) {
			this.triangleList.add(triangle);
//			this.variableMap.put(triangle.name, false);
		}
	}

	public double getMass() {
		return this.mass;
	}

	public double getCentroidMass() {
		return Unit.ELECTRON_MASS;
	}

	/**
	 * Joules is calculated using mass and the speed of light
	 * E=m*c^2
	 * 
	 * @return
	 */
	public double getJoules() {
		return this.getMass() * Math.pow(Unit.c, 2);
	}

//	/**
//	 * Using the mass of an electron and the speed of light and the time it takes to reach the barycenter traveling at the speed of light, 
//	 * I was able to calculate Amperes for an object with mass. The same Ampere result is given by getAmperes() 
//	 * 
//	 * @return
//	 */
//	public double getCentroidJoules() {
//		return this.getCentroidMass() * Math.pow(Unit.c, 2);
//	}

	/**
	 * Voltage is calculated by dividing Joules by Coulombs.
	 * 
	 * @return
	 */
	public double getVoltage() {
		return this.getJoules() / this.getCentroidCoulomb();
	}

//	public double getCentroidVoltage() {
//		return this.getJoules()/this.getCentroidCoulomb();
//	}

	/**
	 * Coulombs is calculated by dividing Joules by Voltage of Spheriod.
	 * 
	 * @return
	 */
	public double getCoulomb() {
		return -(this.mass / Unit.ELECTRON_MASS) * Unit.COULOMBS;// -Unit.COULOMBS;
	}

	public double getCentroidCoulomb() {
		return Unit.COULOMBS;// (this.mass/Unit.ELECTRON_MASS)*Unit.COULOMBS;
	}

//	public double getElipticDistance(Energy energy) {
//		Vector3D difference = this.space.elliptic.subtract(energy.space.elliptic);
//		double distance = Math.sqrt(Math.pow((double) difference.getX(), 2) + Math.pow((double) difference.getY(), 2)
//				+ Math.pow((double) difference.getZ(), 2));
//		return distance;
//	}

	/**
	 * Function returns distance in AU
	 * 
	 * @param energy
	 * @return
	 */
	public double getDistance(Energy energy) {
		Vector3D difference = this.buffer.rectangular.subtract(energy.buffer.rectangular);
		double distance = Math.sqrt(Math.pow((double) difference.getX(), 2) + Math.pow((double) difference.getY(), 2)
				+ Math.pow((double) difference.getZ(), 2));
//		distance *= Unit.ASTRONOMICAL;
//		distance *= 1000;
		return distance;
	}

	public double getKineticEnergy() {
		double kineticEnergy = 0;
		kineticEnergy += 0.5 * this.mass * Math.pow(((Orbital) this).angularVelocity, 2)
				* Math.pow(((Spheroid) this).radius, 2);
		return kineticEnergy;
	}

	public double getKineticLinearOrbitEnergy() {
		return 0.5 * this.mass * Math.pow(((Orbital) this).angularVelocity, 2);
	}

	public Vector3D getGravity(Energy energy) {
		Vector3D difference = energy.space.rectangular.subtract(this.space.rectangular);
		double distance = Math.sqrt(Math.pow((double) difference.getX(), 2) + Math.pow((double) difference.getY(), 2)
				+ Math.pow((double) difference.getZ(), 2));
		distance *= Unit.ASTRONOMICAL;// Kilometers
		distance *= 1000;// Meters
		double gravityForce = (Unit.GRAVITATIONAL_CONSTANT * energy.mass * this.mass) / (Math.pow(distance, 2));
		Vector3D force = null;
		if (distance != 0) {
			force = difference.normalize();
			force.scalarMultiply(gravityForce);
		}
		return force;
	}

	public double getMagnitude(Vector3D difference) {
		double distance = Math.sqrt(Math.pow((double) difference.getX(), 2) + Math.pow((double) difference.getY(), 2)
				+ Math.pow((double) difference.getZ(), 2));
		return distance;
	}

	public Vector3D getBarycenter(List<Tunnel> tunnelList) throws Exception {
		Vector3D baryCenter = new Vector3D(0, 0, 0);
		for (Tunnel t : tunnelList) {
			baryCenter = baryCenter.add(t.getBarycenterA());
		}
		return baryCenter;
	}

	public double getBarycenterMass(List<Tunnel> tunnelList) throws Exception {
		double baryCenter = 0;
		for (Tunnel t : tunnelList) {
			baryCenter += t.getMass();
		}
		baryCenter /= tunnelList.size();
		logger.info("getBarycenterMass(" + tunnelList.size() + ") baryCenter=" + baryCenter);
		return baryCenter;
	}

	public double getBaryCenter(Energy energy) {
		return this.getDistance(energy) * (energy.mass) / (this.mass + energy.mass);
	}

	public double getGravitationalPotentialEnergy(Energy energy) {
		double distance = this.getDistance(energy);
		return -(Unit.GRAVITATIONAL_CONSTANT * energy.mass * this.mass) / (distance);
	}

	public double getGravityForce(Energy energy) {
		double distance = this.getDistance(energy);
		return (Unit.GRAVITATIONAL_CONSTANT * ((energy.mass * this.mass) / (Math.pow(distance, 2))));
	}

	public double getChargeAcceleration(double radius) {
		return -Unit.k * this.getCoulomb() / Math.pow(radius, 2);
	}

	public double getChargeAcceleration(double charge, double radius) {
		return -Unit.k * charge / Math.pow(radius, 2);
	}

	public double getGravityAcceleration(double radius) {
		return Unit.GRAVITATIONAL_CONSTANT * this.mass / Math.pow(radius, 2);
	}

	public double getGravityAcceleration(double mass, double radius) {
		return Unit.GRAVITATIONAL_CONSTANT * mass / Math.pow(radius, 2);
	}

	public double getGravityForce(double mass, double distance) {
		return (Unit.GRAVITATIONAL_CONSTANT * ((mass * this.mass) / (Math.pow(distance, 2))));
	}

	public double getCentroidGravityAcceleration() {
		return this.getGravityAcceleration(Unit.ELECTRON_MASS, Unit.ELECTRON_RADIUS * 100);
	}

	public double getCentroidChargeAcceleration() {
		return this.getChargeAcceleration(Unit.COULOMBS, Unit.ELECTRON_RADIUS);
	}

	public double getCentroidGravityForce() {
		return this.getCentroidMass() * this.getCentroidGravityAcceleration();
	}

	public double getCentroidChargeForce() {
		return this.getCentroidMass() * this.getCentroidChargeAcceleration();
	}

	public double getX() {
		return this.getX(this.tunnelList);
	}

	public double getX(List<Tunnel> tunnelList) {
		double sum = 0;
		for (Tunnel tunnel : tunnelList) {
			sum += tunnel.getX();
		}
		return sum;
	}

	/**
	 * mass/electron mass provides the number of electrons multiplied by C for one
	 * electron, gives C for Spheroid
	 * 
	 * @return
	 */
	public double getCharge() {
		return this.getCentroidCoulomb() * this.getCoulomb();
	}

	public double getChargeForce(List<Tunnel> tunnelList) {
		double sum = 0;
		for (Tunnel tunnel : tunnelList) {
			sum += tunnel.getChargeForce();
		}
		return sum;
	}

	public double getResistanceRatio() {
		return this.getResistanceRatio(this.tunnelList);
	}

	public double getResistanceRatio(List<Tunnel> tunnelList) {
		double sum = 0;
		for (Tunnel tunnel : tunnelList) {
			sum += tunnel.getResistanceRatio();
		}
		return sum;
	}
	
	public static TableModel getTableModel(List<Energy> energyList) {
		Object[] objectArray = getObjectArray(energyList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}
	
	public static Object[] getObjectArray(List<Energy> energyList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		if (energyList != null) {
			if (energyList.size() > 0) {
				for (int i = 0; i < energyList.size(); i++) {
					Energy e = energyList.get(i);
					if (e instanceof Spheroid) {
						Spheroid s = (Spheroid) e;
						if (i == 0) {
							columnArray = Table.getColumnNames(18).toArray();
							dataMatrix = new Object[energyList.size() + 1][18];
							dataMatrix[i][0] = "mass";
							dataMatrix[i][1] = "mass(centroid)";
							dataMatrix[i][2] = "coulomb";
							dataMatrix[i][3] = "coulomb(centroid)";
							dataMatrix[i][4] = "acceleration(charge)";
							dataMatrix[i][5] = "force(charge)";
							dataMatrix[i][6] = "force(calculate charge)";
							dataMatrix[i][7] = "force(ratio charge)";
							dataMatrix[i][8] = "acceleration(gravity)";
							dataMatrix[i][9] = "acceleration (centroid gravity)";
							dataMatrix[i][10] = "force(gravity)";
							dataMatrix[i][11] = "force(centroid gravity)";
							dataMatrix[i][12] = "voltage";
							dataMatrix[i][13] = "joules";
							dataMatrix[i][14] = "resistence(ratio)";
						}
						double X = s.getX();
						dataMatrix[i + 1][0] = s.getMass();
						dataMatrix[i + 1][1] = s.getCentroidMass();
						dataMatrix[i + 1][2] = s.getCoulomb();
						dataMatrix[i + 1][3] = s.getCentroidCoulomb();
						dataMatrix[i + 1][4] = s.getChargeAcceleration();
						dataMatrix[i + 1][5] = s.getChargeForce();
						dataMatrix[i + 1][6] = s.calculateChargeForce(X);
						dataMatrix[i + 1][7] = s.getChargeForceRatio(X);
						dataMatrix[i + 1][8] = s.getGravityAcceleration();
						dataMatrix[i + 1][9] = s.getCentroidGravityAcceleration();
						dataMatrix[i + 1][10] = s.getGravityForce();
						dataMatrix[i + 1][11] = s.getCentroidGravityForce();
						dataMatrix[i + 1][12] = s.getVoltage();
						dataMatrix[i + 1][13] = s.getJoules();
						dataMatrix[i + 1][14] = s.getResistanceRatio();
					} else if(e instanceof Tunnel) {
						Tunnel t = (Tunnel)e;
						if (i == 0) {
							columnArray = Table.getColumnNames(13).toArray();
							dataMatrix = new Object[energyList.size() + 1][13];
							dataMatrix[i][0] = "time seconds";
							dataMatrix[i][1] = "time seconds a";
							dataMatrix[i][2] = "time seconds b";
							dataMatrix[i][3] = "distance";
							dataMatrix[i][4] = "distance barycenter a";
							dataMatrix[i][5] = "distance barycenter b";
							dataMatrix[i][6] = "mass";
							dataMatrix[i][7] = "mass centroid";
							dataMatrix[i][8] = "mass a";
							dataMatrix[i][9] = "mass b";
							dataMatrix[i][10] = "mass sum";
							dataMatrix[i][11] = "mass product";
							dataMatrix[i][12] = "x";
						}
						dataMatrix[i + 1][0] = t.getSeconds();
						dataMatrix[i + 1][1] = t.getSecondsA();
						dataMatrix[i + 1][2] = t.getSecondsB();
						dataMatrix[i + 1][3] = t.getDistance();
						dataMatrix[i + 1][4] = t.getBarycenterDistanceA();
						dataMatrix[i + 1][5] = t.getBarycenterDistanceB();
						dataMatrix[i + 1][6] = t.getMass();
						dataMatrix[i + 1][7] = t.getCentroidMass();
						dataMatrix[i + 1][8] = t.a.getMass();
						dataMatrix[i + 1][9] = t.b.getMass();
						dataMatrix[i + 1][10] = t.getMassSum();
						dataMatrix[i + 1][11] = t.getMassProduct();
						dataMatrix[i + 1][12] = t.getX();
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}

	public void print() {
		logger.info(this.name + " Mass:" + this.getMass());
		logger.info(this.name + " Voltage:" + this.getVoltage());
		logger.info(this.name + " Joules:" + this.getJoules());
		logger.info(this.name + " Coulomb:" + this.getCoulomb());
	}

	@JsonIgnore
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
	}
}
//@Override
//public void initVariableMap() {
//	super.initVariableMap();
//	if (this.tunnelList != null) {
//		this.variableMap.put("Gravity Force", false);
//		this.variableMap.put("Charge Force", false);
//		this.variableMap.put("Calculate Gravity Force", false);
//		this.variableMap.put("Calculate Charge Force", false);
//		this.variableMap.put("Charge", false);
//		this.variableMap.put("Distance", false);
//		this.variableMap.put("X", false);
//		this.variableMap.put("Z", false);
//		this.variableMap.put("A", false);
//		this.variableMap.put("B", false);
//		this.variableMap.put("C", false);
//		this.variableMap.put("Print", false);
//		this.variableMap.put("Resistance", false);
//		this.variableMap.put("Tesla", false);
//		this.variableMap.put("Voltage", false);
//		this.variableMap.put("Print", false);
//		this.variableMap.put("X Ratio", false);
//	}
//	if (this.triangleList != null) {
//		this.variableMap.put("Angle", false);
//	}
//}
//List<List<Index>> blackPointMatrix = new ArrayList<>();
//blackPointMatrix.add((indexList));
//@Override
//public void initVariableMap() {
//	super.initVariableMap();
//	if (this.tunnelList != null) {
//		this.variableMap.put("Gravity Force", false);
//		this.variableMap.put("Charge Force", false);
//		this.variableMap.put("Calculate Gravity Force", false);
//		this.variableMap.put("Calculate Charge Force", false);
//		this.variableMap.put("Charge", false);
//		this.variableMap.put("Distance", false);
//		this.variableMap.put("X", false);
//		this.variableMap.put("Z", false);
//		this.variableMap.put("A", false);
//		this.variableMap.put("B", false);
//		this.variableMap.put("C", false);
//		this.variableMap.put("Print", false);
//		this.variableMap.put("Resistance", false);
//		this.variableMap.put("Tesla", false);
//		this.variableMap.put("Voltage", false);
//		this.variableMap.put("Print", false);
//		this.variableMap.put("X Ratio", false);
//	}
//	if (this.triangleList != null || this instanceof Triangle) {
//		this.variableMap.put("Angle", false);
//	}
//}
//@JsonIgnore
//public Map<String, List<Index>> indexListMap;
//@JsonIgnore
//public DecimalFormat df = new DecimalFormat("0.######E0");
//@JsonIgnore
//public String period = "18000101-20200101";
//for (Variable n : this.getList()) {
//if (n instanceof Energy) {
//	energyList.add((Energy) n);
//}
//}
//public void scale(double percentage) {
//this.space.elliptic = this.space.elliptic.scalarMultiply(percentage);
//}
//public Point getPoint(Point point) {
//Point p = null;
//double theta = Math.PI * azimuth / 180.0;
//double phi = Math.PI * elevation / 180.0;
//float cosT = (float) Math.cos(theta);
//float sinT = (float) Math.sin(theta);
//float cosP = (float) Math.cos(phi);
//float sinP = (float) Math.sin(phi);
//float cosTcosP = cosT * cosP;
//float cosTsinP = cosT * sinP;
//float sinTcosP = sinT * cosP;
//float sinTsinP = sinT * sinP;
//// The following two lines fixed defects when viewing in 3 Dimensions
//float near = 32; // distance from eye to near plane
//float nearToObj = 8f; // 1.5// distance from near plane to center of object
//double x0 = point.x;
//double y0 = point.y;
//double z0 = point.z;
//// compute an orthographic projection
//float x1 = (float) (cosT * x0 + sinT * z0);
//float y1 = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);
//// now adjust things to get a perspective projection
//float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);
//x1 = x1 * near / (z1 + near + nearToObj);
//y1 = y1 * near / (z1 + near + nearToObj);
//p = new Point();
//p.x = x1;
//p.y = y1;
//p.z = z1;
//return p;
//}
//@JsonProperty
//public double scale;	
//@JsonIgnore
//public int azimuth = 0;
//@JsonIgnore
//public int elevation = 0;
//public double getGravityForce(Energy energy) {
//Vector3D distance = energy.space.eliptic.subtract(this.space.eliptic);// distance is in kilometers
//System.out.println(energy.mass + ":" + this.mass);
//double magnitude = Math.sqrt(Math.pow((double) distance.getX(), 2) + Math.pow((double) distance.getY(), 2)
//		+ Math.pow((double) distance.getZ(), 2));
//System.out.println("distance: " + magnitude);
//magnitude *= 1000; // convert to meters for calculation
//return (Unit.GRAVITATIONAL_CONSTANT * ((energy.mass * this.mass) / (Math.pow(magnitude, 2))));
//}
//public double getGravitationalPotentialEnergy(Energy energy) {
//Vector3D distance = energy.space.eliptic.subtract(this.space.eliptic);
//double magnitude = Math.sqrt(Math.pow((double) distance.getX(), 2) + Math.pow((double) distance.getY(), 2)
//		+ Math.pow((double) distance.getZ(), 2));
//magnitude *= 1000;
//System.out.println(energy.mass + ":" + this.mass);
//double gravitationalPotentialEnergy = -(Unit.GRAVITATIONAL_CONSTANT * energy.mass * this.mass) / (magnitude);
//return gravitationalPotentialEnergy;
//}

//public void setPosition(Vector3D position) {
//this.position = position;
//}

//public String getName() {
//return name;
//}
//
//public void setName(String name) {
//this.name = name;
//}

//public GregorianCalendar getCalendar(String format, String time) {
//GregorianCalendar calendar = new GregorianCalendar();
//Date date = this.getDate(format, time);
//if (date != null)
//	calendar.setTime(date);
//return calendar;
//}
//
//public GregorianCalendar getCalendar(Date date) {
//GregorianCalendar calendar = new GregorianCalendar();
//calendar.setTime(date);
//return calendar;
//}
//
//public Date getDate(String format, String time) {
//SimpleDateFormat sdf = new SimpleDateFormat((format == null) ? defaultTimeFormat : format);
//Date date = null;
//try {
//	date = sdf.parse(time);
//} catch (ParseException e) {
//	e.printStackTrace();
//}
//return date;
//}
//
//public String getDateString(String format, Date date) {
//String string = new SimpleDateFormat((format == null) ? defaultTimeFormat : format).format(date);
//return string;
//}
//
//public String getCalendarString(String format, Calendar calendar) {
//return this.getDateString(format, calendar.getTime());
//}

//public double getDistance(Energy energy) {
//Vector3D distance = energy.space.eliptic.subtract(this.space.eliptic);
//double magnitude = Math.sqrt(Math.pow((double) distance.getX(), 2) + Math.pow((double) distance.getY(), 2)
//		+ Math.pow((double) distance.getZ(), 2));
//magnitude *= 1000;
//return magnitude;
//}
//public Vector3D position = new Vector3D(0, 0, 0);
//public Vector3D force = new Vector3D(0, 0, 0);

//default: {
//for(Tunnel t: this.tunnelList) {
//	if(variableKey.equals(t.name)) {
//		Map<String,List<Index>> map = t.getIndexListMap();
//		for(Entry<String,List<Index>> entry: map.entrySet()) {
//			indexList = entry.getValue();
//			List<List<Index>> blackPointMatrix = new ArrayList<>();
//			blackPointMatrix.add((indexList));
//			plot = new TimePlot(this.startCalendar,
//					this.endCalendar, blackPointMatrix, null);
//			plot.setTitle(variableKey+" "+entry.getKey());
//			plot.setXLabel("Time");
//			switch (entry.getKey()) {
//			case "Calculate Gravity Force":{
//				unit = "Newtons(N)";
//				break;
//			}
//			case "Gravity Force":{
//				unit = "Newtons(N)";
//				break;
//			}
//			case "Charge Force":{
//				unit = "Newtons(N)";
//				break;
//			}
//			case "Distance":{
//				unit = "Meters(m)";
//				break;
//			}
//			case "Calculate Distance":{
//				unit = "Meters(m)";
//				break;
//			}
//			case "Tesla":{
//				unit = "Tesla";
//				break;
//			}
//			default: {
//				unit = null;
//			}
//			}
//			plot.setYLabel(unit);
//			plotList.add(plot);
//		}
//	}
//}
//indexList = null;
//}

//public List<Index> getCList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("C");
//	for (int x = 0; x < list.size(); x++) { // Index index:t.getGravityForceList()) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}
//
//public List<Index> getCalculateGravityForceList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("Calculate Gravity Force");
//	for (int x = 0; x < list.size(); x++) { // Index index:t.getGravityForceList()) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}
//
//public List<Index> getGravityForceList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("Gravity Force");
//	for (int x = 0; x < list.size(); x++) { // Index index:t.getGravityForceList()) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}
//
//public List<Index> getChargeForceList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("Charge Force");
//	for (int x = 0; x < list.size(); x++) { // Index index:t.getGravityForceList()) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}
//
//public List<Index> getCalculateDistanceList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("Calculate Distance");
//	for (int x = 0; x < list.size(); x++) { // Index index:t.getGravityForceList()) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}
//
//public List<Index> getDistanceList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("Distance");
//	for (int x = 0; x < list.size(); x++) { // Index index:t.getGravityForceList()) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}
//
//public List<Index> getResistanceList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("Resistance");
//	for (int x = 0; x < list.size(); x++) { // Index index:t.getGravityForceList()) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}
//
//public List<Index> getTeslaList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("Tesla");
//	for (int x = 0; x < list.size(); x++) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}
//
//public List<Index> getVoltageList(List<Tunnel> tunnelList) throws Exception {
//List<Index> indexList = new ArrayList<>();
//List<Index> list = null;
//// Have to iterate over tunnels
//for (Tunnel t : tunnelList) {
//	list = t.getIndexListMap().get("Voltage");
//	for (int x = 0; x < list.size(); x++) {
//		Index index = list.get(x);
//		Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
//		i.value += index.value;
//		i.startCalendar = index.startCalendar;
//		indexList.add(i);
//	}
//}
//return indexList;
//}

//public Map<String, List<Index>> getAnglePlanetsIndexListMap() throws Exception {
//Map<String, List<Index>> indexListMap = new HashMap<>();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	this.setCalendar(c);
//	List<Triangle> triangleList = this.getTriangleList(this.getData());
//	Index index;
//	for (Triangle t : triangleList) {
//		double angle = t.A;
//		List<Index> list = indexListMap.get(t.toString());
//		if (list == null) {
//			list = new ArrayList<>();
//		}
//		index = new Index();
//		index.value = angle;
//		index.startCalendar = c;
//		list.add(index);
//		indexListMap.put(t.toString(), list);
//	}
//}
//return indexListMap;
//}
