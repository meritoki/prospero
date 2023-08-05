package org.meritoki.prospero.library.model.provider;

import java.util.Map;
import java.util.TreeMap;

import org.meritoki.prospero.library.model.Model;

public class Provider {
	public String name;
	public Model model;
	public Map<String, Object> serviceMap = new TreeMap<>();

	public Provider(String name) {
		this.name = name;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public boolean isAvailable() throws Exception {
		return true;
	}

	public void save() {

	}

	public void init() {

	}
}