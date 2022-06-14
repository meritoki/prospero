package org.meritoki.prospero.library.model.terra.hydrosphere.ocean.ice;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.grid.Grid;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Station;

public class Ice extends Grid {

	static Logger logger = LogManager.getLogger(Ice.class.getName());
	
	public Ice() {
		super("Ice");
		this.sourceMap.put("Toth","8b2215c6-945b-4109-bfb4-6c764636e390");
	}
	
	@Override
	public void load(Result result) {
		Object object = result.map.get("stationList");
		if(object != null) {
			this.stationList = (List<Station>)object;
		}
	}
	
	public void process(List<Station> stationList) {
		
	}
	
	@Override
	public void paint(Graphics graphics) throws Exception {
		if(this.load) { 
			if (this.stationList != null) {
				List<Coordinate> coordinateList = new ArrayList<>();
				for (Station s : this.stationList) {
					Coordinate c = s.getDefaultCoordinate();
					coordinateList.add(c);
					c = this.projection.getCoordinate(0,c.latitude,c.longitude);
					graphics.setColor(this.chroma.getColor(s.getAverageDensity(), 0, 100));
					graphics.fillOval((int) ((c.point.x) * this.projection.scale),
							(int) ((c.point.y) * this.projection.scale), (int) 4, (int) 4);
				}
			}
		}
	}
}
