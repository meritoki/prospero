/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.library.model.node.data.source;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

public class CityNaturalEarth extends Source {
	

	
	public CityNaturalEarth() {
		super();
		this.setRelativePath("NaturalEarth"+seperator+"ne_10m_populated_places"+seperator);
		this.setFileName("ne_10m_populated_places.shp");
	}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("pointList",this.box(-180, 90, 180, -90));
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}
	
	public Point point(double latitude, double longitude) {
		File file = new File(this.getFilePath());
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
					WKTReader reader = new WKTReader();
					p = (Point) reader.read(feature.getDefaultGeometry() + "");
				}
			} finally {
				iterator.close();
			}

		} catch (Throwable e) {
		}
		return p;
	}

	public List<Point> box(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
		File file = new File(this.getFilePath());
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
//private String fileName = this.basePath+"prospero-data/NaturalEarth/ne_10m_populated_places/ne_10m_populated_places.shp";
//private List<Point> pointList;
//GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
//WKTReader reader = new WKTReader(geometryFactory);
//p = (Point) reader.read(feature.getDefaultGeometry() + "");
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
