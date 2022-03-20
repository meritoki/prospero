package com.meritoki.library.prospero.model.terra.atmosphere.wind;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.terra.atmosphere.wind.intensity.Jetstream;

public class Wind extends Variable {

	static Logger logger = LogManager.getLogger(Wind.class.getName());
	public Jetstream intensity = new Jetstream();

	public Wind() {
		super("Wind");
		this.addChild(intensity);
	}
}
