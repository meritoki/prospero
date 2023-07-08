package org.meritoki.prospero.library.model.node.data.source;

import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Time;

public class VorticityERA5 extends ERANetCDF {
	
	public VorticityERA5() {
		super();
		this.variable = "vo";
		this.dataType = DataType.VORTICITY;
		this.prefix = "vorticity_";
		this.suffix = "_F128";
		this.startTime = new Time(1979,1,1,0,-1,-1);
		this.endTime = new Time(2019,12,31,24,-1,-1);
		this.setRelativePath("ECMWF" + seperator + "File" + seperator + "Data" + seperator + "ERA" + seperator + "5"
				+ seperator + "Vorticity"+seperator);
	}
}
