package org.meritoki.prospero.library.model.terra.lithosphere.magnetic.field;

import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.terra.lithosphere.Lithosphere;
import org.meritoki.prospero.library.model.terra.lithosphere.magnetic.anamoly.Anomaly;
import org.meritoki.prospero.library.model.unit.Coordinate;

public class Field extends Lithosphere {

	static Logger logger = LogManager.getLogger(Anomaly.class.getName());
	
	public Field() {
		super("Field");
		this.sourceMap.put("NOAA WMM", "c983e688-0e94-405f-a513-7565c6e13b03");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paint(Graphics graphics) throws Exception {
		if (this.load) {

			if (this.coordinateList != null) {
				String variable = "intensity";
				this.initCoordinateMinMax(variable,null);
				List<Coordinate> coordinateList = this.getProjection().getCoordinateList(0, this.coordinateList);
				if (coordinateList != null) {
					for (Coordinate c : coordinateList) {
						if (c != null) {
							if (c.attribute.get(variable) != null) {
								graphics.setColor(this.chroma.getColor((double) c.attribute.get(variable),
										this.min, this.max));
							}
							graphics.fillOval((int) ((c.point.x) * this.getProjection().scale),
									(int) ((c.point.y) * this.getProjection().scale), (int) 6, (int) 6);
						}
					}
				}
			}
		}
	}
}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.coordinateList = (List<Coordinate>) this.data.query(sourceUUID, this.query);
//@Override
//public void initCoordinateMinMax(String variable, Double nullValue) {
//	double min = Double.MAX_VALUE;
//	double max = Double.MIN_VALUE;
//	for (Coordinate c : this.coordinateList) {
//		double elevation = (double) c.attribute.map.get(variable);
//		if(Math.abs(elevation) != nullValue && Math.abs(elevation) < 100) {
//			if (elevation > max) {
//				max = elevation;
//			}
//			if (elevation < min) {
//				min = elevation;
//			}
//		}
//	}
//	this.min = min;
//	this.max = max;
//}
