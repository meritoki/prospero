/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meritoki.prospero.library.model.solar.planet.jupiter;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;

import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.cartesian.CartesianPlot;
import org.meritoki.prospero.library.model.plot.time.TimePlot;
import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.solar.planet.earth.Earth;
import org.meritoki.prospero.library.model.solar.star.sun.Sun;
import org.meritoki.prospero.library.model.solar.unit.Energy;
import org.meritoki.prospero.library.model.solar.unit.Orbital;
import org.meritoki.prospero.library.model.solar.unit.Unit;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Point;

public class Jupiter extends Planet {

	public Jupiter(Sun sun) {
		super("Jupiter");
		this.centroid = sun;
		this.mass = 1.89813e27;
		this.radius = 71492;
		this.color = Color.PINK;
		this.orbitalPeriod = 4332.589;//*24*60*60;//days
		this.longitudeOfAscendingNode[0] = 100.4542;// o
		this.longitudeOfAscendingNode[1] = 2.76854E-5;// o
		this.inclination[0] = 1.3030;// i//0.00005
		this.inclination[1] = -1.557E-7;// i//0.00005
		this.argumentOfPeriapsis[0] = 273.8777;
		this.argumentOfPeriapsis[1] = 1.64505E-5;
		this.semiMajorAxis[0] = 5.20256;// * Unit.ASTRONOMICAL;//
		this.semiMajorAxis[1] = 0;
		this.eccentricity[0] = 0.048498;// e//0.01671022
		this.eccentricity[1] = 4.469E-9;// e
		this.meanAnomaly[0] = 19.8950;
		this.meanAnomaly[1] = 0.0830853001;
		this.angularVelocity = 1.773408215404907e-04;
	}
}
//public static double getMin(List<Index> list) {
//double min = Double.MAX_VALUE;
//for (Index d : list) {
//	if (d.value < min) {
//		min = d.value;
//	}
//}
//System.out.println("getMin(...) min=" + min);
//return min;
//}
//
//public static double getVolume(double radius) {
//return (4 / 3) * Math.PI * Math.pow(radius, 3);
//}
//public static double getMax(List<Index> list) {
//double max = Double.MIN_VALUE;
//for (Index d : list) {
//	if (d.value > max) {
//		max = d.value;
//	}
//}
//System.out.println("getMax(...) max=" + max);
//return max;
//}
//
//public static double getZero(double min, double max) {
//double zero = ((max - min) / 2) + min;
//System.out.println("getZero(...) zero=" + zero);
//return zero;
//}
//
//public static List<Index> getZeroList(List<Index> list) {
////System.out.println("getZeroList(...,"+mass+")");
//double zero = getZero(getMin(list), getMax(list));
//List<Index> zeroList = new ArrayList<>();
//for (int i = 0; i < list.size(); i++) {
//	Index d = list.get(i);
//	d.value -= zero;
//	zeroList.add(d);
//}
//return zeroList;
//}
