package com.meritoki.library.prospero.model.unit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import com.meritoki.library.prospero.model.table.Table;

public class Index implements Comparable<Index> {
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
	
	public static TableModel getTableModel(List<Index> regressionList) {
		Object[] objectArray = getObjectArray(regressionList);
		return new javax.swing.table.DefaultTableModel((Object[][])objectArray[1], (Object[])objectArray[0]);
	}
	
	
	
	@Override
	public String toString() {
		return this.flag+","+this.value+", "+this.startCalendar.getTime();//+", "+this.endCalendar.getTime();
	}

	@Override
	public int compareTo(Index index) {
		// TODO Auto-generated method stub
		return this.startCalendar.compareTo(index.startCalendar);
	}
}
