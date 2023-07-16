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
}
//private Form form = new Form();	
//@Override
//public void form(Time time, Integer pressure) {
//	super.form(time,pressure);
//	if(!this.form.levelList.contains(pressure.toString())) {
//		this.form.levelList.add(pressure.toString());
//	}
//	if(!this.form.year.contains(String.valueOf(time.year))) {
//		this.form.year.add(String.valueOf(time.year));
//	}
//	String month = String.format("%02d", time.month);
//	if(!this.form.month.contains(month)) {
//		this.form.month.add(month);
//	}
//}
//
//@Override
//public void download() {
//	super.download();
//	this.form.outputPath = this.getPath();
//	Batch batch = new Batch(this.form);
//	String batchPath = this.getPath() + batch.uuid + ".json";
//	Interim.executeBatch(batchPath, new Batch(this.form));
//}
//this.form.grid.add("F128");
//this.form.levtype = "pl";
//this.form.clazz = "ei";
//this.form.time.add("00:00");
//this.form.time.add("06:00");
//this.form.time.add("12:00");
//this.form.time.add("18:00");
//for(int i=1;i<=31;i++) {
//	this.form.day.add(String.format("%02d", i));
//}
//this.form.param.add("138.128");