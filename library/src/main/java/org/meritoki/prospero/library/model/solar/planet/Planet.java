package org.meritoki.prospero.library.model.solar.planet;

import java.awt.Color;
import java.awt.Graphics;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.time.TimePlot;
import org.meritoki.prospero.library.model.solar.planet.earth.Earth;
import org.meritoki.prospero.library.model.solar.unit.Energy;
import org.meritoki.prospero.library.model.solar.unit.Orbital;
import org.meritoki.prospero.library.model.solar.unit.Triangle;
import org.meritoki.prospero.library.model.solar.unit.Tunnel;
import org.meritoki.prospero.library.model.trig.Sine;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Point;

public class Planet extends Orbital {

	public Planet(String name) {
		super(name);
	}
	

	
	
	public List<Point> getSineFunction(List<Index> indexList) {
		Sine sine = new Sine(indexList);
		List<Point> pointList = new ArrayList<>();
		for(Index i: indexList) {
			Point p = new Point(i.startCalendar.getTimeInMillis(),sine.getY(i.startCalendar.getTimeInMillis()));
			pointList.add(p);
		}
		return pointList;
	}
	


	@Override
	public void paint(Graphics graphics) throws Exception {
		this.initVariableMap();
		Point point = this.getPoint(this.space.getPoint());
		double x = point.x * scale;
		double y = point.y * scale;
		graphics.setColor(this.color);
		double radius = 5;
		x = x - (radius / 2);
		y = y - (radius / 2);
		graphics.fillOval((int) x, (int) y, (int) radius, (int) radius);
		graphics.setColor(Color.black);
		graphics.drawString(this.name.substring(0, 1).toUpperCase() + this.name.substring(1) + "", (int) x, (int) y);
		List<Point> vertexList = this.getOrbit();
		graphics.setColor(Color.gray);
		radius = 5;
		for (int i = 1; i < vertexList.size(); i++) {
			graphics.drawLine((int) (vertexList.get(i - 1).x * scale),
					(int) (vertexList.get(i - 1).y * scale), (int) (vertexList.get(i).x * scale),
					(int) (vertexList.get(i).y * scale));
		}

		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			if(n instanceof Energy) {
				n.paint(graphics);
			}
		}
	}
}

//if(this.name.equals("Earth")) {
//System.out.println(this.name+": Joules:"+this.getJoules()+": E_k:"+this.getKineticEnergy());
//System.out.println("Gravity Force:"+this.getGravityForce(this.centroid));
//System.out.println("Gravity Potential Energy:"+this.getGravitationalPotentialEnergy(this.centroid));
//
//}

//public List<Index> getSunDistanceIndexList() {
//List<Index> indexList = new ArrayList<>();
//List<String> dateList = getDateList("18000101-20200101", Calendar.DATE);
//Calendar c = null;
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//for (String date : dateList) {
//	c = Calendar.getInstance();
//	try {
//		c.setTime(sdf.parse(date));
//		this.getParent().setCalendar(c);
//		double distance = ((Energy) this).getRectangularDistance((Energy) this.getParent());
//		Index index = new Index();
//		index.value = distance;
//		index.startCalendar = c;
//		indexList.add(index);
//	} catch (ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//}
//return indexList;
//}

//public List<Index> getSunGravityForceIndexList() {
//	List<Index> indexList = new ArrayList<>();
//	List<String> dateList = getDateList("18000101-20200101", Calendar.DATE);
//	Calendar c = null;
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//	for (String date : dateList) {
//		c = Calendar.getInstance();
//		try {
//			c.setTime(sdf.parse(date));
//			this.setCalendar(c);
//			double distance = ((Energy) this).getGravityForce((Energy) this.getParent());
//			Index index = new Index();
//			index.value = distance;
//			index.startCalendar = c;
//			indexList.add(index);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	return indexList;
//}
//
//public List<Index> getDistanceIndexList(String name) {
//	List<Index> indexList = new ArrayList<>();
//	List<String> dateList = getDateList("18000101-20200101", Calendar.DATE);
//	Calendar c = null;
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//	for (String date : dateList) {
//		c = Calendar.getInstance();
//		try {
//			c.setTime(sdf.parse(date));
//			this.getParent().setCalendar(c);
//			Energy energy = (Energy)this.getParent().getNode(name);
//			double distance = ((Energy) this).getRectangularDistance(energy);
//			Index index = new Index();
//			index.value = distance;
//			index.startCalendar = c;
//			indexList.add(index);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	return indexList;
//}

//public static List<String> getDateList(String value, int increment) {
//	List<String> dateList = new ArrayList<>();
//	String[] dashArray = value.split("-");
//	if (dashArray.length == 2) {
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//		try {
//			Date startDate = simpleDateFormat.parse(dashArray[0]);
//			Date endDate = simpleDateFormat.parse(dashArray[1]);
//			Date currentDate = startDate;
//			dateList.add(simpleDateFormat.format(startDate));
//			do {
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(currentDate);
//				calendar.add(increment, 1);// Calendar.MONTH
//				currentDate = calendar.getTime();
//				dateList.add(simpleDateFormat.format(currentDate));
//			} while (currentDate.before(endDate));
//
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//	} else {
//		dateList.add(value);
//	}
////	System.out.println("getDateList("+value+") dateList="+dateList);
//	return dateList;
//}
