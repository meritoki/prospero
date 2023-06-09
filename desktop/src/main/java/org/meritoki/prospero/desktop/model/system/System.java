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
package org.meritoki.prospero.desktop.model.system;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.meritoki.prospero.desktop.controller.node.NodeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class System {
	static Logger logger = LoggerFactory.getLogger(System.class.getName());
	public String product;
	public String vendor;
	public String version;
	public String author;
	public String contributor;
	
	Map<String,String> map = new HashMap<>();
	
	@JsonIgnore
	public String defaultFileName = "Untitled.json";
	
	@JsonIgnore
	public File file = null;
	
	@JsonIgnore
	public boolean newDocument = true;
	
	public System() {
		this.init();
	}
	
	public void init() {
		logger.info("init()");
		this.initDirectories();
	}
	
	public void initDirectories() {
		if(!new File(NodeController.getSystemHome()).exists()) {
			new File(NodeController.getSystemHome()).mkdirs();
		}
		if(!new File(NodeController.getDocumentCache()).exists()) {
			new File(NodeController.getDocumentCache()).mkdirs();
		}
		if(!new File(NodeController.getResourceCache()).exists()) {
			new File(NodeController.getResourceCache()).mkdirs();
		}
	}
}
