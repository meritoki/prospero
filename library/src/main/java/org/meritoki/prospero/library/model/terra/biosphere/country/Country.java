package org.meritoki.prospero.library.model.terra.biosphere.country;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.MultiPolygon;
import org.meritoki.prospero.library.model.terra.biosphere.Biosphere;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Polygon;
import org.meritoki.prospero.library.model.unit.Result;

public class Country extends Biosphere {

	static Logger logger = LogManager.getLogger(Country.class.getName());
	public Color color = Color.BLACK;
	private List<MultiPolygon> multiPolygonList;

	public Country() {
		super("Country");
		this.sourceMap.put("Natural Earth", "d6eb88d6-100c-4948-8fd1-5300b724ec2d");
	}

	@Override
	public void load(Result result) {
		Object object = result.map.get("multiPolygonList");
		if (object != null) {
			this.multiPolygonList = (List<MultiPolygon>) object;
			if (this.multiPolygonList.size() == 0) {
				logger.warn("load(...) this.multiPolygonList.size() == 0");
			}
		}
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		if (this.load) {
			if (this.multiPolygonList != null) {
				graphics.setColor(this.color);
				List<Polygon> polygonList = this.projection.getPolygonList(0, this.multiPolygonList);
				for (Polygon p : polygonList) {
					if (p.coordinateList.size() > 0) {
						for (int i = 0; i < p.coordinateList.size(); i++) {
							Coordinate c = p.coordinateList.get(i);
							graphics.drawLine((int) ((c.point.x) * this.projection.scale),
									(int) ((c.point.y) * this.projection.scale),
									(int) ((c.point.x) * this.projection.scale),
									(int) ((c.point.y) * this.projection.scale));
						}
					}
				}
			}
		}
	}
}
//for (Coordinate c : coordinateList) {
//graphics.drawLine((int) ((c.point.x) * this.projection.scale),
//		(int) ((c.point.y) * this.projection.scale), (int) ((c.point.x) * this.projection.scale),
//		(int) ((c.point.y) * this.projection.scale));
//}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.multiPolygonList = (List<MultiPolygon>) this.data.query(sourceUUID, this.query);
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
