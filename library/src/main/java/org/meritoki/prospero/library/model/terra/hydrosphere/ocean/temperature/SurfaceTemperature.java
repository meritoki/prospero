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
package org.meritoki.prospero.library.model.terra.hydrosphere.ocean.temperature;

import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.Ocean;
import org.meritoki.prospero.library.model.unit.DataType;

public class SurfaceTemperature extends Ocean {
	
	public SurfaceTemperature() {
		super("SurfaceTemperature");
		this.sourceMap.put("ERA 5","e0538d57-044d-48c2-b3a7-b985eada81fc");
		this.sourceMap.put("ERA Interim","f4f4b169-fdc4-44db-b93a-85f8416aec2c");
		this.dataType = DataType.SST;
	}
}