package com.meritoki.library.prospero.model.terra.atmosphere.cyclone.unit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meritoki.library.prospero.model.unit.Coordinate;
import com.meritoki.library.prospero.model.unit.Event;

public class ERA5Event extends CycloneEvent {

	public ERA5Event() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ERA5Event(CycloneEvent event) {
		super(event);
		this.classify();
		// TODO Auto-generated constructor stub
	}

	public ERA5Event(String id, List<Coordinate> pointList) {
		super(id, pointList);
		this.classify();
		// TODO Auto-generated constructor stub
	}
	
	@JsonIgnore
	public void classify() {
		int maxTimeLevelCount = this.getMaxTimeLevelCount();// this.getLevelList().size();//
		int lowerMostLevel = this.getLowerMostLevel();
		if (2 <= maxTimeLevelCount && maxTimeLevelCount <= 4) {
			this.family = Family.SHALLOW;
			if (lowerMostLevel >= 700) {
				this.classification = Classification.LOW;
			} else if (lowerMostLevel >= 400) {
				this.classification = Classification.MID;
			} else if (lowerMostLevel >= 125) {
				this.classification = Classification.UPPER;
			}
		} else if (5 <= maxTimeLevelCount && maxTimeLevelCount <= 8) {
			this.family = Family.INTERMEDIATE;
			if (lowerMostLevel >= 500) {
				this.classification = Classification.LOW_MID;
			} else if (lowerMostLevel >= 125) {
				this.classification = Classification.MID_UPPER;
			}
		} else if (maxTimeLevelCount >= 9) {
			this.family = Family.DEEP;
		}
	}

}
