package org.meritoki.prospero.library.model.data.source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Data;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Frame;
import org.meritoki.prospero.library.model.unit.Time;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class WindJetstreamERA5 extends WindSource {

	static Logger logger = LogManager.getLogger(WindJetstreamERA5.class.getName());
	public static char seperator = File.separatorChar;
	public static String path = basePath+"prospero-data" + seperator + "ECMWF" + seperator + "File" + seperator
			+ "Data" + seperator + "ERA" + seperator +"5"+seperator +"UV";
	public static String prefix = "u_component_of_wind-v_component_of_wind_";
	public static String suffix = "_200-250-300_F128";
	public static String extension = "nc";
	public int startYear = 2001;
	public int endYear = 2017;
	
	public WindJetstreamERA5() {
		this.calendarFlag = true;
	}
	

	
	public List<Frame> read(int year, int month) {
		logger.info("read(" + year + "," + month + ")");
		String start = year + String.format("%02d", month) + String.format("%02d", 1);
		String stop = year + String.format("%02d", month) + String.format("%02d", Time.getYearMonthDays(year, month));
		String fileName = path + seperator + prefix + start + "-" + stop + suffix + "." + extension;
		List<Frame> frameList = new ArrayList<>();
		NetcdfFile dataFile = null;
		try {
			dataFile = NetcdfFile.open(fileName, null);
			Variable latVar = dataFile.findVariable("latitude");
			Variable lonVar = dataFile.findVariable("longitude");
			Variable level = dataFile.findVariable("level");
			Variable time = dataFile.findVariable("time");
			Variable uVar = dataFile.findVariable("u");
			Variable vVar = dataFile.findVariable("v");
			Attribute uScaleFactorAttribute = uVar.findAttribute("scale_factor");
			Attribute uAddOffsetAttribute = uVar.findAttribute("add_offset");
			Attribute vScaleFactorAttribute = vVar.findAttribute("scale_factor");
			Attribute vAddOffsetAttribute = vVar.findAttribute("add_offset");
			double uScaleFactor = (Double) uScaleFactorAttribute.getNumericValue();
			double uAddOffset = (Double) uAddOffsetAttribute.getNumericValue();
			double vScaleFactor = (Double) vScaleFactorAttribute.getNumericValue();
			double vAddOffset = (Double) vAddOffsetAttribute.getNumericValue();
			int longitudeCount = (int) lonVar.getSize();
			int latitudeCount = (int) latVar.getSize();
			int levelCount = (int) level.getSize();
			int timeCount = (int) time.getSize();
			ArrayFloat.D1 latArray = (ArrayFloat.D1) latVar.read();
			ArrayFloat.D1 lonArray = (ArrayFloat.D1) lonVar.read();
			ArrayInt.D1 timeArray = (ArrayInt.D1) time.read();
			ArrayShort.D4 uArray = (ArrayShort.D4) uVar.read();
			ArrayShort.D4 vArray = (ArrayShort.D4) vVar.read();
			double intensity = 0;
			float intensitySum;
			float intensityAverage;
			float u = 0;
			float v = 0;
			float latitude;
			float longitude;
			Coordinate p = null;
			Frame frame;
			for (int t = 0; t < timeCount; t++) {
				frame = new Frame();
				frame.calendar = Calendar.getInstance();
				long milliseconds = Time.getNineteenHundredJanuaryFirstDate(timeArray.get(t)).getTime();
				frame.calendar.setTimeInMillis(milliseconds);
				for (int j = 0; j < latitudeCount; j++) {
					latitude = latArray.get(j);
//					if (latitude < 0) {
						latitude += 90;
						for (int i = 0; i < longitudeCount; i++) {
							longitude = lonArray.get(i);
							intensitySum = 0;
							for (int l = 0; l < levelCount; l++) {
								u = uArray.get(t, l, j, i);
								v = vArray.get(t, l, j, i);
								u *= uScaleFactor;
								u += uAddOffset;
								v *= vScaleFactor;
								v += vAddOffset;
								intensity = Math.sqrt((Math.pow(u, 2) + Math.pow(v, 2)));
								intensitySum += intensity;
							}
							intensityAverage = intensitySum / levelCount;
							frame.flag = true;
							short latitudeIndex = (short) latitude;
							short longitudeIndex = (short) longitude;
							if (frame.data.get(latitudeIndex+","+longitudeIndex) == null) {
								frame.data.put(latitudeIndex+","+longitudeIndex, new ArrayList<>());
							}
							frame.data.get(latitudeIndex+","+longitudeIndex).add(new Data(DataType.INTENSITY, intensityAverage));
						}
//					}
				}
				frameList.add(frame);
			}
			dataFile.close();
			System.gc();
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
		MemoryController.log();
		return frameList;
	}
}
//@Override
//public List<Frame> frameMapGet(int y, int m) {
//	if (this.frameMap == null)
//		this.frameMap = new HashMap<>();
//	List<Frame> eList = this.frameMap.get(y + "" + m);
//	if (eList == null) {
//		eList = (List<Frame>)this.read(y, m);
//		if (eList != null) {
//			this.frameMap.put(y + "" + m, eList);
//		} else {
//			eList = new ArrayList<>();
//		}
//	}
//	eList = new ArrayList<>(eList);
//	return eList;
//}
//@Override 
//public Object get(Calendar calendar) {
//	return this.frameMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}

//@Override
//public Object get(Calendar calendar) {
//	return this.frameMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}
