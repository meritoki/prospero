/*
 * Copyright 2020 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.library.model.unit;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Table {
	private static Logger logger = LogManager.getLogger(Table.class.getName());
	public String name;
	public TableModel tableModel;
	
	public Table() {
	}
	
	public Table(String name, TableModel tableModel) {
		this.name = name;
		this.tableModel = tableModel;
	}

	public static void printDataMatrix(Object[][] matrix) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[i].length;j++) {
				sb.append(matrix[i][j]+",");
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
	
	public static List<String> getColumnNames(int n) {
		logger.debug("getColumnNames("+n+")");
	    List<String> result = new ArrayList<String>();
	    String alphabets[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	    StringBuilder sb = new StringBuilder();
	    for(int j = 0; j < n; j++){
	        int index = j/26;   
	            char ch = (char) (j % 26 + 'A');               
	          sb.append(ch);
	          String item = "";
	          if(index > 0) {
	              item += alphabets[index-1];
	          }
	          item += alphabets[j % 26];
	          result.add(item);
	    }
	    sb.reverse();
	    return result;
	}
	
    public static Object[][] getTableData (TableModel dtm) {
        
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Object[][] tableData = new Object[nRow][nCol];
        for (int i = 0 ; i < nRow ; i++)
            for (int j = 0 ; j < nCol ; j++)
                tableData[i][j] = dtm.getValueAt(i,j);
        return tableData;
    }
}
//public static DefaultTableModel getDefaultTableMode(List<Regression> regressionList) {
//System.out.println("getDefaultTableModel("+regressionList+")");
//Object[] objectArray = getObjectArray(regressionList);
//return new javax.swing.table.DefaultTableModel((Object[][])objectArray[1], (Object[])objectArray[0]);
//}
//
//public static Object[] getObjectArray(List<Index> regressionList) {
//
//}
//
//public static Object[] getObjectArray(List<Regression> regressionList) {
//Object[] objectArray = new Object[2];
//Object[] columnArray = new Object[0];
//Object[][] dataMatrix = null;
//if (regressionList != null) {
//	if (regressionList.size() > 0) {
////		Map map = regressionList.get(0).map;
////		columnArray = getColumnNames(map.size()+2).toArray();
////		dataMatrix = new Object[regressionList.size()+1][map.size()+2];
//		for(int i=0;i<regressionList.size();i++) {
//			Regression r = regressionList.get(i);
//			if(i==0) {
//				columnArray = getColumnNames(r.map.size()+2).toArray();
//				dataMatrix = new Object[regressionList.size()+1][r.map.size()+2];
//				dataMatrix[i][0] = "startCalendar";
//				dataMatrix[i][1] = "endCalendar";
//				int index = 2;
//				for(String key: r.map.keySet()) {
//					dataMatrix[i][index] = key;
//					index++;
//				}
//			}
//			dataMatrix[i+1][0] = r.startCalendar.getTime();
//			dataMatrix[i+1][1] = r.endCalendar.getTime();
//			int index = 2;
//			for(String key: r.map.keySet()) {
//				dataMatrix[i+1][index] = r.map.get(key);
//				index++;
//			}
//		}
//	}
//	objectArray[0] = columnArray;
//	objectArray[1] = dataMatrix;
//	printDataMatrix(dataMatrix);
//}
//return objectArray;
//}
//shapeList = rowList.get(0);
//columnArray = new Object[shapeList.size()];
//for (int j = 0; j < shapeList.size(); j++) {
//	shape = shapeList.get(j);
//	data = shape.data;
//	value = data.text.value;
//	if(value != null) {
//		columnArray[j] = value;
//	} else {
//		value = data.unit.value;
//		if(value != null) {
//			columnArray[j] = value;
//		} else {
//			value = data.unit.type.toString();
//			columnArray[j] = value;
//		}
//	}
//}
//dataMatrix = new Object[rowList.size()-1][matrix.getShapeListMax()];
//for (int i = 1; i < rowList.size(); i++) {
//	shapeList = rowList.get(i);
//	for (int j = 0; j < shapeList.size(); j++) {
//		shape = shapeList.get(j);
//		data = shape.data;
//		value = data.text.value;
//		dataMatrix[i-1][j] = value;
//	}
//}
