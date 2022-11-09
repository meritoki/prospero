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
package org.meritoki.prospero.library.model.terra.gravitation.geopotential;

import org.meritoki.prospero.library.model.terra.gravitation.Gravitation;
import org.meritoki.prospero.library.model.unit.DataType;

public class Geopotential extends Gravitation {

	public Geopotential() {
		super("Geopotential");
		this.sourceMap.put("ERA 5","80607606-1671-4f9f-967b-db7f59e87b81");
		this.dataType = DataType.GEOPOTENTIAL;
	}
}
