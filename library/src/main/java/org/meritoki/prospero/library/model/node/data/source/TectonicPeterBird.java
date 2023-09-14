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
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.WKTReader;
import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.opengis.feature.simple.SimpleFeature;

public class TectonicPeterBird extends Source {

	public String downloadURL = "https://github.com/fraxen/tectonicplates/archive/master.zip";
	public String zipFile = "master.zip";

	public TectonicPeterBird() {
		super();
		this.setRelativePath("PeterBird");
		this.setDownloadPath("PeterBird");
		this.setFileName("PB2002_boundaries.shp");
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
				NodeController.unzipFile(this.getDownloadPath() + zipFile, this.getDownloadPath());
			}
		}
		Result result = new Result();
		result.map.put("multiLineStringList", this.box(-180, 90, 180, -90));
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}

	public List<MultiLineString> box(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
		File file = new File(this.getFilePath());
		MultiLineString m = null;
		List<MultiLineString> mList = new LinkedList<MultiLineString>();
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
					m = (MultiLineString) reader.read(feature.getDefaultGeometry() + "");
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
//GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
//public String fileName = basePath+"prospero-data/PeterBird/PB2002_boundaries.shp";
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
