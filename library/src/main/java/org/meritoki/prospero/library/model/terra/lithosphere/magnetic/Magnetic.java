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
package org.meritoki.prospero.library.model.terra.lithosphere.magnetic;

import org.meritoki.prospero.library.model.terra.lithosphere.Lithosphere;
import org.meritoki.prospero.library.model.terra.lithosphere.magnetic.anamoly.Anomaly;
import org.meritoki.prospero.library.model.terra.lithosphere.magnetic.field.Field;

public class Magnetic extends Lithosphere {
	
	public Magnetic() {
		super("Magnetic");
		this.addChild(new Anomaly());
		this.addChild(new Field());
	}

}
