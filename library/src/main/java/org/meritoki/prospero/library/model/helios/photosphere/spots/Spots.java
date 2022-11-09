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
package org.meritoki.prospero.library.model.helios.photosphere.spots;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.Grid;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.time.TimePlot;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.enso.ENSO;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Result;

public class Spots extends Grid {

	static Logger logger = LogManager.getLogger(Spots.class.getName());
	
	public Spots() {
		super("Spots");
		this.sourceMap.put("SILSO", "ecb98f29-fc40-4025-ab0e-24faeaa39d5e");
	}
	
	@Override
	public void load(Result result) {
		Object object = result.map.get("indexList");
		if(object != null) {
			this.indexList = (List<Index>)object;
			if (this.indexList.size() == 0) {
				logger.warn("load(...) this.indexList.size() == 0");
			}
			try {
				Plot plot = this.getPlot(this.indexList);
				if (plot != null) {
					this.plotMap.put("main", plot);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Plot getPlot(List<Index> indexList) throws Exception {
		Plot plot = null;
		if (indexList != null) {
			List<List<Index>> blackPointMatrix = new ArrayList<>();
			blackPointMatrix.add((this.indexList));
			plot = new TimePlot(this.startCalendar, this.endCalendar,
					blackPointMatrix, null);
			plot.setTitle("SILSO Sunspot Daily Counts");
			plot.setXLabel("Time");
			plot.setYLabel("Daily Count");
		}
		return plot;
	}

	@Override
	public List<Plot> getPlotList() throws Exception {
		return new ArrayList<Plot>(plotMap.values());
	}
//	public List<Point> getPointList(List<Index> indexList) {
//		List<Point> pointList = new ArrayList<>();
//		int i = 0;
//		for (Index index : indexList) {
//			Point point = new Point(i, index.value);
//			pointList.add(point);
//			i++;
//		}
//		return pointList;
//	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		if (this.load) {
//			this.init(this.calendar);
			super.paint(graphics);
		}
	}

}
//@Override
//public List<Plot> getPlotList() throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.indexList = (List<Index>) this.data.query(sourceUUID, this.query);
//	List<Plot> plotList = new ArrayList<>();
//	if (indexList != null) {
//		List<List<Index>> blackPointMatrix = new ArrayList<>();
//		blackPointMatrix.add((this.indexList));
//		TimePlot plot = new TimePlot(this.startCalendar, this.endCalendar,
//				blackPointMatrix, null);
//		plot.setTitle("SILSO Sunspot Daily Counts");
//		plot.setXLabel("Time");
//		plot.setYLabel("Daily Count");
//		plotList.add(plot);
//	}
//	return plotList;
//}
//@Override
//public void load() {
//	if (this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
//
//public void init(Calendar calendar) throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.indexList = (List<Index>) this.data.query(sourceUUID, this.query);
//	if (this.indexList != null) {
//		for (int i = 0; i < this.indexList.size(); i++) {
//			Index index = this.indexList.get(i);
//			index.flag = index.containsCalendar(calendar);
//		}
////		this.setIndexList(this.indexList);
//	}
//}
