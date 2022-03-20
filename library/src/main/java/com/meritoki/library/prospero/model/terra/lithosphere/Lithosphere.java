package com.meritoki.library.prospero.model.terra.lithosphere;

import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.meritoki.library.prospero.model.color.Chroma;
import com.meritoki.library.prospero.model.color.Scheme;
import com.meritoki.library.prospero.model.grid.Grid;
import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.query.Query;
import com.meritoki.library.prospero.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import com.meritoki.library.prospero.model.terra.hydrosphere.ocean.enso.ENSO;
import com.meritoki.library.prospero.model.terra.lithosphere.earthquake.Earthquake;
import com.meritoki.library.prospero.model.terra.lithosphere.magnetic.Magnetic;
import com.meritoki.library.prospero.model.terra.lithosphere.magnetic.anamoly.Anomaly;
import com.meritoki.library.prospero.model.terra.lithosphere.tectonic.Tectonic;
import com.meritoki.library.prospero.model.terra.lithosphere.volcano.Volcanic;
import com.meritoki.library.prospero.model.unit.Coordinate;
import com.meritoki.library.prospero.model.unit.Event;
import com.meritoki.library.prospero.model.unit.Index;
import com.meritoki.library.prospero.model.unit.Result;

public class Lithosphere extends Grid {
	
	static Logger logger = LogManager.getLogger(Lithosphere.class.getName());
	
	public Lithosphere() {
		super("Lithosphere");
//		this.addChild(new Magnetic());
//		this.addChild(new Earthquake());
		this.addChild(new Tectonic());
//		this.addChild(new Volcanic());
		this.sourceMap.put("GEBCO", "1aac29c0-e2f6-45e8-9921-c88397957795");
	}
	
	@Override
	public void load(Result result) {
		Object object = result.map.get("coordinateList");
		if(object != null) {
			this.coordinateList = (List<Coordinate>)object;
			if (this.coordinateList.size() == 0) {
				logger.warn("load(...) this.coordinateList.size() == 0");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paint(Graphics graphics) throws Exception {
		if (this.load) {
//			String sourceUUID = this.sourceMap.get(this.sourceKey);
//			this.coordinateList = (List<Coordinate>) this.data.get(sourceUUID, this.query);
			if (this.coordinateList != null) {
				this.initCoordinateMinMax("elevation", null);
				List<Coordinate> coordinateList = this.projection.getCoordinateList(0, this.coordinateList);
				if (coordinateList != null) {
					for (Coordinate c : coordinateList) {
						if (c != null) {
							if (c.attribute.get("elevation") != null) {
								graphics.setColor(this.chroma.getColor((double) c.attribute.get("elevation"),
										this.min, this.max));
							}
							graphics.fillOval((int) ((c.point.x) * this.projection.scale),
									(int) ((c.point.y) * this.projection.scale), (int) 5, (int) 5);
						}
					}
				}
			}
		}
		List<Variable> nodeList = this.getChildren();
		for(Variable n: nodeList) {
			n.paint(graphics);
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
