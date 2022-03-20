package org.meritoki.prospero.library.model.unit;

import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.query.Query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meritoki.library.controller.json.JsonController;

public class Script {

	@JsonProperty
	public List<Query> queryList = new ArrayList<>();
	
	@JsonIgnore
	public String getJson() {
		return JsonController.getJson(this);
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