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
package org.meritoki.prospero.library.model.function;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.meritoki.prospero.library.model.unit.Index;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SlopeFunction extends Function {

	@JsonProperty
	public double slope;
	@JsonProperty
	public double intercept = 0;

	public SlopeFunction(Time time, double slope, double intercept) {
		super(time);
//		System.out.println("SlopeFuntion(time, "+slope+","+intercept+")");
		this.slope = slope;
		this.intercept = intercept;
	}

	public List<Index> getIndexList(Calendar startCalendar) {
		Calendar calendar;
		List<Index> indexList = null;
		if (startCalendar != null) {
			indexList = new ArrayList<>();
			for (Double t : time.getList()) {
				calendar = (Calendar) startCalendar.clone();
				Index index = new Index();
				calendar.add(Calendar.DATE, t.intValue());
				index.startCalendar = (Calendar)calendar.clone();
				index.value = this.slope * t + this.intercept;
				indexList.add(index);
			}
		}
		return indexList;
	}
}
