package org.meritoki.prospero.library.model.node.data.source;

import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Time;

public class VorticityERAInterim extends ERANetCDF {
	
	public VorticityERAInterim() {
		super();
		this.variable = "vo";
		this.dataType = DataType.VORTICITY;
		this.prefix = "138-128_";
		this.suffix = "_F128";
		this.startTime = new Time(2001,1,1,0,-1,-1);
		this.endTime = new Time(2017,12,31,24,-1,-1);
		this.setRelativePath("ECMWF" + seperator + "File" + seperator + "Data" + seperator + "ERA" + seperator + "Interim"
				+ seperator + "Vorticity"+seperator);
	}
	
	@Override
	public Time getStartTime() {
		return this.startTime;
	}

	@Override
	public Time getEndTime() {
		return this.endTime;
	}
	
	

}
