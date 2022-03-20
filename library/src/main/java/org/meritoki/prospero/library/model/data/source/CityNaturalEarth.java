package org.meritoki.prospero.library.model.data.source;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.meritoki.prospero.library.model.query.Query;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.geometry.coordinate.GeometryFactory;

public class CityNaturalEarth extends Source {
	
	private String fileName = this.basePath+"prospero-data/NaturalEarth/ne_10m_populated_places/ne_10m_populated_places.shp";
	private List<Point> pointList;
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("pointList",this.box(-180, 90, 180, -90));
		result.mode = Mode.LOAD;
		query.outputList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.outputList.add(result);
	}
	
	public Point point(double latitude, double longitude) {
		File file = new File(fileName);
		Point p = null;
		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
			Filter filter = ECQL.toFilter(" CONTAINS (the_geom, POINT(" + latitude + " " + longitude + "))");
			SimpleFeatureCollection collection = featureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = collection.features();
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory( null );

					WKTReader reader = new WKTReader();
					p = (Point) reader.read(feature.getDefaultGeometry() + "");

					
//					GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
//					WKTReader reader = new WKTReader(geometryFactory);
//					p = (Point) reader.read(feature.getDefaultGeometry() + "");
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
		}
		return p;
	}

	public List<Point> box(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
		File file = new File(fileName);
		List<Point> pList = new LinkedList<Point>();
		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
			Filter filter = ECQL.toFilter(
					"BBOX (the_geom, " + latitudeA + ", " + longitudeA + ", " + latitudeB + ", " + longitudeB + ")");
			SimpleFeatureCollection collection = featureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = collection.features();
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
//					GeometryFactory geometryFactory = (GeometryFactory) JTSFactoryFinder.getGeometryFactory(null);
					WKTReader reader = new WKTReader();
					Point p = (Point) reader.read(feature.getDefaultGeometry() + "");
					pList.add(p);
//					String name = (String) feature.getAttribute("NAMEASCII");
//					System.out.println("Name:" + name);
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
		}
		return pList;
	}
}
//@Override
//public Object get() {
//	if(this.pointList == null) {
//		this.pointList = this.box(-180, 90, 180, -90);
//	}
//	return this.pointList;
//}

///**
// * Point list is later converted to a Coordiante List
// */
//@Override
//public Object get() {
//	return this.pointList;
//}
