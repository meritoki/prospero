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
package org.meritoki.prospero.library.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.provider.aws.s3.goes16.NOAA;
import org.meritoki.prospero.library.model.vendor.r.tsclust.TSClust;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meritoki.library.controller.model.SystemInterface;
import com.meritoki.library.controller.model.provider.Provider;
import com.meritoki.library.controller.model.vendor.Vendor;

public class System implements SystemInterface {
	static Logger logger = LoggerFactory.getLogger(System.class.getName());
	@JsonIgnore
	public String vendor;
	@JsonIgnore
	public String version;
	@JsonIgnore
	public Properties properties;
	@JsonIgnore
	Map<String, String> map = new HashMap<>();
	@JsonIgnore
	public String defaultFileName = "Untitled.json";
	@JsonIgnore
	public File file = null;
	@JsonIgnore
	public boolean newDocument = true;
	@JsonIgnore
	public String xmlFile = "prospero.xml";
	@JsonProperty
	public Map<String, Provider> providerMap = new HashMap<>();
	@JsonProperty
	public Map<String, Vendor> vendorMap = new HashMap<>();

	public System() {
		this.init();
	}

	public void init() {
		logger.trace("init()");
		this.initDirectories();
		this.properties = this.initProperties();
		this.initProviders();
		this.initVendors();
	}

	public void initDirectories() {
		if (!new File(NodeController.getSystemHome()).exists()) {
			new File(NodeController.getSystemHome()).mkdirs();
		}
		if (!new File(NodeController.getDocumentCache()).exists()) {
			new File(NodeController.getDocumentCache()).mkdirs();
		}
		if (!new File(NodeController.getResourceCache()).exists()) {
			new File(NodeController.getResourceCache()).mkdirs();
		}
	}

	public Properties initProperties() {
		Properties properties = null;
		File propertiesFile = new File(this.xmlFile);
		if (propertiesFile.exists()) {
			properties = NodeController.openPropertiesXML(propertiesFile);
		} else {
			properties = new Properties();
			properties.put("basePath", "");
			properties.put("calendar", "");
			properties.put("startCalendar", "");
			properties.put("endCalendar", "");
			properties.put("timeZone", "");
			properties.put("copernicusURL", "");
			properties.put("copernicusKey", "");
			properties.put("awsRegion","");
			properties.put("awsAccessKeyID","");
			properties.put("awsSecretAccessKey", "");
			NodeController.savePropertiesXML(properties, this.xmlFile, "Prospero");
		}
		return properties;
	}
	
	public Properties getProperties() {
		return this.properties;
	}

	public void saveProperties(Properties properties) {
		NodeController.savePropertiesXML(properties, this.xmlFile, "Prospero");
	}

	public void initProviders() {
		logger.info("initProviders()");
		this.providerMap.put("goes16",new NOAA());
	}
	
	public void initVendors() {
		logger.info("initVendors()");
//		this.vendorMap.put("r", new R());
		this.vendorMap.put("tsclust", new TSClust());
	}
}
