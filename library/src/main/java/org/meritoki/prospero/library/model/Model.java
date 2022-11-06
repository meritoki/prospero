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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.data.Data;
import org.meritoki.prospero.library.model.node.Orbital;
import org.meritoki.prospero.library.model.node.Spheroid;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.solar.Solar;
import org.meritoki.prospero.library.model.unit.Script;

import com.meritoki.library.controller.node.NodeController;
import com.meritoki.module.library.model.N;

public class Model extends Variable {

	static Logger logger = LogManager.getLogger(Model.class.getName());
	public static final double DEFAULT_SCALE = 1000;
	public List<System> systemList = new ArrayList<>();
	public Variable node = this;
	public boolean cache = false;
	public Solar solar = new Solar();
	public int defaultAzimuth = -10;
	public int defaultElevation = 150;

	public Model() {
		super("Model");
		this.solar.setAzimuth(this.defaultAzimuth);
		this.solar.setElevation(this.defaultElevation);
		this.addChild(this.solar);
		this.setData(new Data());
		this.calendar = Calendar.getInstance();
		this.calendar.setTimeZone(TimeZone.getTimeZone(this.timeZone));
		this.calendar.set(Calendar.YEAR, 2001);
		this.calendar.set(Calendar.MONTH, 0);
		this.calendar.set(Calendar.DATE, 1);
		this.calendar.set(Calendar.HOUR_OF_DAY, 0);
		this.calendar.set(Calendar.MINUTE, 0);
		this.calendar.set(Calendar.SECOND, 0);
		this.setCalendar(this.calendar);
	}
	
	public void setNode(Variable variable) {
		this.node = variable;
		if(this.node instanceof Solar) {
			this.solar.sun.setCenter(this.solar.sun.space);//Must Include Sun b/c Solar is Not Orbital
			this.solar.setScale(this.solar.defaultScale);
			this.solar.setAzimuth(-10);
			this.solar.setElevation(147);
		} else if(this.node instanceof Orbital) {
			logger.info("setNode("+variable+") Orbital");
			Orbital o = (Orbital)this.node;
			o.updateSpace();
			this.solar.sun.setCenter(o.space);//Must Include Sun b/c Solar is Not Orbital
			o.setScale(o.defaultScale);
		} else if(this.node instanceof Spheroid){
			logger.info("setNode("+variable+") Spheroid");
			Spheroid s = (Spheroid)this.node;
			s.setElevation(s.projection.elevation);
			s.setAzimuth(s.projection.azimuth);
			s.setScale(s.defaultScale);
			Object root = s.getRoot();
			while(root != null) {
				if(root instanceof Orbital) {
					Orbital o = (Orbital)root;
					o.updateSpace();
					this.solar.sun.setCenter(o.space);
					break;
				} else {
					root = ((Variable)root).getRoot();
				}
			}
		}
	}
	
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public N getTree() {
		N root = new N(this.toString());
		this.getChildren().forEach(each -> getTree(each, root));
		return root;
	}

	public void setScriptPath(String inputPath) throws Exception {
		if (inputPath != null) {
			File input = new File(inputPath);
			if (input.isDirectory()) {
				File[] inputArray = input.listFiles();
				if (inputArray.length > 0) {
					for (File i : inputArray) {
						if(i.isFile()) {
							Script script = this.getScript(i);
							if (script != null) {
								this.scriptList.add(script);
							}
						}
					}
				} else {
					throw new Exception("Script Folder Empty");
				}
			} else if (input.isFile()) {
				Script script = this.getScript(input);
				if (script != null) {
					this.scriptList.add(script);
				}
			} else {
				throw new Exception("Invalid Script Input");
			}
		}
	}

	public Script getScript(File file) throws Exception {
		logger.info("getScript("+file+")");
		Script script = null;
		if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("json")) {
			script = (Script) NodeController.openJson(file, Script.class);
		} else {
			throw new Exception("Invalid Script Format");
		}
		return script;
	}
}
//Object root = e.getRoot();
//if(root instanceof Orbital) {
//	Orbital o = (Orbital)root;
//	this.solar.setCenter(o.space);
//	o.setScale(o.defaultScale);
//}
//Object root = e.getRoot();
//if(root instanceof Orbital) {
//	Orbital o = (Orbital)root;
//	o.updateSpace();
//	logger.info("setNode("+variable+") e.space="+o.space);
//	this.solar.setCenter(o.space);
//}
//e.updateSpace();
//logger.info("setNode("+variable+") e.space="+e.space);
//this.solar.setCenter(e.space);
