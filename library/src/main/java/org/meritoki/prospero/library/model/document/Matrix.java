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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Matrix {

	static Logger logger = LogManager.getLogger(Matrix.class.getName());

	@JsonIgnore
	public double threshold = 16;

	@JsonProperty
	public Script script = new Script();

//	@JsonProperty 
//	public Position position = null; //new Position(0,0,1024, 512);
	
	@JsonIgnore
	public int width = 128;
	
	@JsonIgnore
	public int height = 64;

	@JsonIgnore
	public List<Shape> shapeList;

	public Matrix(List<Shape> shapeList, Script script, double threshold) {
		this.shapeList = shapeList;
		this.script = script;
		this.threshold = threshold;
	}

//	@JsonIgnore
//	public void setScale(double scale) {
//		this.position.setScale(scale);
//	}
	
//	@JsonIgnore
//	public Position getPosition() {
//		List<ArrayList<Shape>> rowList = this.getTableRowList();
//		double absoluteHeight = rowList.size() * height;
//		double absoluteWidth = 0;
//		if(rowList.size()>0) 
//			absoluteWidth = rowList.get(0).size() * width;
//		return new Position(0,0,(int)absoluteWidth,(int)absoluteHeight);
//	}

	@JsonIgnore
	public List<ArrayList<Shape>> getPageRowList() {
		List<Shape> shapeList = this.shapeList;
		List<ArrayList<Shape>> list = new ArrayList<>();
		if (shapeList != null && shapeList.size() > 0) {
			Shape shape = null;
			boolean flag = true;
			for (int i = 0; i < shapeList.size(); i++) {
				shape = shapeList.get(i);

				for (List<Shape> rowList : list) {
					if (this.isShapeListYInThreshold(rowList, shape)) {
						rowList.add(shape);
						flag = false;
					}
				}
				if (flag) {
					ArrayList<Shape> rowList = new ArrayList<>();
					rowList.add(shape);
					list.add(rowList);
				} else {
					flag = true;
				}
				this.sortShapeMatrix(list);

			}
//			this.print(list);
		}
		if (this.script != null && !this.script.value.equals("")) {
			try {
				this.sortShapeMatrix(list, this.script.value);
			} catch (Exception e) {
				logger.error("Exception " + e.getMessage());
			}
		}
		return list;
	}

	@JsonIgnore
	public List<ArrayList<Shape>> getTableRowList() {
		List<Shape> shapeList = this.shapeList;
		List<ArrayList<Shape>> list = new ArrayList<>();
		if (shapeList != null && shapeList.size() > 0) {
			Shape shape = null;
			boolean flag = true;
			for (int i = 0; i < shapeList.size(); i++) {
				shape = shapeList.get(i);
				if (shape.data.unit.type != UnitType.LANGUAGE) {
					for (List<Shape> rowList : list) {
						if (this.isShapeListYInThreshold(rowList, shape)) {
							rowList.add(shape);
							flag = false;
						}
					}
					if (flag) {
						ArrayList<Shape> rowList = new ArrayList<>();
						rowList.add(shape);
						list.add(rowList);
					} else {
						flag = true;
					}
					this.sortShapeMatrix(list);
				}
			}
//			this.print(list);
		}
		if (this.script != null && !this.script.value.equals("")) {
			try {
				this.sortShapeMatrix(list, this.script.value);
			} catch (Exception e) {
				logger.error("Exception " + e.getMessage());
			}
		}
		return list;
	}
	
	public Shape getShape(int row, int column) {
		List<ArrayList<Shape>> shapeMatrix = this.getTableRowList();
		List<Shape> shapeList = shapeMatrix.get(row);
		Shape shape = null;
		if(shapeList != null && column < shapeList.size()) {
			shape = shapeList.get(column);
		}
		return shape;
	}

	@JsonIgnore
	public List<ArrayList<Shape>> getArchiveRowList() {
		List<Shape> shapeList = this.shapeList;
		List<ArrayList<Shape>> list = new ArrayList<>();
		if (shapeList != null && shapeList.size() > 0) {
			Shape shape = null;
			boolean flag = true;
			for (int i = 0; i < shapeList.size(); i++) {
				shape = shapeList.get(i);
				if (shape.data.unit.type == UnitType.LANGUAGE) {
					for (List<Shape> rowList : list) {
						if (this.isShapeListYInThreshold(rowList, shape)) {
							rowList.add(shape);
							flag = false;
						}
					}
					if (flag) {
						ArrayList<Shape> rowList = new ArrayList<>();
						rowList.add(shape);
						list.add(rowList);
					} else {
						flag = true;
					}
					this.sortShapeMatrix(list);
				}
			}
//			this.print(list);
		}
		if (this.script != null && !this.script.value.equals("")) {
			try {
				this.sortShapeMatrix(list, this.script.value);
			} catch (Exception e) {
				logger.error("Exception " + e.getMessage());
			}
		}
		return list;
	}

	public int getShapeListMax() {
		int max = 0;
		int size = 0;
		for (List<Shape> shapeList : this.getTableRowList()) {
			size = shapeList.size();
			if (size > max) {
				max = size;
			}
		}
		return max;
	}

	@JsonIgnore
	public List<Shape> getShapeList() {
		List<Shape> shapeList = null;
		List<ArrayList<Shape>> shapeMatrix = this.getPageRowList();
		if (shapeMatrix != null) {
			shapeList = new LinkedList<>();
			for (int i = 0; i < shapeMatrix.size(); i++) {
				for (int j = 0; j < shapeMatrix.get(i).size(); j++) {
					shapeList.add(shapeMatrix.get(i).get(j));
				}
			}
		}
		return shapeList;
	}

	@JsonIgnore
	public void print(List<ArrayList<Shape>> list) {
		String string = null;
		if (list != null && list.size() > 0) {
			string = "\n";
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < list.get(i).size(); j++) {
					if (list.get(i).get(j) != null) {
						string += "*";
					}
				}
				if (i < (list.size() - 1)) {
					string += "\n";
				}
			}
		}
//		if (string != null) {
//			logger.debug(string);
//		}
	}

	@JsonIgnore
	public double getShapeListYAverage(List<Shape> shapeList, Shape shape) {
		double average = 0;
		int count = 0;
		double sum = 0;
		for (Shape s : shapeList) {
			sum += s.position.center.y;
			count += 1;
		}
		sum += shape.position.center.y;
		count += 1;
		average = sum / count;
		return average;
	}

	@JsonIgnore
	public boolean isShapeListYInThreshold(List<Shape> shapeList, Shape shape) {
		boolean flag = true;
		double a = 0;
		double average = this.getShapeListYAverage(shapeList, shape);
		a = shape.position.center.y;
		a = Math.abs(average - a);
		if (a > (threshold * shape.position.scale)) {
			flag = false;
		}
		return flag;
	}

	@JsonIgnore
	public void sortShapeMatrix(List<ArrayList<Shape>> list) {
		for (List<Shape> shapeList : list) {
			this.sortRowList(shapeList);
		}
		Collections.sort(list, new Comparator<List<Shape>>() {
			public int compare(List<Shape> ideaVal1, List<Shape> ideaVal2) {
				Double idea1 = ideaVal1.get(0).position.center.y;
				Double idea2 = ideaVal2.get(0).position.center.y;
				return idea1.compareTo(idea2);
			}
		});
	}

	@JsonIgnore
	public void sortRowList(List<Shape> shapeList) {
		Collections.sort(shapeList, new Comparator<Shape>() {
			public int compare(Shape ideaVal1, Shape ideaVal2) {
				Double idea1 = ideaVal1.position.center.x;// pointList.get(0).x;
				Double idea2 = ideaVal2.position.center.x;// pointList.get(0).x;
				return idea1.compareTo(idea2);
			}
		});
	}

	@JsonIgnore
	public boolean columnListContains(List<Shape> shapeList, Shape shape) {
		boolean flag = false;
		for (Shape s : shapeList) {
			if (s.uuid.equals(shape.uuid)) {
				flag = true;
			}
		}
		return flag;
	}

	@JsonIgnore
	public boolean rowListContains(List<List<Shape>> shapeList, Shape shape) {
		boolean flag = false;
		for (List<Shape> s : shapeList) {
			flag = this.columnListContains(s, shape);
			if (flag) {
				break;
			}
		}
		return flag;
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			logger.error("IOException " + ex.getMessage());
		}

		return string;
	}

	/**
	 * COMMAND PAGE(S) ROW,COLUMN:ROW,COLUMN SWAP 0-n 1,2:3,4 | SWAP 0 1,2:3,4
	 * INSERT 0-n 1,2:3,4 | INSERT 0 1,2:3,4 MERGE 0-n 1,1:1,2 | MERGE 0 1,1:1,2
	 * COMMAND PAGE(S) RANGE:RANGE JOIN 0-n 1:1 | JOIN 0-1 1-6:2-7 | JOIN even-odd
	 * RANGE:RANGE COMMAND PAGE(S) COL/ROW RANGE:RANGE JOIN 0-n ROW 1:1 | JOIN 0-1
	 * ROW 1-6:2-7
	 * 
	 * @param value
	 * @throws Exception
	 */
	public void sortShapeMatrix(List<ArrayList<Shape>> shapeMatrix, String value) {
		//logger.info("sortShapeMatrix(shapeMatrix, " + value + ")");
		if (shapeMatrix != null && value != null && !value.equals("null")) {
			value = value.replace("\n", "").replace("\r", "");
			String[] instructions = value.split(";");
			String instruction;
			String[] parameters;
			String a;
			String b;
			for (String i : instructions) {
				if (i != null && !i.equals("null") && !i.contentEquals("")) {
					logger.info(i);
					if (i.contains("SWAP")) {// 0:0 0:0;
						instruction = i.replaceFirst("SWAP", "");
						instruction = instruction.trim();
						parameters = instruction.split(" ");
						a = parameters[0].trim();
						swapShape(shapeMatrix, a);
					} else if (i.contains("INSERT")) {// 0:0 0:0
						instruction = i.replaceFirst("INSERT", "");
						instruction = instruction.trim();
						parameters = instruction.split(" ");
						a = parameters[0].trim();
						insertShape(shapeMatrix, a);
					}
				}
			}
		}
	}

	public void swapShape(List<ArrayList<Shape>> shapeMatrix, String a) {
		logger.info("swapShape(shapeMatrix," + a + ")");
		String[] coordinateArray = a.split(":");
		String[] coordinateZero = coordinateArray[0].split(",");// (x,y)
		String[] coordinateOne = coordinateArray[1].split(",");// (i,j)
		int x = Integer.parseInt(coordinateZero[0]);
		int y = Integer.parseInt(coordinateZero[1]);
		int i = Integer.parseInt(coordinateOne[0]);
		int j = Integer.parseInt(coordinateOne[1]);
		if (x >= 0 && x < shapeMatrix.size() && i >= 0 && i < shapeMatrix.size()) {
			if (y >= 0 && y < shapeMatrix.get(x).size() && j >= 0 && j < shapeMatrix.get(i).size()) {
				if (x == i) {
					Collections.swap(shapeMatrix.get(x), y, j);
				} else {
					Shape one = shapeMatrix.get(x).remove(y);
					Shape two = shapeMatrix.get(i).remove(j);
					if (y >= 0 && y < shapeMatrix.get(x).size()) {
						shapeMatrix.get(x).set(y, two);
					} else {
						shapeMatrix.get(x).add(two);
					}
					if (j >= 0 && j < shapeMatrix.get(i).size()) {
						shapeMatrix.get(i).set(j, one);
					} else {
						shapeMatrix.get(i).add(one);
					}
				}
			}
		}
	}

	public void insertShape(List<ArrayList<Shape>> shapeMatrix, String a) {
		logger.info("insertShape(shapeMatrix," + a + ")");
		String[] indexArray = a.split(":");
		String[] indexZero = indexArray[0].split(",");// (x,y)
		String[] indexOne = indexArray[1].split(",");// (i,j)
		int x = Integer.parseInt(indexZero[0]);
		int y = Integer.parseInt(indexZero[1]);
		int i = Integer.parseInt(indexOne[0]);
		int j = Integer.parseInt(indexOne[1]);
		Shape one = shapeMatrix.get(x).remove(y);
		shapeMatrix.get(i).add(j, one);
	}

//	public void paint(Graphics graphics) {
//		graphics.setColor(Color.black);
//		List<ArrayList<Shape>> rowList = this.getTableRowList();
//		List<Shape> shapeList;
//		int width = this.width;//(int) (this.position.dimension.width * 0.10);
//		int height = this.height;//(int) (this.position.dimension.height * 0.10);
//		int widthIndex = 0;
//		int heightIndex = 0;
//		graphics.setFont(new Font("default", Font.BOLD, (int) (8 * this.position.scale)));
//		Data data;
//		Shape s;
//		for (int i = 0; i < rowList.size(); i++) {
//			shapeList = rowList.get(i);
//			heightIndex = (int) (i * height);
//			for (int j = 0; j < shapeList.size(); j++) {
//				graphics.setColor(Color.BLACK);
//				widthIndex = (int) (j * width);
//				graphics.drawRect(widthIndex, heightIndex, width, height);
//				s = shapeList.get(j);
//				data = s.data;
//				if (data != null) {
//					Text text = data.text;
//					switch (data.unit.type) {
//					case DATA: {
//						graphics.setColor(Color.BLACK);
//						graphics.drawString("D", widthIndex + (width / 2), heightIndex + (height / 2));
//						break;
//					}
//					case TIME: {
//						graphics.drawString("T", widthIndex + (width / 2), heightIndex + (height / 2));
//						break;
//					}
//					case SPACE: {
//						graphics.drawString("S", widthIndex + (width / 2), heightIndex + (height / 2));
//						break;
//					}
//					case ENERGY: {
//						graphics.drawString("E", widthIndex + (width / 2), heightIndex + (height / 2));
//						break;
//					}
//					}
////					if(text.value == null) {
//						String id = s.uuid.substring(0,7);
//						int z = graphics.getFontMetrics().stringWidth(id);
//						graphics.drawString(id, widthIndex + (width/2) - (z / 2), heightIndex + (height*3/4));
////					} else {
////						String id = text.value;
////						int z = graphics.getFontMetrics().stringWidth(id);
////						graphics.drawString(id, widthIndex + (width/2) - (z / 2), heightIndex + (height*3/4));
////					}
//				}
//			}
//		}
//	}
}
