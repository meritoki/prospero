package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit;

import java.util.List;

import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ERA5Event extends CycloneEvent {

	public ERA5Event() {
		super();
	}

	public ERA5Event(CycloneEvent event) {
		super(event);
	}

	public ERA5Event(List<Coordinate> coordinateList) {
		super(coordinateList);
	}
	
	public ERA5Event(String id, List<Coordinate> coordinateList) {
		super(id, coordinateList);
	}
	
	@JsonIgnore
	@Override
	public void classify() {
		int maxTimeLevelCount = this.getMaxTimeLevelCount();
		int lowerMostLevel = this.getLowerMostLevel();// Need to fix
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
