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
package org.meritoki.prospero.library.model.terra.atmosphere.cloud.goes;

import java.util.Calendar;
import java.util.List;

import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.node.color.Scheme;
import org.meritoki.prospero.library.model.terra.atmosphere.cloud.Cloud;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class N extends Cloud {
	
	public N() {
		super("N");
		this.sourceMap.put("GOES","aefbd8d1-d423-458d-90c0-7c8429f2a653");
		this.unit = "K";
		this.scheme = Scheme.GRAYSCALE;
	}
	
	@JsonIgnore
	@Override
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		this.query();
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.setCalendar(calendar);
		}
	}
}
