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
package org.meritoki.prospero.library.model.node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.meritoki.prospero.library.model.document.Document;
import org.meritoki.prospero.library.model.node.data.Data;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.unit.Dimension;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Operator;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Script;
import org.meritoki.prospero.library.model.unit.Table;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meritoki.module.library.model.Module;
import com.meritoki.module.library.model.N;
import com.meritoki.module.library.model.Node;

public class Variable extends Node {

	static Logger logger = LoggerFactory.getLogger(Variable.class.getName());
	@JsonIgnore
	public Mode mode = Mode.NULL;
	@JsonIgnore
	public Calendar calendar;
	@JsonIgnore
	public Calendar startCalendar;
	@JsonIgnore
	public Calendar endCalendar;
	@JsonIgnore
	public List<Script> scriptList = new ArrayList<>();
	@JsonIgnore
	public List<String> orderList = new ArrayList<>();
	@JsonIgnore
	public LinkedList<Time> timeList = new LinkedList<>();
	@JsonIgnore
	public LinkedList<Query> queryStack = new LinkedList<>();
	@JsonIgnore
	public LinkedList<Query> queryQueue = new LinkedList<>();
	@JsonIgnore
	public Map<String, String> sourceMap = new HashMap<String, String>();
	@JsonIgnore
	public Map<String, Boolean> variableMap = new HashMap<>();
	@JsonIgnore
	public Script script = null;
	@JsonIgnore
	public Query query = new Query();
	@JsonIgnore
	public Data data;
	@JsonIgnore
	public Operator operator;
	@JsonIgnore
	public Document document;
	@JsonIgnore
	public Dimension dimension = new Dimension();
	@JsonIgnore
	public String source;
	@JsonIgnore
	public String timeZone = "GMT-3";
	@JsonIgnore
	public String unit;
	@JsonIgnore
	public String format = "#.###E0";
	@JsonIgnore
	public boolean load;
	@JsonIgnore
	public boolean correlation;
	@JsonIgnore
	public boolean cache;
//	@JsonIgnore
//	public boolean visible;

	public Variable() {
	}

	public Variable(String name) {
		super(name);
		this.filter = false;
	}

	public Variable(String name, Module module) {
		super(name, module);
		this.filter = false;
	}

	@Override
	public boolean equals(Object object) {
		Variable node = (Variable) object;
		return this.name.equals(node.name);
	}

	public void start() {
		super.start();
		this.load = true;
		this.initVariableMap();
	}

	@Override
	public void stop() {
		super.stop();
		this.load = false;
	}

	@Override
	protected void defaultState(Object object) {
		if (object instanceof Result) {
			Result result = (Result) object;
			switch (result.mode) {
			case LOAD: {
				this.mode = Mode.LOAD;
				this.load(result);
				this.addRootObject(new Result(Mode.PAINT));
				break;
			}
			case COMPLETE: {
				this.complete();
				this.mode = Mode.COMPLETE;
				this.addRootObject(new Result(Mode.PAINT));
				break;
			}
			case EXCEPTION: {
				this.complete();
				this.mode = Mode.EXCEPTION;
				logger.warn("defaultState(" + (object != null) + ") EXCEPTION");
				logger.warn("defaultState(" + (object != null) + ") result.message=" + result.message);
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
	 * Initialize all parameters from Query
	 */
	@JsonIgnore
	public void init() {
		logger.debug("init()");
		try {
			this.operator = this.query.getOperator();
			this.script = this.query.getScript();
			this.timeList = new LinkedList<>();
		} catch (Exception e) {
			logger.error("init() e=" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Support Result from Data Source
	 * 
	 * @param result
	 */
	public void load(Result result) {
		logger.debug("load(" + (result != null) + ")");
	}

	public void complete() {
		logger.info(this + ".complete()");
	}

	public boolean isComplete() {
		return this.mode == Mode.COMPLETE;// || this.mode == Mode.EXCEPTION;
	}

	public boolean isException() {
		return this.mode == Mode.EXCEPTION;
	}

	@JsonIgnore
	public void query() {
		this.query(this.query);
	}

	/**
	 * Query is an important function. If the query is completely unknown, the
	 * global query is set for the first time.
	 */
	@JsonIgnore
	public void query(Query query) {
		this.query = query;
		query.put("sourceUUID", this.sourceMap.get(query.getSource()));
		logger.info("query(" + query + ")");
		if (!query.equals(this.queryStack.peek())) {// Used to Detect Same Time Query More than Once In Time Order
			query.objectList = this.objectList;
			this.reset();// Reset b/c Found a New Query, Older No Longer Matter
			this.init();
			try {
				this.data.add(query);
				this.queryStack.push(new Query(query));
			} catch (Exception e) {
				logger.warn("query(" + query + ") Exception " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			if (this.mode == Mode.COMPLETE) {
				try {
					this.process();
				} catch (Exception e) {
					logger.warn("query(" + query + ") Exception " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	@JsonIgnore
	public void process() throws Exception {
		logger.info("process()");
		this.init();
	}

	@JsonIgnore
	public void reset() {
		logger.info("reset()");
		this.mode = Mode.NULL;
		this.queryStack = new LinkedList<>();
	}

	public void initVariableMap() {
	}

	@JsonIgnore
	public Variable getVariable(String name) {
//		logger.debug(this.name+".getVariable("+name+")");
		if (this.name != null && this.name.equals(name)) {
			return this;
		} else {
			List<Variable> nodeList = this.getChildren();
			for (Variable n : nodeList) {
				Variable node = n.getVariable(name);
				if (node != null) {
					return node;
				}
			}
			return null;
		}
	}

	public List<Plot> getPlotList() throws Exception {
		List<Plot> plotList = new ArrayList<>();
		for (Variable n : this.getList()) {
			if (n.load) {
				plotList.addAll(n.getPlotList());
			}
		}
		return plotList;
	}

	public List<Table> getTableList() throws Exception {
		List<Table> tableList = new ArrayList<>();
		for (Variable n : this.getList()) {
			if (n.load) {
				tableList.addAll(n.getTableList());
			}
		}
		return tableList;
	}

	@JsonIgnore
	public void setDocument(Document document) {
		this.document = document;
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.setDocument(this.document);
		}
	}

//	@JsonIgnore
//	public void setTimeZone(String timeZone) {
//		this.timeZone = timeZone;
//		List<Variable> nodeList = this.getChildren();
//		for (Variable n : nodeList) {
//			n.setCalendar(calendar);
//		}
//	}

	@JsonIgnore
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.setCalendar(calendar);
		}
	}

	@JsonIgnore
	public void setStartCalendar(Calendar startCalendar) {
		this.startCalendar = startCalendar;
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.setStartCalendar(this.startCalendar);
		}
	}

	@JsonIgnore
	public void setEndCalendar(Calendar endCalendar) {
		this.endCalendar = endCalendar;
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.setEndCalendar(this.endCalendar);
		}
	}

	@JsonIgnore
	public void setFilter(Query filter) {
		this.query = filter;
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.setFilter(filter);
		}
	}

	@JsonIgnore
	public void setData(Data data) {
		this.data = data;
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.setData(data);
		}
	}

	@JsonIgnore
	public List<String> getSourceList() {
		return this.sourceMap.keySet().stream().collect(Collectors.toList());
	}

	@JsonIgnore
	public List<String> getVariableList() {
		return this.variableMap.keySet().stream().collect(Collectors.toList());
	}

	@JsonIgnore
	public List<Variable> getChildren() {
		List<Module> moduleList = new ArrayList<Module>(this.moduleMap.values());
		List<Variable> variableList = new ArrayList<>();
		for (Module m : moduleList) {
			if (m instanceof Variable) {
				variableList.add((Variable) m);
			}
		}
		return variableList;
	}

	@JsonIgnore
	public Calendar getCalendar() {
		return this.calendar;
	}

	@JsonIgnore

	public void addRootObject(Object object) {
		Module module = this.getModel();
		logger.debug(module + ".addRootObject(" + (object != null) + ")");
		module.add(object);
	}

	public Module getModel() {
		if (this.getParents() == null) {
			return this;
		}
		return this.getParents().getModel();
	}

	public List<Variable> getList() {
		List<Variable> nodeList = new ArrayList<>();
		this.getList(this, nodeList);
		return nodeList;
	}

	public void getList(Variable node, List<Variable> nodeList) {
		List<Variable> nList = node.getChildren();
		for (Variable n : nList) {
			Variable v = n;
			nodeList.add(v);
			v.getList(v, nodeList);
		}
	}

	/**
	 * N parameter is the hook that provides access to the whole structure, without
	 * passing n, cannot get Tree.
	 * 
	 * @param module
	 * @param n
	 */
	@JsonIgnore
	public static void getTree(Variable module, N n) {
		N m = new N((String) module.toString());
		n.add(m);
		module.getChildren().forEach(each -> getTree(each, m));
	}

	public Variable getParents() {
		return (Variable) this.getRoot();
	}

	@JsonIgnore
	public void addChild(Variable child) {
		logger.debug(this.name + ".addChild(" + child + ")");
		this.orderList.add(child.toString());
		this.moduleMapPut(child);
	}

	@JsonIgnore
	public void addChildren(List<Variable> children) {
		for (Variable v : children) {
			this.addChild(v);
		}
	}

	@JsonIgnore
	public static void printTree(Variable node, String appender) {
		node.getChildren().forEach(each -> printTree(each, appender + appender));
	}

	public Image getImage(Image image) throws Exception {
		logger.debug(this + ".getImage(" + image + ")");
		Graphics graphics = (image != null) ? image.getGraphics() : null;
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, (int) dimension.width, (int) dimension.height);
		graphics.translate((int) (dimension.width / 2.0), (int) (dimension.height / 2.0));
		this.paint(graphics);
		return image;
	}

	@JsonIgnore
	public void paint(Graphics graphics) throws Exception {
//		logger.debug(this+".paint(" + (graphics != null) + ")");
		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.paint(graphics);
		}
	}
}
//if (query.isReady()) {// should only move forward if we have a time & source
//query.calendar = this.calendar;
//else if(this.mode == Mode.EXCEPTION) {
//this.reset();
//}
//= new GregorianCalendar(2001, 0, 1, 0, 0, 0);
//public List<Variable> getVisibleList() {
//List<Variable> nodeList = this.getList();
//List<Variable> nList = new ArrayList<>();
//for (Variable n : nodeList) {
//	if(n.visible) {
//		nList.add(n);
//	}
//}
//return nList;
//}

//public void getVisibleList(Variable node, List<Variable> nodeList) {
//List<Variable> nList = this.getChildren();
//for (Variable n : nList) {
//	Variable v = n;
//	if(v.visible) {
//		nodeList.add(v);
//	}
//	v.getList(v, nodeList);
//}
//}
//= new GregorianCalendar(2001, 11, 31, 0, 0, 0);// Calendar.getInstance();
//@JsonIgnore
//public void plot(Graphics graphics) throws Exception {
//	List<Variable> nodeList = this.getChildren();
//	for (Variable n : nodeList) {
//		n.plot(graphics);
//	}
//}
//@JsonIgnore
//public void setProjection(Projection projection) {
//	this.projection = projection;
//	List<Variable> nodeList = this.getChildren();
//	for (Variable n : nodeList) {
//		n.setProjection(projection);
//	}
//}
//for(String s: this.orderList) {
//System.out.println(s+".equals("+m+")");
//if(s.equals(m.toString())) {
//}
//}
//List<Module> moduleList = new ArrayList<Module>(this.moduleMap.values());
//List<Variable> variableList = new ArrayList<>();
//for (Module m : moduleList) {
//for(String s: this.orderList) {
//	if (s.equals(m.toString()) && m instanceof Variable) {
//		variableList.add((Variable) m);
//	}
//}
//
//}
//Page page = this.document.pageMap.get(query.getTime()+"-"+query.getSourceUUID());
//if(page == null) {
//	page = new Page();
//}
//page.queryList.add(query);
//this.document.pageMap.put(query.getTime()+"-"+query.getSourceUUID(),page);
//@JsonIgnore
//public Projection projection = new Globe();
//@JsonIgnore
//public void unload() {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.data.unload(sourceUUID);
//}
//public List<Event> filter(List<Event> eventList) throws Exception {
//if (!Thread.interrupted()) {
//	if (eventList != null) {
//		for (Event e : eventList) {
//			if (this.intervalList != null && this.intervalList.size() > 0) {
//				for (Interval r : this.intervalList) {
//					if (r.contains(e)) {
//						e.flag = true;
//					}
//				}
//			} else {
//				e.flag = true;
//			}
////			for (Coordinate c : e.coordinateList) {
////				boolean regionFlag = false;
////				if (this.intervalList != null && this.intervalList.size() > 0) {
////					for (Interval r : this.intervalList) {
////						if (r.contains(c)) {
////							regionFlag = true;
////							e.flag = true;
////						}
////					}
////				} else {
////					regionFlag = true;
////					e.flag = true;
////				}
////				c.flag = regionFlag;
////			}
//		}
//	}
//	if (eventList.size() == 0) {
//		logger.warn("filter(" + eventList.size() + ") zero");
//	}
//} else {
//	throw new InterruptedException();
//}
//return eventList;
//}
//		if (this.delayExpired()) {
//			this.query();
//			this.setDelay(this.newDelay(3.0));
//		}
//System.out.println(this.name+".setCalendar("+calendar.getTime()+")");
//this.data.setCalendar(this.calendar);
//@JsonIgnore
//public void load() {
//	List<Variable> nodeList = this.getChildren();
//	for(Variable n: nodeList) {
//		n.load();
//	}
//}

//@JsonIgnore
//public void init() throws Exception {
//	System.err.println("init() Unsupported");
//}

//@JsonIgnore
//public void query() throws Exception {
//	if (this.load) {
//		this.init();
//	}
//	List<Variable> nodeList = this.getChildren();
//	for(Variable n: nodeList) {
//		n.query();
//	}
//}
//
//@JsonIgnore
//public List<Variable> getChildren() {
//	return children;
//}
//
//@JsonIgnore
//public String getData() {
//	return name;
//}
//
//@JsonIgnore
//public void setData(String data) {
//	this.name = data;
//}
//
//@JsonIgnore
//private void setParent(Variable parent) {
//	this.parent = parent;
//}
//
//@JsonIgnore
//public Variable getParent() {
//	return this.parent;
//}
//
//public List<Variable> getCorrelationNodeList() {
//List<Variable> nodeList = new ArrayList<>();
//for(Variable n:this.getChildren()) {
//	if(n.correlation) {
//		nodeList.add(n);
//	}
//	nodeList.addAll(n.getCorrelationNodeList());
//}
//return nodeList;
//}
//@JsonIgnore
//public static  void getTree(Node node) {
//	getTree(node, null);
//}
//
///**
// * N parameter is the hook that provides access to the whole structure, without passing n, cannot get Tree.
// * @param node
// * @param n
// */
//@JsonIgnore
//public static  void getTree(Node node, N n) {
//	N newN = new N((String)node.getData());	
//	n.add(newN);
//	node.getChildren().forEach(each -> getTree(each, newN));
//}