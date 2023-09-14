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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;

import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class MagneticNOAAEMAG extends Source {
	
//	public String fileName  = this.basePath+"prospero-data/NOAA/EMAG2/EMAG2_V2.nc";
//	public List<Coordinate> list;
//	public int latitudeInterval = 15;
//	public int longitudeInterval = 15;
	public DataType dataType = DataType.MAGNETIC;
	
//	public static void main(String[] args) {
//		MagneticNOAAEMAG n = new MagneticNOAAEMAG();
//		n.box(-180,90,180, -90);
//	}
	
	public MagneticNOAAEMAG() {
		super();
		this.setRelativePath("NOAA"+seperator+"EMAG2"+seperator);
		this.setFileName("EMAG2_V2.nc");
	}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("netCDFList",this.read());
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}
	
	/**
	 *  head -25
netcdf EMAG2_V2 {
dimensions:
	x = 2 ;
	x_2 = 58336201 ;
variables:
	double x_range(x) ;
		x_range:units = "longitude [degrees_east]" ;
	double y_range(x) ;
		y_range:units = "latitude [degrees_north]" ;
	double z_range(x) ;
		z_range:units = "z" ;
	double spacing(x) ;
	int dimension(x) ;
	float z(x_2) ;
		z:_FillValue = NaNf ;
		z:missing_value = NaNf ;
		z:node_offset = 0 ;
	 * 
	 */
	public List<NetCDF> read() {
		NetcdfFile dataFile = null;
		List<NetCDF> netCDFList = new LinkedList<>();
		try {
			dataFile = NetcdfFile.open(this.getFilePath(), null);
			Variable yRangeVar = dataFile.findVariable("y_range");//latitude
			Variable xRangeVar = dataFile.findVariable("x_range");//longitude
			Variable zVar = dataFile.findVariable("z");
			Variable dimensionVar = dataFile.findVariable("dimension");
			Variable spacingVar = dataFile.findVariable("spacing");
			ArrayDouble.D1 xRangeArray = (ArrayDouble.D1) xRangeVar.read();
			ArrayDouble.D1 yRangeArray = (ArrayDouble.D1) yRangeVar.read();
			ArrayFloat.D1 zArray = (ArrayFloat.D1)zVar.read();
			ArrayInt.D1 dimensionArray = (ArrayInt.D1)dimensionVar.read();
			ArrayDouble.D1 spacingArray = (ArrayDouble.D1)spacingVar.read();
			NetCDF netCDF = new NetCDF();
			netCDF.type = this.dataType;
			netCDF.latArray = new ArrayFloat.D1(dimensionArray.get(1));
			netCDF.lonArray = new ArrayFloat.D1(dimensionArray.get(0));
			netCDF.variableMatrix = new ArrayFloat.D2(dimensionArray.get(1), dimensionArray.get(0));
			float latitude = 0;
			float longitude = 0;
			float z;
			for(int i=0;i<dimensionArray.get(1);i++) {
				latitude = (float)(yRangeArray.get(1)-i*spacingArray.get(0));
				netCDF.latArray.set(i,latitude);
				for(int j=0;j<dimensionArray.get(0);j++) {
					longitude = (float)(xRangeArray.get(0)+j*spacingArray.get(1));
					netCDF.lonArray.set(j,longitude);
					z = (zArray.get(i * dimensionArray.get(0) + j));
					netCDF.variableMatrix.set(i,j,z);
				}
			}
			netCDFList.add(netCDF);
		}catch (java.io.IOException e) {
			e.printStackTrace();
		} finally {
			if (dataFile != null) {
				try {
					dataFile.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		return netCDFList;
	}
}
//int xRangeCount = (int) xRangeVar.getSize();
//int yRangeCount = (int) yRangeVar.getSize();
//int zCount = (int) zVar.getSize();
//int dimensionCount = (int) dimensionVar.getSize();
//int spacingCount = (int) spacingVar.getSize();
//System.out.println(xRangeCount);//latitude
//System.out.println(yRangeCount);//longitude
//System.out.println(zCount);
//System.out.println(dimensionCount);
//System.out.println(spacingCount);
//System.out.println(yRangeArray.get(0)+":"+yRangeArray.get(1));//latitude
//System.out.println(xRangeArray.get(0)+":"+xRangeArray.get(1));//longitude
//System.out.println(dimensionArray.get(0)+":"+dimensionArray.get(1));//longitude, latitude
//System.out.println(spacingArray.get(0)+":"+spacingArray.get(1));
//Coordinate c = null;
//System.out.println(latitude+":"+longitude);
//c = new Coordinate();
//c.latitude = latitude;
//c.longitude = longitude;
//c.attribute.put("z", z);
//cList.add(c);
//@Override
//public Object get() {
//	if(this.list == null) {
//		this.list = this.box(-90,-180, 90, 180);
//	}
//	return this.list;
//}

//@Override
//public Object get() {
//
//	return this.list;//new ArrayList<>(this.list);
//}
