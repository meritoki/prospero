package org.meritoki.prospero.library.model.data.source;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.meritoki.prospero.library.model.query.Query;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;

public class MagneticNOAAWMM extends Source {
	
	public TSAGeoMag tsaGeoMag = new TSAGeoMag();
	public List<Coordinate> coordinateList;
	public double latitude = 180;
	public double longitude = 360;
	public double dimension = 2;
	
	public static void main(String[] args) {
		MagneticNOAAWMM n = new MagneticNOAAWMM();
		n.box(-180,90,180, -90);
	}
	
	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("coordinateList",this.box(-90,-180, 90, 180));
		result.mode = Mode.LOAD;
		query.outputList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.outputList.add(result);
	}
	
	public List<Coordinate> box(double latA, double lonA, double latB, double lonB) {
		List<Coordinate> coordinateList = new ArrayList<>();
		Coordinate coordinate;
		for(int i=0;i<this.latitude;i+=this.dimension) {
			for(int j=0;j<this.longitude;j+=this.dimension) {
//				for(int k = 2000; k< 2025;k++) {
					double declination = tsaGeoMag.getDeclination(90-i, 180-j, 2001, 0);
					double intensity = tsaGeoMag.getIntensity(90-i, 180-j, 2001, 0);
					coordinate = new Coordinate();
					coordinate.calendar = new GregorianCalendar(2001,0,1);
					coordinate.latitude = 90-i;
					coordinate.longitude = 180-j;
					coordinate.attribute.put("declination",declination);
					coordinate.attribute.put("intensity",intensity);
					coordinateList.add(coordinate);
//				}
			}
		}
		return coordinateList;
	}

}
//@Override
//public Object get() {
//	if(this.coordinateList == null) {
//		this.coordinateList = this.box(-90,-180, 90, 180);
//	}
//	return this.coordinateList;
//}
