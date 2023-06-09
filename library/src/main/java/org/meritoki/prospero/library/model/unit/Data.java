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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Data {
	@JsonIgnore
	static Logger logger = LoggerFactory.getLogger(Data.class.getName());
//	@JsonProperty
//	public long milliseconds;
//	@JsonProperty
//	public DataType type;
//	@JsonProperty
//	public float value;
	@JsonProperty
	public Map<DataType,Float> map = new HashMap<>();
	
	public Data() {
		
	}
	
//	public Data(DataType type, float value) {
////		this.milliseconds = milliseconds;
//		this.type=type;
//		this.value = value;
//	}
	
//	public Data(DataType type, float value, long milliseconds) {
//		this.milliseconds = milliseconds;
//		this.type=type;
//		this.value = value;
//	}
	
	@JsonIgnore
    @Override
    public String toString(){
        String string = "";
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            string = ow.writeValueAsString(this);
        } catch (IOException ex) {
        	logger.error(ex.getMessage());
        }
        return string;
    }
}
