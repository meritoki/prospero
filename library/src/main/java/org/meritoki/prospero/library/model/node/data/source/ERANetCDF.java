package org.meritoki.prospero.library.model.node.data.source;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;
import org.utn.app.command.era.Five;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class ERANetCDF extends Source {

	public String variable;
	public DataType dataType;
//	public ArrayFloat.D3 variableCube;
	public String prefix;
	public String suffix;
	public String extension = "nc";
	public Time startTime = new Time(2017, 6, 1, 0, -1, -1);
	public Time endTime = new Time(2019, 9, 30, 0, -1, -1);
	public List<Integer> pressureList = new ArrayList<>();
	public boolean cache = false;
	protected Map<String, List<NetCDF>> netCDFMap = new HashMap<>();


	public ERANetCDF() {
		super();

	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		if (query.getBasePath() != null) {
			this.setBasePath(query.getBasePath());
		}
		if (query.getRelativePath() != null) {
			this.setRelativePath(query.getRelativePath());
		}
		this.intervalList = query.getIntervalList(this.getStartTime(), this.getEndTime());
		this.pressureList = query.getPressureList();
		if (this.intervalList != null) {
			for (Interval i : this.intervalList) {
				this.load(query, i);
			}
			if (query.getDownload()) {
				this.download(query);
			}
			query.objectListAdd(new Result(Mode.COMPLETE));
		}
	}

	public void load(Query query, Interval interval) throws Exception {
		List<Time> timeList = Time.getTimeList(1, interval);
		List<NetCDF> loadList;
		for (Time time : timeList) {
			if (!Thread.interrupted()) {
				loadList = this.read(time);
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("netCDFList", new ArrayList<NetCDF>((loadList)));
				query.objectList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
	}

	public List<NetCDF> read(Time time) throws Exception {
		List<NetCDF> netCDFList = new ArrayList<>();
		String start = time.year + String.format("%02d", time.month) + String.format("%02d", 1);
		String end = time.year + String.format("%02d", time.month)
				+ String.format("%02d", this.getYearMonthDays(time.year, time.month));
		String fileName = prefix + start + "-" + end + "_}*{" + suffix;
		if (this.pressureList.size() > 0) {
			for (int pressure : this.pressureList) {
				logger.info("read(" + time + ") pressure=" + pressure);
				this.setFileName(prefix + start + "-" + end + "_" + pressure + suffix + "." + extension);
				if (this.fileExists()) {
					netCDFList.addAll(this.read(this.getFilePath()));
				} else {
					this.form(time, pressure);
				}
			}
		} else {
			String pattern = "glob:{" + fileName + "}*.{" + extension + "}";
			logger.info("read(" + time + ") pattern=" + pattern);
			List<String> matchList = this.getWildCardFileList(Paths.get(this.getPath()), pattern);
			for (String m : matchList) {
				this.setFileName(m);
				netCDFList.addAll(this.read(this.getFilePath()));
			}
		}

		return netCDFList;
	}

	public void form(Time time) {
		logger.info("form(" + time + ")");
	}

	public void form(Time time, Integer pressure) {
		logger.info("form(" + time + ", " + pressure + ")");
	}

	public void download(Query query) {
		logger.info("download(" + (query != null) + ")");
	}

	public String getPressure(String fileName) {
		String[] fileNameArray = fileName.split("_");
		String pressure = fileNameArray[2];
		return pressure;
	}

	public List<NetCDF> read(String fileName) {
		logger.info("read(" + fileName + ")");
		List<NetCDF> netCDFList = this.netCDFMap.get(fileName);
		if (netCDFList == null) {
			logger.info("read(" + fileName + ") netCDFList == null"); 
			MemoryController.log();
			netCDFList = new ArrayList<>();
			if (MemoryController.bumper()) {
				NetcdfFile dataFile = null;
				try {
					dataFile = NetcdfFile.open(fileName, null);
					Integer pressure = Integer.parseInt(this.getPressure(fileName));
					Variable latitudeVar = dataFile.findVariable("latitude");
					Variable longitudeVar = dataFile.findVariable("longitude");
					Variable timeVar = dataFile.findVariable("time");
					Variable variableVar = dataFile.findVariable(this.variable);
					Attribute scaleFactorAttribute = variableVar.findAttribute("scale_factor");
					Attribute addOffsetAttribute = variableVar.findAttribute("add_offset");
					double scaleFactor = (Double) scaleFactorAttribute.getNumericValue();
					double addOffset = (Double) addOffsetAttribute.getNumericValue();
					int longitudeCount = (int) longitudeVar.getSize();
					int latitudeCount = (int) latitudeVar.getSize();
					int timeCount = (int) timeVar.getSize();
					ArrayFloat.D1 latArray = (ArrayFloat.D1) latitudeVar.read();
					ArrayFloat.D1 lonArray = (ArrayFloat.D1) longitudeVar.read();
					ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
					ArrayShort.D3 variableArray = (ArrayShort.D3) variableVar.read();
					NetCDF netCDF = new NetCDF();
					netCDF.type = this.dataType;
					netCDF.latArray = latArray;
					netCDF.lonArray = lonArray;
					netCDF.timeArray = timeArray;
					netCDF.variableCube = this.getVariableArray(variableArray, timeCount, latitudeCount, longitudeCount,
							scaleFactor, addOffset);
					netCDF.pressure = pressure;
					dataFile.close();
					System.gc();
					netCDFList.add(netCDF);
					if (this.cache)
						this.netCDFMap.put(fileName, netCDFList);
				} catch (java.io.IOException e) {
					logger.error("IOException " + e.getMessage());

				} finally {
					if (dataFile != null) {
						try {
							dataFile.close();
						} catch (IOException e) {
							logger.error("IOException " + e.getMessage());
						}
					}
				}
			}
		}
		return netCDFList;
	}

	public ArrayFloat.D3 getVariableArray(ArrayShort.D3 variableArray, int timeCount, int latitudeCount,
			int longitudeCount, double scaleFactor, double addOffset) {
		ArrayFloat.D3 newVariableArray = new ArrayFloat.D3(timeCount, latitudeCount, longitudeCount);
		for (int t = 0; t < timeCount; t++) {
			for (int lat = 0; lat < latitudeCount; lat++) {
				for (int lon = 0; lon < longitudeCount; lon++) {
					float variable = variableArray.get(t, lat, lon);
					variable *= scaleFactor;
					variable += addOffset;
					newVariableArray.set(t, lat, lon, variable);
				}
			}
		}
		return newVariableArray;
	}

}
