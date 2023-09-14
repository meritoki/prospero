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
package org.meritoki.prospero.library.model.terra.lithosphere;

import java.util.List;

import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.lithosphere.earthquake.Earthquake;
import org.meritoki.prospero.library.model.terra.lithosphere.magnetic.Magnetic;
import org.meritoki.prospero.library.model.terra.lithosphere.tectonic.Tectonic;
import org.meritoki.prospero.library.model.terra.lithosphere.volcano.Volcanic;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lithosphere extends Terra {

	static Logger logger = LoggerFactory.getLogger(Lithosphere.class.getName());
	protected DataType dataType;

	public Lithosphere() {
		super("Lithosphere");
		this.addChild(new Magnetic());
		this.addChild(new Earthquake());
		this.addChild(new Tectonic());
		this.addChild(new Volcanic());
		this.sourceMap.put("GEBCO", "1aac29c0-e2f6-45e8-9921-c88397957795");
		this.dataType = DataType.ELEVATION;
	}

	public Lithosphere(String name) {
		super(name);
	}

	@Override
	public void init() {
//		this.dimension = 1;
		super.init();
	}

	@Override
	public void load(Result result) {
		super.load(result);
		List<NetCDF> netCDFList = result.getNetCDFList();
		this.netCDFList.addAll(netCDFList);
		try {
			this.process(netCDFList);
		} catch (Exception e) {
			logger.error("load(" + (result != null) + ") exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void process() throws Exception {
		super.process();
		try {
//			this.process(this.netCDFList);
			this.complete();
		} catch (Exception e) {
			logger.error("process() exception=" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void process(List<NetCDF> netCDFList) throws Exception {
		this.setMatrix(netCDFList);
		this.tileList = this.getTileList();
		this.tileFlag = true;
		this.initTileMinMax();
	}

	public void setMatrix(List<NetCDF> netCDFList) {
		this.setCoordinateAndDataMatrix(this.coordinateMatrix, this.dataMatrix, netCDFList);
		this.initMonthArray(null);
		this.initYearMap(null);
	}

	public void setCoordinateAndDataMatrix(int[][][] coordinateMatrix, float[][][] dataMatrix,
			List<NetCDF> netCDFList) {
		for (NetCDF netCDF : netCDFList) {
			if (netCDF.type == this.dataType) {
				long latSize = netCDF.latArray.getSize();
				long lonSize = netCDF.lonArray.getSize();
				for (int lat = 0; lat < latSize; lat++) {
					float latitude = netCDF.latArray.get(lat);
					latitude *= this.resolution;
					for (int lon = 0; lon < lonSize; lon++) {
						float longitude = netCDF.lonArray.get(lon);
						longitude *= this.resolution;
						int x = (int) (((latitude + (this.latitude * this.resolution) / 2)) % (this.latitude * this.resolution));
						int y = (int) (((longitude + (this.longitude * this.resolution) / 2)) % (this.longitude * this.resolution));
						int z = 0;
						dataMatrix[x][y][z] += netCDF.variableMatrix.get(lat, lon);
						coordinateMatrix[x][y][z]++;
					}
				}
			}
		}
	}
}
//@SuppressWarnings("unchecked")
//@Override
//public void paint(Graphics graphics) throws Exception {
//	super.paint(graphics);
//	if (this.load) {
////		String sourceUUID = this.sourceMap.get(this.sourceKey);
////		this.coordinateList = (List<Coordinate>) this.data.get(sourceUUID, this.query);
//		if (this.coordinateList != null) {
//			this.initCoordinateListMinMax("elevation", null);
//			List<Point> coordinateList = this.projection.getCoordinateList(0, this.coordinateList);
//			if (coordinateList != null) {
//				for (Point c : coordinateList) {
//					if (c != null) {
//						if (c.attribute.get("elevation") != null) {
//							graphics.setColor(this.chroma.getColor((double) c.attribute.get("elevation"),
//									this.min, this.max));
//						}
//						graphics.fillOval((int) ((c.x) * this.projection.scale),
//								(int) ((c.y) * this.projection.scale), (int) 5, (int) 5);
//					}
//				}
//			}
//		}
//	}
////	List<Variable> nodeList = this.getChildren();
////	for(Variable n: nodeList) {
////		n.paint(graphics);
////	}
//}
//Object object = result.map.get("coordinateList");
//if(object != null) {
//	this.coordinateList = (List<Coordinate>)object;
//	if (this.coordinateList.size() == 0) {
//		logger.warn("load(...) this.coordinateList.size() == 0");
//	}
//}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
//@Override
//public void init() throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	if (sourceUUID != null && !sourceUUID.equals("null")) {
//		Query query = null;
//		if (this.queryStack.size() > 0) {
//			query = this.queryStack.poll();
//		}
//		if (!this.query.equals(query)) {
//			Object object = this.data.get(sourceUUID, this.query, "cyclone");
//			this.coordinateList = (List<Coordinate>)object;
//		}
//	}
//}
