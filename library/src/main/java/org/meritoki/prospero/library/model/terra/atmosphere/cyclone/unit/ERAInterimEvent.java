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
package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit;

import java.util.List;

import org.meritoki.prospero.library.model.unit.Coordinate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ERAInterimEvent extends CycloneEvent {
	
	public static Integer[] pressureArray = { 100, 125, 150, 200, 250, 300, 400, 500, 600, 700, 850, 925 };

	public ERAInterimEvent() {
		super();
	}

	public ERAInterimEvent(CycloneEvent event) {
		super(event);
	}
	
	public ERAInterimEvent(List<Coordinate> coordinateList) {
		super(coordinateList);
	}

	public ERAInterimEvent(String id, List<Coordinate> pointList) {
		super(id, pointList);
	}
	
	@JsonIgnore
	@Override
	public void classify() {
		int maxTimeLevelCount = this.getMaxTimeLevelCount();
		int lowerMostLevel = this.getLowerMostLevel();
		if (maxTimeLevelCount == 2 || maxTimeLevelCount == 3) {
			this.family = Family.SHALLOW;
			if (lowerMostLevel >= 700) {
				this.classification = Classification.LOW;
			} else if (lowerMostLevel >= 400) {
				this.classification = Classification.MID;
			} else if (lowerMostLevel >= 125) {
				this.classification = Classification.UPPER;
			}
		} else if (maxTimeLevelCount == 4 || maxTimeLevelCount == 5) {
			this.family = Family.INTERMEDIATE;
			if (lowerMostLevel >= 500) {
				this.classification = Classification.LOW_MID;
			} else if (lowerMostLevel >= 125) {
				this.classification = Classification.MID_UPPER;
			}
		} else if (maxTimeLevelCount >= 6) {
			this.family = Family.DEEP;
		}
	}
}
