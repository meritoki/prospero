package org.meritoki.prospero.library.model.data.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.unit.Frame;

import com.meritoki.library.controller.memory.MemoryController;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class ModulusZeroTwoFiveNetCDFGenerator extends OceanGenerator {

	static Logger logger = LogManager.getLogger(ModulusZeroTwoFiveNetCDFGenerator.class.getName());
//	public static String path = "." + seperator + "data" + seperator + "hydrosphere" + seperator + "ocean" + seperator
//			+ "sst";
	public static String basePath = "/home/jorodriguez/Prospero/";
	public static String path = basePath+"prospero-data" + seperator + "ECMWF" + seperator + "File" + seperator
			+ "Data" + seperator + "ERA" + seperator +"Interim"+seperator +"SST";
	public static String prefix = "34-128_";
	public static String suffix = "_0-25-0-25";
	public static String extension = "nc";
	public int startYear = 2001;
	public int endYear = 2017;
	public ArrayFloat.D3 sstArray;
	public ArrayFloat.D3 modulusArray;
	public ArrayFloat.D1 latitudeArray;
	public ArrayFloat.D1 longitudeArray;
	public ArrayInt.D1 timeArray;
	Variable latitudeVar;
	Variable longitudeVar;
	Variable timeVar;

	public static void main(String[] args) {
		ModulusZeroTwoFiveNetCDFGenerator modulusGenerator = new ModulusZeroTwoFiveNetCDFGenerator();
		modulusGenerator.write(2001, 01);
	}

	public List<Frame> write(int year, int month) {
		logger.info("write(" + year + "," + month + ")");
		MemoryController.log();
		List<Frame> frameList;
		String start = year + String.format("%02d", month) + String.format("%02d", 1);
		String stop = year + String.format("%02d", month) + String.format("%02d", this.getYearMonthDays(year, month));
		frameList = new ArrayList<>();
		String fileName = path + seperator + prefix + start + "-" + stop + suffix + "." + extension;
		NetcdfFile dataFile = null;
		try {
			dataFile = NetcdfFile.open(fileName, null);
			this.latitudeVar = dataFile.findVariable("latitude");
			this.longitudeVar = dataFile.findVariable("longitude");
			this.timeVar = dataFile.findVariable("time");
			Variable sstVar = dataFile.findVariable("sst");
			Attribute scaleFactorAttribute = sstVar.findAttribute("scale_factor");
			Attribute addOffsetAttribute = sstVar.findAttribute("add_offset");
			double scaleFactor = (Double) scaleFactorAttribute.getNumericValue();
			double addOffset = (Double) addOffsetAttribute.getNumericValue();
			int longitudeCount = (int) longitudeVar.getSize();
			int latitudeCount = (int) latitudeVar.getSize();
			int timeCount = (int) timeVar.getSize();
			this.latitudeArray = (ArrayFloat.D1) latitudeVar.read();
			this.longitudeArray = (ArrayFloat.D1) longitudeVar.read();
			this.timeArray = (ArrayInt.D1) timeVar.read();
			ArrayShort.D3 sstArray = (ArrayShort.D3) sstVar.read();
			this.modulusArray = new ArrayFloat.D3(timeCount, latitudeCount, longitudeCount);
			this.sstArray = this.getSSTArray(sstArray, timeCount, latitudeCount, longitudeCount, scaleFactor, addOffset);
			float continent = this.getContinent(scaleFactor, addOffset);
			boolean latitudeCentralFlag = false;
			boolean latitudeBackwardFlag = false;
			boolean latitudeForwardFlag = false;
			boolean longitudeCentralFlag = false;
			boolean longitudeBackwardFlag = false;
			boolean longitudeForwardFlag = false;
			boolean dPhiFlag = false;
			boolean dThetaFlag = false;
			for (int time = 0; time < timeCount; time++) {
				for (int lat = 0; lat < latitudeCount; lat++) {
					float latitude = latitudeArray.get(lat);
					float latitudeA = 0;
					float latitudeB = 0;
					latitudeCentralFlag = false;
					latitudeForwardFlag = false;
					latitudeBackwardFlag = false;
					if ((lat) == 0) {
						latitudeForwardFlag = true;
						latitudeA = latitudeArray.get(lat);
						latitudeB = latitudeArray.get(lat + 1);
					} else if ((lat + 1) == latitudeCount) {
						latitudeBackwardFlag = true;
						latitudeA = latitudeArray.get(lat - 1);
						latitudeB = latitudeArray.get(lat);
					} else if ((lat - 1) >= 0 && (lat + 1) < latitudeCount) {
						latitudeCentralFlag = true;
						latitudeA = latitudeArray.get(lat - 1);
						latitudeB = latitudeArray.get(lat + 1);
					}
					if (latitude < 0) {
						float phi = 90 - latitude;
						float phiA = 90 - latitudeA;
						float phiB = 90 - latitudeB;
						phi = (float) Math.toRadians(phi);
						phiA = (float) Math.toRadians(phiA);
						phiB = (float) Math.toRadians(phiB);
						latitude += 90;
						for (int lon = 0; lon < longitudeCount; lon++) {
							float longitude = longitudeArray.get(lon);
							float longitudeA = 0;
							float longitudeB = 0;
							longitudeCentralFlag = false;
							longitudeForwardFlag = false;
							longitudeBackwardFlag = false;
							if (lon == 0) {
								longitudeForwardFlag = true;
								longitudeA = longitudeArray.get(lon);
								longitudeB = longitudeArray.get(lon + 1);
							} else if (lon + 1 == longitudeCount) {
								longitudeBackwardFlag = true;
								longitudeA = longitudeArray.get(lon - 1);
								longitudeB = longitudeArray.get(lon);
							} else if ((lon - 1) >= 0 && (lon + 1) < longitudeCount) {
								longitudeCentralFlag = true;
								longitudeA = longitudeArray.get(lon - 1);
								longitudeB = longitudeArray.get(lon + 1);
							}
							double theta = longitude;
							double thetaA = longitudeA;
							double thetaB = longitudeB;
							theta = Math.toRadians(theta);
							thetaA = Math.toRadians(thetaA);
							thetaB = Math.toRadians(thetaB);
							float sst = this.sstArray.get(time, lat, lon);
							float sstPhiA;
							float sstPhiB;
							float sstThetaA;
							float sstThetaB;
							float modulus = 0;
							double dPhi = -1;
							double dTheta = -1;
							if (sst != continent) {
								dPhiFlag = false;
								if (latitudeForwardFlag) {
									sstPhiA = this.sstArray.get(time, lat, lon);
									sstPhiB = this.sstArray.get(time, lat + 1, lon);
									if (sstPhiA != continent && sstPhiB != continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("forward", sstPhiA, sstPhiB, phiA, phiB);
									}
								} else if (latitudeBackwardFlag) {
									sstPhiA = this.sstArray.get(time, lat - 1, lon);
									sstPhiB = this.sstArray.get(time, lat, lon);
									if (sstPhiA != continent && sstPhiB != continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("backward", sstPhiA, sstPhiB, phiA, phiB);
									}
								} else if (latitudeCentralFlag) {
									sstPhiA = this.sstArray.get(time, lat - 1, lon);
									sstPhiB = this.sstArray.get(time, lat + 1, lon);
									if (sstPhiA != continent && sstPhiB != continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("central", sstPhiA, sstPhiB, phiA, phiB);
									}
								}
								dThetaFlag = false;
								if (longitudeForwardFlag) {
									sstThetaA = this.sstArray.get(time, lat, lon);
									sstThetaB = this.sstArray.get(time, lat, lon + 1);
									if (sstThetaA != continent && sstThetaB != continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("forward", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								} else if (longitudeBackwardFlag) {
									sstThetaA = this.sstArray.get(time, lat, lon - 1);
									sstThetaB = this.sstArray.get(time, lat, lon);
									if (sstThetaA != continent && sstThetaB != continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("backward", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								} else if (longitudeCentralFlag) {
									sstThetaA = this.sstArray.get(time, lat, lon - 1);
									sstThetaB = this.sstArray.get(time, lat, lon + 1);
									if (sstThetaA != continent && sstThetaB != continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("central", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								}
								if (dPhiFlag && dThetaFlag) {
									modulus = (float)this.getModulus(this.earthRadius, phi, theta, 0, dPhi, dTheta);
								} else {
									modulus = 0;
								}
								this.modulusArray.set(time, lat, lon, modulus);
							} else {
								this.modulusArray.set(time, lat, lon, 0);
							}
						}
					}
				}
			}

			NetcdfFileWriter netCDFFile = null;
			fileName = path + seperator + "modulus"+"_" + start + "-" + stop + suffix + "." + extension;
			try {
				netCDFFile = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf4, fileName);
				Dimension latitudeDimension = netCDFFile.addDimension(null, "latitude", (int) latitudeArray.getSize());
				Dimension longitudeDimension = netCDFFile.addDimension(null, "longitude", (int) longitudeArray.getSize());
				Dimension timeDimension = netCDFFile.addUnlimitedDimension("time");
				Dimension speedDimension = netCDFFile.addDimension(null, "modulus", (int) modulusArray.getSize());
				Variable timeVar = netCDFFile.addVariable(null, "time", DataType.INT, "time");
				timeVar.addAttribute(new Attribute("standard_name", "time"));
				timeVar.addAttribute(new Attribute("calendar", "proleptic_gregorian"));
				timeVar.addAttribute(new Attribute("axis", "T"));
				Variable longitudeVar = netCDFFile.addVariable(null, "longitude", DataType.FLOAT, "longitude");
				Variable latitudeVar = netCDFFile.addVariable(null, "latitude", DataType.FLOAT, "latitude");
				longitudeVar.addAttribute(new Attribute("standard_name", "longitude"));
				longitudeVar.addAttribute(new Attribute("long_name", "longitude"));
				longitudeVar.addAttribute(new Attribute("units", "degrees_east"));
				longitudeVar.addAttribute(new Attribute("axis", "X"));
				latitudeVar.addAttribute(new Attribute("standard_name", "latitude"));
				latitudeVar.addAttribute(new Attribute("long_name", "latitude"));
				latitudeVar.addAttribute(new Attribute("units", "degrees_north"));
				latitudeVar.addAttribute(new Attribute("axis", "Y"));
				Variable modulusVariable = netCDFFile.addVariable(null, "modulus", DataType.FLOAT,
						"time latitude longitude");
				netCDFFile.create();
				netCDFFile.write(timeVar, timeArray);
				netCDFFile.write(latitudeVar, latitudeArray);
				netCDFFile.write(longitudeVar, longitudeArray);
				netCDFFile.write(modulusVariable, modulusArray);
				netCDFFile.close();
			} catch (IOException | InvalidRangeException e) {
				e.printStackTrace();

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
		logger.info("write(" + year + "," + month + ") complete");
		MemoryController.log();
		return frameList;
	}

	public void write() {

	}
}
