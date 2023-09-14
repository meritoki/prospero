package org.meritoki.prospero.library.model.node;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.cartography.Projection;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.solar.Solar;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Series;
import org.meritoki.prospero.library.model.unit.Table;
import org.meritoki.prospero.library.model.unit.Time;
import org.meritoki.prospero.library.model.unit.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tunnel extends Spheroid {

	static Logger logger = LoggerFactory.getLogger(Tunnel.class.getName());
	public Orbital a;
	public Orbital b;

	public Tunnel(Orbital a, Orbital b) {
		super(a.name + "," + b.name);
		this.a = a;
		this.b = b;
		this.a.addTunnel(this);
		this.b.addTunnel(this);
	}

	@Override
	public boolean equals(Object o) {
		Tunnel t = (Tunnel) o;
		return (this.a == t.a && this.b == t.b) || (this.a == t.b && this.b == t.a);
	}

	@Override
	public void initVariableMap() {
		super.initVariableMap();
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

	@Override
	public void setProjection(Projection projection) {
		super.setProjection(projection);
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			if (n instanceof Spheroid) {
				((Spheroid) n).setProjection(projection);
			}
		}
	}

	@Override
	public Map<String, Series> getSeriesMap() throws Exception {
		logger.info("getSeriesMap()");
		if (this.seriesMap == null) {
			this.seriesMap = this.initSeriesMap();
		}
		return this.seriesMap;
	}

	@Override
	public Map<String, Series> initSeriesMap() throws Exception {
		logger.info("initSeriesMap()");
		Map<String, Series> map = new HashMap<>();
		Model model = (Model) this.getModel();
		List<String> dateList = Time.getDateStringList(Time.getPeriod(this.startCalendar, this.endCalendar),
				Calendar.DATE);
		Calendar calendar = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (String date : dateList) {
			calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			model.setCalendar(calendar);
			this.seriesMapPut(map,"Charge", new Index(1 / this.getCharge(), calendar));
			this.seriesMapPut(map,"Charge Force", new Index(this.getChargeForce(), calendar));
			this.seriesMapPut(map,"Gravity Force", new Index(this.getGravityForce(), calendar));
			this.seriesMapPut(map,"Calculate Charge Force", new Index(this.calculateChargeForce(), calendar));
			this.seriesMapPut(map,"Calculate Gravity Force", new Index(this.calculateGravityForce(), calendar));
			this.seriesMapPut(map,"X", new Index(this.getX(), calendar));
			this.seriesMapPut(map,"Z", new Index(this.getZ(), calendar));
			this.seriesMapPut(map,"A", new Index(this.getA(), calendar));
			this.seriesMapPut(map,"B", new Index(this.getB(), calendar));
			this.seriesMapPut(map,"C", new Index(this.getC(), calendar));
			this.seriesMapPut(map,"Tesla", new Index(this.getTesla(), calendar));
			this.seriesMapPut(map,"Voltage", new Index(this.getVoltage(), calendar));
			this.seriesMapPut(map,"Resistance", new Index(this.getResistance(), calendar));
			this.seriesMapPut(map,"Resistance Ratio", new Index(this.getResistanceRatio(), calendar));
			this.seriesMapPut(map,"Distance", new Index(this.getDistance(), calendar));
		}
		return map;
	}

	public Series getDistanceList() throws Exception {
		Series series = new Series();
		series.map.put("startCalendar", this.startCalendar);
		series.map.put("endCalendar", this.endCalendar);
		series.map.put("query", new Query());
		series.map.put("region", name);
		Solar solar = (Solar) this.getParent().getParent();
		List<String> dateList = Time.getDateStringList(Time.getPeriod(this.startCalendar, this.endCalendar),
				Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Energy energy = (Energy) this.getVariable(name);
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			solar.setCalendar(c);
			Index index = new Index();
			index.value = this.getDistance();
			index.startCalendar = c;
			series.add(index);
		}

		return series;
	}

	public Series getCalculateGravityForceList() throws Exception {
		Series series = new Series();
		series.map.put("startCalendar", this.startCalendar);
		series.map.put("endCalendar", this.endCalendar);
		series.map.put("query", new Query());
		series.map.put("region", name);
		Solar solar = (Solar) this.getParent().getParent();
		List<String> dateList = Time.getDateStringList(Time.getPeriod(this.startCalendar, this.endCalendar),
				Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Energy energy = (Energy) this.getVariable(name);
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			solar.setCalendar(c);
			Index index = new Index();
			index.value = this.getChargeForce() * this.getC();
			index.startCalendar = c;
			series.add(index);
		}

		return series;
	}

	public Series getCalculateDistanceList() throws Exception {
		Series indexList = new Series();
		Solar solar = (Solar) this.getParent().getParent();
		List<String> dateList = Time.getDateStringList(Time.getPeriod(this.startCalendar, this.endCalendar),
				Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Energy energy = (Energy) this.getVariable(name);
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			solar.setCalendar(c);
			Index index = new Index();
			index.value = this.getResistance() * this.getDistanceResistanceQuotient();
			index.startCalendar = c;
			indexList.add(index);
		}

		return indexList;
	}

	public double getDistanceResistanceQuotient() {
		return this.getDistance() / this.getResistance() * 2;
	}

	/**
	 * Mass if obtained from Joules. This is the total mass of the tunnel that
	 * traverses the center of mass or barycenter.
	 * 
	 * @return
	 */
	@Override
	public double getMass() {
		return this.getJoules() / Math.pow(Unit.LIGHT_SPEED, 2);
	}

//	@Override
//	public double getCentroidMass() {
//		return this.getCentroidJoules()/Math.pow(Unit.LIGHT_SPEED, 2);
//	}

	public double getMassSum() {
		return this.a.getMass() + this.b.getMass();
	}

	public double getMassProduct() {
		return this.a.getMass() * this.b.getMass();
	}

	/**
	 * Joules are obtained from Voltage and Coulombs per second This is the total
	 * Joules of the tunnel.
	 */
	@Override
	public double getJoules() {
		return this.getVoltage() * Unit.COULOMBS;
	}

//	@Override
//	public double getCentroidJoules() {
//		return this.getCentroidVoltage();
//	}

	public double getTesla() {
		return this.getVoltage() * this.getSeconds() / Math.pow(this.getDistance(), 2);
	}

	/**
	 * Voltage obtained from Resistance and Amperes. This is the total voltage of
	 * the tunnel.
	 */
	@Override
	public double getVoltage() {
		return this.getResistance() * this.getAmperes();
	}

//	@Override
//	public double getCentroidVoltage() {
//		return this.getCentroidResistance()*this.getAmperes();
//	}

	public double getVoltageRatio() {
		return this.getVoltage() / this.getVoltageSum();
	}

	public double getAmperes() {
		return this.getAmperesSum();
	}

//	/**
//	 * Has the value of the old way
//	 * @return
//	 */
//	public double getCentroidAmperes() {
//		return this.getCentroidAmperesSum();
//	}

	/**
	 * Amperes calculated as the net of amperes from Orbitals a and b. I was able to
	 * use the net amperes to calculate a mass, which was used to detect a point
	 * where force of gravity equals zero. This
	 * 
	 * @return
	 */
	public double getAmperesSum() {
		return this.a.getAmperes(this.getSecondsA()) + this.b.getAmperes(this.getSecondsB());
	}

//	public double getCentroidAmperesSum() {
//		return this.a.getCentroidAmperes(this.getSecondsA())+this.b.getCentroidAmperes(this.getSecondsB());
//	}

	/**
	 * Find average resistance
	 * 
	 * @return
	 */
	public double getResistance() {
		return (this.getResistanceSum() / 2);
	}

//	public double getCentroidResistance() {
//		return (this.getCentroidResistanceSum()/2);
//	}

	/**
	 * Return Resistance > 0 if the Resistance from Spherioid A and B is the same.
	 * 
	 * @return
	 */
	public double getResistanceA() {
		return this.a.getResistance(this.getSecondsA());
	}

	public double getResistanceB() {
		return this.b.getResistance(this.getSecondsB());
	}

//	public double getCentroidResistanceA() {
//		return this.a.getCentroidResistance(this.getSecondsA());
//	}
//	
//	public double getCentroidResistanceB() {
//		return this.b.getCentroidResistance(this.getSecondsB());
//	}

	public double getResistanceSum() {
		return this.getResistanceA() + this.getResistanceB();
	}

	public double getResistanceDifference() {
		return this.getResistanceA() - this.getResistanceB();
	}

//	/**
//	 * Do Not Touch! Important method. Centroid Resistance appears to have no value. However
//	 * the net Centroid Resistance corresponds to Distance between objects with mass.
//	 * @return
//	 */
//	public double getCentroidResistanceDifference() {
//		return this.getCentroidResistanceA()-this.getCentroidResistanceB();
//	}
//	
//	
//	
//	public double getCentroidResistanceSum() {
//		return this.getCentroidResistanceA()+this.getCentroidResistanceB();
//	}

	public double getResistanceRatio() {
		return 0;// this.getResistanceSum()/this.getCentroidResistanceSum();
	}

	public double getResistanceQuotient() {
		return this.getResistanceRatio() / (this.a.getResistanceRatio() + this.b.getResistanceRatio());/// (this.a.getResistanceRatio()+this.b.getResistanceRatio());
	}

	public double getDistance() {
		return a.getDistance(b);
	}

	/**
	 * Get the overall time required for a transfer of data. It cannot go faster
	 * than the speed of light. Light Speed is the maximum speed a particle can move
	 * in space time.
	 * 
	 * @return
	 */
	public double getSeconds() {
		return this.getDistance() / Unit.LIGHT_SPEED;
	}

	/**
	 * If we know the distance in meters to the barycenter from the centroid of an
	 * object, we can calculate the time it took for the mass to get to that point.
	 * 
	 * @return
	 */
	public double getSecondsA() {
		double seconds = this.getBarycenterDistanceA() / Unit.LIGHT_SPEED;
		return seconds;
	}

	public double getSecondsB() {
		double seconds = this.getBarycenterDistanceB() / Unit.LIGHT_SPEED;
		return seconds;
	}

	/**
	 * Here is where I assume there is a tunnel that connects the two charges
	 * 
	 * @return
	 */
	public double getCharge() {
		return this.a.getCoulomb() * this.b.getCoulomb();// this.a.getCentroidCoulomb()*this.b.getCentroidCoulomb();//this.getSeconds()*this.getAmperes();
	}

	/**
	 * Returns Newtons
	 * 
	 * @return
	 */
	public double getChargeForceA() {
		return Unit.k * ((-a.getCoulomb() * a.getCentroidCoulomb()) / Math.pow(this.getBarycenterDistanceA(), 2));
	}

	public double getChargeForceB() {
		return Unit.k * ((-b.getCoulomb() * b.getCentroidCoulomb()) / Math.pow(this.getBarycenterDistanceB(), 2));
	}

	/**
	 * Charge force is driven by the constant k
	 * 
	 * @return
	 */
	public double getChargeForce() {
		return -Unit.k * ((this.getCharge()) / Math.pow(this.getDistance(), 2));
	}

	public double calculateChargeForce() {
		return this.getGravityForce() / this.getX();
	}

	public double getC() {
		return Unit.C;// (Unit.G/Unit.k)*this.getMassProduct()*Unit.OSVALDO*8;
	}

	/**
	 * Result is a constant for a given tunnel. The G and k units do not change The
	 * Mass Product does not change The Charge does not change
	 * 
	 * @return
	 */
	@Override
	public double getX() {
		return ((Unit.G / Unit.k) * ((this.getMassProduct()) / (-this.getCharge())));
	}

	/**
	 * What is Z? Z is X without the constants and the mass product Z is just a
	 * complex why of writing get 1/C^2 It yields a constant for charge, which does
	 * not include G or k
	 * 
	 * @return
	 */
	public double getZ() {
		return this.getX() / (Unit.G / Unit.k) / this.getMassProduct();// Math.pow(this.getMass(),2)/(this.getCharge());//this.getVoltage();
	}

	public double getA() {
		// Math.pow(this.getMass(),2)
		return (Unit.k / Unit.G) * Math.pow(this.getDistance(), 2) * this.getAmperesSum()
				* Math.pow(this.getSeconds(), 3);// (Math.pow(this.getMass(),
													// 4)*Math.pow(this.getDistance(),2))/Math.pow((this.getNetAmperes()*Math.pow(this.getSeconds(),
													// 3)),
													// 3);//(Unit.G/Unit.k)*this.getMassProduct()/Math.pow(this.getDistance(),
													// 2)*this.getNetAmperes()*Math.pow(this.getSeconds(),
													// 3);//this.getMass()/this.getNetChargeForce()/this.getNetVoltage();//this.getMass()/this.getJoules()*this.getCoulomb();//1/Math.pow(this.getDistance(),
													// 2)*this.getNetAmperes()*Math.pow(this.getSeconds(), 3);
	}

	public double getB() {
		return 0;// Unit.OSVALDO*(-this.getCharge());
	}

	public double getVoltageSum() {
		return this.a.getVoltage() + this.b.getVoltage();
	}

	/**
	 * Returns the difference between the Net Charge Force and the Charge Force
	 * 
	 * @return
	 */
	public double getChargeForceDifference() {
		return this.getChargeForceA() - this.getChargeForceB();// this.getChargeForceSum()+this.getChargeForce();
	}

//	public double getDifferenceResistance() {
//		return this.getCentroidResistance()-this.getResistance();
//	}

	/**
	 * Sum of the Charge Forces from A and B to barycenter
	 * 
	 * @return
	 */
	public double getChargeForceSum() {
		return this.getChargeForceA() + this.getChargeForceB();
	}

	/**
	 * Returns the quotient of the difference divided by the net, equals 1
	 * 
	 * @return
	 */
	public double getChargeForceQuotient() {
		return this.getChargeForce() / this.getChargeForceDifference();
	}

	public double getChargeForceAQuotient() {
		return this.getChargeForce() / this.getChargeForceA();
	}

	public double getChargeForceBQuotient() {
		return this.getChargeForce() / this.getChargeForceB();
	}

//	public double getResistanceQuotient() {
//		return this.getDifferenceResistance()/this.getCentroidResistance();
//	}

	public boolean isValidChargeForce() {
		double a = this.getChargeForceA();
		double b = this.getChargeForceB();
		double c = this.getChargeForce();
		if (a < b && a <= c && c <= b) {
			return true;
		}
		if (a > b && a >= c && c >= b) {
			return true;
		}
		return false;
	}

	public double getGravityForceA() {
		return this.a.getGravityForce(this.getMass(), this.getDistance()); // this.getBarycenterDistanceA());
	}

	public double getGravityForceB() {
		return this.b.getGravityForce(this.getMass(), this.getDistance()); // this.getBarycenterDistanceB());
	}

	public double getNetGravityForce() {
		return this.getGravityForceB() - this.getGravityForceA();
	}

	public double getGravityForce() {
		return a.getGravityForce(b);
	}

	public double calculateGravityForce() {
		return this.getChargeForce() * this.getX();
	}

	public double getBarycenterGravityAcelerationA() {
		return this.a.getGravityAcceleration(this.getBarycenterDistanceA());
	}

	public double getBarycenterGravityAcelerationB() {
		return this.b.getGravityAcceleration(this.getBarycenterDistanceB());
	}

	public double getBarycenterDistanceA() {
		return a.getBaryCenter(b);/// 1000/Unit.ASTRONOMICAL;
	}

	/**
	 * Vector is a distance in meters with direction from the source object.
	 * 
	 * @return
	 */
	public Vector3D getBarycenterA() {
		return a.getDirection(b).scalarMultiply(this.getBarycenterDistanceA());
	}

	public double getBarycenterDistanceB() {
		return b.getBaryCenter(a);
	}

	public Vector3D getBarycenterB() {
		return b.getDirection(a).scalarMultiply(this.getBarycenterDistanceB());
	}

	public static TableModel getTableModel(List<Tunnel> tunnelList) {
		Object[] objectArray = getObjectArray(tunnelList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	public static Object[] getObjectArray(List<Tunnel> eventList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (eventList != null) {
			if (eventList.size() > 0) {
				for (int i = 0; i < eventList.size(); i++) {
					Tunnel t = eventList.get(i);
					if (i == 0) {
						columnArray = Table.getColumnNames(13).toArray();
						dataMatrix = new Object[eventList.size() + 1][13];
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
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}

	@Override
	public void print() {
//		System.out.println("TIME");
//		System.out.println(this.name + " Time:" + this.getSeconds());
//		System.out.println(this.a.name + " Time:" + this.getSecondsA());
//		System.out.println(this.b.name + " Time:" + this.getSecondsB());
//		System.out.println("DISTANCE");
//		System.out.println(this.name + " Distance:" + this.getDistance());
//		System.out.println(this.a.name + " Distance: " + this.getBarycenterDistanceA());
//		System.out.println(this.b.name + " Distance: " + this.getBarycenterDistanceB());
//		System.out.println("MASS");
//		System.out.println(this.name + " Mass: " + this.getMass());
//		System.out.println(this.name + " Centroid Mass: " + this.getCentroidMass());
//		System.out.println(this.a.name + " Mass: " + this.a.mass);
//		System.out.println(this.b.name + " Mass: " + this.b.mass);
//		System.out.println(this.name + " Mass Sum: " + this.getMassSum());
//		System.out.println(this.name + " Mass Product: " + this.getMassProduct());
//		System.out.println("X");
//		System.out.println(this.name + " X: " + this.getX());
		System.out.println("CHARGE");
		System.out.println(this.name + " Charge:" + this.getCharge());
		System.out.println(this.name + " Charge Force:" + this.getChargeForce());
		System.out.println(this.a.name + " Charge Force:" + this.getChargeForceA());
		System.out.println(this.b.name + " Charge Force:" + this.getChargeForceB());
		System.out.println(this.name + " Charge Force Sum:" + (this.getChargeForceSum()));
		System.out.println(this.name + " Charge Force Difference:" + (this.getChargeForceDifference()));
		System.out.println(this.name + " Charge Force Quotient:" + (this.getChargeForceQuotient()));
		System.out.println(this.a.name + " Charge Force Quotient:" + (this.getChargeForceAQuotient()));
		System.out.println(this.b.name + " Charge Force Quotient:" + (this.getChargeForceBQuotient()));
		System.out.println("GRAVITY");
		System.out.println(this.name + " Gravity Force: " + this.getGravityForce());
		System.out.println(this.name + " Calculate Gravity Force: " + this.calculateGravityForce());
		System.out.println(this.a.name + " Gravity Force Sum:" + this.a.getGravityForceSum());
		System.out.println(this.b.name + " Gravity Force Sum:" + this.b.getGravityForceSum());
		System.out.println(
				this.a.name + " Barycenter Gravity Force Acceleration:" + this.getBarycenterGravityAcelerationA());
		System.out.println(
				this.b.name + " Barycenter Gravity Force Acceleration:" + this.getBarycenterGravityAcelerationB());
		System.out.println("AMPERES");
//		System.out.println(this.a.name+" Centroid Amp:"+this.a.getCentroidAmperes(this.getSecondsA()));
//		System.out.println(this.b.name+" Centroid Amp:"+this.b.getCentroidAmperes(this.getSecondsB()));
		System.out.println(this.a.name + " Amperes:" + this.a.getAmperes(this.getSecondsA()));
		System.out.println(this.b.name + " Amperes:" + this.b.getAmperes(this.getSecondsB()));
		System.out.println(this.name + " Amperes Sum:" + this.getAmperesSum());
//		System.out.println(this.name+" Centroid Amperes Sum:"+this.getCentroidAmperesSum());
		System.out.println("RESISTANCE");
		System.out.println(this.name + " Resistance:" + this.getResistance());
//		System.out.println(this.name+" Centroid Resistance:"+this.getCentroidResistance());
		System.out.println(this.a.name + " Resistance:" + this.getResistanceA());
//		System.out.println(this.a.name+" Centroid Resistance:"+this.a.getCentroidResistance(this.getSecondsA()));
		System.out.println(this.b.name + " Resistance:" + this.getResistanceB());
//		System.out.println(this.b.name+" Centroid Resistance:"+this.b.getCentroidResistance(this.getSecondsB()));
		System.out.println(this.name + " Resistance Sum:" + this.getResistanceSum());
//		System.out.println(this.name+" Centroid Resistance Sum:"+this.getCentroidResistanceSum());
		System.out.println(this.name + " Resistance Difference:" + this.getResistanceDifference());
//		System.out.println(this.name+" Centroid Resistance Difference:"+this.getCentroidResistanceDifference());
		System.out.println(this.name + " Resistance Ratio:" + this.getResistanceRatio());
		System.out.println(this.name + " Resistance Quotient:" + this.getResistanceQuotient());
		System.out.println("TESLA");
		System.out.println(this.name + " Tesla: " + this.getTesla());
		System.out
				.println(this.a.name + " Tesla: " + this.a.getTesla(this.getSecondsA(), this.getBarycenterDistanceA()));
		System.out
				.println(this.b.name + " Tesla: " + this.b.getTesla(this.getSecondsB(), this.getBarycenterDistanceB()));
		System.out.println("VOLTAGE");
		System.out.println(this.name + " Voltage:" + this.getVoltage());
//		System.out.println(this.name+" Centroid Voltage:"+this.getCentroidVoltage());
		System.out.println(this.a.name + " Voltage:" + this.a.getVoltage());
//		System.out.println(this.a.name+" Centroid Voltage:"+this.a.getCentroidVoltage());
		System.out.println(this.b.name + " Voltage:" + this.b.getVoltage());
//		System.out.println(this.b.name+" Centroid Voltage:"+this.b.getCentroidVoltage());
		System.out.println(this.name + " Voltage Sum: " + this.getVoltageSum());
		System.out.println(this.name + " Voltage Ratio: " + this.getVoltageRatio());
		System.out.println("JOULES");
		System.out.println(this.name + " Joules:" + this.getJoules());
//		System.out.println(this.name+" Centroid Joules:"+this.getCentroidJoules());
		System.out.println(this.a.name + " Joules:" + this.a.getJoules());
//		System.out.println(this.a.name+" Centroid Joules:"+this.a.getCentroidJoules());
		System.out.println(this.b.name + " Joules:" + this.b.getJoules());
//		System.out.println(this.b.name+" Centroid Joules:"+this.b.getCentroidJoules());

		// System.out.println(this.name+" Distance Resistance Quotient:
		// "+this.getDistanceResistanceQuotient());
//		System.out.println("Calculate C2: "+this.calculateC2());
//		System.out.println("Calculate C "+this.calculateC());
//		System.out.println("Difference Resistance:"+this.getDifferenceResistance());
//		System.out.println("Quotient Resistance:"+this.getResistanceQuotient());
	}

	@Override
	public void paint(Graphics g) throws Exception {
		super.paint(g);
		this.initVariableMap();
		Vector3D barycenterA = this.getBarycenterA();
		Vector3D barycenterB = this.getBarycenterB();
//		barycenterA.scalarMultiply(1 / (Unit.ASTRONOMICAL * 1000));
//		barycenterB.scalarMultiply(1 / (Unit.ASTRONOMICAL * 1000));
//		barycenterA.scalarMultiply(this.getProjection().scale);
//		barycenterB.scalarMultiply(this.getProjection().scale);
		Point pointA = (a.buffer.getPoint());
		Point pointB = (b.buffer.getPoint());
		Point pointC = (new Point(pointA.x - barycenterA.getX(), pointA.y - barycenterA.getY(),
				pointA.z - barycenterA.getZ()));
		Point pointD = (new Point(pointB.x - barycenterB.getX(), pointB.y - barycenterB.getY(),
				pointB.z - barycenterB.getZ()));
		pointA = this.getProjection().getPoint(pointA);
		pointB = this.getProjection().getPoint(pointB);
		pointC = this.getProjection().getPoint(pointC);// pointA.scale(this.getProjection().scale);
		pointD = this.getProjection().getPoint(pointD);// pointB.scale(this.getProjection().scale);
		pointA = pointA.scale(this.getProjection().scale);
		pointB = pointB.scale(this.getProjection().scale);
		pointC = pointC.scale(this.getProjection().scale);
		pointD = pointD.scale(this.getProjection().scale);

//		g.setColor(Color.BLUE);
//		g.drawLine((int) (pointA.x), (int) (pointA.y), (int) (pointB.x), (int) (pointB.y));
//		Point pointC = (new Point(pointA.x-barycenterA.getX(),pointA.y-barycenterA.getY(),pointA.z-barycenterA.getZ()));
//		Point pointD = (new Point(pointB.x-barycenterB.getX(),pointB.y-barycenterB.getY(),pointB.z-barycenterB.getZ()));

//		pointC = pointC.scale(this.getProjection().scale);
//		pointD = pointD.scale(this.getProjection().scale);

//		pointC = this.getProjection().getPoint(pointC);
//		pointD = this.getProjection().getPoint(pointD);
//		System.out.println(pointA+":"+pointB+":"+pointC+":"+pointD);
//		g.setColor(Color.GRAY);
//		g.drawLine((int) (pointA.x*scale), (int) (pointA.y*scale), (int) (pointB.x*scale), (int) (pointB.y * scale));
		double magnitudeA = this.getMagnitude(barycenterA);
		double magnitudeB = this.getMagnitude(barycenterB);
		if (magnitudeA < magnitudeB) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLUE);
		}
		g.drawLine((int) (pointA.x), (int) (pointA.y), (int) (pointC.x), (int) (pointC.y));
		if (magnitudeA < magnitudeB) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.RED);
		}
		g.drawLine((int) (pointB.x), (int) (pointB.y), (int) (pointD.x), (int) (pointD.y));
//		g.drawLine((int) (pointA.x*this.getProjection().scale), (int) (pointA.y*this.getProjection().scale), (int) (pointC.x*this.getProjection().scale), (int) (pointC.y*this.getProjection().scale));
//		if(magnitudeA < magnitudeB) {
//			g.setColor(Color.BLUE);
//		}
//		g.drawLine((int) (pointB.x*this.getProjection().scale), (int) (pointB.y*this.getProjection().scale), (int) (pointD.x*this.getProjection().scale), (int) (pointD.y * this.getProjection().scale));
	}

}
//this.mapPut(map, calendar, "Charge", 1 / this.getCharge());
//this.mapPut(map, calendar, "Charge Force", this.getChargeForce());
//this.mapPut(map, calendar, "Gravity Force", this.getGravityForce());
//this.mapPut(map, calendar, "Calculate Charge Force", this.calculateChargeForce());
//this.mapPut(map, calendar, "Calculate Gravity Force", this.calculateGravityForce());
//this.mapPut(map, calendar, "X", this.getX());
//this.mapPut(map, calendar, "Z", this.getZ());
//this.mapPut(map, calendar, "A", this.getA());
//this.mapPut(map, calendar, "B", this.getB());
//this.mapPut(map, calendar, "C", this.getC());
//this.mapPut(map, calendar, "Tesla", this.getTesla());
//this.mapPut(map, calendar, "Voltage", this.getVoltage());
//this.mapPut(map, calendar, "Resistance", this.getResistance());
////this.mapPut(map, calendar, "Resistance", this.getCentroidResistanceDifference());
//this.mapPut(map, calendar, "Resistance Ratio", this.getResistanceRatio());
//this.mapPut(map, calendar, "Distance", this.getDistance());
//public void mapPut(Map<String,List<Index>> map, Calendar calendar, String key, double value) {
//Index index = new Index();
//index.startCalendar = calendar;
//List<Index> indexList = map.get(key);
//if(indexList == null) {
//	indexList = new ArrayList<>();
//}
//index.value = value;
//indexList.add(index);
//map.put(key,indexList);
//}

//System.out.println("A Cent. Gravity: "+this.a.getCentroidGravity());
//System.out.println("A Gravity Force: "+this.getGravityForceA());
//System.out.println("B Gravity Force: "+this.getGravityForceB());
//System.out.println("Net Gravity Force: "+this.getNetGravityForce());

//public double getSurfaceChargeA() {
//return -this.a.getSurfaceForce()*Math.pow(this.getBarycenterDistanceA(), 2)/Unit.k/Unit.COULOMBS;
//}
//
//public double getSurfaceChargeB() {
//return -this.b.getSurfaceForce()*Math.pow(this.getBarycenterDistanceB(), 2)/Unit.k/Unit.COULOMBS;
//}
//

//public List<Index> getResistanceList() throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = this.a.getResistance(this.getSecondsA());
//	index.startCalendar = c;
//	System.out.println(a.space.rectangular+":"+b.space.rectangular);
//	System.out.println(index);
//	indexList.add(index);
//}
//
//return indexList;
//}

//@Override
//public List<Plot> getPlotList() throws Exception {
//	List<Plot> plotList = new ArrayList<>();
//	for (Entry<String, Boolean> variable : this.variableMap.entrySet()) {
//		String variableKey = variable.getKey();
//		Boolean variableLoad = variable.getValue();
//		if (variableLoad) {
//			TimePlot plot = null;
//			switch (variableKey) {
//			case "Resistance": {
//				List<Index> indexList = this.getResistanceList();
//				if (indexList != null) {
//					List<List<Index>> blackPointMatrix = new ArrayList<>();
//					blackPointMatrix.add((indexList));
//					TimePlot cPlot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//					cPlot.setTitle(variableKey);
//					cPlot.setXLabel("Time");
//					cPlot.setYLabel("Distance");
//					plotList.add(cPlot);
//				}
//				break;
//			}
//			case "Charge Force": {
//				List<Index> indexList = this.getChargeForceList();
//				if (indexList != null) {
//					List<List<Index>> blackPointMatrix = new ArrayList<>();
//					blackPointMatrix.add((indexList));
//					TimePlot cPlot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//					cPlot.setTitle(variableKey);
//					cPlot.setXLabel("Time");
//					cPlot.setYLabel("Distance");
//					plotList.add(cPlot);
//				}
//				break;
//			}
//			case "Amperes": {
//				List<Index> indexList = this.getAmperesList();
//				if (indexList != null) {
//					List<List<Index>> blackPointMatrix = new ArrayList<>();
//					blackPointMatrix.add((indexList));
//					TimePlot cPlot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//					cPlot.setTitle(variableKey);
//					cPlot.setXLabel("Time");
//					cPlot.setYLabel("Distance");
//					plotList.add(cPlot);
//				}
//				break;
//			}
//			}
//		}
//	}
//	return plotList;
//}

//public List<Index> getCList() throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = this.getC();
//	index.startCalendar = c;
//	indexList.add(index);
//}
//
//return indexList;
//}

//public List<Index> getChargeForceList() throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = this.getChargeForce();
//	index.startCalendar = c;
//	indexList.add(index);
//}
//
//return indexList;
//}

//public List<Index> getGravityForceList(double gravityForce) throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = gravityForce/this.getGravityForce();
//	index.startCalendar = c;
//	indexList.add(index);
//}
//return indexList;
//}
//
//public List<Index> getGravityForceList() throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = this.getGravityForce();
//	index.startCalendar = c;
//	indexList.add(index);
//}
//return indexList;
//}
//
//public List<Index> getAmperesList() throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = this.getNetAmperes();
//	index.startCalendar = c;
////	System.out.println(a.space.rectangular+":"+b.space.rectangular);
////	System.out.println(index);
//	indexList.add(index);
//}
//
//return indexList;
//}
//
//public List<Index> getResistanceList() throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = this.getNetCentroidResistance();
//	index.startCalendar = c;
////	System.out.println(a.space.rectangular+":"+b.space.rectangular);
////	System.out.println(index);
//	indexList.add(index);
//}
//return indexList;
//}
//
//public List<Index> getTeslaList() throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = this.getTesla();
//	index.startCalendar = c;
//	indexList.add(index);
//}
//return indexList;
//}
//
//public List<Index> getVoltageList() throws Exception{
//List<Index> indexList = new ArrayList<>();
//Solar solar = (Solar)this.getParent().getParent();
//List<String> dateList = getDateList(this.period, Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//Energy energy = (Energy) this.getNode(name);
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	c.setTime(sdf.parse(date));
//	solar.setCalendar(c);
//	Index index = new Index();
//	index.value = this.getVoltage();
//	index.startCalendar = c;
//	indexList.add(index);
//}
//return indexList;
//}