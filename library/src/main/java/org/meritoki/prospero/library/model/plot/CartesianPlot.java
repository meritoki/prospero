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
package org.meritoki.prospero.library.model.plot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.List;

import org.meritoki.prospero.library.model.color.Chroma;
import org.meritoki.prospero.library.model.unit.Point;

public class CartesianPlot extends Plot {

//	public final double DEFAULT_SCALE = 1;
//	public int panelWidth;
//	public int panelHeight;
//	public double plotWidth = 512;
//	public double plotHeight = 256;
//	public double scale;
//	public String title = "Plot";
//	public String xLabel = "X";
//	public String yLabel = "Y";
	public double yMin = 0;
	public double yMax = 1000;
	public double xMin = 0;
	public double xMax = 1000;
//	public double yInterval = 100;
//	public double xInterval = 100;
//	public int padding = 10;
//	public int horizon = 450;
//	public double yIncrement;
//	public double xIncrement;
//	public double step = 10;
//	public String fontName = "SanSerif";
//	public int titleFontStyle = Font.BOLD;
//	public int barFontStyle = Font.PLAIN;
//	public int yIncrementFontStyle = Font.PLAIN;
//	public int xLabelFontStyle = Font.BOLD;
//	public int yLabelFontStyle = Font.BOLD;
//	public int titleFontSize = 18;
//	public int barFontSize = 16;
//	public int yIncrementFontSize = 16;
//	public int xLabelFontSize = 16;
//	public int yLabelFontSize = 16;
//	public Color barColor = Color.GRAY;
	public List<List<Point>> blackPointMatrix;
	public List<List<Point>> colorPointMatrix;
//	public final boolean print = false;
//	public final boolean detail = false;
//	public Chroma chroma = new Chroma();

	public CartesianPlot() {
		super();
	}

	public CartesianPlot(List<List<Point>> blackPointMatrix, List<List<Point>> colorPointMatrix) {
		super();
		this.blackPointMatrix = blackPointMatrix;
		this.colorPointMatrix = colorPointMatrix;
		this.initYMin();
		this.initYMax();
		this.initXMin();
		this.initXMax();
	}

	public void initYMin() {
		double min = Double.MAX_VALUE;
		for (List<Point> pointList : this.blackPointMatrix) {
			for (Point t : pointList) {
				if (t.y < min) {
					min = t.y;
				}
			}
		}
		this.yMin = min;
		if (print)
			System.out.println("initYMax() this.yMin=" + this.yMin);
	}

	public void initXMin() {
		double min = Double.MAX_VALUE;
		for (List<Point> pointList : this.blackPointMatrix) {
			for (Point t : pointList) {
				if (t.x < min) {
					min = t.x;
				}
			}
		}
		this.xMin = min;
		if (print)
			System.out.println("initXMin() this.xMin=" + this.xMin);
	}

	public void initYMax() {
		double max = Double.MIN_VALUE;
		for (List<Point> pointList : this.blackPointMatrix) {
			for (Point t : pointList) {
				if (t.y > max) {
					max = t.y;
				}
			}
		}
		this.yMax = max;
		if (print)
			System.out.println("initYMax() this.yMax=" + this.yMax);
	}

	public void initXMax() {
		double max = Double.MIN_VALUE;
		for (List<Point> pointList : this.blackPointMatrix) {
			for (Point t : pointList) {
				if (t.x > max) {
					max = t.x;
				}
			}
		}
		this.xMax = max;
		if (print)
			System.out.println("initXMax() this.xMax=" + this.xMax);
	}

	@Override
	public void paint(Graphics g, Image image) {
		Graphics graphics = image.getGraphics();
		graphics = image.getGraphics();
		Graphics2D g2d = (Graphics2D) graphics;
		graphics.setColor(Color.WHITE);
		this.plotWidth = this.panelWidth-256;
		this.plotHeight = this.panelHeight-128;
		this.horizon = (int)this.plotHeight;
		graphics.fillRect(0, 0, this.panelWidth, this.panelHeight);
		Point panelCenter = new Point(this.panelWidth / 2.0, this.panelHeight / 2.0);
		Point plotCenter = new Point(this.plotWidth / 2.0, this.plotHeight / 2.0);
		int verticalCenter = (int) plotCenter.x;// (this.plotWidth / 2);
		int horizontalCenter = (int) -panelCenter.y;/// -(this.panelHeight / 2);//y-axis shift
		if (print)
			graphics.drawLine((int) panelCenter.x, 0, (int) panelCenter.x, 1000);
		if (print)
			graphics.drawLine(0, (int) panelCenter.y, 2000, (int) panelCenter.y);
		graphics.translate((int) (panelCenter.x - plotCenter.x), (int) (panelCenter.y - plotCenter.y));
		graphics.setColor(Color.black);
		graphics.drawRect(0, 0, (int) (plotWidth), (int) (plotHeight));
		graphics.setFont(new Font(this.fontName, this.titleFontStyle, this.titleFontSize));
		this.yInterval = (this.yMax - this.yMin) / this.step;// works
		this.xInterval = (this.xMax - this.xMin) / this.step;//
		this.yIncrement = this.plotHeight / (this.yMax - this.yMin);
		this.xIncrement = this.plotWidth / (this.xMax - this.xMin);
		this.horizon = (int) this.plotHeight;
		if (print)
			System.out.println("paint(...) this.yInterval=" + this.yInterval);
		if (print)
			System.out.println("paint(...) this.xInterval=" + this.xInterval);
		if (print)
			System.out.println("paint(...) this.yIncrement=" + this.yIncrement);
		if (print)
			System.out.println("paint(...) this.xIncrement=" + this.xIncrement);
		if (print)
			System.out.println("paint(...) this.horizon=" + this.horizon);
		if (print)
			System.out.println("paint(...) xMin=" + xMin);
		if (print)
			System.out.println("paint(...) yMin=" + yMin);
		graphics.setColor(Color.magenta);
		if (print && detail)
			graphics.drawLine(verticalCenter, 0, verticalCenter, (int) plotHeight);
		if (print && detail)
			graphics.drawLine(0, horizontalCenter, (int) plotWidth, horizontalCenter);
		if (print && detail)
			graphics.drawLine(0, this.horizon, (int) plotWidth, this.horizon);
		if (Double.isFinite(yIncrement) && Double.isFinite(xIncrement)) {
			DecimalFormat df = new DecimalFormat("#.#");
			graphics.setFont(new Font(this.fontName, this.yIncrementFontStyle, this.yIncrementFontSize));
			graphics.setColor(Color.GRAY);
			for (double i = (yMax - yMin); i >= 0; i -= yInterval) {
				double increment = (yMax) - i;
				increment = Double.parseDouble(df.format(increment));
				if (increment == 0.0) {
					graphics.drawLine(0, (int) (i * yIncrement), (int) this.plotWidth, (int) (i * yIncrement));
				}
			}
			for (double i = (xMax - xMin); i >= 0; i -= xInterval) {
				double increment = (xMin) + i;
				increment = Double.parseDouble(df.format(increment));
				if (increment == 0.0) {
					graphics.drawLine((int) (i * xIncrement), 0, (int) (i * xIncrement), (int) this.plotHeight);
				}
			}
			graphics.setColor(Color.BLACK);
			for (double i = (yMax - yMin); i >= 0; i -= yInterval) {
				double increment = (yMax) - i;
				String label = df.format(increment) + "";
				graphics.drawLine(0, (int) (i * yIncrement), (int) (3), (int) (i * yIncrement));
				int z = graphics.getFontMetrics().stringWidth(label);
				graphics.drawString(label, (int) ((-5 - z)), (int) (((i * yIncrement) + 5)));
			}
			for (double i = (xMax - xMin); i >= 0; i -= xInterval) {
				double increment = xMin + i;
				String label = df.format(increment) + "";
				graphics.drawLine((int) (i * xIncrement), horizon, (int) (i * xIncrement), horizon - 3);
				int z = graphics.getFontMetrics().stringWidth(label);
				graphics.drawString(label, (int) ((i * xIncrement) - (z / 2)), (int) (horizon + 16));
			}
			Point point;
			graphics.setFont(new Font(this.fontName, this.barFontStyle, this.barFontSize));
			// Black
			if (this.blackPointMatrix != null) {
				graphics.setColor(Color.BLACK);
				for (int j = 0; j < this.blackPointMatrix.size(); j++) {
					List<Point> pointList = this.blackPointMatrix.get(j);
					for (int i = 0; i < pointList.size(); i++) {
						point = pointList.get(i);
						int x1 = (int) (this.plotWidth - ((xMax - point.x) * xIncrement));
						int y1 = (int) (horizon - (-(yMin - point.y) * yIncrement));
						graphics.drawOval(x1, y1, 2, 2);
					}
				}
			}
			// Color
			if (this.colorPointMatrix != null) {
				for (int j = 0; j < this.colorPointMatrix.size(); j++) {
					List<Point> pointList = this.colorPointMatrix.get(j);
					graphics.setColor(chroma.getColor(j, 0, this.blackPointMatrix.size()));
					for (int i = 0; i < pointList.size(); i++) {
						point = pointList.get(i);
						int x1 = (int) (this.plotWidth - ((xMax - point.x) * xIncrement));
						int y1 = (int) (horizon - (-(yMin - point.y) * yIncrement));
						graphics.drawOval(x1, y1, 2, 2);
					}
				}
			}
		}
		// Title And Labels
		graphics.setColor(Color.BLACK);
		int titleWidth = graphics.getFontMetrics().stringWidth(this.title);
		int titleStringHalf = titleWidth / 2;
		int titleStartPosition = verticalCenter - titleStringHalf;
		graphics.drawString(this.title, (int) (titleStartPosition), (int) (-64));
		graphics.setFont(new Font(this.fontName, this.xLabelFontStyle, this.xLabelFontSize));
		int xLabelWidth = graphics.getFontMetrics().stringWidth(this.xLabel);
		int xLabelHalf = xLabelWidth / 2;
		int xLabelStartPosition = verticalCenter - xLabelHalf;
		graphics.drawString(this.xLabel, (int) (xLabelStartPosition), (int) ((horizon + 64)));
		AffineTransform at = new AffineTransform();
		at.setToRotation(Math.toRadians(-90));
		g2d.setTransform(at);
		// x becomes y position, y becomes x position
		graphics.setFont(new Font(this.fontName, this.yLabelFontStyle, this.yLabelFontSize));
		int yLabelWidth = graphics.getFontMetrics().stringWidth(this.yLabel);
		int yLabelStartPosition = horizontalCenter - yLabelWidth / 2;
		g2d.drawString(this.yLabel, (int) (yLabelStartPosition), (int) (((panelWidth / 2) - (plotWidth / 2) - (64))));
		image = image.getScaledInstance((int) (this.panelWidth * this.scale), (int) (this.panelHeight * this.scale),
				Image.SCALE_DEFAULT);
		int x = (this.panelWidth - image.getWidth(null)) / 2;
		int y = (this.panelHeight - image.getHeight(null)) / 2;
		g.drawImage(image, x, y, null);
	}
}
//for(List<Point> pointList: this.pointMatrix) {
//Point previousPoint = null;
//for (int i = 0; i < pointList.size(); i++) {
//	point = pointList.get(i);
//	int x2 = (int)((point.x <= this.xMax) ? (int) (point.x * xIncrement) : (int) (this.xMax * xIncrement));
//	int y2 = (point.y <= this.yMax) ? (int) (point.y * yIncrement+plotCenter.y) : (int) (this.yMax * yIncrement+plotCenter.y);
//	y2 = (horizon - y2*scale);
//	int x1 = x2;
//	int y1 = y2;
//	if(previousPoint != null) {
//		x1 = (int)((previousPoint.x <= this.xMax) ? (int) (previousPoint.x * xIncrement) : (int) (this.xMax * xIncrement));
//		y1 = (previousPoint.y <= this.yMax) ? (int) (previousPoint.y * yIncrement+plotCenter.y) : (int) (this.yMax * yIncrement+plotCenter.y);
//		y1 = (horizon - y1*scale);
//	}
//	graphics.drawLine(x1, y1, x2, y2);
//	previousPoint = point;
//}
//}
//for (double i = yMax; i >= yMin; i -= yInterval) {
//double increment = yMax - i;
//System.out.println("yMax="+yMax+", yMin="+yMin+", yIncrement="+increment+", i="+i);
//String label = df.format(increment) + "";
//graphics.drawLine(0, (int) (i * yIncrement), (int) (3), (int) (i * yIncrement));
////graphics.drawLine(0, (int) (i), (int) (3), (int) (i));
////graphics.drawLine(0, (int) (yIncrement), (int) (3), (int) (yIncrement));
//int z = graphics.getFontMetrics().stringWidth(label);
//graphics.drawString(label, (int) ((-5 - z)), (int) (((i * yIncrement) + 5)));
//}
//for (double i = xMin; i <= xMax; i += xInterval) {
//double increment = (Math.abs(xMin-i));
//System.out.println("xIncrement="+increment);
//String label =  df.format(increment)+ "";
//graphics.drawLine((int) (i * xIncrement), horizon, (int) (i * xIncrement), horizon-3);
//int z = graphics.getFontMetrics().stringWidth(label);
//graphics.drawString(label, (int)((i * xIncrement) - (z/2)), (int) (horizon+16));
//}
//int x2 = (int)((point.x <= this.xMax) ? (int) (point.x * xIncrement) : (int) (this.xMax * xIncrement));
//int y2 = (point.y <= this.yMax) ? (int) (point.y * yIncrement) : (int) (this.yMax * yIncrement);
//y2 = (horizon - y2*scale);
//graphics.drawOval(x, y, 2, 2);
//for (int i = 0; i < size; i++) {
//point = this.pointList.get(i);
//h = (point.y <= this.yMax) ? (int) (point.y * yIncrement) : (int) (this.yMax * yIncrement);
//y = (horizon - h*scale);
//w = (point.x <= this.xMax) ? (int) (point.x * xIncrement) : (int) (this.xMax * xIncrement);
//x = (int)(w);
//graphics.drawOval(x, y, 2, 2);
//}
//graphics.setColor(Color.blue);
//for (int i = 0; i < this.curveList.size(); i++) {
//point = this.curveList.get(i);
//h = (point.y <= this.yMax) ? (int) (point.y * yIncrement) : (int) (this.yMax * yIncrement);
//y = (horizon - h*scale);
//w = (point.x <= this.xMax) ? (int) (point.x * xIncrement) : (int) (this.xMax * xIncrement);
//x = (int)(w);
//graphics.drawOval(x, y, 2, 2);
//}