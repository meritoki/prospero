package com.meritoki.library.prospero.model.terra.cartography;

import java.awt.Graphics;
import java.util.List;

import com.meritoki.library.prospero.model.unit.Coordinate;
import org.locationtech.jts.geom.MultiPolygon;

public interface ProjectionInterface {
		
	public Coordinate getCoordinate(double vertical, double latitude, double longitude);
	
//	public abstract List<Coordinate> getMultiPolygonList(double vertical,List<MultiPolygon> countryList); 
//	
//	public abstract List<Coordinate> getGridCoordinateList(double vertical,int latitudeInterval, int longitudeInterval);
//	
//	public void paint(Graphics graphics, double vertical);
}
