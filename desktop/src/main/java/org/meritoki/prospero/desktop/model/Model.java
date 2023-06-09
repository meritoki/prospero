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
package org.meritoki.prospero.desktop.model;

import java.io.File;
import java.util.Properties;

import org.meritoki.prospero.desktop.controller.node.NodeController;
import org.meritoki.prospero.desktop.model.resource.Resource;
import org.meritoki.prospero.desktop.model.system.System;
import org.meritoki.prospero.desktop.view.frame.MainFrame;
import org.meritoki.prospero.library.model.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Model extends org.meritoki.prospero.library.model.Model {

	static Logger logger = LoggerFactory.getLogger(Model.class.getName());
	public System system = new System();
	public Resource resource = new Resource();
	public MainFrame mainFrame;
	public String version;
//	public Properties properties = NodeController.openPropertiesXML(new File("prospero.xml"));
	
	public Model(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
		this.initProperties();
	}
	
	@Override
	public void init() {
//		logger.info(this+".init()");
		this.mainFrame.init();
	}
	
	public void initProperties() {
		Properties properties = null;
		File propertiesFile = new File("prospero.xml");
		if(propertiesFile.exists()) {
			properties = NodeController.openPropertiesXML(propertiesFile);
		} else {
			properties = new Properties();
			NodeController.savePropertiesXML(properties, "prospero.xml", "Prospero");
		}
		this.setProperties(properties);
	}
	
	public void newDocument() {
		logger.info("newDocument()");
		this.setDocument(new Document());
		this.system.newDocument = true;
		this.system.file = null;
	}
	
	public void openDocument(File file) {
		logger.info("openDocument("+file+")");
		this.system.file = file;
		this.setDocument(NodeController.openDocument(this.system.file));
		this.system.newDocument = false;
		this.resource.addRecent(this.system.file.getAbsolutePath());
		File directory = new File(NodeController.getDocumentCache(this.document.uuid));
		if(!directory.exists()) {
			directory.mkdirs();
		}
	}
	
	public void saveDocument(File file) {
		logger.info("saveDocument("+file+")");
		this.system.file = file;
		NodeController.saveDocument(this.system.file, this.document);
		this.resource.addRecent(this.system.file.getAbsolutePath());
		this.system.newDocument = false;
	}
	public void saveDocument() {
		logger.info("saveDocument()");
		NodeController.saveDocument(this.system.file, this.document);
		this.resource.addRecent(this.system.file.getAbsolutePath());
		this.system.newDocument = false;
	}
}
