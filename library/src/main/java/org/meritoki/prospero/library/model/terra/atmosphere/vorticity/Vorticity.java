package org.meritoki.prospero.library.model.terra.atmosphere.vorticity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.meritoki.prospero.library.model.terra.atmosphere.Atmosphere;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vorticity extends Atmosphere {
	static Logger logger = LoggerFactory.getLogger(Vorticity.class.getName());
	protected DataType dataType;

	public Vorticity() {
		super("Vorticity");
		this.tileFlag = true;
		this.dataType = DataType.VORTICITY;
		this.sourceMap.put("ERA 5", "e7e20f49-2387-40ce-917f-5b592c0b8b67");
		this.sourceMap.put("ERA INTERIM", "316bab36-ac3b-4930-87ae-5a32e4cdb81c");
	}

	public Vorticity(String name) {
		super(name);
		this.tileFlag = true;
	}

	@Override
	public void init() {
		super.init();
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

	public void process(List<NetCDF> netCDFList) throws Exception {
		this.setMatrix(netCDFList);
		this.tileList = this.getTileList();
		this.tileFlag = true;
		this.initTileMinMax();
		if (this.stackFlag) {
			List<Integer> levelList = this.query.getPressureList();
			for (Integer level : levelList) {
				this.coordinateMatrix = this.coordinateMatrixMap.get(level);
				this.dataMatrix = this.dataMatrixMap.get(level);
				List<Tile> tileList = this.getTileList(this.coordinateMatrix, this.dataMatrix);
				this.tileListMap.put(level, tileList);
			}
		} 
	}

	public void setMatrix(List<NetCDF> netCDFList) {
		logger.info("setMatrix(" + netCDFList.size() + ")");
		List<Time> timeList = this.setCoordinateAndDataMatrix(this.coordinateMatrix, this.dataMatrix, netCDFList);
		for (Time t : timeList) {
			if (!this.timeList.contains(t)) {
				this.timeList.add(t);
			}
		}
		this.initMonthArray(this.timeList);
		this.initYearMap(this.timeList);
	}

	public List<Time> setCoordinateAndDataMatrix(int[][][] coordinateMatrix, float[][][] dataMatrix,
			List<NetCDF> netCDFList) {
		List<Time> timeList = new ArrayList<>();
		for (NetCDF netCDF : netCDFList) {
			if (netCDF.type == this.dataType) {
				long timeSize = netCDF.timeArray.getSize();
				long latSize = netCDF.latArray.getSize();
				long lonSize = netCDF.lonArray.getSize();
				if(this.stackFlag) {
					coordinateMatrix = this.coordinateMatrixMap.get(netCDF.pressure);
					dataMatrix = this.dataMatrixMap.get(netCDF.pressure);
					if(coordinateMatrix == null)
						coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
					if(dataMatrix == null)
						dataMatrix = new float[(int) (latitude * resolution)][(int) (longitude
							* resolution)][12];
				}
				for (int t = 0; t < timeSize; t++) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(Time.getNineteenHundredJanuaryFirstDate(netCDF.timeArray.get(t)));
					boolean flag = true;
					if (this.query.isDateTime()) {
						if (!this.calendar.equals(calendar)) {
							flag = false;
						}else {
							if (this.stackFlag) {
								coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude
										* resolution)][12];
								dataMatrix = new float[(int) (latitude * resolution)][(int) (longitude
										* resolution)][12];
							}
						}
					}
					if (flag) {
						for (int lat = 0; lat < latSize; lat++) {
							float latitude = netCDF.latArray.get(lat);
							for (int lon = 0; lon < lonSize; lon++) {
								float longitude = netCDF.lonArray.get(lon);
								int x = (int) ((latitude + this.latitude / 2) * this.resolution);
								int y = (int) ((longitude + this.longitude / 2) * this.resolution) % this.longitude;
								int z = calendar.get(Calendar.MONTH);
								dataMatrix[x][y][z] += netCDF.variableCube.get(t, lat, lon);
								coordinateMatrix[x][y][z]++;
								Time time = new Time(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, -1,
										-1, -1, -1);
								if (!timeList.contains(time)) {
									timeList.add(time);
								}
							}
						}
					}
				}
				if(this.stackFlag) {
					this.coordinateMatrixMap.put(netCDF.pressure, coordinateMatrix);
					this.dataMatrixMap.put(netCDF.pressure, dataMatrix);
				}
			}
		}
		return timeList;
	}
}
//this.dimension = 1;
