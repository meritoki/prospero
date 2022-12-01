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
package org.meritoki.prospero.library.model.terra.atmosphere.wind.jetstream;

import org.meritoki.prospero.library.model.terra.atmosphere.wind.Wind;

public class Jetstream extends Wind {
	
	public Jetstream() {
		super("Jetstream");
		this.sourceMap.put("ERA 5", "73428541-23ea-4c4a-bc82-4fb4db5afe05");
		this.sourceMap.put("ERA Interim", "f4d6ead6-949a-42a9-9327-a8e22790e0e7");
	}
}
//public List<Frame> frameList;
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
//		this.setFrameList(DataType.INTENSITY,this.getCalendarFrameList(calendar,this.frameList), this.dimension);
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
//			this.setFrameList(DataType.INTENSITY,this.getCalendarFrameList(this.calendar,this.frameList), 2);
//			super.paint(graphics);
//		}
//	}
//}
