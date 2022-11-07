package org.meritoki.prospero.library.model.data.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;

import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class MagneticNOAAEMAG extends Source {
	
	public String fileName  = this.basePath+"prospero-data/NOAA/EMAG2/EMAG2_V2.nc";
//	public List<Coordinate> list;
	public int latitudeInterval = 15;
	public int longitudeInterval = 15;
	
	public static void main(String[] args) {
		MagneticNOAAEMAG n = new MagneticNOAAEMAG();
		n.box(-180,90,180, -90);
	}
	
	public MagneticNOAAEMAG() {}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("coordinateList",this.box(-90,-180, 90, 180));
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
	public List<Coordinate> box(double latA, double lonA, double latB, double lonB) {
		NetcdfFile dataFile = null;
		List<Coordinate> cList = new LinkedList<Coordinate>();
		try {
			dataFile = NetcdfFile.open(fileName, null);
			Variable yRangeVar = dataFile.findVariable("y_range");//latitude
			Variable xRangeVar = dataFile.findVariable("x_range");//longitude
			Variable zVar = dataFile.findVariable("z");
			Variable dimensionVar = dataFile.findVariable("dimension");
			Variable spacingVar = dataFile.findVariable("spacing");
			int xRangeCount = (int) xRangeVar.getSize();
			int yRangeCount = (int) yRangeVar.getSize();
			int zCount = (int) zVar.getSize();
			int dimensionCount = (int) dimensionVar.getSize();
			int spacingCount = (int) spacingVar.getSize();
			System.out.println(xRangeCount);
			System.out.println(yRangeCount);
			System.out.println(zCount);
			System.out.println(dimensionCount);
			System.out.println(spacingCount);
			ArrayDouble.D1 xRangeArray = (ArrayDouble.D1) xRangeVar.read();
			ArrayDouble.D1 yRangeArray = (ArrayDouble.D1) yRangeVar.read();
			ArrayFloat.D1 zArray = (ArrayFloat.D1)zVar.read();
			ArrayInt.D1 dimensionArray = (ArrayInt.D1)dimensionVar.read();
			ArrayDouble.D1 spacingArray = (ArrayDouble.D1)spacingVar.read();
			
			System.out.println(yRangeArray.get(0)+":"+yRangeArray.get(1));
			System.out.println(xRangeArray.get(0)+":"+xRangeArray.get(1));
			System.out.println(dimensionArray.get(0)+":"+dimensionArray.get(1));//longitude, latitude
			System.out.println(spacingArray.get(0)+":"+spacingArray.get(1));
			double latitude = 0;
			double longitude = 0;
			double z;
			Coordinate c = null;
			
			for(int i=0;i<dimensionArray.get(1);i+=this.latitudeInterval) {
				for(int j=0;j<dimensionArray.get(0);j+=this.longitudeInterval) {
					z = (zArray.get(i * dimensionArray.get(0) + j));
					latitude = 90-i*0.03333333333333333333333333333333;
					longitude = -180+j*0.033333333333333333333333333333333;
//					System.out.println(latitude+":"+longitude);
					c = new Coordinate();
					c.latitude = latitude;
					c.longitude = longitude;
					c.attribute.put("z", z);
					cList.add(c);
				}
			}
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
		return cList;
	}
}
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
