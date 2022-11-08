package org.meritoki.prospero.library.model.unit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.function.SlopeFunction;
import org.meritoki.prospero.library.model.function.Time;

public class Regression {

	static Logger logger = LogManager.getLogger(Regression.class.getName());
	public Map<String, Double> map;
	public Calendar startCalendar;
	public Calendar endCalendar;

	public List<Index> getIndexList() {
		SlopeFunction slopeFunction = new SlopeFunction(new Time(0, this.getDays(startCalendar, endCalendar), 1),
				(double) map.get("slope"), (double) map.get("intercept"));
		return slopeFunction.getIndexList(startCalendar);
	}

	public static Map<String, Double> getRegression(List<Point> pointList) {
		Map<String, Double> map = new HashMap<>();
		if (pointList.size() > 0) {
			double[][] data = new double[pointList.size()][2];
			for (int i = 0; i < pointList.size(); i++) {
				Point p = pointList.get(i);
				data[i][0] = p.x;
				data[i][1] = p.y;
			}
			SimpleRegression simpleRegression = new SimpleRegression(true);
			simpleRegression.addData(data);
			map.put("intercept", simpleRegression.getIntercept());
			map.put("interceptStdErr", simpleRegression.getInterceptStdErr());
			map.put("meanSquareError", simpleRegression.getMeanSquareError());
			map.put("r", simpleRegression.getR());
			map.put("regressionSumSquares", simpleRegression.getRegressionSumSquares());
			map.put("rSquare", simpleRegression.getRSquare());
			map.put("significance", simpleRegression.getSignificance());
			map.put("slope", simpleRegression.getSlope());
			map.put("slopeConfidenceInterval", simpleRegression.getSlopeConfidenceInterval());
			map.put("slopeStdErr", simpleRegression.getSlopeStdErr());
			map.put("sumOfCrossProducts", simpleRegression.getSumOfCrossProducts());
			map.put("sumSquaredErrors", simpleRegression.getSumSquaredErrors());
			map.put("totalSumSquares", simpleRegression.getTotalSumSquares());
			map.put("xSumSqaures", simpleRegression.getXSumSquares());
		}
		return map;
	}

	public double getDays(Calendar startCalendar, Calendar endCalendar) {
		Date d1 = startCalendar.getTime();
		Date d2 = endCalendar.getTime();
		long seconds = (d2.getTime() - d1.getTime()) / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		double days = hours / 24;
		return days;
	}

	public static Object[] getObjectArray(List<Regression> regressionList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (regressionList != null) {
			if (regressionList.size() > 0) {
//				Map map = regressionList.get(0).map;
//				columnArray = getColumnNames(map.size()+2).toArray();
//				dataMatrix = new Object[regressionList.size()+1][map.size()+2];
				for (int i = 0; i < regressionList.size(); i++) {
					Regression r = regressionList.get(i);
					if (i == 0) {
						columnArray = Table.getColumnNames(r.map.size() + 2).toArray();
						dataMatrix = new Object[regressionList.size() + 1][r.map.size() + 2];
						dataMatrix[i][0] = "startCalendar";
						dataMatrix[i][1] = "endCalendar";
						int index = 2;
						for (String key : r.map.keySet()) {
							dataMatrix[i][index] = key;
							index++;
						}
					}
					dataMatrix[i + 1][0] = dateFormat.format(r.startCalendar.getTime());
					dataMatrix[i + 1][1] = dateFormat.format(r.endCalendar.getTime());
					int index = 2;
					for (String key : r.map.keySet()) {
						dataMatrix[i + 1][index] = r.map.get(key);
						index++;
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}

	public static TableModel getTableModel(List<Regression> regressionList) {
		logger.debug("getTableModel(" + regressionList + ")");
		Object[] objectArray = getObjectArray(regressionList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	public static String getSeason(int month) {
		logger.info("getSeason(" + month + ")");
		if (0 <= month && month <= 1 || month == 11) {
			return "djf";
		} else if (2 <= month && month <= 4) {
			return "mam";
		} else if (5 <= month && month <= 7) {
			return "jja";
		} else if (8 <= month && month <= 10) {
			return "son";
		} else {
			return null;
		}
	}

	public static int getDecade(int year) {
		return (year / 10) * 10;
	}
}
//map.put("n", simpleRegression.getN());
//printDataMatrix(dataMatrix);
