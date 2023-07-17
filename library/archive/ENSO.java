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
package org.meritoki.prospero.library.model.terra.hydrosphere.ocean.enso;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.time.TimePlot;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.Ocean;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ENSO extends Ocean {

	static Logger logger = LoggerFactory.getLogger(ENSO.class.getName());
	
	public ENSO() {
		super("El Niño-Southern Oscillation");
		this.sourceMap.put("NOAA ENSO", "162baa09-9ad1-4556-9a9f-a967ee37e514");
	}

	@Override
	public void load(Result result) {
		List<Index> indexList = result.getIndexList();
		this.indexList.addAll(indexList);
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
	
	public Plot getPlot(List<Index> indexList) throws Exception {
		Plot plot = null;
		if (indexList != null) {
			List<List<Index>> blackPointMatrix = new ArrayList<>();
			blackPointMatrix.add((this.indexList));
			plot = new TimePlot(this.startCalendar, this.endCalendar,
					blackPointMatrix, null);
			plot.setTitle("ENSO MEI Index");
			plot.setXLabel("Time");
			plot.setYLabel("Temperature");
		}
		return plot;
	}

	@Override
	public List<Plot> getPlotList() throws Exception {
//		System.out.println("getPlotList()");
		return new ArrayList<Plot>(plotMap.values());
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		if (this.load) {
//			this.init(this.calendar);
			super.paint(graphics);
		}
	}

	public void setIndexList(List<Index> indexList) {
		double sum = 0;
		double count = 0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (Index i : indexList) {
			if (Math.abs(i.value) != 999.0) {
				if (i.flag) {
					count++;
					sum += i.value;
				}
				if (i.value > max) {
					max = i.value;
				}
				if (i.value < min) {
					min = i.value;
				}
			}
		}
		double average = sum / count;
		this.tileList = this.getTileList(average);
		this.min = min;
		this.max = max;
	}

	public List<Tile> getTileList(double value) {
		List<Tile> tileList = new ArrayList<>();
		Tile tile;
		// cycle through each tile
		for (int i = 0; i < 180; i += dimension) {
			for (int j = 0; j < 360; j += dimension) {
				int lat = (int) ((i - this.latitude));
				int lon;
				if (j < 180) {
					lon = j;
				} else {
					lon = j - 360;
				}
				tile = new Tile(lat, lon, dimension, value);
//				tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution, dimension, 0);
				tileList.add(tile);
			}
		}
		return tileList;
	}
}
//Object object = result.map.get("indexList");
//if(object != null) {
//	this.indexList = (List<Index>)object;
//	if (this.indexList.size() == 0) {
//		logger.warn("load(...) this.indexList.size() == 0");
//	}
//
//}
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
//		plot.setTitle("ENSO MEI Index");
//		plot.setXLabel("Time");
//		plot.setYLabel("Temperature");
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

//public void init(Calendar calendar) throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.indexList = (List<Index>) this.data.query(sourceUUID, this.query);
//	if (this.indexList != null) {
//		for (int i = 0; i < this.indexList.size(); i++) {
//			Index index = this.indexList.get(i);
//			index.flag = index.containsCalendar(calendar);
//		}
//		this.setIndexList(this.indexList);
//	}
//}
