package org.meritoki.prospero.library.model.terra.lithosphere.magnetic;

import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.terra.lithosphere.magnetic.anamoly.Anomaly;
import org.meritoki.prospero.library.model.terra.lithosphere.magnetic.field.Field;

public class Magnetic extends Variable {
	
	public Magnetic() {
		super("Magnetic");
		this.addChild(new Anomaly());
		this.addChild(new Field());
	}

}
