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
package org.meritoki.prospero.library.model.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.meritoki.prospero.library.model.node.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Series {

	static Logger logger = LoggerFactory.getLogger(Series.class.getName());
	public List<Index> indexList = new ArrayList<>();
	public List<Time> timeList = new ArrayList<>();
	public Map<String, Object> map = new TreeMap<>();
	public Map<String, List<Regression>> regressionMap = new HashMap<>();

	public void addIndexList(List<Index> indexList) {
		for (Index i : indexList) {
			this.add(i);
		}
	}
	
	public String getTitle() {
		String title = "";
		title += (this.map.get("name")!=null)?this.map.get("name")+" ":"";
		title += (this.map.get("cluster")!=null)?"Cluster "+this.map.get("cluster")+" ":"";
		title += (this.map.get("group")!=null)?this.capitalize((String)this.map.get("group"))+" ":"";
		title += (this.map.get("sum")!=null && (boolean)this.map.get("sum"))?"Sum ":"";
		title += (this.map.get("average")!=null && (boolean)this.map.get("average"))?"Average ":"";
		title += (this.map.get("family")!=null)?"Family "+this.capitalize((String)this.map.get("family"))+" ":"";
		title += (this.map.get("class")!=null)?"Class "+this.capitalize((String)this.map.get("class"))+" ":"";
		title += (this.map.get("region")!=null)?"Region "+"("+((String)this.map.get("region")).replace(",", "_").replace(":", ")-(")+")":"";
		return title;
	}
	
	public String capitalize(String string) {
		if(string == null || string.length() == 0) {
			return string;
		}
		return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
	}
	
	public String getData() throws Exception {
		Query query = (Query)this.map.get("query");
		String data = query.getName();
		Integer cluster = (Integer)this.map.get("cluster");
		if(cluster != null) {
			data += "-cluster-"+cluster;
		}
		return data;
	}
	
	public Index getIndex(Calendar calendar) {
		Index index = null;
		for(Index i: this.indexList) {
			if(i.containsCalendar(calendar)) {
				index = i;
				break;
			}
		}
		return index;
	}


	/**
	 * Function addIndex Index index
	 * 
	 * @param index
	 */
	public void add(Index index) {
		if (index != null && !this.indexList.contains(index)) {
			this.indexList.add(index);
		} 
//		else {
//			int x = this.indexList.indexOf(index);// indexOf works with an equals method in index that uses the
//			if()
//			this.indexList.set(x, index);									// startCalendar
////			Index i = this.indexList.get(x);
////			
////			if (i.value != index.value) {
////				
////			}
//		}
	}


	@JsonIgnore
	public void setRegression(String value) throws Exception {
		String[] regressionArray = (value != null) ? value.split(",") : new String[0];
		for (String regression : regressionArray) {
			this.regressionMap.put(regression, this.getRegressionList(regression, this.indexList));
		}
	}

	/**
	 * 
	 * @param regression
	 * @param indexList
	 * @return
	 * @throws Exception
	 */
	@JsonIgnore
	public List<Regression> getRegressionList(String regression, List<Index> indexList) throws Exception {
		List<Regression> regressionList = new ArrayList<>();
		List<List<Index>> periodMatrix = this.getPeriodMatrix(regression, indexList);
		for (List<Index> period : periodMatrix) {
			if (period != null && period.size() > 0) {
				Calendar startCalendar = period.get(0).startCalendar;
				Calendar endCalendar = period.get(period.size() - 1).endCalendar;
				List<Point> pointList = new ArrayList<>();
				for (Index index : period) {
					Point point = index.getPoint(startCalendar);
					pointList.add(point);
				}
				Regression r = new Regression();
				r.map = Regression.getRegression(pointList);
				r.startCalendar = startCalendar;
				r.endCalendar = endCalendar;
				regressionList.add(r);
			}
		}
		return regressionList;
	}

	public List<List<Index>> getPeriodMatrix(String regression, List<Index> indexList) {
		List<List<Index>> periodMatrix = new ArrayList<>();
		if (regression != null && indexList != null && indexList.size() > 0) {
			switch (regression) {
			case "all": {
				List<Index> period = indexList;
				periodMatrix.add(period);
				break;
			}
			case "season": {
				String seasonIndex = null;
				List<Index> period = null;
				for (Index index : indexList) {
					Calendar calendar = index.startCalendar;
					int month = calendar.get(Calendar.MONTH);
					String season = Regression.getSeason(month);
					if (!season.equals(seasonIndex)) {
						if (period != null) {
							periodMatrix.add(period);
						}
						seasonIndex = season;
						period = new ArrayList<>();
					}
					if (period != null) {
						period.add(index);
					}
				}
				break;
			}
			case "year": {// doesn't work if year does not change
				int yearIndex = -1;
				List<Index> period = null;
				for (Index index : indexList) {
					Calendar calendar = index.startCalendar;
					int year = calendar.get(Calendar.YEAR);
					if (yearIndex != year) {
						if (period != null) {
							periodMatrix.add(period);
						}
						yearIndex = year;
						period = new ArrayList<>();
					}
					if (period != null) {
						period.add(index);
					}
				}
				break;
			}
			case "decade": {
				int decadeIndex = -1;
				List<Index> period = null;
				for (Index index : indexList) {
					Calendar calendar = index.startCalendar;
					int year = calendar.get(Calendar.YEAR);
					int decade = Regression.getDecade(year);
					if (decadeIndex != decade) {
						if (period != null) {
							periodMatrix.add(period);
						}
						decadeIndex = decade;
						period = new ArrayList<>();
					}
					if (period != null) {
						period.add(index);
					}
				}
				break;
			}
			case "vicennial": {
				int decadeIndex = -1;
				List<Index> period = null;
				for (Index index : indexList) {
					Calendar calendar = index.startCalendar;
					int year = calendar.get(Calendar.YEAR);
					int decade = Regression.getDecade(year);
					if (decadeIndex != decade) {
						if (period != null) {
							periodMatrix.add(period);
						}
						decadeIndex = decade;
						period = new ArrayList<>();
					}
					if (period != null) {
						period.add(index);
					}
				}
				List<List<Index>> tmpPeriodMatrix = new ArrayList<>();
				for (int i = 0; i < periodMatrix.size(); i++) {
					if ((i + 1) < periodMatrix.size()) {
						List<Index> p = new ArrayList<>();
						p.addAll(periodMatrix.get(i));
						p.addAll(periodMatrix.get(i + 1));
						tmpPeriodMatrix.add(p);
					}
				}
				periodMatrix = tmpPeriodMatrix;
				break;
			}
			case "quinquennial": {// 5-years
				// Pending
			}
			}
		}
		return periodMatrix;
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}
//double[][] data = new double[pointList.size()][2];
//for (int i = 0; i < pointList.size(); i++) {
//	Point p = pointList.get(i);
//	data[i][0] = p.x;
//	data[i][1] = p.y;
//}
//SimpleRegression simpleRegression = new SimpleRegression(true);
//simpleRegression.addData(data);
//Regression r = new Regression();
//Map<String, Double> map = new HashMap<>();
//map.put("intercept", simpleRegression.getIntercept());
//map.put("interceptStdErr", simpleRegression.getInterceptStdErr());
//map.put("meanSquareError", simpleRegression.getMeanSquareError());
////	map.put("n", simpleRegression.getN());
//map.put("r", simpleRegression.getR());
//map.put("regressionSumSquares", simpleRegression.getRegressionSumSquares());
//map.put("rSquare", simpleRegression.getRSquare());
//map.put("significance", simpleRegression.getSignificance());
//map.put("slope", simpleRegression.getSlope());
//map.put("slopeConfidenceInterval", simpleRegression.getSlopeConfidenceInterval());
//map.put("slopeStdErr", simpleRegression.getSlopeStdErr());
//map.put("sumOfCrossProducts", simpleRegression.getSumOfCrossProducts());
//map.put("sumSquaredErrors", simpleRegression.getSumSquaredErrors());
//map.put("totalSumSquares", simpleRegression.getTotalSumSquares());
//map.put("xSumSqaures", simpleRegression.getXSumSquares());
///**
//* Function addIndex Index index
//* 
//* @param index
//*/
//public boolean addIndex(Index index) {
//	if (!this.indexList.contains(index)) {
//		this.indexList.add(index);
//	} else {
//		int x = this.indexList.indexOf(index);// indexOf works with an equals method in index that uses the
//												// startCalendar
//		Index i = this.indexList.get(x);
//		if (i.value != index.value) {
//			i.value += index.value;
//			return true;
//		}
//	}
//	return false;
//}

//@JsonIgnore
//public List<Regression> getRegression(String value) throws Exception {
//	List<Regression> regressionList = new ArrayList<>();
//	String[] regressionArray = (value != null) ? value.split(",") : new String[0];
//	for (String regression : regressionArray) {
//		List<Regression> rList = this.getRegression(regression, this.indexList);
//		regressionList.addAll(rList);
//	}
//	return regressionList;
//}

//if (regression != null && indexList.size() > 0) {
//List<List<Index>> periodMatrix = new ArrayList<>();
//switch (regression) {
//case "all": {
//	List<Index> period = indexList;
//	periodMatrix.add(period);
//	break;
//}
//case "season": {
//	String seasonIndex = null;
//	List<Index> period = null;
//	for (Index index : indexList) {
//		Calendar calendar = index.startCalendar;
//		int month = calendar.get(Calendar.MONTH);
//		String season = Regression.getSeason(month);
//		if (!season.equals(seasonIndex)) {
//			if (period != null) {
//				periodMatrix.add(period);
//			}
//			seasonIndex = season;
//			period = new ArrayList<>();
//		}
//		if (period != null) {
//			period.add(index);
//		}
//	}
//	break;
//}
//case "year": {// doesn't work if year does not change
//	int yearIndex = -1;
//	List<Index> period = null;
//	for (Index index : indexList) {
//		Calendar calendar = index.startCalendar;
//		int year = calendar.get(Calendar.YEAR);
//		if (yearIndex != year) {
//			if (period != null) {
//				periodMatrix.add(period);
//			}
//			yearIndex = year;
//			period = new ArrayList<>();
//		}
//		if (period != null) {
//			period.add(index);
//		}
//	}
//	break;
//}
//case "decade": {
//	int decadeIndex = -1;
//	List<Index> period = null;
//	for (Index index : indexList) {
//		Calendar calendar = index.startCalendar;
//		int year = calendar.get(Calendar.YEAR);
//		int decade = Regression.getDecade(year);
//		if (decadeIndex != decade) {
//			if (period != null) {
//				periodMatrix.add(period);
//			}
//			decadeIndex = decade;
//			period = new ArrayList<>();
//		}
//		if (period != null) {
//			period.add(index);
//		}
//	}
//	break;
//}
//case "vicennial": {
//	int decadeIndex = -1;
//	List<Index> period = null;
//	for (Index index : indexList) {
//		Calendar calendar = index.startCalendar;
//		int year = calendar.get(Calendar.YEAR);
//		int decade = Regression.getDecade(year);
//		if (decadeIndex != decade) {
//			if (period != null) {
//				periodMatrix.add(period);
//			}
//			decadeIndex = decade;
//			period = new ArrayList<>();
//		}
//		if (period != null) {
//			period.add(index);
//		}
//	}
//	List<List<Index>> tmpPeriodMatrix = new ArrayList<>();
//	for (int i = 0; i < periodMatrix.size(); i++) {
//		if ((i + 1) < periodMatrix.size()) {
//			List<Index> p = new ArrayList<>();
//			p.addAll(periodMatrix.get(i));
//			p.addAll(periodMatrix.get(i + 1));
//			tmpPeriodMatrix.add(p);
//		}
//	}
//	periodMatrix = tmpPeriodMatrix;
//	break;
//}
//case "quinquennial": {// 5-years
//	// Pending
//}
//}
