package com.meritoki.library.prospero.model.terra.atmosphere.cyclone.unit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meritoki.library.prospero.model.unit.Coordinate;

public class ERAInterimEvent extends CycloneEvent {

	public ERAInterimEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ERAInterimEvent(CycloneEvent event) {
		super(event);
		this.classify();
		// TODO Auto-generated constructor stub
	}

	public ERAInterimEvent(String id, List<Coordinate> pointList) {
		super(id, pointList);
		this.classify();
		// TODO Auto-generated constructor stub
	}
	
	@JsonIgnore
	public void classify() {
		int maxTimeLevelCount = this.getMaxTimeLevelCount();// this.getLevelList().size();//
		int lowerMostLevel = this.getLowerMostLevel();
		if (maxTimeLevelCount == 2 || maxTimeLevelCount == 3) {
			this.family = Family.SHALLOW;
			if (lowerMostLevel >= 700) {
				this.classification = Classification.LOW;
			} else if (lowerMostLevel >= 400) {
				this.classification = Classification.MID;
			} else if (lowerMostLevel >= 125) {
				this.classification = Classification.UPPER;
			}
		} else if (maxTimeLevelCount == 4 || maxTimeLevelCount == 5) {
			this.family = Family.INTERMEDIATE;
			if (lowerMostLevel >= 500) {
				this.classification = Classification.LOW_MID;
			} else if (lowerMostLevel >= 125) {
				this.classification = Classification.MID_UPPER;
			}
		} else if (maxTimeLevelCount >= 6) {
			this.family = Family.DEEP;
		}
	}
}
