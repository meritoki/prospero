package org.meritoki.prospero.library.model.terra.atmosphere.cloud.goes;

import org.meritoki.prospero.library.model.terra.atmosphere.cloud.Cloud;
import org.meritoki.prospero.library.model.unit.DataType;

public class N extends Cloud {
	
	public N() {
		super("N");
		this.sourceMap.put("GOES","aefbd8d1-d423-458d-90c0-7c8429f2a653");
		this.dataType = DataType.BAND_4;
	}
}
