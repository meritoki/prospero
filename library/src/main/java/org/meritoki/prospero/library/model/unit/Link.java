package org.meritoki.prospero.library.model.unit;

public class Link extends Coordinate {
 
	public static final int START = 0;
	public static final int STOP = 1;	
	public int type = -1;

	public Link(double latitude, double longitude, int type) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
	}
}
