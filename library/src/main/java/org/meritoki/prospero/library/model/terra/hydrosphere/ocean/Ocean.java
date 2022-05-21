package org.meritoki.prospero.library.model.terra.hydrosphere.ocean;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.grid.Grid;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.enso.ENSO;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.modulus.Modulus;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.pdo.PDO;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.tempurature.SeaSurfaceTemperature;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Tile;

public class Ocean extends Grid {

	static Logger logger = LogManager.getLogger(Ocean.class.getName());
	public int[][] countMatrix;
	public float[][] sumMatrix;
	public boolean[][] continentMatrix;
	public DataType dataType;
	public double scale;
	
	public Ocean() {
		super("Ocean");
		this.addChild(new SeaSurfaceTemperature());
		this.addChild(new Modulus());
		this.addChild(new ENSO());
		this.addChild(new PDO());
	}
	
	public Ocean(String name) {
		super(name);
	}
	
	@Override
	public void init() {
		super.init();
		this.dimension = 0.25;
//		this.resolution = 100;
//		this.scale = this.resolution / (this.dimension * this.resolution);
//		this.latitude = 180;
//		if (this.scale > 1) {
//			this.latitude *= this.scale;
//			this.longitude *= this.scale;
//		}
		this.latitude = 721;
		this.longitude = 1440;
		this.countMatrix = new int[latitude][longitude];
		this.sumMatrix = new float[latitude][longitude];
		this.continentMatrix = new boolean[latitude][longitude];
	}
	
	@Override
	public void load(Result result) {
		super.load(result);
		List<NetCDF> netCDFList = result.getNetCDFList();
		this.netCDFList.addAll(netCDFList);
		try {
			this.process(netCDFList);
		} catch (Exception e) {
			logger.error("load(" + (result != null) + ") exception=" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Reviewed 202112160852 Good
	 */
	@Override
	public void process() throws Exception {
		super.process();
		try {
			this.process(this.netCDFList);
			this.complete();
		} catch (Exception e) {
			logger.error("process() exception=" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void process(List<NetCDF> frameList) throws Exception {
		this.setMatrix(frameList);
		this.tileList = this.getTileList();
		this.initTileMinMax();
	}
	
	public void setMatrix(List<NetCDF> netCDFList) {
		System.out.println("setMatrix("+netCDFList.size()+")");
		for (NetCDF netCDF : netCDFList) {
			if (netCDF.type == this.dataType) {
				long timeSize = netCDF.timeArray.getSize();
				long latSize = netCDF.latArray.getSize();
				long lonSize = netCDF.lonArray.getSize();
				for (int t = 0; t < timeSize; t++) {
					for (int lat = 0; lat < latSize - 1; lat++) {
						float latitude = netCDF.latArray.get(lat);
						if (latitude <= 0) {
							for (int lon = 0; lon < lonSize; lon++) {
								float variable = netCDF.variableArray.get(t, lat, lon);
								if (variable == netCDF.continent && this.sumMatrix[lat][lon] == 0 && this.countMatrix[lat][lon] == 0) {
									this.continentMatrix[lat][lon] = true;
								} else {
									this.continentMatrix[lat][lon] = false;
									this.countMatrix[lat][lon]++;
									this.sumMatrix[lat][lon] += variable;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public List<Tile> getTileList() {
		List<Tile> tileList = new ArrayList<>();
		for (int i = 0; i < latitude; i++) {
			for (int j = 0; j < longitude; j++) {
				double latitude = 90 - (i * this.dimension);
				if (latitude <= 0) {
					if (!continentMatrix[i][j]) {
						float temperatureMean = (countMatrix[i][j] > 0) ? sumMatrix[i][j] / countMatrix[i][j] : 0;
						double value = temperatureMean;
						double lat = latitude;
						double lon;
						if ((j * this.dimension) < 180) {
							lon = (j * this.dimension);
						} else {
							lon = (j * this.dimension) - 360;
						}
						Tile tile = new Tile(lat, lon, this.dimension, value);
						if (regionList != null) {
							for (Region region : regionList) {
								if (region.contains(tile)) {
									tileList.add(tile);
									break;
								}
							}
						} else {
							tileList.add(tile);
						}
					}
				}
			}
		}
		return tileList;
	}
}

//public List<Frame> getCalendarFrameList(List<Frame> frameList) {
//List<Frame> fList = new ArrayList<>();
//for(Frame f: frameList) {
//	if(f.containsCalendar(this.calendar)) {
//		fList.add(f);
//	}
//}
//return fList;
//}
