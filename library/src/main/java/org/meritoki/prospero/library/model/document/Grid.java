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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Grid extends Shape {

	@JsonIgnore
	static Logger logger = LogManager.getLogger(Grid.class.getName());
	@JsonProperty
	public int row = 1;
	@JsonProperty
	public int column = 1;
	@JsonProperty
	public Shape[][] matrix = new Shape[row][column];
	@JsonProperty
	public int index = 0;

	public Grid() {
		super();
//		this.initMatrix();
	}

	public Grid(Shape shape, int row, int column) {
		super(shape, true);
		this.row = row;
		this.column = column;
		this.matrix = new Shape[row][column];
//		this.initMatrix();
		this.updateMatrix();
	}

	public Grid(Grid grid, boolean flag) {
		super((Shape) grid, flag);
		this.row = grid.row;
		this.column = grid.column;
		this.index = grid.index;
		this.matrix = this.copyShapeMatrix(grid.matrix);// this.copyShapeMatrix(grid.matrix);
		this.updateMatrix();
	}
	
	public Shape[][] copyShapeMatrix(Shape[][] matrix) {
		Shape[][] m = new Shape[matrix.length][matrix[0].length];
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[i].length;j++) {
				m[i][j] = new Shape(matrix[i][j],true);
			}
		}
		return m;
	}

	public void updateMatrix() {
		Point point = this.position.point;
		Dimension dimension = this.position.dimension;
		double width = dimension.width / column;
		double height = dimension.height / row;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(matrix[i][j] == null) {
					matrix[i][j] = new Shape();
				}
				matrix[i][j].position.point.x = point.x + (j * width);
				matrix[i][j].position.point.y = point.y + (i * height);
				matrix[i][j].position.dimension.width = width;
				matrix[i][j].position.dimension.height = height;
				matrix[i][j].position.center = new Point(
						matrix[i][j].position.point.x + (matrix[i][j].position.dimension.width / 2),
						matrix[i][j].position.point.y + (matrix[i][j].position.dimension.height / 2));
			}
		}
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setScale(double scale) {
		super.setScale(scale);
		this.updateMatrix();

	}

	public void setRelativeScale(double scale) {
		super.setRelativeScale(scale);
		this.updateMatrix();
	}

	public void setMargin(double margin) {
		super.setMargin(margin);
		this.updateMatrix();
	}

	public void setOffset(double offset) {
		super.setOffset(offset);
		this.updateMatrix();
	}

	@JsonIgnore
	public boolean setShape(String uuid) {
		logger.info("setShape(" + uuid + ")");
		boolean flag = false;
		List<Shape> shapeList = this.getShapeList();
		Shape shape = null;
		for (int i = 0; i < shapeList.size(); i++) {
			shape = shapeList.get(i);
			if (shape.uuid.equals(uuid)) {
				flag = true;
				this.index = i;
				break;
			}
		}
		return flag;
	}

	@JsonIgnore
	public boolean setShape(Point point) {
		boolean flag = false;
		List<Shape> shapeList = this.getShapeList();
		Shape shape = null;
		for (int i = 0; i < shapeList.size(); i++) {
			shape = shapeList.get(i);
			if (shape.contains(point)) {
				flag = true;
				this.index = i;
				break;
			}
		}
//		logger.info("setShape(" + point + ") flag=" + flag);
		return flag;
	}

	@JsonIgnore
	public Shape getShape() {
		Shape shape = null;
		List<Shape> shapeList = this.getShapeList();
		if (this.index >= 0 && this.index < shapeList.size()) {
			shape = shapeList.get(this.index);
		}
		return shape;
	}

	public List<Shape> getShapeList() {
		List<Shape> shapeList = new ArrayList<Shape>();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				Shape s = matrix[i][j];
				shapeList.add(s);
			}
		}
		return shapeList;
	}
}
//public void printMatrix() {
////matrix = new Shape[row][column];
//System.out.println("printMatrix()");
//for (int i = 0; i < matrix.length; i++) {
//	for (int j = 0; j < matrix[i].length; j++) {
//		System.out.println(matrix[i][j]);
//	}
//}
//}
//public void initMatrix() {
//matrix = new Shape[row][column];
//for(int i=0;i<matrix.length;i++) {
//	for(int j=0;j<matrix[i].length;j++) {
//		matrix[i][j] = new Shape();
//	}
//}
//}
//public Shape[][] copyShapeMatrix(Shape[][] matrix) {
//Shape[][] m = new Shape[matrix.length][matrix[0].length];
//for(int i=0;i<matrix.length;i++) {
//	for(int j=0;j<matrix[i].length;j++) {
//		m[i][j] = new Shape(matrix[i][j],true);
//	}
//}
//return m;
//}
