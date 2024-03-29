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
package org.meritoki.prospero.library.model.unit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index implements Comparable<Index> {
	
	static Logger logger = LoggerFactory.getLogger(Index.class.getName());
	public Calendar startCalendar;
	public Calendar endCalendar;
	public double value;
	public Map<String,Object> map = new HashMap<>();
	public boolean flag;
	public boolean print = false;
	
	public Index() {}
	
	public Index(Object object) {
		if(object instanceof Index) {
			Index i = (Index)object;
			startCalendar = i.startCalendar;
			endCalendar = i.endCalendar;
			value = i.value;
			map = new HashMap<>(i.map);
			flag = i.flag;
		}
	}
	
	public Index(double value, Calendar startCalendar) {
		this.value = value;
		this.startCalendar = startCalendar;
	}
	
	public boolean equals(Object object) {
		if(object instanceof Index) {
			 Index i = (Index)object;
			 if(this.startCalendar != null) {
				 return this.startCalendar.equals(i.startCalendar);
			 }
		}
		return false;
	}
	
	@Override
	public int compareTo(Index index) {
		// TODO Auto-generated method stub
		return this.startCalendar.compareTo(index.startCalendar);
	}
	
	public boolean containsCalendar(Calendar calendar) {
		Date date = calendar.getTime();
		Date startDate = this.startCalendar.getTime();
		Date endDate = this.endCalendar.getTime();
		if(print)System.out.println("date="+date);
		if(print)System.out.println("startDate="+startDate);
		if(print)System.out.println("endDate="+endDate);
		boolean flag = (startDate != null && endDate != null)?startDate.before(date) && date.before(endDate) || startDate.equals(date) || endDate.equals(date):false;
		if(print)System.out.println("flag="+flag);
		return flag;
	}
	
	public long getSeconds(Calendar startCalendar) {
		Date d1 = startCalendar.getTime();
		Date d2 = this.startCalendar.getTime();
		long seconds = (d2.getTime() - d1.getTime()) / 1000;

		return seconds;
	}
	
	public Double getDays(Calendar startCalendar) {
		Date d1 = startCalendar.getTime();
		Date d2 = this.startCalendar.getTime();
		long seconds = (d2.getTime() - d1.getTime()) / 1000;
		long minutes = seconds/60;
		long hours = minutes/60;
		double days = hours/24;
		return days;
	}
	
	public Point getPoint(Calendar startCalendar) {
		Point point = new Point();
		point.x = this.getDays(startCalendar);
		point.y = this.value;
		return point;
	}
	
	public static Object[] getObjectArray(List<Index> indexList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (indexList != null) {
			if (indexList.size() > 0) {
				for(int i=0;i<indexList.size();i++) {
					Index r = indexList.get(i);
					if(i==0) {
						columnArray = Table.getColumnNames(r.map.size()+3).toArray();
						dataMatrix = new Object[indexList.size()+1][r.map.size()+3];
						dataMatrix[i][0] = "startCalendar";
						dataMatrix[i][1] = "endCalendar";
						dataMatrix[i][2] = "value";
						if(r.map.size()>0 && dataMatrix[i].length > 3) {
							int index = 3;
							for(String key: r.map.keySet()) {
								dataMatrix[i][index] = key;
								index++;
							}
						}
					}
					dataMatrix[i+1][0] = dateFormat.format(r.startCalendar.getTime());
					dataMatrix[i+1][1] = (r.endCalendar != null)?dateFormat.format(r.endCalendar.getTime()):null;
					dataMatrix[i+1][2] = r.value;
					if(r.map.size()>0 && dataMatrix[i+1].length > 3) {
						int index = 3;
						for(String key: r.map.keySet()) {
							dataMatrix[i+1][index] = r.map.get(key);
							index++;
						}
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}
	
	public static TableModel getTableModel(List<Index> indexList) {
		Object[] objectArray = getObjectArray(indexList);
		return new javax.swing.table.DefaultTableModel((Object[][])objectArray[1], (Object[])objectArray[0]);
	}
	
	/**
	 * 20230609 May need to implement with negative
	 * @param list
	 * @return
	 */
	public static double getMin(List<Index> list) {
		double min = Double.MAX_VALUE;
		for (Index d : list) {
			if (d.value < min) {
				min = d.value;
			}
		}
		logger.info("getMin(...) min=" + min);
		return min;
	}
	
	public static double getMax(List<Index> list) {
		double max = Double.MIN_VALUE;
		for (Index d : list) {
			if (d.value > max) {
				max = d.value;
			}
		}
		logger.info("getMax(...) max=" + max);
		return max;
	}

	public static double getZero(double min, double max) {
		double zero = ((max - min) / 2) + min;
		logger.info("getZero(...) zero=" + zero);
		return zero;
	}

	public static List<Index> getZeroList(List<Index> list) {
//		logger.info("getZeroList(...,"+mass+")");
		double zero = getZero(getMin(list), getMax(list));
		List<Index> zeroList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Index d = list.get(i);
			d.value -= zero;
			zeroList.add(d);
		}
		return zeroList;
	}

	
	@Override
	public String toString() {
		return this.flag+","+this.value+", "+this.startCalendar.getTime();//+", "+this.endCalendar.getTime();
	}


}
