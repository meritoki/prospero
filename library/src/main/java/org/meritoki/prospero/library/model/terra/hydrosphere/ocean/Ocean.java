package org.meritoki.prospero.library.model.terra.hydrosphere.ocean;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.enso.ENSO;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.modulus.Modulus;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.pdo.PDO;
import org.meritoki.prospero.library.model.terra.hydrosphere.ocean.tempurature.SeaSurfaceTemperature;
import org.meritoki.prospero.library.model.unit.Frame;

public class Ocean extends Variable {

	static Logger logger = LogManager.getLogger(Ocean.class.getName());

	public Ocean() {
		super("Ocean");
//		this.addChild(new SeaSurfaceTemperature());
//		this.addChild(new Modulus());
		this.addChild(new ENSO());
		this.addChild(new PDO());
	}
	
	public Ocean(String name) {
		super(name);
	}
	
	public List<Frame> getCalendarFrameList(List<Frame> frameList) {
		List<Frame> fList = new ArrayList<>();
		for(Frame f: frameList) {
			if(f.containsCalendar(this.calendar)) {
				fList.add(f);
			}
		}
		return fList;
	}
}


