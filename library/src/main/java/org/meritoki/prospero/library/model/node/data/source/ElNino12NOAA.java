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
public class ElNino12NOAA extends NOAA {

	public ElNino12NOAA() {
		super();
		this.downloadURL = "https://psl.noaa.gov/gcos_wgsp/Timeseries/Data/nino12.long.data";
		this.setRelativePath("NOAA"+seperator+"ElNino");
		this.setDownloadPath("NOAA"+seperator+"ElNino");
		this.setFileName("nino12.long.data");
		Region oneTwo = new Region(-10,-90,0,-80);
		this.regionList = new ArrayList<>();
		this.regionList.add(oneTwo);
	}
}