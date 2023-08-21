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
package org.meritoki.prospero.library.model.node.data.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.utn.app.command.era.Five;
import org.utn.app.command.era.model.five.Batch;
import org.utn.app.command.era.model.five.Form;
import org.utn.app.command.era.model.five.Request;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class MeanSeaLevelPressureERA5 extends OceanERA {

	static Logger logger = LoggerFactory.getLogger(SeaSurfaceTemperatureERAInterim.class.getName());
	private Form form = new Form();

	public MeanSeaLevelPressureERA5() {
		super();
		this.variable = "msl";
		this.prefix = "mean_sea_level_pressure_";
		this.suffix = "_F128";
		this.startTime = new Time(2001, 1, 1, 0, -1, -1);
		this.endTime = new Time(2017, 12, 31, 24, -1, -1);
		this.setRelativePath("ECMWF" + seperator + "File" + seperator + "Data" + seperator + "ERA" + seperator + "5"
				+ seperator + "SeaLevelPressure" + seperator);
		this.form.grid.add("F128");
		this.form.time.add("00:00");
		this.form.time.add("06:00");
		this.form.time.add("12:00");
		this.form.time.add("18:00");
		this.form.path = "reanalysis-era5-single-levels";
		for (int i = 1; i <= 31; i++) {
			this.form.day.add(String.format("%02d", i));
		}
		this.form.variable.add("mean_sea_level_pressure");
	}

	public void form(Time time) {
		super.form(time);
		if (!this.form.year.contains(String.valueOf(time.year))) {
			this.form.year.add(String.valueOf(time.year));
		}
		String month = String.format("%02d", time.month);
		if (!this.form.month.contains(month)) {
			this.form.month.add(month);
		}
	}

	@Override
	public List<NetCDF> read(Time time) throws Exception {
		List<NetCDF> netCDFList = new ArrayList<>();
		String start = time.year + String.format("%02d", time.month) + String.format("%02d", 1);
		String stop = time.year + String.format("%02d", time.month)
				+ String.format("%02d", Time.getYearMonthDays(time.year, time.month));
		this.setFileName(prefix + start + "-" + stop + suffix + "." + extension);
		if (this.fileExists()) {
			netCDFList.addAll(this.read(this.getFilePath()));
		} else {
			this.form(time);
		}
		return netCDFList;
	}

	@Override
	public void download(Query query) {
		super.download(query);
		this.form.outputPath = this.getPath();
		Batch batch = new Batch(this.form);
		String batchPath = this.getPath() + batch.uuid + ".json";
//		Five five = new Five();
//		five.model.system.xmlFile = "prospero.xml";
//		five.model.system.initProperties();
//		five.model.initProvider();
		Object object = (this.toolMap != null) ? this.toolMap.get("five") : null;
		if (object instanceof Five) {
			Five five = (Five) object;
			five.executeBatch(batchPath, batch);
			for (Request r : batch.requestList) {
				if (r.status.equals("complete")) {
					this.setFileName(r.fileName + ".nc");
					Result result = new Result();
					result.map.put("netCDFList", new ArrayList<NetCDF>((this.read(this.getFilePath()))));
					query.objectList.add(result);
				}
			}
		}
	}

	public List<NetCDF> read(String fileName) {
		logger.info("read(" + fileName + ")");
		List<NetCDF> netCDFList = this.netCDFMap.get(fileName);
		if (netCDFList == null) {
			MemoryController.log();
			netCDFList = new ArrayList<>();
			NetcdfFile dataFile = null;
			try {
				dataFile = NetcdfFile.open(fileName, null);
				Variable latitudeVar = dataFile.findVariable("latitude");
				Variable longitudeVar = dataFile.findVariable("longitude");
				Variable timeVar = dataFile.findVariable("time");
				Variable mslVar = dataFile.findVariable("msl");
				Attribute scaleFactorAttribute = mslVar.findAttribute("scale_factor");
				Attribute addOffsetAttribute = mslVar.findAttribute("add_offset");
				double scaleFactor = (Double) scaleFactorAttribute.getNumericValue();
				double addOffset = (Double) addOffsetAttribute.getNumericValue();
				int longitudeCount = (int) longitudeVar.getSize();
				int latitudeCount = (int) latitudeVar.getSize();
				int timeCount = (int) timeVar.getSize();
				ArrayFloat.D1 latArray = (ArrayFloat.D1) latitudeVar.read();
				ArrayFloat.D1 lonArray = (ArrayFloat.D1) longitudeVar.read();
				ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
				ArrayShort.D3 mslArray = (ArrayShort.D3) mslVar.read();

				NetCDF netCDF = new NetCDF();
				netCDF.continent = this.getContinent(scaleFactor, addOffset);
				netCDF.type = DataType.MSL;
				netCDF.latArray = latArray;
				netCDF.lonArray = lonArray;
				netCDF.timeArray = timeArray;
				netCDF.variableCube = this.getMSLArray(mslArray, timeCount, latitudeCount, longitudeCount, scaleFactor,
						addOffset);
				dataFile.close();
				System.gc();
				netCDFList.add(netCDF);
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
		return netCDFList;
	}

}
