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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Station {
	public String name;
	public String country;
	public List<Coordinate> coordinateList = new ArrayList<>();
	public Coordinate defaultCoordinate;
	public boolean flag;
	
	public double getAverageDensity() {
		StandardDeviation standardDeviation = new StandardDeviation();
		Mean mean = new Mean();
		for (Coordinate tile : this.coordinateList) {
			double temp = (double)tile.attribute.get("density");
			if(temp != -8888.0 && temp != -7777) {
				standardDeviation.increment(temp);
				mean.increment(temp);
			}
		}
		return mean.getResult();
	}
	
	public Coordinate getDefaultCoordinate() {
		if(defaultCoordinate == null) {
			defaultCoordinate = this.coordinateList.get(0);
		}
		return defaultCoordinate;
	}
}
