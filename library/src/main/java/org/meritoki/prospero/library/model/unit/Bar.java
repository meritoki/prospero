package org.meritoki.prospero.library.model.unit;

import java.util.List;

import javax.swing.table.TableModel;

public class Bar {
	
	
	public double value;
	public String label;
	
	public Bar(double value, String label) {
		this.value = value;
		this.label = label;
	}
	
	public static Object[] getObjectArray(List<Bar> barList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		if (barList != null) {
			if (barList.size() > 0) {
				for(int i=0;i<barList.size();i++) {
					Bar r = barList.get(i);
					if(i==0) {
						columnArray = Table.getColumnNames(2).toArray();
						dataMatrix = new Object[barList.size()+1][2];
						dataMatrix[i][0] = "label";
						dataMatrix[i][1] = "value";
					}
					dataMatrix[i+1][0] = r.label;
					dataMatrix[i+1][1] = r.value;
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}

	
	public static TableModel getTableModel(List<Bar> barList) {
		Object[] objectArray = getObjectArray(barList);
		return new javax.swing.table.DefaultTableModel((Object[][])objectArray[1], (Object[])objectArray[0]);
	}
}
