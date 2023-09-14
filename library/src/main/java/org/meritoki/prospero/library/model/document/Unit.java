/*
 * Copyright 2021 Joaquin Osvaldo Rodriguez
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
package org.meritoki.prospero.library.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author osvaldo.rodriguez
 */
public class Unit {
	@JsonProperty
    public UnitType type = UnitType.DATA;
    @JsonProperty
    public String value;
    
    public Unit() {
    }
    
    public Unit(Unit unit) {
    	this.type = unit.type;
    	this.value = unit.value;
    }
}
