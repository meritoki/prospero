package org.meritoki.prospero.library.model.vendor;

import org.meritoki.prospero.library.model.Model;

public class Vendor {

	public String name;
	public Model model;

	public Vendor(String name) {
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
