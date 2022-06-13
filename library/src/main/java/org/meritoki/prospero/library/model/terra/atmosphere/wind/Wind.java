package org.meritoki.prospero.library.model.terra.atmosphere.wind;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.unit.Data;
import org.meritoki.prospero.library.model.grid.Grid;
import org.meritoki.prospero.library.model.terra.atmosphere.wind.jetstream.Jetstream;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Frame;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Region;

public class Wind extends Grid {

	static Logger logger = LogManager.getLogger(Wind.class.getName());
	public int[][] counterMatrix = new int[(int) latitude][(int) longitude];
	public float[][] intensityMatrix = new float[(int) latitude][(int) longitude];

	public Wind() {
		super("Wind");
		this.addChild(new Jetstream());
	}
	
	public Wind(String name) {
		super(name);
	}
	
	@Override
	public void reset() {
		super.reset();
	}
	
	@Override
	public void init() {
		super.init();
		dimension = 1;
		counterMatrix = new int[(int) latitude][(int) longitude];
		intensityMatrix = new float[(int) latitude][(int) longitude];
	}
	
	@Override
	public void load(Result result) {
		super.load(result);
		List<Frame> frameList = result.getFrameList();
		try {
			this.process(frameList);
		} catch (Exception e) {
			logger.error("load(" + (result != null) + ") exception=" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void process(List<Frame> frameList) throws Exception {
		this.setMatrix(frameList);
		this.tileList = this.getTileList();
		this.initTileMinMax();
	}
	
	public void setMatrix(List<Frame> frameList) {
		System.out.println("setMatrix("+netCDFList.size()+")");
		if (frameList != null) {
			this.initDateList(frameList);
			int latitude = (int) (this.latitude);// * this.resolution);
			int longitude = (int) (this.longitude);// * this.resolution);
			List<Data> dataList;
			for (int i = 0; i < latitude; i += dimension) {
				for (int j = 0; j < longitude; j += dimension) {
					for (int a = i; a < (i + dimension); a++) {
						for (int b = j; b < (j + dimension); b++) {
							for (Frame f : frameList) {
								dataList = f.data.get(a + "," + b);
								for (Data d : dataList) {
									if (d.type == DataType.INTENSITY) {
										counterMatrix[a][b]++;
										intensityMatrix[a][b] += d.value;
									}
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
		for (int i = 0; i < latitude; i += dimension) {
			for (int j = 0; j < longitude; j += dimension) {
				float temperatureMean = (counterMatrix[i][j] > 0) ? intensityMatrix[i][j] / counterMatrix[i][j] : 0;
				float value = temperatureMean;
				int lat = (int) ((i - this.latitude));
				int lon;
				if (j < 180) {
					lon = j;
				} else {
					lon = j - 360;
				}
				Tile tile = new Tile(lat, lon, dimension, value);
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
		
		return tileList;
	}
}
