package org.meritoki.prospero.library.model.node.cartography;

import org.meritoki.prospero.library.model.unit.Point;

public interface ProjectionInterface {
		
	public Point getPoint(double vertical, double latitude, double longitude);

}
//public abstract List<Coordinate> getMultiPolygonList(double vertical,List<MultiPolygon> countryList); 
//
//public abstract List<Coordinate> getGridCoordinateList(double vertical,int latitudeInterval, int longitudeInterval);
//
//public void paint(Graphics graphics, double vertical);
