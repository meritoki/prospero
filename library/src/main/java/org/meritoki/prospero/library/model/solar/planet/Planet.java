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
package org.meritoki.prospero.library.model.solar.planet;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.function.Sine;
import org.meritoki.prospero.library.model.node.Orbital;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Point;

public class Planet extends Orbital {

	static Logger logger = LogManager.getLogger(Planet.class.getName());
	
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
		super.paint(graphics);

	}
}
//this.initVariableMap();
//List<Point> vertexList = this.getOrbit();
//graphics.setColor(Color.gray);
//radius = 5;
//for (int i = 1; i < vertexList.size(); i++) {
//	graphics.drawLine((int) (vertexList.get(i - 1).x * this.projection.scale),
//			(int) (vertexList.get(i - 1).y * this.projection.scale), (int) (vertexList.get(i).x * this.projection.scale),
//			(int) (vertexList.get(i).y * this.projection.scale));
//}

//super.paint(graphics);
//List<Variable> nodeList = this.getChildren();
//for (Variable n : nodeList) {
//	if(n instanceof Energy) {
//		n.paint(graphics);
//	}
//}
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
