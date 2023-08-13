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

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.meritoki.prospero.library.model.node.Camera;
import org.meritoki.prospero.library.model.node.Orbital;
import org.meritoki.prospero.library.model.node.Spheroid;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.node.data.Data;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.solar.Solar;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Script;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meritoki.library.controller.model.ModelInterface;
import com.meritoki.library.controller.model.SystemInterface;
import com.meritoki.library.controller.model.provider.Provider;
import com.meritoki.library.controller.model.vendor.Vendor;
import com.meritoki.library.controller.node.NodeController;
import com.meritoki.module.library.model.N;

public class Model extends Variable implements ModelInterface {

	static Logger logger = LoggerFactory.getLogger(Model.class.getName());
	public System system = new System();
	public Data data = new Data();
	public Solar solar = new Solar();
	public List<Camera> cameraList = new ArrayList<>();
	public int index;
	public boolean execute;
	public Query query;

	public Model() {
		super("Model");
		this.initProvider();
		this.initVendor();
		this.addChild(this.solar);
		this.addCamera(new Camera(this.solar));
		this.setData(this.data);
	}
	
	public SystemInterface getSystem() {
		return this.system;
	}
	
	public void initProvider() {
		for(Entry<String, Provider> entry:this.system.providerMap.entrySet()) {
			Provider provider = entry.getValue();
			provider.setModel(this);
			try {
				provider.init();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void initVendor() {
		for(Entry<String, Vendor> entry:this.system.vendorMap.entrySet()) {
			Vendor vendor = entry.getValue();
			vendor.setModel(this);
			try {
				vendor.init();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start() {
		super.start();
	}

	public void removeCameras() {
		this.cameraList = new ArrayList<>();
	}

	@JsonIgnore
	public void setProperties(Properties properties) {
		this.system.properties = properties;
		Object basePath = this.system.properties.get("basePath");
		if (basePath instanceof String) {
			this.data.setBasePath((String) basePath);
		}
		Object calendar = this.system.properties.get("calendar");
		if (calendar instanceof String && ((String)calendar).length()>0) {
			this.calendar = Time.getCalendar("yyyy/MM/dd HH:mm:ss", (String) calendar);
			
		} else {
			this.calendar = Calendar.getInstance();
		}
		Object startCalendar = this.system.properties.get("startCalendar");
		if (startCalendar instanceof String && ((String)startCalendar).length()>0) {
			this.startCalendar = Time.getCalendar("yyyy/MM/dd HH:mm:ss", (String) startCalendar);
			
		} else {
			this.startCalendar = Time.getStartCalendar(this.calendar);
		}
		Object endCalendar = this.system.properties.get("endCalendar");
		if (endCalendar instanceof String && ((String)endCalendar).length()>0) {
			this.endCalendar = Time.getCalendar("yyyy/MM/dd HH:mm:ss", (String) endCalendar);
			
		} else {
			this.endCalendar = Time.getEndCalendar(this.calendar);
		}
		Object timeZone = this.system.properties.get("timeZone");
		if (timeZone instanceof String && ((String)timeZone).length()>0) {
			this.calendar.setTimeZone(TimeZone.getTimeZone((String)timeZone));
			this.startCalendar.setTimeZone(TimeZone.getTimeZone((String)timeZone));
			this.endCalendar.setTimeZone(TimeZone.getTimeZone((String)timeZone));
		} 
		this.setCalendar(this.calendar);
		this.setStartCalendar(this.startCalendar);
		this.setEndCalendar(this.endCalendar);
	}
	
	@Override
	public void setCalendar(Calendar calendar) {
		logger.debug("setCalendar("+Time.getCalendarString(null,calendar)+")");
		super.setCalendar(calendar);
	}
	
	@Override
	public void setStartCalendar(Calendar calendar) {
		logger.debug("setStartCalendar("+Time.getCalendarString(null,calendar)+")");
		super.setStartCalendar(calendar);
	}
	
	@Override
	public void setEndCalendar(Calendar calendar) {
		logger.debug("setEndCalendar("+Time.getCalendarString(null,calendar)+")");
		super.setEndCalendar(calendar);
	}



	@JsonIgnore
	public void setBasePath(String basePath) {
		if (basePath != null) {
			String path = (String)this.system.properties.get("basePath");
			if(path == null) {
				this.system.properties.put("basePath", basePath);
			}
			this.data.setBasePath(basePath);
		}
	}

	@Override
	protected void defaultState(Object object) {
		if (object instanceof Result) {
			Result result = (Result) object;
			switch (result.mode) {
			case PAINT: {
				logger.debug("defaultState(" + (object != null) + ") result.mode=" + result.mode);
				this.init();
				break;
			}
			default: {
				logger.warn("defaultState(" + (object != null) + ") default");
				break;
			}
			}
		}
	}

	/**
	 * Add Camera to Camera List
	 * 
	 * @param camera
	 */
	@JsonIgnore
	public void addCamera(Camera camera) {
		if (camera != null) {
			this.cameraList.add(camera);
			this.index = this.cameraList.size() - 1;
			logger.debug(this + ".addCamera(" + camera + ") this.index=" + this.index);
		}
	}

	public void keyPressed(KeyEvent e) {
		e.consume();
		if (e.isControlDown()) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP: {
				if (this.index > 0) {
					this.index -= 1;
				}
				logger.info("keyPressed(e) UP this.index=" + this.index);
				break;
			}
			case KeyEvent.VK_LEFT: {
				if (this.index > 0) {
					this.index -= 1;
				}
				logger.info("keyPressed(e) LEFT this.index=" + this.index);
				break;
			}
			case KeyEvent.VK_DOWN: {
				if (this.index < this.cameraList.size() - 1) {
					this.index += 1;
				}
				logger.info("keyPressed(e) DOWN this.index=" + this.index);
				break;
			}
			case KeyEvent.VK_RIGHT: {
				if (this.index < this.cameraList.size() - 1) {
					this.index += 1;
				}
				logger.info("keyPressed(e) RIGHT this.index=" + this.index);
				break;
			}
			}
		}
	}

	/**
	 * Add Camera to Camera List
	 * 
	 * @param camera
	 */
	@JsonIgnore
	public void setCamera(Camera camera) {
		this.cameraList.set(this.index, camera);
	}

	@JsonIgnore
	public Camera getCamera() {
		return this.getCamera(this.index);
	}

	@JsonIgnore
	public Camera getCamera(int index) {
		return (this.cameraList.size() > 0) ? this.cameraList.get(index) : null;
	}

	@SuppressWarnings("resource")
	public void setCameraBuffer(Camera c) {
		Variable node = c.getNode();
		if (node instanceof Solar) {
			logger.debug("setCameraBuffer(" + node + ") instanceof Solar");
			this.solar.setCenter(this.solar.sun.space);// Must Include Sun b/c Solar is Not Orbital
			this.solar.setAzimuth(c.azimuth);
			this.solar.setElevation(c.elevation);
			this.solar.setScale(c.scale);
			c.buffer = this.solar;
		} else if (node instanceof Orbital) {
			logger.debug("setCameraBuffer(" + node + ") instanceof Orbital");
			Orbital o = (Orbital) node;
			o.updateSpace();
			this.solar.setSelectable(false);
			this.solar.setCenter(o.space);// Must Include Sun b/c Solar is Not Orbital
			this.solar.setAzimuth(c.azimuth);
			this.solar.setElevation(c.elevation);
			this.solar.setScale(c.scale);// o.getProjection().scale);
			c.buffer = this.solar;
		} else if (node instanceof Spheroid) {
			logger.debug("setCameraBuffer(" + node + ") instanceof Spheroid");
			Spheroid s = (Spheroid) node;
			s.setSelectable(true);
			s.setAzimuth(c.azimuth);
			s.setElevation(c.elevation);
			s.setScale(c.scale);
			Object root = s.getRoot();
			while (root != null) {
				if (root instanceof Orbital) {
					Orbital o = (Orbital) root;
					o.updateSpace();
					this.solar.setCenter(o.space);// Must Include Sun b/c Solar is Not Orbital
					break;
				} else {
					root = ((Variable) root).getRoot();
				}
			}
			c.buffer = s;
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
						if (i.isFile()) {
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
		logger.info("getScript(" + file + ")");
		Script script = null;
		if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("json")) {
			script = (Script) NodeController.openJson(file, Script.class);
		} else {
			throw new Exception("Invalid Script Format");
		}
		return script;
	}
}
//this.calendar = Calendar.getInstance();
//this.startCalendar = new GregorianCalendar(2016, 0, 1, 0, 0, 0);
//this.endCalendar = new GregorianCalendar(2016, 11, 31, 0, 0, 0);
//this.calendar = Calendar.getInstance();
//this.calendar.setTime(new Date());
//this.calendar.setTimeZone(TimeZone.getTimeZone(this.timeZone));
//this.setCalendar(this.calendar);
//this.setStartCalendar(this.startCalendar);
//this.setEndCalendar(this.endCalendar);
//, this.scale, this.azimuth, this.elevation));
//this.solar.setScale(o.defaultScale);
//this.solar.setAzimuth(this.defaultAzimuth);
//this.solar.setElevation(this.defaultElevation);
//public Variable node = solar;
//public int defaultAzimuth = 0;
//public int defaultElevation = 150;
//protected double azimuth = 0;
//protected double elevation = 0;
//protected double scale = 1;
//this.solar.setAzimuth(c.azimuth);
//this.solar.setElevation(c.elevation);
//camera = new Camera(this.solar);
//c.setNode(this.solar);
//o.setScale(o.defaultScale);
//@SuppressWarnings("resource")
//public void updateNodeList() {
//	if (this.nodeList.size() > 0) {
//		for (Variable node : this.nodeList) {
//			if (node instanceof Solar) {
//				this.solar.setCenter(this.solar.sun.space);// Must Include Sun b/c Solar is Not Orbital
//				this.solar.setScale(this.solar.defaultScale);
//				this.solar.setAzimuth(this.defaultAzimuth);
//				this.solar.setElevation(this.defaultElevation);
//			} else if(node instanceof Terra)  {
//				
//			} else if (node instanceof Orbital) {
//				logger.info("updateNode() " + node + " instanceof Orbital");
//				Orbital o = (Orbital) node;
//				o.updateSpace();
//				this.solar.setCenter(o.space);// Must Include Sun b/c Solar is Not Orbital
////				o.setScale(o.defaultScale);
//			} else if (node instanceof Spheroid) {
//				logger.info("updateNode() " + node + " instanceof Spheroid");
//				Spheroid s = (Spheroid) node;
//				s.setElevation(s.getProjection().elevation);
//				s.setAzimuth(s.getProjection().azimuth);
//				s.setScale(s.defaultScale);
//				Object root = s.getRoot();
//				while (root != null) {
//					if (root instanceof Orbital) {
//						Orbital o = (Orbital) root;
//						o.updateSpace();
//						this.solar.setCenter(o.space);
//						break;
//					} else {
//						root = ((Variable) root).getRoot();
//					}
//				}
//			}
//		}
//	}
//}
//this.data.setBasePath("/home/jorodriguez/Prospero/prospero-data/");
//this.calendar.set(Calendar.YEAR, 2001);
//this.calendar.set(Calendar.MONTH, 0);
//this.calendar.set(Calendar.DATE, 1);
//this.calendar.set(Calendar.HOUR_OF_DAY, 0);
//this.calendar.set(Calendar.MINUTE, 0);
//this.calendar.set(Calendar.SECOND, 0);
//public List<System> systemList = new ArrayList<>();
//public boolean cache = false;
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
