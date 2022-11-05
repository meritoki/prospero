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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.time.TimePlot;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Space;
import org.meritoki.prospero.library.model.unit.Unit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Energy extends Variable {

	static Logger logger = LogManager.getLogger(Energy.class.getName());
	@JsonProperty
	public double mass;
	@JsonProperty
	public Space space = new Space();//Global X,Y,Z Point
	@JsonProperty
	public Space center = new Space();//Global X,Y,Z Center
	@JsonProperty
	public Space buffer = new Space();//Global X,Y,Z space minus center 
	@JsonIgnore
	public Color color = Color.BLACK;
	///////////////////////////////////////
	@JsonIgnore
	public List<Tunnel> tunnelList = null;
	@JsonIgnore
	public List<Triangle> triangleList = null;
	@JsonIgnore
	public Map<String, List<Index>> indexListMap;
	@JsonIgnore
	public DecimalFormat df = new DecimalFormat("0.######E0");
	@JsonIgnore
	public String period = "18000101-20200101";

	public Energy(String name) {
		super(name);
	}

	public void initVariableMap() {
		if (this.tunnelList != null || this instanceof Tunnel) {
			this.variableMap.put("Gravity Force", false);
			this.variableMap.put("Charge Force", false);
			this.variableMap.put("Calculate Gravity Force", false);
			this.variableMap.put("Calculate Charge Force", false);
			this.variableMap.put("Charge", false);
			this.variableMap.put("Distance", false);
			this.variableMap.put("X", false);
			this.variableMap.put("Z", false);
			this.variableMap.put("A", false);
			this.variableMap.put("B", false);
			this.variableMap.put("C", false);
			this.variableMap.put("Print", false);
			this.variableMap.put("Resistance", false);
			this.variableMap.put("Tesla", false);
			this.variableMap.put("Voltage", false);
			this.variableMap.put("Print", false);
			this.variableMap.put("X Ratio", false);
		}
		if (this.triangleList != null || this instanceof Triangle) {
			this.variableMap.put("Angle", false);
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
//		logger.info(this.name+".setCenter("+center+") A this.buffer="+this.buffer);
		this.buffer.subtract(this.center);
//		logger.info(this.name+".setCenter("+center+") B this.buffer="+this.buffer);
// Disabled, Must be done by Orbitals
//		List<Variable> nodeList = this.getChildren();
//		for (Variable n : nodeList) {
//			if(n instanceof Energy) {
//				Energy e = (Energy)n;
//				e.setCenter(center);
//			}
//		}
	}
	


	public List<Energy> getEnergyList() {
		List<Energy> energyList = new ArrayList<>();
		for (Variable n : this.getList()) {
			if (n instanceof Energy) {
				energyList.add((Energy) n);
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

	public List<Tunnel> getTunnelList() {
		List<Tunnel> tunnelList = new ArrayList<>();
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
	
	public Map<String,List<Index>> initIndexListMap() throws Exception{
		System.out.println("initIndexListMap()");
		Map<String,List<Index>> map = new HashMap<>();
		return map;
	}

	public Map<String, List<Index>> getIndexListMap() throws Exception {
		Map<String, List<Index>> map = this.indexListMap;
		if (map == null) {
			map = new HashMap<>();
			this.indexListMap = map;
		}
		this.mapPut(map, "X", this.tunnelList);
		this.mapPut(map, "Z", this.tunnelList);
		this.mapPut(map, "A", this.tunnelList);
		this.mapPut(map, "B", this.tunnelList);
		this.mapPut(map, "C", this.tunnelList);
		this.mapPut(map, "Distance", this.tunnelList);
		this.mapPut(map, "Gravity Force", this.tunnelList);
		this.mapPut(map, "Charge Force", this.tunnelList);
		this.mapPut(map, "Calculate Gravity Force", this.tunnelList);
		this.mapPut(map, "Calculate Charge Force", this.tunnelList);
		this.mapPut(map, "Charge", this.tunnelList);
		this.mapPut(map, "Resistance", this.tunnelList);
		this.mapPut(map, "Resistance Ratio", this.tunnelList);
		this.mapPut(map, "Tesla", this.tunnelList);
		this.mapPut(map, "Voltage", this.tunnelList);
		this.mapPut(map, "Angle", this.triangleList);
		return map;
	}
	
	public void mapPut(Map<String,List<Index>> map, String key, Object object) throws Exception {
//		System.out.println("mapPut("+map.size()+","+key+","+object+")");
		List<Index> indexList = map.get(key);
		if (indexList == null && object != null) {
			map.put(key, this.getIndexList(key,object));
		}
	}
	
	public void mapPut(Map<String,List<Index>> map, Calendar calendar, String key, double value) {
//		System.out.println("mapPut("+map.size()+","+calendar.getTime()+","+key+","+value+")");
		Index index = new Index();
		index.startCalendar = calendar;
		List<Index> indexList = map.get(key);
		if(indexList == null) {
			indexList = new ArrayList<>();
		}
		index.value = value;
		indexList.add(index);
		map.put(key,indexList);
	}
	
	public List<Index> getIndexList(String key, Object object) throws Exception {
		System.out.println("getIndexList("+key+","+object+")");
		List<Index> indexList = new ArrayList<>();
		if(object instanceof List<?>) {
			List<Object> objectList = (List<Object>)object;
			List<Index> list = null;
			// Have to iterate over tunnels
			for (Object o : objectList) {
				if(o instanceof Tunnel) {
					list = ((Tunnel)o).getIndexListMap().get(key);
				} else if(o instanceof Triangle) {
					list = ((Triangle)o).getIndexListMap().get(key);
				} 
				for (int x = 0; x < list.size(); x++) { // Index index:t.getGravityForceList()) {
					Index index = list.get(x);
					Index i = (indexList.size() > x) ? indexList.get(x) : new Index();
					i.value += index.value;
					i.startCalendar = index.startCalendar;
					indexList.add(i);
				}
			}
		}
		return indexList;
	}



	@Override
	public List<Plot> getPlotList() throws Exception {
		List<Plot> plotList = new ArrayList<>();
		for (Entry<String, Boolean> variable : this.variableMap.entrySet()) {
			String variableKey = variable.getKey();
			Boolean variableLoad = variable.getValue();
			if (variableLoad) {
				TimePlot plot = null;
				List<Index> indexList = null;
				String unit = null;
				switch (variableKey) {
				case "X": {
					indexList = this.getIndexListMap().get("X");
					unit = "(N/N)";
					break;
				}
				case "Z": {
					indexList = this.getIndexListMap().get("Z");
					unit = "  ";
					break;
				}
				case "A": {
					indexList = this.getIndexListMap().get("A");
					unit = "  ";
					break;
				}
				case "B": {
					indexList = this.getIndexListMap().get("B");
					unit = "  ";
					break;
				}
				case "C": {
					indexList = this.getIndexListMap().get("C");
					unit = "  ";
					break;
				}
				case "Calculate Distance": {
					indexList = this.getIndexListMap().get("Calculate Distance");
					unit = "meters(m)";
					break;
				}
				case "Calculate Gravity Force": {
					indexList = this.getIndexListMap().get("Calculate Gravity Force");
					unit = "Newtons(N)";
					break;
				}
				case "Gravity Force": {
					indexList = this.getIndexListMap().get("Gravity Force");
					unit = "Newtons(N)";
					break;
				}
				case "Charge Force": {
					indexList = this.getIndexListMap().get("Charge Force");
					unit = "Newtons(N)";
					break;
				}
				case "Calculate Charge Force": {
					indexList = this.getIndexListMap().get("Calculate Charge Force");
					unit = "Newtons(N)";
					break;
				}
				case "Charge": {
					indexList = this.getIndexListMap().get("Charge");
					unit = "C^2";
					break;
				}
				case "Distance": {
					indexList = this.getIndexListMap().get("Distance");
					unit = "Meters(m)";
					break;
				}
				case "Resistance": {
					indexList = this.getIndexListMap().get("Resistance");
					unit = "Ohms";
					break;
				}
				case "Resistance Ratio": {
					indexList = this.getIndexListMap().get("Resistance Ratio");
					unit = "";
					break;
				}
				case "Tesla": {
					indexList = this.getIndexListMap().get("Tesla");
					unit = "T";
					break;
				}
				case "Angle": {
					indexList = this.getIndexListMap().get("Angle");
					unit = "Degrees";
					break;
				}
				case "Voltage": {
					indexList = this.getIndexListMap().get("Voltage");
					unit = "Volts (V)";
					break;
				}
				case "Print": {
					this.print();
					this.variableMap.put(variableKey,false);
					break;
				}
				case "X Ratio": {
					indexList = this.getIndexListMap().get("X Ratio");
					unit = "";
					break;
				}
				case "Charge Force Ratio": {
					indexList = this.getIndexListMap().get("Charge Force Ratio");
					unit = "";
					break;
				}
				case "Gravity Force Ratio": {
					indexList = this.getIndexListMap().get("Gravity Force Ratio");
					unit = "";
					break;
				}
				}
				if (indexList != null) {
					List<List<Index>> blackPointMatrix = new ArrayList<>();
					blackPointMatrix.add((indexList));
					plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
					plot.setTitle(this.name + " " + variableKey);
					plot.setXLabel("Time");
					plot.setYLabel(unit);
					plotList.add(plot);
				}
			}
		}

		return plotList;
	}

	public void addTunnel(Tunnel tunnel) {
		if (this.tunnelList == null) {
			this.tunnelList = new ArrayList<>();
		}
		if (!this.tunnelList.contains(tunnel)) {
			this.tunnelList.add(tunnel);
//			this.variableMap.put(tunnel.name, false);
		}
	}

	public void addTriangle(Triangle triangle) {
		if (!this.triangleList.contains(triangle)) {
			this.triangleList.add(triangle);
//			this.variableMap.put(triangle.name, false);
		}
	}

//	public Point getPoint(Point point) {
//		Point p = null;
//		double theta = Math.PI * azimuth / 180.0;
//		double phi = Math.PI * elevation / 180.0;
//		float cosT = (float) Math.cos(theta);
//		float sinT = (float) Math.sin(theta);
//		float cosP = (float) Math.cos(phi);
//		float sinP = (float) Math.sin(phi);
//		float cosTcosP = cosT * cosP;
//		float cosTsinP = cosT * sinP;
//		float sinTcosP = sinT * cosP;
//		float sinTsinP = sinT * sinP;
//		// The following two lines fixed defects when viewing in 3 Dimensions
//		float near = 32; // distance from eye to near plane
//		float nearToObj = 8f; // 1.5// distance from near plane to center of object
//		double x0 = point.x;
//		double y0 = point.y;
//		double z0 = point.z;
//		// compute an orthographic projection
//		float x1 = (float) (cosT * x0 + sinT * z0);
//		float y1 = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);
//		// now adjust things to get a perspective projection
//		float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);
//		x1 = x1 * near / (z1 + near + nearToObj);
//		y1 = y1 * near / (z1 + near + nearToObj);
//		p = new Point();
//		p.x = x1;
//		p.y = y1;
//		p.z = z1;
//		return p;
//	}

	public double getMass() {
		return this.mass;
	}
	
	public double getCentroidMass() {
		return Unit.ELECTRON_MASS;
	}

	/**
	 * Joules is calculated using mass and the speed of light
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
		return -(this.mass/Unit.ELECTRON_MASS)*Unit.COULOMBS;//-Unit.COULOMBS;
	}
	
	public double getCentroidCoulomb() {
		return Unit.COULOMBS;//(this.mass/Unit.ELECTRON_MASS)*Unit.COULOMBS;
	}

//	public double getElipticDistance(Energy energy) {
//		Vector3D difference = this.space.elliptic.subtract(energy.space.elliptic);
//		double distance = Math.sqrt(Math.pow((double) difference.getX(), 2) + Math.pow((double) difference.getY(), 2)
//				+ Math.pow((double) difference.getZ(), 2));
//		return distance;
//	}

	public double getRectangularDistance(Energy energy) {
		Vector3D difference = this.space.rectangular.subtract(energy.space.rectangular);
		double distance = Math.sqrt(Math.pow((double) difference.getX(), 2) + Math.pow((double) difference.getY(), 2)
				+ Math.pow((double) difference.getZ(), 2));
		distance *= Unit.ASTRONOMICAL;
		distance *= 1000;
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
		distance *= Unit.ASTRONOMICAL;//Kilometers
		distance *= 1000;//Meters
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
		System.out.println(baryCenter);
		return baryCenter;
	}

	public double getBaryCenter(Energy energy) {
		return this.getRectangularDistance(energy) * (energy.mass) / (this.mass + energy.mass);
	}

	public double getGravitationalPotentialEnergy(Energy energy) {
		double distance = this.getRectangularDistance(energy);
		return -(Unit.GRAVITATIONAL_CONSTANT * energy.mass * this.mass) / (distance);
	}

	public double getGravityForce(Energy energy) {
		double distance = this.getRectangularDistance(energy);
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
		return this.getGravityAcceleration(Unit.ELECTRON_MASS,Unit.ELECTRON_RADIUS*100);
	}
	
	public double getCentroidChargeAcceleration() {
		return this.getChargeAcceleration(Unit.COULOMBS, Unit.ELECTRON_RADIUS);
	}
	
    public double getCentroidGravityForce() {
    	return this.getCentroidMass()*this.getCentroidGravityAcceleration();
    }
    
    public double getCentroidChargeForce() {
    	return this.getCentroidMass()*this.getCentroidChargeAcceleration();
    }
    
    public double getX() {
    	return this.getX(this.tunnelList);
    }
	
	public double getX(List<Tunnel> tunnelList) {
		double sum = 0;
		for(Tunnel tunnel:tunnelList) {
			sum += tunnel.getX();
		}
		return sum;
	}
	
	public double getCharge() {
		//mass/electron mass provides the number of electrons
		//multiplied by C for one electron, gives C for Spheroid
		return this.getCentroidCoulomb()*this.getCoulomb();
	}
	

	
	public double getChargeForce(List<Tunnel> tunnelList) {
		double sum = 0;
		for(Tunnel tunnel:tunnelList) {
			sum += tunnel.getChargeForce();
		}
		return sum;
	}
	
	public double getResistanceRatio() {
		return this.getResistanceRatio(this.tunnelList);
	}

	public double getResistanceRatio(List<Tunnel> tunnelList) {
		double sum = 0;
		for(Tunnel tunnel:tunnelList) {
			sum += tunnel.getResistanceRatio();
		}
		return sum;
	}
	
//	public void scale(double percentage) {
//		this.space.elliptic = this.space.elliptic.scalarMultiply(percentage);
//	}

	public static List<String> getDateList(String value, int increment) {
			List<String> dateList = new ArrayList<>();
			String[] dashArray = value.split("-");
			if (dashArray.length == 2) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
				try {
					Date startDate = simpleDateFormat.parse(dashArray[0]);
					Date endDate = simpleDateFormat.parse(dashArray[1]);
					Date currentDate = startDate;
					dateList.add(simpleDateFormat.format(startDate));
					do {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(currentDate);
						calendar.add(increment, 1);// Calendar.MONTH
						currentDate = calendar.getTime();
						dateList.add(simpleDateFormat.format(currentDate));
					} while (currentDate.before(endDate));
	
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				dateList.add(value);
			}
	//		System.out.println("getDateList("+value+") dateList="+dateList);
			return dateList;
		}

	public void print() {
		System.out.println(this.name+" Mass:" + this.getMass());
		System.out.println(this.name+" Voltage:" + this.getVoltage());
		System.out.println(this.name+" Joules:" + this.getJoules());
		System.out.println(this.name+" Coulomb:" + this.getCoulomb());
	}
	
	@JsonIgnore
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
	}
}
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
