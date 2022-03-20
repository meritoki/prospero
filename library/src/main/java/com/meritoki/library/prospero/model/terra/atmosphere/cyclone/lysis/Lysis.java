package com.meritoki.library.prospero.model.terra.atmosphere.cyclone.lysis;

import java.util.List;

import com.meritoki.library.prospero.model.terra.atmosphere.cyclone.density.Density;
import com.meritoki.library.prospero.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import com.meritoki.library.prospero.model.unit.Coordinate;
import com.meritoki.library.prospero.model.unit.Event;

public class Lysis extends Density {

	public Lysis() {
		super("Lysis");
	}
	
	public void setEventList(List<Event> eventList) {
		for (Event e : eventList) {
			if (e.flag) {
				Coordinate p = ((CycloneEvent)e).getEndCoordinate();
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
//				this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][monthCount];
//				for (Event e : eventList) {
//					Coordinate p = e.getEndCoordinate();
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
//				this.pointMatrixMap.put(level, coordinateMatrix);
//			}
//		} else {
//			for (Event e : eventList) {
//				if (e.flag) {
//					Coordinate p = ((CycloneEvent)e).getEndCoordinate();
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
