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

import java.util.ArrayList;

import org.meritoki.prospero.library.model.unit.Region;

//El-Nino
//3 : 
//SST 5N-5S,150W-90W	
//1870/01 - 2021/09
//3.4 :
//SST 5N-5S,170W-120W	
//1870/01 - 2021/09
//4 : 
//SST 5N-5S, 160E-150W	
//1870/01 - 2021/09
//1+2 : 
//SST 0N-10S, 90W-80W	
//1870/01 - 2021/09
public class ENSONOAA extends NOAA {

	public ENSONOAA() {
		super();
		this.downloadURL = "https://psl.noaa.gov/enso/mei/data/meiv2.data";
		this.setRelativePath("NOAA"+seperator+"MEI");
		this.setDownloadPath("NOAA"+seperator+"MEI");
		this.setFileName("meiv2.data");
		Region three = new Region(-5,-150,5,-90);
		Region threeFour = new Region(-5,170,5,120);
		Region four = new Region(-5,160,5,-150);
		Region oneTwo = new Region(-10,-90,0,-80);
		this.regionList = new ArrayList<>();
		this.regionList.add(three);
		this.regionList.add(threeFour);
		this.regionList.add(four);
		this.regionList.add(oneTwo);

	}
}
//@Override
//public void query(Query query) throws Exception {
//	logger.info("query(" + query + ")");
//	Result result = new Result();
//	result.map.put("indexList",this.read());
//	result.mode = Mode.LOAD;
//	query.objectList.add(result);
//	result = new Result();
//	result.mode = Mode.COMPLETE;
//	query.objectList.add(result);
//}
//public String fileName = this.basePath+"prospero-data/NOAA/MEI/meiv2.data";
//public List<Index> indexList;

//public static void main(String[] args) {
//	ENSONOAA p = new ENSONOAA();
//	p.read();
//}
//@Override
//public Object get() {
//	if (this.indexList == null) {
//		this.indexList = read();
//	}
//	return this.indexList;
//}