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
import org.opengis.feature.simple.SimpleFeature;
import org.locationtech.jts.io.WKTReader;
import org.meritoki.prospero.library.model.query.Query;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.locationtech.jts.geom.MultiPolygon;

public class CountryNaturalEarth extends Source {
	
	private String fileName = this.basePath+"prospero-data/NaturalEarth/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
	private List<MultiPolygon> multiPolygonList;
	
	public CountryNaturalEarth() {}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("multiPolygonList",this.box(-180, 90, 180, -90));
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}
	
//	@Override
//	public Object get() {
//		if(this.multiPolygonList == null) {
//			this.multiPolygonList = (List<MultiPolygon>) this.box(-180, 90, 180, -90);
//		}
//		return this.multiPolygonList;
//	}
	
	public MultiPolygon point(double latitude, double longitude) {
		MultiPolygon f = null;
		File file = new File(this.fileName);
		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
			org.opengis.filter.Filter filter = ECQL.toFilter("CONTAINS (the_geom, POINT(" + latitude + " " + longitude + "))");
			SimpleFeatureCollection collection = featureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = collection.features();
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					System.out.println(feature.getAttribute("NAME"));
//					GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
					WKTReader reader = new WKTReader();
					MultiPolygon polygon = (MultiPolygon) reader.read(feature.getDefaultGeometry()+"");
					f=polygon;
				}
			} finally {
				iterator.close();
			}
		} catch (Throwable e) {
		}
		return f;
	}

	public List<MultiPolygon> box(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
//		System.out.println("box("+latitudeA+","+longitudeA+")");
		File file = new File(fileName);
		MultiPolygon f =null;
		List<MultiPolygon> mList = new LinkedList<MultiPolygon>();
		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
			org.opengis.filter.Filter filter = ECQL.toFilter("BBOX (the_geom, " + latitudeA + ", " + longitudeA + ", " + latitudeB + ", " + longitudeB + ")");
			SimpleFeatureCollection collection = featureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = collection.features();
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
//					GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
					WKTReader reader = new WKTReader();
					MultiPolygon polygon = (MultiPolygon) reader.read(feature.getDefaultGeometry()+"");
					f=polygon;
					mList.add(f);
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return mList;
	}
}
