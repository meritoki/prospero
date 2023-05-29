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
import java.util.Map;
import java.util.TreeMap;

import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.node.cartography.Cartography;
import org.meritoki.prospero.library.model.node.query.Query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meritoki.library.controller.json.JsonController;

public class Script {
	
	@JsonProperty
	public Map<String, String> map = new TreeMap<>();

	@JsonProperty
	public List<Query> loadList = new ArrayList<>();
	
	@JsonProperty
	public List<Query> queryList = new ArrayList<>();
	
	public Script() {
		
	}
	
	public Script(Script s) {
		this.queryList = new ArrayList<>(s.queryList);
	}
	
	@JsonIgnore
	public String getJson() {
		return JsonController.getJson(this);
	}
	
	@JsonIgnore
	public String getView() {
		String view = map.get("view");
		return view;
	}
	
	@JsonIgnore
	public String getProjection() {
		String projection = map.get("projection");
		return projection;
	}
	
	@JsonIgnore
	public Cartography getCartography() {
		String projection = this.getProjection();
		Cartography cartography = null;
		if(projection != null) {
			cartography = Cartography.valueOf(projection);
		}
		return cartography;
	}

}
//public static void main(String[] args) {
////Script s = new Script();
////Command c = new Command();
////c.variable = "Test";
////c.query = new Query();
////c.query.sourceUUID = "281cbf52-7014-4229-bffd-35c8ba41bcb5";
////c.query.map.put("test","test");
////s.commandList.add(c);
////System.out.print(JsonController.getJson(s));
//}