package org.meritoki.prospero.library.model.node.data.generator;

import org.meritoki.prospero.library.model.solar.planet.earth.Earth;

import ucar.ma2.ArrayFloat;

public class CMIGenerator extends CloudGenerator {
	
	public ArrayFloat.D1 latitudeArray;
	public ArrayFloat.D1 longitudeArray;
	public ArrayFloat.D2 dataArray;
	public Earth earth = new Earth();
	
//	public List<NetCDF> write(String fileName) {
//		logger.info("read("+fileName+")");
//		List<NetCDF> netCDFList = new ArrayList<>();
////		String fileName = this.getPath() + "goes13.2011.017.114519.BAND_04.nc";
//		NetcdfFile dataFile = null;
//		try {
//			dataFile = NetcdfFile.open(fileName, null);
//			Dimension xDimension = dataFile.findDimension("x");
//			Dimension yDimension = dataFile.findDimension("y");
//			Variable xVariable = dataFile.findVariable("x");
//			Variable yVariable = dataFile.findVariable("y");
//			Variable dataVar = dataFile.findVariable("CMI");
//			ArrayShort.D1 xArray = (ArrayShort.D1) xVariable.read();
//			ArrayShort.D1 yArray = (ArrayShort.D1) yVariable.read();
//			ArrayShort.D2 dataArray = (ArrayShort.D2) dataVar.read();
//			int xSize = (int)xVariable.getSize();
//			int ySize = (int)yVariable.getSize();
//			this.latitudeArray = new ArrayFloat.D1(xSize);
//			this.longitudeArray = new ArrayFloat.D1(ySize);
//			this.dataArray = new ArrayFloat.D2(xSize,ySize); 
//			
//			for(int x = 0; x < xSize; x++ ) {
//				short X = xArray.get(x);
//				for(int y = 0; y < ySize; y++) {
//					short Y = yArray.get(y);
//
//				}
//			}
//		
//			dataFile.close();
//			System.gc();
//			netCDFList.add(netCDF);
//		} catch (java.io.IOException e) {
//			e.printStackTrace();
////			logger.error("IOException " + e.getMessage());
//
//		} finally {
//			if (dataFile != null) {
//				try {
//					dataFile.close();
//				} catch (IOException e) {
////					logger.error("IOException " + e.getMessage());
//				}
//			}
//		}
//		return netCDFList;
//	}

}
