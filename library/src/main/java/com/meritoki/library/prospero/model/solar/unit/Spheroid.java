package com.meritoki.library.prospero.model.solar.unit;

import com.meritoki.library.prospero.model.node.Variable;

//Work that remains to be done
//Have possibility to caculate charge of whole object and charge
//of centroid.
public class Spheroid extends Energy {
	/**
	 * Radius given in meters
	 */
    public double radius;
    public double a;
    public double b;
    public double c;
    
    public Spheroid(String name) {
    	super(name);
    }
    

	

    
    public double getVolume() {
		return (4/3)*Math.PI*Math.pow(this.radius, 3);
	}
    
    @Override
    public double getX() {
    	return Unit.C;
    }
    
    /**
     * Return the quotient of Gravity Force Sum and the Charge Force
     * between all tunnels 
     * @param x
     * @return
     */
    public double calculateX(double x) {
		return this.getGravityForceSum()/this.getChargeForceDifference();//this.calculateChargeForce(x);
	}

    /**
     * Same as Charge Force Ratio b/c doig the same arithmetic
     * @param x
     * @return
     */
	public double getXRatio(double x) {
		return x/this.calculateX(x);
	}
	
	public double getChargeAcceleration() {
		return this.getChargeAcceleration(this.radius);
	}
	
	public double getChargeForce() {
		return this.getMass()*this.getChargeAcceleration();//this.getGravityForce()/Unit.GRAVITY_CHARGE_CONSTANT;//-Unit.k * ((this.getCharge())/Math.pow(this.radius,2));//this.getChargeForce(this.tunnelList);
	}

	public double getChargeForceRatio(double x) {
		return this.getChargeForce()/this.calculateChargeForce(x);
	}

	public double getGravityForceRatio(double x) {
		return this.calculateGravityForce(x)/this.getGravityForceSum();
	}

	public double getGravityAcceleration() {
		return this.getGravityAcceleration(this.radius)/1000000;//dividing to get meters
	}
    
    public double getGravityForce() {
    	return this.getMass()*this.getGravityAcceleration();
    }
    
    public double getChargeForceDifference() {
		return this.getCentroidChargeForce()-this.getChargeForce();
	}

    /**
     * Gravity Force Sum sums the force of the centroid gravity and surface gravity
     * @return
     */
	public double getChargeForceSum() {
		return this.getCentroidChargeForce()+this.getChargeForce();
	}
    
    public double getGravityForceDifference() {
		return this.getCentroidGravityForce()-this.getGravityForce();
	}

    /**
     * Gravity Force Sum sums the force of the centroid gravity and surface gravity
     * @return
     */
	public double getGravityForceSum() {
		return this.getCentroidGravityForce()+this.getGravityForce();
	}

	public double getGravityForceQuotient() {
		return this.getCentroidGravityForce()/this.getGravityForce();
	}
	
	public double calculateGravityForce(double x) {
		return this.getChargeForce()*x;
	}

	/**
     * Really the Charge Force
     * @param x
     * @return
     */
    public double calculateChargeForce(double x) {
    	return this.getGravityForceSum()/x;
    }
    
    /**
	 * Vital method, shows the Amperes that can traverse the tunnel over the time it takes to reach the barycenter.
	 * The time it takes to reach barycenter, it the time it takes for a point on the surface of a spheroid to
	 * reach the centroid, where it traverses instantaneously to another object. Therefore, we can 
	 * divide it by the Coulombs transfered and by the time it takes to reach the barycenter to get the Amperes
	 * or rate of Coulomb transfer per second.
	 * @return
	 */
	public double getCentroidAmperes(double time) {
		return this.getCentroidCoulomb()/time;
	}
	/**
	 * Amperes are calculated by dividing Coulombs by time.
	 * Here time is the seconds it takes a photon to travel from the centroid of the spheriod to the barycenter.
	 * The barycenter is the core of a gravitational anomaly that allows for instantaneous transfer of data from
	 * one Speriod to another.
	 * @return
	 */
	public double getAmperes(double time) {
		return Unit.COULOMBS/time;//this.getCoulomb()/time;
	}
	
	public double getWatts(double time) {
		return this.getJoules()/time;
	}
	
	public double getTesla(double time, double distance) {
		return this.getVoltage()*time/distance;
	}
	
//	public double getCentroidTesla(double time, double distance) {
//		return this.getCentroidVoltage()*time/distance;
//	}
	/**
	 * Interesting result; When the voltage of a Spheriod is divided by amperes, 
	 * we get the same resistance between any two Spheriods.
	 * When we divide voltage
	 * @return
	 */
	public double getResistance(double time) {
		double resistance = this.getVoltage()/this.getAmperes(time);
//		resistance = Double.parseDouble(df.format(resistance));
		return resistance;
	}
	
//	public double getCentroidResistance(double time) {
//		double resistance = this.getCentroidVoltage()/this.getCentroidAmperes(time);
////		resistance = Double.parseDouble(df.format(resistance));
//		return resistance;
//	}
	
	@Override
	public void print() {
		super.print();
		System.out.println("Mass");
		System.out.println(this.name+" Mass:" + this.getMass());
		System.out.println(this.name+" Centroid Mass:" + this.getCentroidMass());
		System.out.println("X");
		double X = this.getX();
		System.out.println(this.name+" X: "+X);
		System.out.println(this.name+" Calculate X: "+this.calculateX(X));
		System.out.println(this.name+" X Ratio: "+this.getXRatio(X));
		System.out.println("CHARGE");
		System.out.println(this.name+" Coulomb:" + this.getCoulomb());
		System.out.println(this.name+" Centroid Coulomb:"+this.getCentroidCoulomb());
		System.out.println(this.name+" Charge Acceleration: "+this.getChargeAcceleration());
		System.out.println(this.name+" Charge Force: "+this.getChargeForce());
		System.out.println(this.name+" Calculate Charge Force: "+this.calculateChargeForce(X));
		System.out.println(this.name+" Charge Force Ratio: "+this.getChargeForceRatio(X));
		System.out.println("GRAVITY");
		System.out.println(this.name+" Gravity Acceleration: "+this.getGravityAcceleration());
		System.out.println(this.name+" Centroid Gravity Acceleration: "+this.getCentroidGravityAcceleration());
		System.out.println(this.name+" Gravity Force: "+this.getGravityForce());
		System.out.println(this.name+" Centroid Gravity Force: "+this.getCentroidGravityForce());
		System.out.println(this.name+" Gravity Force Quotient: "+this.getGravityForceQuotient());
		System.out.println(this.name+" Gravity Force Difference: "+this.getGravityForceDifference());
		System.out.println(this.name+" Gravity Force Sum: "+this.getGravityForceSum());
		System.out.println(this.name+" Calculate Gravity Force: "+this.calculateGravityForce(X));
		System.out.println(this.name+" Gravity Force Ratio: "+this.getGravityForceRatio(X));
//		System.out.println(this.name+" Gravity Force Ratio Charge Quotient: "+this.getGravityForceRatio(X)/Unit.COULOMBS);
		System.out.println("Voltage");
		System.out.println(this.name+" Voltage:" + this.getVoltage());
//		System.out.println(this.name+" Centroid Voltage:"+this.getCentroidVoltage());
		System.out.println("Joules");
		System.out.println(this.name+" Joules:" + this.getJoules());
//		System.out.println(this.name+" Centroid Joules:"+this.getCentroidJoules());
		System.out.println("RESISTANCE");
		System.out.println(this.name+" Resistance Ratio: "+this.getResistanceRatio());
	}
}

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
