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
package org.meritoki.prospero.library.model.terra.hydrosphere.ocean.modulus;


import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.Ocean;
import org.meritoki.prospero.library.model.unit.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Modulus extends Ocean {
	static Logger logger = LoggerFactory.getLogger(Modulus.class.getName());
	
	public Modulus() {
		super("Modulus");
		this.sourceMap.put("ERA 5","e0538d57-044d-48c2-b3a7-b985eada81fc");
		this.sourceMap.put("ERA Interim","f4f4b169-fdc4-44db-b93a-85f8416aec2c");
		this.dataType = DataType.MODULUS;
	}
}
//@Override
//public void load() {
//	if(this.load) { 
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
//
//public void init(Calendar calendar) throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.frameList = (List<Frame>) this.data.query(sourceUUID, this.query);
//	if(this.frameList != null) {
//		this.setFrameList(DataType.MODULUS,this.getCalendarFrameList(calendar,this.frameList), this.dimension);
//	}
//}
//
//@Override
//public void paint(Graphics graphics) throws Exception {
//	if(this.load) {
//		this.init(this.calendar);
//		super.paint(graphics);
//	}
//}
//@Override
//public void paint(Graphics graphics) throws Exception {
//	if(this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.frameList = (List<Frame>) this.data.get(sourceUUID, this.filter);
//		if(this.frameList != null) {
//			this.setFrameList(DataType.MODULUS,this.getCalendarFrameList(this.calendar,this.frameList), 2);
//			super.paint(graphics);
//		}
//	}
//}
