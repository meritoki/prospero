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
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.WKTReader;
import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.opengis.feature.simple.SimpleFeature;

public class CountryNaturalEarth extends Source {

	public String downloadURL = "https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/cultural/ne_10m_admin_0_countries.zip";
	public String zipFile = "ne_10m_admin_0_countries.zip";

	public CountryNaturalEarth() {
		super();
		this.setRelativePath("NaturalEarth" + seperator + "ne_10m_admin_0_countries");
		this.setDownloadPath("NaturalEarth");
		this.setFileName("ne_10m_admin_0_countries.shp");
	}

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		if (query.getDownload()) {
			File file = new File(this.getFilePath());
			if (!file.exists()) {
				logger.info("query(" + (query != null) + ") download");
				File zip = new File(this.getDownloadPath() + zipFile);
				if (!zip.exists()) {
					logger.info("query(" + (query != null) + ") downloading");
					NodeController.downloadFile(downloadURL, this.getDownloadPath(), zipFile);
				}
				logger.info("query(" + (query != null) + ") extracting");
				NodeController.unzipFile(this.getDownloadPath() + zipFile,
						this.getDownloadPath() + "ne_10m_admin_0_countries");
			}
		}
		Result result = new Result();
		result.map.put("multiPolygonList", this.box(-180, 90, 180, -90));
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}

	public MultiPolygon point(double latitude, double longitude) {
		MultiPolygon f = null;
		File file = new File(this.getFilePath());
		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
			org.opengis.filter.Filter filter = ECQL
					.toFilter("CONTAINS (the_geom, POINT(" + latitude + " " + longitude + "))");
			SimpleFeatureCollection collection = featureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = collection.features();
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					System.out.println(feature.getAttribute("NAME"));
					WKTReader reader = new WKTReader();
					MultiPolygon polygon = (MultiPolygon) reader.read(feature.getDefaultGeometry() + "");
					f = polygon;
				}
			} finally {
				iterator.close();
			}
		} catch (Throwable e) {
		}
		return f;
	}

	public List<MultiPolygon> box(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
		File file = new File(this.getFilePath());
		MultiPolygon f = null;
		List<MultiPolygon> mList = new LinkedList<MultiPolygon>();
		try {
			ShapefileDataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
			String[] typeNames = dataStore.getTypeNames();
			String typeName = typeNames[0];
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
			org.opengis.filter.Filter filter = ECQL.toFilter(
					"BBOX (the_geom, " + latitudeA + ", " + longitudeA + ", " + latitudeB + ", " + longitudeB + ")");
			SimpleFeatureCollection collection = featureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = collection.features();
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					WKTReader reader = new WKTReader();
					MultiPolygon polygon = (MultiPolygon) reader.read(feature.getDefaultGeometry() + "");
					f = polygon;
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
//GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
//System.out.println("box("+latitudeA+","+longitudeA+")");
//private String fileName = this.basePath+"prospero-data/NaturalEarth/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
//private List<MultiPolygon> multiPolygonList;

//@Override
//public Object get() {
//	if(this.multiPolygonList == null) {
//		this.multiPolygonList = (List<MultiPolygon>) this.box(-180, 90, 180, -90);
//	}
//	return this.multiPolygonList;
//}
