package org.meritoki.prospero.library.model.unit;

import java.util.ArrayList;
import java.util.List;

public class Band {
	public List<Tile> tileList = new ArrayList<>();
	public double value;
	public double latitude;
	
	public Band(List<Tile> tileList, double latitude) {
		this.latitude = latitude;
		this.tileList = tileList;
		this.value = this.average(this.tileList);
	}
	
	public double average(List<Tile> tileList) {
		double quotient = 0;
		double sum=0;
		double size = 0;
		for(Tile t: tileList) {
			if(t.value != 0) {
				sum += t.value;
				size++;
			}
		}
		quotient = (size>0)?sum/size:quotient;
//		System.out.println("average("+tileList+") quotient="+quotient);
		return quotient;
	}
}
