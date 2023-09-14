/*
 * Copyright 2021 Joaquin Osvaldo Rodriguez
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
package org.meritoki.prospero.library.model.document;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Table {
	private static Logger logger = LogManager.getLogger(Table.class.getName());
	public Matrix matrix;

	public Table(Matrix matrix) {
		this.matrix = matrix;
	}
	
	public DefaultTableModel getDefaultTableMode() {
		Object[] objectArray = this.getObjectArray();
		return new javax.swing.table.DefaultTableModel((Object[][])objectArray[1], (Object[])objectArray[0]);
	}
	
	public Object[][] getSpreadsheetObjectArray() {
		Object[][] dataMatrix = null;
		if (matrix != null) {
			List<ArrayList<Shape>> rowList = matrix.getTableRowList();
			List<Shape> shapeList;
			Shape shape;
			Data data;
			String value;
			if (rowList.size() > 0) {
				dataMatrix = new Object[rowList.size()][matrix.getShapeListMax()];
				for (int i = 0; i < rowList.size(); i++) {
					shapeList = rowList.get(i);
					for (int j = 0; j < shapeList.size(); j++) {
						shape = shapeList.get(j);
						data = shape.data;
						value = data.text.value;
						dataMatrix[i][j] = value;
					}
				}
			}
		}
		return dataMatrix;
	}

	public Object[] getObjectArray() {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		if (matrix != null) {
			List<ArrayList<Shape>> rowList = matrix.getTableRowList();
			List<Shape> shapeList;
			Shape shape;
			Data data;
			String value;
			if (rowList.size() > 0) {
				columnArray = this.getColumnNames(matrix.getShapeListMax()).toArray();
				dataMatrix = new Object[rowList.size()][matrix.getShapeListMax()];
				for (int i = 0; i < rowList.size(); i++) {
					shapeList = rowList.get(i);
					for (int j = 0; j < shapeList.size(); j++) {
						shape = shapeList.get(j);
						data = shape.data;
						value = data.text.value;
						dataMatrix[i][j] = value;
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}
	
	public List<String> getColumnNames(int n) {
//		logger.info("getColumnNames("+n+")");
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
}
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
