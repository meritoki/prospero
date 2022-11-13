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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meritoki.prospero.library.model.solar.Solar;
import org.meritoki.prospero.library.model.unit.Index;

public class Triangle extends Energy {

	public double a, b, c;
	public double A, B, C;
	public Energy i, j, k;

	public Triangle(Energy i, Energy j, Energy k) {
		super(i.name+","+j.name+","+k.name);
		this.i = i;
		this.j = j;
		this.k = k;
		this.update();
	}
	
	public void update() {
		this.a = j.getDistance(k);
		this.b = i.getDistance(k);
		this.c = i.getDistance(j);
		this.A = (Math.pow(this.b, 2) + Math.pow(this.c, 2) - Math.pow(this.a, 2)) / (2 * this.b * this.c);
		this.A = Math.acos(this.A);
		this.A = Math.toDegrees(this.A);
		this.B = (Math.pow(this.c, 2) + Math.pow(this.a, 2) - Math.pow(this.b, 2)) / (2 * this.a * this.c);
		this.B = Math.acos(this.B);
		this.B = Math.toDegrees(this.B);
		this.C = 180 - this.A - this.B;
//		System.out.println(this.name+":"+a+":"+b+":"+c+":"+A+":"+B+":"+C);
	}
	
	public boolean equals(Object o) {
		Triangle t = (o instanceof Triangle)?(Triangle)o:null;
		return (t != null)?(this.j == t.j && this.k == t.k)|| (this.j == t.k && this.k == t.j):false; 
	}
	
	@Override
	public Map<String,List<Index>> getIndexListMap() throws Exception {
		
		Map<String, List<Index>> map = this.indexListMap;
		if(map == null) {
			map = new HashMap<>();
			this.indexListMap = map;
		}
		List<Index> indexList = null;
		indexList = map.get("Angle");
		if(indexList == null) {
			map.put("Angle", this.getAngleList());
		}
		indexList = map.get("Angle");
		return map;
	}
	
	public List<Index> getAngleList() throws Exception{
		List<Index> indexList = new ArrayList<>();
		Solar solar = (Solar)this.getParent().getParent();
		List<String> dateList = getDateList(this.period, Calendar.DATE);
		Calendar c = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (String date : dateList) {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			solar.setCalendar(c);
			this.update();
			Index index = new Index();
			index.value = this.A;
			index.startCalendar = c;
//			System.out.println(index);
			indexList.add(index);
		}
		return indexList;
	}
	
	public boolean contains(String name) {
		return name.equals(j.name)|| name.equals(k.name); 
	}
	
	public double getMass() {
		return (j.mass < k.mass)?j.mass:k.mass;//j.mass+k.mass;
	}
	
	public double getMassSum() {
		return j.mass + k.mass;
	}
	
	public double getMassRatio() {
		return (j.mass < k.mass)?j.mass/k.mass:k.mass/j.mass;//j.mass+k.mass;
	}
	
	public double getDistance() {
		return (this.b < this.c)? this.b: this.c; 
	}
	
	public double getDistanceSum() {
		return this.b + this.c;
	}
	
	public double getGravityForce() {
		return this.j.getGravityForce(this.k);
	}
	
//	public double getVolume() {
////		return (j > k.mass)?j.mass:k.mass;
//	}
	
//	public boolean contains()

	public boolean thresholdA(double a, double threshold) {
		boolean flag = false;
		if (threshold <=1 && threshold >=0) {//threshold has to be between 0 and 1
			double value = (this.A>0)?(a/this.A):1;
			if (threshold <= value && value < (1+(1-threshold))) {
				flag = true;
			}
		}
		return flag;
	}
	
//	public String toString(){
//		return this.getData();
//	}
	
	public void paint(Graphics graphics) {
		this.initVariableMap();
		this.update();
	}
}