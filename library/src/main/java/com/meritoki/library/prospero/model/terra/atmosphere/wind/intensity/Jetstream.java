package com.meritoki.library.prospero.model.terra.atmosphere.wind.intensity;

import com.meritoki.library.prospero.model.grid.Grid;

public class Jetstream extends Grid {

//	public List<Frame> frameList;
	
	
	public Jetstream() {
		super("Jetstream");
		this.sourceMap.put("ERA Interim", "f4d6ead6-949a-42a9-9327-a8e22790e0e7");
	}
}
//@Override
//public void load() {
//	if(this.load) { 
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.data.load(sourceUUID);
//	}
//}
//
//public void init(Calendar calendar) throws Exception {
//	String sourceUUID = this.sourceMap.get(this.sourceKey);
//	this.frameList = (List<Frame>) this.data.query(sourceUUID, this.query);
//	if(this.frameList != null) {
//		this.setFrameList(DataType.INTENSITY,this.getCalendarFrameList(calendar,this.frameList), this.dimension);
//	}
//}
//
//@Override
//public void paint(Graphics graphics) throws Exception {
//	if(this.load) {
//		this.init(this.calendar);
//		super.paint(graphics);
//	}
//}
//@Override
//public void paint(Graphics graphics) throws Exception {
//	if(this.load) {
//		String sourceUUID = this.sourceMap.get(this.sourceKey);
//		this.frameList = (List<Frame>) this.data.get(sourceUUID, this.filter);
//		if(this.frameList != null) {
//			this.setFrameList(DataType.INTENSITY,this.getCalendarFrameList(this.calendar,this.frameList), 2);
//			super.paint(graphics);
//		}
//	}
//}
