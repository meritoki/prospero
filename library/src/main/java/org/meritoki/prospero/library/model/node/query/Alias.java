package org.meritoki.prospero.library.model.node.query;

import java.util.ArrayList;
import java.util.List;

public class Alias {
	
	public List<String> keys;
	public String value;
	
	public Alias(String value, List<String> keys) {
		this.keys = keys;
		this.value = value;
	}
	
	public Alias(String value, String[] keys) {
		this.keys = this.getStringList(keys);
		this.value = value;
	}
	
	public List<String> getStringList(String[] keys) {
		List<String> keyList = new ArrayList<>();
		for(String s: keys) {
			keyList.add(s);
		}
		return keyList;
	}
	
	public String getValue(String key) { 
		return keys.contains(key)?this.value:null;
	}

}
