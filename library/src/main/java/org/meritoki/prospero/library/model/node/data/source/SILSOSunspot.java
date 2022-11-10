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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;

public class SILSOSunspot extends Source {
	
	static Logger logger = LogManager.getLogger(SILSOSunspot.class.getName());
//	public String fileName = basePath+"prospero-data/SILSO/SN_d_tot_V2.0.csv";
	public List<Index> indexList;
	
	public SILSOSunspot() {
		super();
		this.setRelativePath("SILSO");
		this.setFileName("SN_d_tot_V2.0.csv");
	}

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("indexList",this.read());
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);
	}


	public List<Index> read() {
		List<Index> indexList = new ArrayList<>();
		File file = new File(this.getFilePath());
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] commaArray = line.split(";");
				List<String> dataList = new ArrayList<>();
				for(String s:commaArray) {
					s = s.trim();
					if(!s.isEmpty()) {
						dataList.add(s);
					}
				}
				if(dataList.size() == 8) {
					int year = Integer.parseInt(dataList.get(0));
					int month = Integer.parseInt(dataList.get(1));
					int day = Integer.parseInt(dataList.get(2));
					int value = Integer.parseInt(dataList.get(4));
					Index index = new Index();
					index.value = value;
					index.startCalendar = new GregorianCalendar(year, month-1,day,0,0,0);
					index.endCalendar = new GregorianCalendar(year, month-1,day,24,0,0);
					indexList.add(index);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return indexList;
		
	}
}
//@Override
//public Object get() {
//	if (this.indexList == null) {
//		this.indexList = read();
//	}
//	return this.indexList;
//}
//int year = Integer.parseInt(dataList.remove(0));
//for(int i = 0; i<dataList.size();i++) {
//	Index index = new Index();
//	index.value = Double.parseDouble(dataList.get(i));
//	if(i == 0) {
//		int startYear = year - 1;
//		int endYear = year;
//		int startMonth = 11;
//		int endMonth = i;
//		Calendar startCalendar = new GregorianCalendar(startYear,startMonth,1);
//		Calendar endCalendar = new GregorianCalendar(endYear,endMonth,1);
//		endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
////		System.out.println(startCalendar.getTime());
////		System.out.println(endCalendar.getTime());
//		index.startCalendar = startCalendar;
//		index.endCalendar = endCalendar;
//	} else {
//		int startYear = year;
//		int endYear = year;
//		int startMonth = i-1;
//		int endMonth = i;
//		Calendar startCalendar = new GregorianCalendar(startYear,startMonth,1);
//		Calendar endCalendar = new GregorianCalendar(endYear,endMonth,1);
//		endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//		index.startCalendar = startCalendar;
//		index.endCalendar = endCalendar;
//	}
//	indexList.add(index);
//}
