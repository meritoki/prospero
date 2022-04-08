package org.meritoki.prospero.library.model.terra.hydrosphere.ocean.pdo;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.grid.Grid;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.plot.TimePlot;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.enso.ENSO;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Tile;

public class PDO extends Grid {
	
	static Logger logger = LogManager.getLogger(PDO.class.getName());

	public PDO() {
		super("Pacific Decadal Oscillation");
		this.sourceMap.put("NOAA", "671b1a22-53e4-47b1-b148-5ba83420b0cd");
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
			plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
			plot.setTitle("NOAA Pacific Decadal Oscillation");
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
//@Override
//public List<Plot> getPlotList() throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.indexList = (List<Index>) this.data.query(sourceUUID, this.query);
//	List<Plot> plotList = new ArrayList<>();
//	if (indexList != null) {
//		List<List<Index>> blackPointMatrix = new ArrayList<>();
//		blackPointMatrix.add((this.indexList));
//		TimePlot plot = new TimePlot(this.startCalendar, this.endCalendar, blackPointMatrix, null);
//		plot.setTitle("NOAA Pacific Decadal Oscillation");
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
//
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
