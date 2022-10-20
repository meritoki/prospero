package org.meritoki.prospero.library.model.unit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.table.Table;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.unit.CycloneEvent;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Band {
	
	@JsonIgnore
	static Logger logger = LogManager.getLogger(Band.class.getName());
	public List<Tile> tileList = new ArrayList<>();
	public double value;
	public double latitude;
	
	public Band(List<Tile> tileList, double latitude) {
		this.latitude = latitude;
		this.tileList = tileList;
		this.value = this.average(this.tileList);
	}
	
	public double average(List<Tile> tileList) {
		double quotient = 0;
		double sum=0;
		double size = 0;
		for(Tile t: tileList) {
			if(t.value != 0) {
				sum += t.value;
				size++;
			}
		}
		quotient = (size>0)?(sum/size):quotient;
//		logger.info("average("+tileList.size()+") quotient="+quotient);
		return quotient;
	}
	
	public static TableModel getTableModel(List<Band> bandList) {
		Object[] objectArray = getObjectArray(bandList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	public static Object[] getObjectArray(List<Band> bandList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (bandList != null) {
			if (bandList.size() > 0) {
				for (int i = 0; i < bandList.size(); i++) {
					Band e = bandList.get(i);
					if (e instanceof Band) {
						Band band = (Band) e;
						if (i == 0) {
							columnArray = Table.getColumnNames(3).toArray();
							dataMatrix = new Object[bandList.size() + 1][3];
							dataMatrix[i][0] = "latitude";
							dataMatrix[i][1] = "dimension";
							dataMatrix[i][2] = "mean";
						}
						dataMatrix[i + 1][0] = band.latitude;
						dataMatrix[i + 1][1] = band.tileList.get(0).dimension;
						dataMatrix[i + 1][2] = band.value;
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}
}
