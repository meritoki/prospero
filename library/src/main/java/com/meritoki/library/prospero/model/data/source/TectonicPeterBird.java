package com.meritoki.library.prospero.model.data.source;

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

import com.meritoki.library.prospero.model.query.Query;
import com.meritoki.library.prospero.model.unit.Mode;
import com.meritoki.library.prospero.model.unit.Result;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.WKTReader;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.MultiLineString;
//import com.vividsolutions.jts.io.WKTReader;

public class TectonicPeterBird extends Source {
	
	public String fileName = basePath+"prospero-data/PeterBird/PB2002_boundaries.shp";
	
	public TectonicPeterBird() {}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("multiLineStringList",this.box(-180, 90, 180, -90));
		result.mode = Mode.LOAD;
		query.outputList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.outputList.add(result);
	}
	
	public List<MultiLineString> box(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
		File file = new File(this.fileName);
		MultiLineString m = null;
		List<MultiLineString> mList = new LinkedList<MultiLineString>();
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
					m = (MultiLineString) reader.read(feature.getDefaultGeometry()+"");
					mList.add(m);
					
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
		}
		return mList;
	}
}
//private List<MultiLineString> multiLineStringList;
//@Override
//public Object get() {
//	if(this.multiLineStringList == null) {
//		this.multiLineStringList = box(-180, 90, 180, -90);
//	}
//	return this.multiLineStringList;
//}

//@Override 
//public Object get() {
//	return this.multiLineStringList;
//}
