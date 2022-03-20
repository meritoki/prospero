/**
 * https://github.com/fraxen/tectonicplates
 */
package org.meritoki.prospero.library.model.terra.lithosphere.tectonic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.MultiLineString;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Result;

public class Tectonic extends Variable {

	static Logger logger = LogManager.getLogger(Tectonic.class.getName());
	public Color color = Color.GRAY;
	private List<MultiLineString> multiLineStringList;

	public Tectonic() {
		super("Tectonic");
		this.sourceMap.put("Peter Bird", "8f6ef7b8-b8d1-452c-944a-c77d2e971db2");
	}
	
	@Override
	public void load(Result result) {
		Object object = result.map.get("multiLineStringList");
		if(object != null) {
			this.multiLineStringList = (List<MultiLineString>)object;
			if (this.multiLineStringList.size() == 0) {
				logger.warn("load(...) this.multiPolygonList.size() == 0");
			}
		}
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		if(this.load) { 
			if (this.multiLineStringList != null) {
				graphics.setColor(this.color);
				List<Coordinate> coordinateList = projection.getMultiLineStringList(0, multiLineStringList);
				for (Coordinate c : coordinateList) {
					graphics.fillOval((int) ((c.point.x) * this.projection.scale),
							(int) ((c.point.y) * this.projection.scale), (int) 2, (int) 2);
				}
			}
		}
	}
}
//String sourceUUID = this.sourceMap.get(this.sourceKey);
//this.multiLineStringList = (List<MultiLineString>) this.data.query(sourceUUID, this.query);
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
