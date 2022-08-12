package org.meritoki.prospero.library.model.unit;

public class Tile {

	public double latitude;
	public double longitude;
	public double dimension;
	public float value;
	private boolean print = false;

	public Tile(Tile tile) {
		if (tile != null) {
			this.latitude = tile.latitude;
			this.longitude = tile.longitude;
			this.dimension = tile.dimension;
			this.value = tile.value;
		}
	}
	
	public Tile(double latitude, double longitude, double dimension) {
		if (print)
			System.out.println("Tile(" + latitude + "," + longitude + "," + dimension + "," + value + ")");
		this.latitude = latitude;
		this.longitude = longitude;
		this.dimension = dimension;
	}

	public Tile(double latitude, double longitude, double dimension, double value) {
		if (print)
			System.out.println("Tile(" + latitude + "," + longitude + "," + dimension + "," + value + ")");
		this.latitude = latitude;
		this.longitude = longitude;
		this.dimension = dimension;
		this.value = (float) value;// (float) Math.abs(value);
	}
	
	public boolean equals(Object o) {
		if(o instanceof Tile) {
			Tile t = (Tile)o;
			return t.latitude == this.latitude && t.longitude == this.longitude && t.dimension == this.dimension;
		}
		return false;
	}

	public String toString() {
		return "latitude=" + this.latitude + ", longitude=" + this.longitude + ", dimension=" + dimension; 
				//+ ", value=" + value;
	}
}
