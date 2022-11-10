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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;

public class PDONOAA extends Source {

	public PDONOAA() {
		super();
		this.setRelativePath("NOAA"+seperator+"PDO"+seperator);
		this.setFileName("data.csv");
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
			br.readLine();
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] commaArray = line.split(",");
				Date date = new SimpleDateFormat("yyyyMM").parse(commaArray[0]);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				double value = Double.parseDouble(commaArray[1]);
				Index index = new Index();
				index.startCalendar = calendar;
				index.endCalendar = (Calendar)calendar.clone();
				index.endCalendar.set(Calendar.DAY_OF_MONTH, index.endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				index.value = value;
				indexList.add(index);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indexList;
	}
}
//public String fileName = basePath+"prospero-data/NOAA/PDO/data.csv";
//public List<Index> indexList;
//public static void main(String[] args) {
//PDONOAA p = new PDONOAA();
//p.read();
//}
//@Override
//public Object get() {
//	if (this.indexList == null) {
//		this.indexList = read();
//	}
//	return this.indexList;
//}

