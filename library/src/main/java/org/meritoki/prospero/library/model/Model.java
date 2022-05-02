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
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.solar.Solar;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.Cyclone;
import org.meritoki.prospero.library.model.unit.Script;

import com.meritoki.library.controller.node.NodeController;
import com.meritoki.module.library.model.N;

public class Model extends Variable {

	static Logger logger = LogManager.getLogger(Model.class.getName());
	public List<System> systemList = new ArrayList<>();
	public Variable node = this;
	public boolean cache = false;

	public Model() {
		super("Model");
		this.addChild(new Solar());
		this.setData(new Data());
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(this.timeZone));
		calendar.set(Calendar.YEAR, 2001);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		this.setCalendar(calendar);
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
