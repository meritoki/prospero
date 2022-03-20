package com.meritoki.library.prospero.model.terra.hydrosphere.ocean.modulus;

import com.meritoki.library.prospero.model.terra.hydrosphere.ocean.Ocean;

public class Modulus extends Ocean {
	
	public Modulus() {
		super("Modulus");
		this.sourceMap.put("ERA Interim","8edb8e7d-d0e1-4204-ac2f-12c456f0a1b1");
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
//		this.setFrameList(DataType.MODULUS,this.getCalendarFrameList(calendar,this.frameList), this.dimension);
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
//			this.setFrameList(DataType.MODULUS,this.getCalendarFrameList(this.calendar,this.frameList), 2);
//			super.paint(graphics);
//		}
//	}
//}
