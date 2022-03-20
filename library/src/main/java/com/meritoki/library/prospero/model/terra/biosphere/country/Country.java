package com.meritoki.library.prospero.model.terra.biosphere.country;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.unit.Coordinate;
import com.meritoki.library.prospero.model.unit.Result;
import org.locationtech.jts.geom.MultiPolygon;

public class Country extends Variable {

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
		if(object != null) {
			this.multiPolygonList = (List<MultiPolygon>)object;
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
				List<Coordinate> coordinateList = this.projection.getMultiPolygonList(0, this.multiPolygonList);
				for (Coordinate c : coordinateList) {
					graphics.drawLine((int) ((c.point.x) * this.projection.scale),
							(int) ((c.point.y) * this.projection.scale), (int) ((c.point.x) * this.projection.scale),
							(int) ((c.point.y) * this.projection.scale));
				}
			}
		}
	}
}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.multiPolygonList = (List<MultiPolygon>) this.data.query(sourceUUID, this.query);
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
