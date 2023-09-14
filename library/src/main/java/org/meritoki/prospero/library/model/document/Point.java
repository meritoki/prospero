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

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Point {
	@JsonIgnore
    static Logger logger = LogManager.getLogger(Point.class.getName());
	@JsonProperty
	public double x = 0;
	@JsonProperty
	public double y = 0;
	
	public Point() {
	}
	
	/** 
	 * Copy constructor
	 * @param p
	 */
	public Point(Point p) {
		if(p != null) {
			this.x = p.x;
			this.y = p.y;
		}
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@JsonIgnore
	public boolean equals(Point point) {
		boolean flag = false;
		if(this.x == point.x && this.y == point.y) {
			flag = true;
		}
		return flag;
	}
	
    @JsonIgnore
    @Override
    public String toString(){
        String string = "";
        ObjectWriter ow = new ObjectMapper().writer();//.withDefaultPrettyPrinter();
        try {
            string = ow.writeValueAsString(this);
        } catch (IOException e) {
            logger.error("IOException "+e.getMessage());
        }
        return string;
    }
}
