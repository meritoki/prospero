package org.meritoki.prospero.desktop.model;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.desktop.controller.node.NodeController;
import org.meritoki.prospero.desktop.model.resource.Resource;
import org.meritoki.prospero.desktop.model.system.System;
import org.meritoki.prospero.library.model.document.Document;

public class Model extends org.meritoki.prospero.library.model.Model {

	private static final Logger logger = LogManager.getLogger(Model.class.getName());
	
	public System system = new System();
	
	public Resource resource = new Resource();
	
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
