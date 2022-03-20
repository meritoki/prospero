package com.meritoki.library.prospero.model.data.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.meritoki.library.controller.memory.MemoryController;
import com.meritoki.library.prospero.model.unit.Data;
import com.meritoki.library.prospero.model.unit.DataType;
import com.meritoki.library.prospero.model.unit.Frame;
import com.meritoki.library.prospero.model.unit.Time;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayShort;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class OceanERAInterim extends OceanSource {
	
	static Logger logger = LogManager.getLogger(OceanERAInterim.class.getName());
	public static String path = basePath+"prospero-data" + seperator + "ECMWF" + seperator + "File" + seperator
			+ "Data" + seperator + "ERA" + seperator +"Interim"+seperator +"SST";
	public static String prefix = "34-128_";
	public static String suffix = "_F128";
	public static String extension = "nc";
	public int startYear = 2001;
	public int endYear = 2017;
	public int resolution = 1;
	public boolean test = false;
	public ArrayFloat.D3 sstArray;
	
	public OceanERAInterim() {}
	
	public List<Frame> read(int year, int month) {
		logger.info("read(" + year + "," + month + ")");
		String start = year + String.format("%02d", month) + String.format("%02d", 1);
		String stop = year + String.format("%02d", month) + String.format("%02d", Time.getYearMonthDays(year, month));
		String fileName = path + seperator + prefix + start + "-" + stop + suffix + "." + extension;
		List<Frame> frameList = new ArrayList<>();
		NetcdfFile dataFile = null;
		try {
			dataFile = NetcdfFile.open(fileName, null);
			Variable latitudeVar = dataFile.findVariable("latitude");
			Variable longitudeVar = dataFile.findVariable("longitude");
			Variable timeVar = dataFile.findVariable("time");
			Variable sstVar = dataFile.findVariable("sst");
			Attribute scaleFactorAttribute = sstVar.findAttribute("scale_factor");
			Attribute addOffsetAttribute = sstVar.findAttribute("add_offset");
			double scaleFactor = (Double) scaleFactorAttribute.getNumericValue();
			double addOffset = (Double) addOffsetAttribute.getNumericValue();
			int longitudeCount = (int) longitudeVar.getSize();
			int latitudeCount = (int) latitudeVar.getSize();
			int timeCount = (int) timeVar.getSize();
			ArrayFloat.D1 latArray = (ArrayFloat.D1) latitudeVar.read();
			ArrayFloat.D1 lonArray = (ArrayFloat.D1) longitudeVar.read();
			ArrayInt.D1 timeArray = (ArrayInt.D1) timeVar.read();
			ArrayShort.D3 sstArray = (ArrayShort.D3) sstVar.read();
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
			if (test) {
				timeCount = 1;
			}
			Frame frame;
			for (int time = 0; time < timeCount; time++) {
				frame = new Frame();
				frame.calendar = Calendar.getInstance();
				long milliseconds = Time.getNineteenHundredJanuaryFirstDate(timeArray.get(time)).getTime();
				frame.calendar.setTimeInMillis(milliseconds);
				for (int lat = 0; lat < latitudeCount; lat++) {
					float latitude = latArray.get(lat);
					float latitudeA = 0;
					float latitudeB = 0;
					latitudeCentralFlag = false;
					latitudeForwardFlag = false;
					latitudeBackwardFlag = false;
					if ((lat) == 0) {
						latitudeForwardFlag = true;
						latitudeA = latArray.get(lat);
						latitudeB = latArray.get(lat + 1);
					} else if ((lat + 1) == latitudeCount) {
						latitudeBackwardFlag = true;
						latitudeA = latArray.get(lat - 1);
						latitudeB = latArray.get(lat);
					} else if ((lat - 1) >= 0 && (lat + 1) < latitudeCount) {
						latitudeCentralFlag = true;
						latitudeA = latArray.get(lat - 1);
						latitudeB = latArray.get(lat + 1);
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
							float longitude = lonArray.get(lon);
							float longitudeA = 0;
							float longitudeB = 0;
							longitudeCentralFlag = false;
							longitudeForwardFlag = false;
							longitudeBackwardFlag = false;
							if (lon == 0) {
								longitudeForwardFlag = true;
								longitudeA = lonArray.get(lon);
								longitudeB = lonArray.get(lon + 1);
							} else if (lon + 1 == longitudeCount) {
								longitudeBackwardFlag = true;
								longitudeA = lonArray.get(lon - 1);
								longitudeB = lonArray.get(lon);
							} else if ((lon - 1) >= 0 && (lon + 1) < longitudeCount) {
								longitudeCentralFlag = true;
								longitudeA = lonArray.get(lon - 1);
								longitudeB = lonArray.get(lon + 1);
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
							double modulus = 0;
							int latitudeIndex = (int) (latitude);
							int longitudeIndex = (int) (longitude);
							double dPhi = -1;
							double dTheta = -1;
							if (sst != continent) {
								dPhiFlag = false;
								if (latitudeForwardFlag) {
									sstPhiA = this.sstArray.get(time, lat, lon);
									sstPhiB = this.sstArray.get(time, lat + 1, lon);
									if (sstPhiA > continent && sstPhiB > continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("forward", sstPhiA, sstPhiB, phiA, phiB);
									}
								} else if (latitudeBackwardFlag) {
									sstPhiA = this.sstArray.get(time, lat - 1, lon);
									sstPhiB = this.sstArray.get(time, lat, lon);
									if (sstPhiA > continent && sstPhiB > continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("backward", sstPhiA, sstPhiB, phiA, phiB);
									}
								} else if (latitudeCentralFlag) {
									sstPhiA = this.sstArray.get(time, lat - 1, lon);
									sstPhiB = this.sstArray.get(time, lat + 1, lon);
									if (sstPhiA > continent && sstPhiB > continent) {
										dPhiFlag = true;
										dPhi = this.getDerivative("central", sstPhiA, sstPhiB, phiA, phiB);
									}
								}
								dThetaFlag = false;
								if (longitudeForwardFlag) {
									sstThetaA = this.sstArray.get(time, lat, lon);
									sstThetaB = this.sstArray.get(time, lat, lon + 1);
									if (sstThetaA > continent && sstThetaB > continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("forward", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								} else if (longitudeBackwardFlag) {
									sstThetaA = this.sstArray.get(time, lat, lon - 1);
									sstThetaB = this.sstArray.get(time, lat, lon);
									if (sstThetaA > continent && sstThetaB > continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("backward", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								} else if (longitudeCentralFlag) {
									sstThetaA = this.sstArray.get(time, lat, lon - 1);
									sstThetaB = this.sstArray.get(time, lat, lon + 1);
									if (sstThetaA > continent && sstThetaB > continent) {
										dThetaFlag = true;
										dTheta = this.getDerivative("central", sstThetaA, sstThetaB, thetaA, thetaB);
									}
								}

								if (dPhiFlag && dThetaFlag) {
									modulus = this.getModulus(this.earthRadius, phi, theta, 0, dPhi, dTheta);
								} else {
									modulus = 0;
								}

								frame.flag = true;
								if (frame.data.get(latitudeIndex + "," + longitudeIndex) == null) {
									frame.data.put(latitudeIndex + "," + longitudeIndex, new ArrayList<>());
								}
								frame.data.get(latitudeIndex + "," + longitudeIndex)
										.add(new Data(DataType.MODULUS, (float) modulus));
								frame.data.get(latitudeIndex + "," + longitudeIndex)
										.add(new Data(DataType.SST, sst));
							} else {
								if (frame.data.get(latitudeIndex + "," + longitudeIndex) == null) {
									frame.data.put(latitudeIndex + "," + longitudeIndex, new ArrayList<>());
								}
								frame.data.get(latitudeIndex + "," + longitudeIndex)
										.add(new Data(DataType.CONTINENT, 0));
							}
						}
					}
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
//public Object get(Calendar calendar) {
//	return this.frameMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}

//@Override
//public Object get(Calendar calendar) {
//	return this.frameMapGet(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1);
//}
//public void setFrameList(Interval i, List<OceanPlot> oceanList) {
//DataType dataType = null;
//List<Frame> frameList = new ArrayList<>();
//int startYear = (i.startYear == -1) ? this.startYear : i.startYear;
//int startMonth = (i.startMonth == -1) ? 1 : i.startMonth;
//int endYear = (i.endYear == -1) ? this.endYear : i.endYear;
//int endMonth = (i.endMonth == -1) ? 12 : i.endMonth;
//if (i.startYear == -1 && i.endYear == -1) {
//	if (startMonth <= endMonth) {
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList,  dataType);
//				}
//			}
//		}
//	} else {
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= 12; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList, dataType);
//				}
//			}
//			for (int m = 1; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList,dataType);
//				}
//			}
//		}
//	}
//} else {
//	int yearDifference = endYear - startYear - 1;
//	if (yearDifference == -1) {// same year
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						//	public void setFrameList(Interval i, List<OceanPlot> oceanList) {
//DataType dataType = null;
//List<Frame> frameList = new ArrayList<>();
//int startYear = (i.startYear == -1) ? this.startYear : i.startYear;
//int startMonth = (i.startMonth == -1) ? 1 : i.startMonth;
//int endYear = (i.endYear == -1) ? this.endYear : i.endYear;
//int endMonth = (i.endMonth == -1) ? 12 : i.endMonth;
//if (i.startYear == -1 && i.endYear == -1) {
//	if (startMonth <= endMonth) {
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList,  dataType);
//				}
//			}
//		}
//	} else {
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= 12; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList, dataType);
//				}
//			}
//			for (int m = 1; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList,dataType);
//				}
//			}
//		}
//	}
//} else {
//	int yearDifference = endYear - startYear - 1;
//	if (yearDifference == -1) {// same year
//		for (int y = startYear; y <= endYear; y++) {
//			for (int m = startMonth; m <= endMonth; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList, dataType);
//				}
//			}
//		}
//	} else if (yearDifference > -1) {
//		for (int m = startMonth; m <= 12; m++) {
//			frameList = this.read(startYear, m);
//			for(OceanPlot o: oceanList) {
//				if(o instanceof SeaSurfaceTemperature) {
//					dataType = DataType.SST;
//				} else if(o instanceof Modulus) {
//					dataType = DataType.MODULUS;
//				}
//				//o.addFrameList(frameList, dataType);
//			}
//		}
//		for (int y = startYear + 1; y <= endYear - 1; y++) {// skipped is years are consecutive
//			for (int m = 1; m <= 12; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList, dataType);
//				}
//			}
//		}
//		for (int m = 1; m <= endMonth; m++) {
//			frameList = this.read(endYear, m);
//			for(OceanPlot o: oceanList) {
//				if(o instanceof SeaSurfaceTemperature) {
//					dataType = DataType.SST;
//				} else if(o instanceof Modulus) {
//					dataType = DataType.MODULUS;
//				}
//				//o.addFrameList(frameList, dataType);
//			}
//		}
//	}
//}
//for(OceanPlot o: oceanList) {
//	o.finalize(this.regionList, this.month, this.year);
//}
//
//}dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList, dataType);
//				}
//			}
//		}
//	} else if (yearDifference > -1) {
//		for (int m = startMonth; m <= 12; m++) {
//			frameList = this.read(startYear, m);
//			for(OceanPlot o: oceanList) {
//				if(o instanceof SeaSurfaceTemperature) {
//					dataType = DataType.SST;
//				} else if(o instanceof Modulus) {
//					dataType = DataType.MODULUS;
//				}
//				//o.addFrameList(frameList, dataType);
//			}
//		}
//		for (int y = startYear + 1; y <= endYear - 1; y++) {// skipped is years are consecutive
//			for (int m = 1; m <= 12; m++) {
//				frameList = this.read(y, m);
//				for(OceanPlot o: oceanList) {
//					if(o instanceof SeaSurfaceTemperature) {
//						dataType = DataType.SST;
//					} else if(o instanceof Modulus) {
//						dataType = DataType.MODULUS;
//					}
//					//o.addFrameList(frameList, dataType);
//				}
//			}
//		}
//		for (int m = 1; m <= endMonth; m++) {
//			frameList = this.read(endYear, m);
//			for(OceanPlot o: oceanList) {
//				if(o instanceof SeaSurfaceTemperature) {
//					dataType = DataType.SST;
//				} else if(o instanceof Modulus) {
//					dataType = DataType.MODULUS;
//				}
//				//o.addFrameList(frameList, dataType);
//			}
//		}
//	}
//}
//for(OceanPlot o: oceanList) {
//	o.finalize(this.regionList, this.month, this.year);
//}
//
//}
