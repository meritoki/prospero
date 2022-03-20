package com.meritoki.library.prospero.model.terra.lithosphere.magnetic;

import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.terra.lithosphere.magnetic.anamoly.Anomaly;
import com.meritoki.library.prospero.model.terra.lithosphere.magnetic.field.Field;

public class Magnetic extends Variable {
	
	public Magnetic() {
		super("Magnetic");
		this.addChild(new Anomaly());
		this.addChild(new Field());
	}

}
