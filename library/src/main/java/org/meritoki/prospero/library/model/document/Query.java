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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Query {
	static Logger logger = LogManager.getLogger(Query.class.getName());
	@JsonProperty
	public Map<String,String> map = new TreeMap<>();
	
	public String getIndex() {
		String index = this.map.get("index");
		return index;
	}
	
	public boolean getTime() {
		String flag = this.map.get("time");
		if(flag != null) {
			return Boolean.valueOf(flag);
		}
		return false;
	}
	
	public boolean getSpace() {
		String flag = this.map.get("space");
		if(flag != null) {
			return Boolean.valueOf(flag);
		}
		return false;
	}
	
	public boolean getEnergy() {
		String flag = this.map.get("energy");
		if(flag != null) {
			return Boolean.valueOf(flag);
		}
		return false;
	}
	
	public boolean getLanguage() {
		String flag = this.map.get("language");
		if(flag != null) {
			return Boolean.valueOf(flag);
		}
		return false;
	}
	
	public boolean getRandom() {
		String flag = this.map.get("random");
		if(flag != null) {
			return Boolean.valueOf(flag);
		}
		return false;
	}
	
	public List<Integer> getIndexList() throws Exception {
		return this.getIndexList(this.getIndex());
	}
	
	public List<Integer> getIndexList(String value) throws Exception {
		List<Integer> pageList = new ArrayList<>();
		if(value != null) {
		value = value.toLowerCase();
		value.trim();
		if (value.contains("all")) {
			if (value.equals("all")) {
				pageList.add(-1);
			} else {
				throw new Exception("Invalid page(s)");
			}
		} else {
			String[] commaArray = value.split(",");
			for (String c : commaArray) {
				c.trim();
				if (c.contains("-")) {
					String[] dashArray = c.split("-");
					try {
						int a = Integer.parseInt(dashArray[0].trim());
						int b = Integer.parseInt(dashArray[1].trim());
						if (a < b) {
							for (int i = a; i <= b; i++) {
								pageList.add(i);
							}
						}
					} catch (Exception e) {
						throw new Exception("Not integer page(s)");
					}
				} else {
					try {
						int a = Integer.parseInt(c);
						pageList.add(a);
					} catch (Exception e) {
						throw new Exception("Not integer page(s)");
					}
				}
			}
		}
		}
		logger.info("getIndexList(" + value + ") pageList=" + pageList);
		return pageList;
	}
}
