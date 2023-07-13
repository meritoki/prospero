package org.meritoki.prospero.library.model.node.data.source;

import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Time;
import org.utn.app.command.era.Five;
import org.utn.app.command.era.model.five.Batch;
import org.utn.app.command.era.model.five.Form;

public class GeopotentialERA5 extends ERANetCDF {
	
	private Form form = new Form();	
	
	public GeopotentialERA5() {
		super();
		this.variable = "z";
		this.dataType = DataType.GEOPOTENTIAL;
		this.prefix = "geopotential_";
		this.suffix = "_F128";
		this.startTime = new Time(1979,1,1,0,-1,-1);
		this.endTime = new Time(2019,12,31,24,-1,-1);
		this.setRelativePath("ECMWF" + seperator + "File" + seperator + "Data" + seperator + "ERA" + seperator + "5"
				+ seperator + "Geopotential"+seperator);
		this.form.grid.add("F128");
		this.form.time.add("00:00");
		this.form.time.add("06:00");
		this.form.time.add("12:00");
		this.form.time.add("18:00");
		this.form.path = "reanalysis-era5-pressure-levels";
		for(int i=1;i<=31;i++) {
			this.form.day.add(String.format("%02d", i));
		}
		this.form.variable.add("geopotential");
	}
	
	@Override
	public void form(Time time, Integer pressure) {
		super.form(time,pressure);
		if(!this.form.pressure_level.contains(pressure.toString())) {
			this.form.pressure_level.add(pressure.toString());
		}
		if(!this.form.year.contains(String.valueOf(time.year))) {
			this.form.year.add(String.valueOf(time.year));
		}
		String month = String.format("%02d", time.month);
		if(!this.form.month.contains(month)) {
			this.form.month.add(month);
		}
	}
	
	@Override
	public void download() {
		super.download();
		this.form.outputPath = this.getPath();
		Batch batch = new Batch(this.form);
		String batchPath = this.getPath() + batch.uuid + ".json";
		Five.executeBatch(batchPath, new Batch(this.form));
	}
}
