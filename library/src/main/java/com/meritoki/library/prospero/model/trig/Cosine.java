package com.meritoki.library.prospero.model.trig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.meritoki.library.prospero.model.unit.Index;
import com.meritoki.library.prospero.model.unit.Point;

public class Cosine {
	
	//y = A sin(B(x - D)) + C
	public Calendar startCalendar = new GregorianCalendar(1800,0,1,0,0,0);
	public double A;
	public double B;
	public double C;
	public double D;
	public List<Index> indexList = null;
	
	public Cosine(Calendar startCalendar, long milliseconds) {
		this.D = startCalendar.getTimeInMillis();
		this.A = 1;
		this.B = this.getAngularFrequency(this.getFrequency(milliseconds));
		this.C = 0;
	}
	
	public Cosine(List<Index> indexList) {
		this.setIndexList(indexList);
	}
	
	public double getY(long x) {
		return this.A * Math.cos(this.B*(x - this.D)) + this.C;
	}
	
	public List<Point> getPointList() {
		List<Point> pointList = new ArrayList<>();
		for(Index i: this.indexList) {
			Point p = new Point(i.startCalendar.getTimeInMillis(),this.getY(i.startCalendar.getTimeInMillis()));
			pointList.add(p);
		}
		return pointList;
	}

	public void setIndexList(List<Index> indexList) {
		this.indexList = indexList;
		this.C = getZero(indexList);
		this.A = getMax(indexList) - this.C;
		this.B = this.getAngularFrequency(this.getFrequency(this.getAveragePeriod(this.indexList)));
		this.D = getStart(this.indexList);
//		indexList = getZeroList(this.indexList);
//		this.A = getMax(this.indexList);
	}
	
	public static List<Index> getZeroList(List<Index> list) {
//		System.out.println("getZeroList(...,"+mass+")");
		double zero = getZero(getMin(list), getMax(list));
		List<Index> zeroList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Index d = list.get(i);
			d.value -= zero;
			zeroList.add(d);
		}
		return zeroList;
	}
	
	public long getStart(List<Index> list) {
		double zero = getZero(list);
		List<Index> maxList = new ArrayList<>();
		double distance = Double.MAX_VALUE;
		Index zeroIndex = null;
		boolean flag = false;
		for(int i = 0; i < list.size(); i++) {
			Index index = list.get(i);
			double cdistance = Math.abs(index.value - zero);
		    if(cdistance < distance){
		        zeroIndex = index;
		        distance = cdistance;
		    }
		}
		return zeroIndex.startCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
	}
	
	public double getFrequency(long milliseconds) {
		return 1.0/milliseconds;
	}
	
	public double getAngularFrequency(double frequency) {
		return 2*Math.PI * frequency;
	}
	
	public Calendar getMinCalendar(List<Index> list) {
		System.out.println("getMinCalendar("+list.size()+")");
		double zero = getZero(list);
		double distance = Double.MAX_VALUE;
		Index zeroIndex = null;
		for(int i = 0; i < list.size(); i++) {
			Index index = list.get(i);
			double cdistance = Math.abs(index.value - zero);
		    if(cdistance < distance){
		        zeroIndex = index;
		        distance = cdistance;
		    } 
		}
		return zeroIndex.startCalendar;
	}
	
	public long getAveragePeriod(List<Index> list) {
		double zero = getZero(list);
		List<Index> indexList = new ArrayList<>();
		boolean flag = false;
		double threshold = zero * 0.5;
		double zeroMinus = zero - threshold;
		double zeroPlus = zero + threshold;
		System.out.println("Threshold:"+threshold);
		System.out.println("Zero:"+zero);
		System.out.println("ZeroMinus:"+zeroMinus);
		System.out.println("ZeroPlus:"+zeroPlus);
		
		List<List<Index>> indexMatrix = new ArrayList<>();
		for(int i = 0; i < list.size(); i++) {
			Index index = list.get(i);
			if(zeroMinus < index.value && index.value < zeroPlus) {
				indexList.add(index);
				flag = true;
			} else {
				if(flag) {
					indexMatrix.add(indexList);
					indexList = new ArrayList<>();
					flag = false;
				}
			}
		}
		
		List<Calendar> calendarList = new ArrayList<>();
		for(List<Index> iList:indexMatrix) {
			calendarList.add(this.getMinCalendar(iList));
		}
		
		long sum = 0;
		int count = 0;
		for(int i = 0; i < calendarList.size(); i++) {
			if(i < calendarList.size() - 1) {
				long a = calendarList.get(i).getTimeInMillis();
				long b = calendarList.get(i+1).getTimeInMillis();
				long difference = b - a;
				sum += difference;
				count++;
			}
		}
		System.out.println("getAveragePeriod(...) sum="+sum);
		System.out.println("getAveragePeriod(...) count="+count);
		return (count > 0)?(sum/count)*2:0;
	}
	
	public double getZero(List<Index> list) {
		return getZero(getMin(list),getMax(list));
	}
	
	public static double getZero(double min, double max) {
		double zero = ((max - min) / 2) + min;
//		System.out.println("getZero(...) zero=" + zero);
		return zero;
	}
	
	public static double getMin(List<Index> list) {
		double min = Double.MAX_VALUE;
		for (Index d : list) {
			if (d.value < min) {
				min = d.value;
			}
		}
//		System.out.println("getMin(...) min=" + min);
		return min;
	}

	public static double getMax(List<Index> list) {
		double max = Double.MIN_VALUE;
		for (Index d : list) {
			if (d.value > max) {
				max = d.value;
			}
		}
//		System.out.println("getMax(...) max=" + max);s
		return max;
	}
	
	public double getA() {
		return A;
	}
	
	public double getB() {
		return B;
	}
	
	public double getC() {
		return C;
	}
	
	public double getD() {
		return D;
	}
	
}
