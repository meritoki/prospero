package org.meritoki.prospero.library.model.terra.gravitation.geopotential;

import org.meritoki.prospero.library.model.terra.gravitation.Gravitation;
import org.meritoki.prospero.library.model.unit.DataType;

public class Geopotential extends Gravitation {

	public Geopotential() {
		super("Geopotential");
		this.sourceMap.put("ERA 5","80607606-1671-4f9f-967b-db7f59e87b81");
		this.dataType = DataType.GEOPOTENTIAL;
	}
}
