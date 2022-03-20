package org.meritoki.prospero.library.model.terra.biosphere.city;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.terra.biosphere.country.Country;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Result;

public class City extends Variable {

	static Logger logger = LogManager.getLogger(City.class.getName());
	public Color color = Color.BLACK;
	public List<Point> pointList;
	
	public City() {
		super("City");
		this.sourceMap.put("Natural Earth", "9bc2dd83-85c9-48fe-818f-f62db97c594a");
	}
	
	@Override
	public void load(Result result) {
		Object object = result.map.get("pointList");
		if(object != null) {
			this.pointList = (List<Point>)object;
			if (this.pointList.size() == 0) {
				logger.warn("load(...) this.pointList.size() == 0");
			}
		}
	}



	@Override
	public void paint(Graphics graphics) throws Exception {
		if(this.load) { 
			if(this.pointList != null) {
				graphics.setColor(Color.black);
				List<Coordinate> coordinateList = this.projection.getPointList(0, this.pointList);
				if (coordinateList != null) {
					for (Coordinate c : coordinateList) {
						graphics.drawLine((int) ((c.point.x) * this.projection.scale),
								(int) ((c.point.y) * this.projection.scale), (int) ((c.point.x) * this.projection.scale),
								(int) ((c.point.y) * this.projection.scale));
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
//this.pointList = (List<Point>) this.data.query(sourceUUID, this.query);
