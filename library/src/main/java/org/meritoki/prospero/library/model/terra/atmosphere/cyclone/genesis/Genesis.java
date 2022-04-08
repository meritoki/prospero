package org.meritoki.prospero.library.model.terra.atmosphere.cyclone.genesis;

import java.util.List;

import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.density.Density;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Event;

public class Genesis extends Density {

	public Genesis() {
		super("Genesis");
	}
	
	public void setEventList(List<Event> eventList) {
		for (Event e : eventList) {
			if (e.flag) {
				Coordinate p = ((CycloneEvent)e).getStartCoordinate();
				if (p.flag) {
					coordinateMatrix[(int) ((p.latitude + this.latitude)
							* this.resolution)][(int) ((p.longitude + this.longitude / 2) * this.resolution)][p
									.getMonth() - 1]++;
					String date = p.getYear() + "-" + p.getMonth();
					if (!this.dateList.contains(date)) {
						this.dateList.add(date);
					}
				}
			}
		}
	}


//	@Override
//	public void initCoordinateMatrix(List<Event> eventList) {
//		String date;
//		coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//		dateList = new ArrayList<>();
//		if (this.stackFlag) {
//			List<Integer> levelList = this.getEventLevelList(eventList);
//			for (Integer level : levelList) {
//				this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude
//						* resolution)][monthCount];
//				for (Event e : eventList) {
//					Coordinate p = e.getStartCoordinate();
//					if (p.flag && ((Integer) p.attribute.map.get("pressure")).equals(level)) {
//						int x = (int) ((p.latitude + this.latitude) * this.resolution);
//						int y = (int) ((p.longitude + this.longitude / 2) * this.resolution) % this.longitude;
//						int z = p.getMonth() - 1;
//						this.coordinateMatrix[x][y][z]++;
//						date = p.getYear() + "-" + p.getMonth();
//						if (!this.dateList.contains(date)) {
//							this.dateList.add(date);
//						}
//					}
//				}
//				this.pointMatrixMap.put(level, this.coordinateMatrix);
//			}
//		} else {
//			for (Event e : eventList) {
//				if (e.flag) {
//					Coordinate p = ((CycloneEvent)e).getStartCoordinate();
//					if (p.flag) {
//						coordinateMatrix[(int) ((p.latitude + this.latitude)
//								* this.resolution)][(int) ((p.longitude + this.longitude / 2) * this.resolution)][p
//										.getMonth() - 1]++;
//						date = p.getYear() + "-" + p.getMonth();
//						if (!this.dateList.contains(date)) {
//							this.dateList.add(date);
//						}
//					}
//				}
//			}
//		}
//		this.initMonthArray();
//		this.initYearMap();
//	}
}
