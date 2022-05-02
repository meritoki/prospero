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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.color.Chroma;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.table.Table;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Label;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Regression;
import org.meritoki.prospero.library.model.unit.Series;
import org.meritoki.prospero.library.model.unit.Window;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TimePlot extends Plot {

	static Logger logger = LogManager.getLogger(TimePlot.class.getName());
	public List<List<Index>> blackIndexMatrix;
	public List<List<Index>> colorIndexMatrix;
	public Calendar startCalendar;
	public Calendar endCalendar;
	public int calendarIncrement = Calendar.SECOND;

	public TimePlot() {
		super();
	}
	
	public TimePlot(Series series) {
		super();
		this.blackIndexMatrix = new ArrayList<>();
		this.colorIndexMatrix = new ArrayList<>();
		this.blackIndexMatrix.add(series.indexList);
		for(Entry<String,List<Regression>> entry:series.regressionMap.entrySet()) {
			this.colorIndexMatrix.addAll(this.getMatrix(entry.getValue()));
		}
		Calendar[] window = (series.map.get("window") != null)?(Calendar[])series.map.get("window"):null;
		if(window != null && window.length == 2) {
			this.startCalendar = window[0];
			this.endCalendar = window[1];
		} else {
			this.startCalendar = (Calendar)series.map.get("startCalendar");
			this.endCalendar = (Calendar)series.map.get("endCalendar");
		}
		this.initYMin();
		this.initYMax();
		this.initXMin();
		this.initXMax();
		double[] range = (series.map.get("range") != null)?(double[])series.map.get("range"):null;
		if(range != null && range.length == 2) {
			this.setYMin(range[0]);
			this.setYMax(range[1]);
		}
		this.chroma.initRainbow();
		this.scale = DEFAULT_SCALE;
		this.setTitle(series.getTitle());
		this.setData(series.getData());
		this.setXLabel("Time");
		this.tableList.add(new Table("("+((String)series.map.get("region")).replace(",", "_").replace(":", ")-(")+")", Index.getTableModel(series.indexList)));
	}

	public TimePlot(Calendar startCalendar, Calendar endCalendar, List<List<Index>> blackIndexMatrix,
			List<List<Index>> colorIndexMatrix) {
		super();

		this.blackIndexMatrix = blackIndexMatrix;
		this.colorIndexMatrix = colorIndexMatrix;
		this.startCalendar = startCalendar;
		this.endCalendar = endCalendar;
		logger.info("startCalendar: "+this.startCalendar);
		logger.info("endCalendar: "+this.endCalendar);
		this.initYMin();
		this.initYMax();
		this.initXMin();
		this.initXMax();
		this.chroma.initRainbow();
		this.scale = DEFAULT_SCALE;
	}
	
	public List<List<Index>> getMatrix(List<Regression> regressionList) {
		List<List<Index>> matrix = new ArrayList<>();
		if (regressionList != null) {
			for (Regression r : regressionList) {
				if (r.getIndexList().size() > 0) {
					matrix.add(r.getIndexList());
				}
			}
		}
		return matrix;
	}

	public void setStartCalendar(Calendar startCalendar) {
		this.startCalendar = startCalendar;
	}

	public void setEndCalendar(Calendar endCalendar) {
		this.endCalendar = endCalendar;
	}

	public long getSeconds() {
		Date d1 = this.startCalendar.getTime();
		Date d2 = this.endCalendar.getTime();
		long seconds = (d2.getTime() - d1.getTime()) / 1000;
		return seconds;
	}

	public long getSeconds(Calendar calendar) {
		Date d1 = this.startCalendar.getTime();
		Date d2 = calendar.getTime();
		long seconds = (d2.getTime() - d1.getTime()) / 1000;
		return seconds;
	}

	public int getInterval(long seconds) {
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		long months = days / 12;
		double years = days / 365.25;
		double decades = years / 10;
		double century = decades / 10;
		double millenia = century / 10;
		if (millenia > 1) {
			return -2;
		} else if (century > 1) {
			return -1;
		} else if (decades > 1) {
			return 0;
		} else if (years > 1) {
			return 1;
		} else if (months > 1) {
			return 2;
		} else if (days > 1) {
			return 3;
		} else if (hours > 1) {
			return 4;
		} else {
			return 5;
		}
	}

	public List<Label> getXLabelList(long seconds) {
		List<Label> labelList = new ArrayList<>();
		int interval = this.getInterval(seconds);
		switch (interval) {
		case -2: {
			Calendar calendar = (Calendar) this.startCalendar.clone();
			int startYear = calendar.get(Calendar.YEAR);
			int yearInMillenia = startYear % 1000;// 1996 returns 6
			int difference = 1000 - yearInMillenia;
			calendar.add(Calendar.YEAR, difference);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Label label = null;
			while (calendar.before(this.endCalendar)) {
				label = new Label();
				label.calendar = (Calendar) calendar.clone();
				label.seconds = this.getSeconds(calendar);
				label.interval = interval;
				calendar.add(Calendar.YEAR, 1000);
				labelList.add(label);
			}
			break;
		}
		case -1: {
			Calendar calendar = (Calendar) this.startCalendar.clone();
			int startYear = calendar.get(Calendar.YEAR);
			int yearInCentury = startYear % 100;// 1996 returns 6
			int difference = 100 - yearInCentury;
			calendar.add(Calendar.YEAR, difference);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Label label = null;
			while (calendar.before(this.endCalendar)) {
				label = new Label();
				label.calendar = (Calendar) calendar.clone();
				label.seconds = this.getSeconds(calendar);
				label.interval = interval;
				calendar.add(Calendar.YEAR, 100);
				labelList.add(label);
			}
			break;
		}
		case 0: {
			Calendar calendar = (Calendar) this.startCalendar.clone();
			int startYear = calendar.get(Calendar.YEAR);
			int yearInDecade = startYear % 10;// 1996 returns 6
			int difference = 10 - yearInDecade;
			calendar.add(Calendar.YEAR, difference);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Label label = null;
			while (calendar.before(this.endCalendar)) {
				label = new Label();
				label.calendar = (Calendar) calendar.clone();
				label.seconds = this.getSeconds(calendar);
				label.interval = interval;
				calendar.add(Calendar.YEAR, 10);
				labelList.add(label);
			}
			break;
		}
		case 1: {
			Calendar calendar = (Calendar) this.startCalendar.clone();
			Label label = null;
			while (calendar.before(this.endCalendar)) {
				label = new Label();
				label.calendar = (Calendar) calendar.clone();
				label.seconds = this.getSeconds(calendar);
				label.interval = interval;
				calendar.add(Calendar.YEAR, 1);
				labelList.add(label);
			}
			break;
		}
		case 2: {
			Calendar calendar = (Calendar) this.startCalendar.clone();
			Label label = null;
			while (calendar.before(this.endCalendar)) {
				label = new Label();
				label.calendar = (Calendar) calendar.clone();
				label.seconds = this.getSeconds(calendar);
				label.interval = interval;
				calendar.add(Calendar.MONTH, 1);
				labelList.add(label);
			}
			break;
		}
		}
		return labelList;
	}

	public void initYMin() {
		Double a = this.getYMin(this.blackIndexMatrix);
		Double b = this.getYMin(this.colorIndexMatrix);
		if(a != null && b != null) {
			this.yMin = (a < b) ? a : b;
		} else if(a == null && b != null) {
			this.yMax = b;
		} else if(a != null && b == null) {
			this.yMax = a;
		} else {
			this.yMax = 0;
		}
		if (print)
			System.out.println("initYMax() this.yMin=" + this.yMin);
	}

	public void initXMin() {
		this.xMin = 0;
		if (print)
			System.out.println("initXMin() this.xMin=" + this.xMin);
	}

	/**
	 * Here was found a defect that when fixed solved a lot of problems with plots.
	 * Defect was that it did not detect Max correctly, because it did not know how
	 * to handle max of a negative number. The initial max value was set to
	 * Double.MIN_VALUE which makes sense if we are dealing with positive numbers.
	 * However, what happens when the Max is a negative number? The max can never
	 * compy with the expression Max > Double.MIN_VALUE, because the min value will
	 * always be greater. To solve this we must first set the min value to negative
	 * sign. Then we have to choose a value the is the smallest negative number we
	 * can define. This is -Double.MAX_VALUE. With this change in place, now the
	 * yDifference is calculated correctly, because it has the correct yMax and
	 * yMin.
	 */
	public void initYMax() {
		Double a = this.getYMax(this.blackIndexMatrix);
		Double b = this.getYMax(this.colorIndexMatrix);
		if(a != null && b != null) {
			this.yMax = (a > b) ? a : b;
		} else if(a == null && b != null) {
			this.yMax = b;
		} else if(a != null && b == null) {
			this.yMax = a;
		} else {
			this.yMax = 0;
		}
		if (print)
			System.out.println("initYMax() this.yMax=" + this.yMax);
	}

	public Double getYMax(List<List<Index>> matrix) {
		Double max = null;
		if (matrix != null && matrix.size() > 0) {
			max = -Double.MAX_VALUE;
			for (List<Index> pointList : matrix) {
				for (Index t : pointList) {
					if (t.value > max) {
						max = t.value;
					}
				}
			}
		}
		return max;
	}

	public Double getYMin(List<List<Index>> matrix) {
		Double min = Double.MAX_VALUE;// The biggest value we can think of that an index value has to beat.
		if (matrix != null && matrix.size() > 0) {
			min = Double.MAX_VALUE;
			for (List<Index> pointList : matrix) {// this.blackIndexMatrix) {
				for (Index t : pointList) {
					if (t.value < min) {
						min = t.value;
					}
				}
			}
		}
		return min;
	}

	public void initXMax() {
		this.xMax = this.getSeconds();
		if (print)
			System.out.println("initXMax() this.xMax=" + this.xMax);
	}

	public double scaleIndex(double value) {
//		System.out.println("scaleIndex("+value+")");
		double scaleValue;
//		if (Math.abs(this.yMin) > Math.abs(this.yMax)) {
//			// System.out.println(index.value);
//			double difference = Math.abs((yMax) + Math.abs(value));
//			// System.out.println("Difference: "+difference);
//			scaleValue = (difference) * yIncrement;
//		} else if (this.yMin < 0 && this.yMax > 0) {
//			double difference =  yMin - value;
//			scaleValue = -(difference) * yIncrement;
//		} else {
//			double difference = yMin - value;
//			scaleValue = -(difference) * yIncrement;
//		}
		double difference = yMin - value;
		scaleValue = -(difference) * yIncrement;
		return scaleValue;
	}

	public double getYDifference() {
		double yDifference = this.yMax - this.yMin;
//		if(this.yMin > 0 && this.yMax > 0) {
//			yDifference = this.yMax - this.yMin;
//		} else if(this.yMin < 0 && this.yMax > 0) {
//			yDifference = this.yMax - this.yMin;
//		} else if(this.yMin < 0 && this.yMax < 0) {
//			yDifference = -((-this.yMax)-(-this.yMin));
//		}

		return yDifference;
	}


//	public void paint(Graphics gX, Image image) {
	@Override
	public Image getImage(Image image) {
		Graphics graphics = (image != null) ? image.getGraphics() : null;
		if (graphics != null) {
			Graphics2D g2d = (Graphics2D) graphics;
			graphics.setColor(Color.WHITE);
			this.plotWidth = this.panelWidth - 256;
			this.plotHeight = this.panelHeight - 128;
			this.horizon = (int) this.plotHeight;
			graphics.fillRect(0, 0, this.panelWidth, this.panelHeight);
			Point panelCenter = new Point(this.panelWidth / 2.0, this.panelHeight / 2.0);
			Point plotCenter = new Point(this.plotWidth / 2.0, this.plotHeight / 2.0);
			int verticalCenter = (int) plotCenter.x;// (this.plotWidth / 2);
			int horizontalCenter = (int) -panelCenter.y;/// -(this.panelHeight / 2);//y-axis shift
			if (print)
				graphics.setColor(Color.GRAY);
			graphics.drawLine((int) panelCenter.x, 0, (int) panelCenter.x, 1000);
			if (print)
				graphics.setColor(Color.GRAY);
			graphics.drawLine(0, (int) panelCenter.y, 2000, (int) panelCenter.y);
			graphics.translate((int) (panelCenter.x - plotCenter.x), (int) (panelCenter.y - plotCenter.y));
			graphics.setColor(Color.black);
			graphics.drawRect(0, 0, (int) (plotWidth), (int) (plotHeight));
			graphics.setFont(new Font(this.fontName, this.titleFontStyle, this.titleFontSize));
			this.yDifference = this.getYDifference();
			this.xDifference = (this.xMax - this.xMin);
			this.yInterval = this.yDifference / this.step;// works
			this.xInterval = this.xDifference / this.xDifference;// 1000 * 3600 * 24;//
			this.yIncrement = this.plotHeight / this.yDifference;
			this.xIncrement = this.plotWidth / this.xDifference;
			this.horizon = (int) this.plotHeight;
			if (print)
				logger.info("paint(...) this.yDifference=" + this.yDifference);
			if (print)
				logger.info("paint(...) this.yInterval=" + this.yInterval);
			if (print)
				logger.info("paint(...) this.xInterval=" + this.xInterval);
			if (print)
				logger.info("paint(...) this.yIncrement=" + this.yIncrement);
			if (print)
				logger.info("paint(...) this.xIncrement=" + this.xIncrement);
			if (print)
				logger.info("paint(...) this.horizon=" + this.horizon);
			if (print)
				logger.info("paint(...) xMin=" + xMin);
			if (print)
				logger.info("paint(...) yMin=" + yMin);
			graphics.setColor(Color.magenta);
			if (print)
				graphics.drawLine(verticalCenter, 0, verticalCenter, (int) plotHeight);
			if (print)
				graphics.drawLine(0, horizontalCenter, (int) plotWidth, horizontalCenter);
			if (print && detail)
				graphics.drawLine(0, this.horizon, (int) plotWidth, this.horizon);
			if (Double.isFinite(yIncrement) && Double.isFinite(xIncrement)) {
				DecimalFormat df = new DecimalFormat("%3.3E");
				graphics.setFont(new Font(this.fontName, this.yIncrementFontStyle, this.yIncrementFontSize));
				graphics.setColor(Color.BLACK);
				for (int s = 0; s <= this.step; s++) {// double i = this.yDifference; i >= 0; i -= yInterval) {
					double i = this.yDifference - (s * this.yInterval);
					double increment = ((yMax) - i);// returns min value, the first iteration returns yMin
					if (print)
						System.out.println(s + ":" + increment + ":" + i);
					String string = String.format("%3.3E", increment);
					try {
						increment = Double.parseDouble(string);
						if (increment == 0) {
							graphics.drawLine(0, (int) (i * yIncrement), (int) this.plotWidth, (int) (i * yIncrement));
						}
						String label = string;
						graphics.drawLine(0, (int) (i * yIncrement), (int) (3), (int) (i * yIncrement));
						int z = graphics.getFontMetrics().stringWidth(label);
						graphics.drawString(label, (int) ((-5 - z)), (int) (((i * yIncrement) + 5)));
					} catch (Exception e) {
						logger.warn(s + ":" + increment + ":" + i);
					}
				}

				List<Label> xLabelList = this.getXLabelList(xMax);
				for (Label l : xLabelList) {
					String label = l.toString();
					long i = xMin + l.seconds;
					graphics.drawLine((int) (i * xIncrement), horizon, (int) (i * xIncrement), horizon - 3);
					int z = graphics.getFontMetrics().stringWidth(label);
					graphics.drawString(label, (int) ((i * xIncrement) - (z / 2)), (int) (horizon + 16));
				}
				Index index;
				graphics.setFont(new Font(this.fontName, this.barFontStyle, this.barFontSize));
				// Black
				if (this.blackIndexMatrix != null) {
					graphics.setColor(Color.BLACK);
					for (int j = 0; j < this.blackIndexMatrix.size(); j++) {
						List<Index> indexList = this.blackIndexMatrix.get(j);
						for (int i = 0; i < indexList.size(); i++) {
							index = indexList.get(i);
							if (this.startCalendar.before(index.startCalendar)
									&& index.startCalendar.before(this.endCalendar)) {
								int x1 = (int) (this.plotWidth
										- ((xMax - index.getSeconds(this.startCalendar)) * xIncrement));
								Integer y1 = null;
								y1 = (int) (horizon - (this.scaleIndex(index.value)));
								if (y1 != null) {
									//graphics.drawLine(x1, y1, x1, y1);
//									graphics.fillOval(x1, y1, 4, 4);
									int s = 3;
									graphics.drawLine(x1-s, y1, x1+s, y1);
									graphics.drawLine(x1,y1-s,x1,y1+s);
								}
							}
						}
					}
				}
				// Color
				if (this.colorIndexMatrix != null) {
					for (int j = 0; j < this.colorIndexMatrix.size(); j++) {
						List<Index> indexList = this.colorIndexMatrix.get(j);
						graphics.setColor(chroma.getColor(j, 0, this.colorIndexMatrix.size()));
						for (int i = 0; i < indexList.size(); i++) {
							index = indexList.get(i);

							if (this.startCalendar.before(index.startCalendar)
									&& index.startCalendar.before(this.endCalendar)) {
								int x1 = (int) (this.plotWidth
										- ((xMax - index.getSeconds(this.startCalendar)) * xIncrement));
								Integer y1 = (int) (horizon - (this.scaleIndex(index.value)));
								if (y1 != null)
									graphics.drawLine(x1, y1, x1, y1);
							}
						}
					}
				}
			} else {
				g2d.setColor(Color.GREEN);
				String constant = this.yMax + "";
				int length = graphics.getFontMetrics().stringWidth(constant);
				g2d.drawString(constant, (int) (panelCenter.x - plotCenter.x + (length / 2)),
						(int) (panelCenter.y - plotCenter.y));// (int) (yLabelStartPosition),
				// (int) (((panelWidth / 2) - (plotWidth / 2) - (64))));
			}
			// Title And Labels
			graphics.setColor(Color.BLACK);
			int titleWidth = graphics.getFontMetrics().stringWidth(this.title);
			int titleStringHalf = titleWidth / 2;
			int titleStartPosition = verticalCenter - titleStringHalf;
			graphics.drawString(this.title, (int) (titleStartPosition), (int) (-16));
			graphics.setFont(new Font(this.fontName, this.xLabelFontStyle, this.xLabelFontSize));
			int xLabelWidth = graphics.getFontMetrics().stringWidth(this.xLabel);
			int xLabelHalf = xLabelWidth / 2;
			int xLabelStartPosition = verticalCenter - xLabelHalf;
//		graphics.drawString(this.xLabel, (int) (xLabelStartPosition), (int) ((horizon + 16)));
			AffineTransform at = new AffineTransform();
			at.setToRotation(Math.toRadians(-90));
			g2d.setTransform(at);
			// x becomes y position, y becomes x position
			graphics.setFont(new Font(this.fontName, this.yLabelFontStyle, this.yLabelFontSize));
			int yLabelWidth = (this.yLabel != null) ? graphics.getFontMetrics().stringWidth(this.yLabel) : 0;
			int yLabelStartPosition = horizontalCenter - yLabelWidth / 2;
			if (this.yLabel != null)
				g2d.drawString(this.yLabel, (int) (yLabelStartPosition),
						(int) (((panelWidth / 2) - (plotWidth / 2) - (64))));
			image = image.getScaledInstance((int) (this.panelWidth * this.scale), (int) (this.panelHeight * this.scale),
					Image.SCALE_DEFAULT);
			int x = (this.panelWidth - image.getWidth(null)) / 2;
			int y = (this.panelHeight - image.getHeight(null)) / 2;
//			g.drawImage(image, x, y, null);
		}
		return image;
	}
}
//for (double i = this.yDifference; i >= 0; i -= yInterval) {
//double increment = (yMax) - i;
//increment = Double.parseDouble(df.format(increment));
//if (increment == 0.0) {
//	graphics.drawLine(0, (int) (i * yIncrement), (int) this.plotWidth, (int) (i * yIncrement));
//}
//}
//for (long i = (xMax - xMin); i >= 0; i -= xInterval) {
//System.out.println(i);
//long increment = (xMin) + i;
//// increment = Double.parseDouble(df.format(increment));
////if (increment == 0) {
////	graphics.drawLine((int) (i * xIncrement), 0, (int) (i * xIncrement), (int) this.plotHeight);
////}
//}
//public void initYMax() {
//double max = Double.MIN_VALUE;
//if (this.blackIndexMatrix != null) {
//	for (List<Index> pointList : this.blackIndexMatrix) {
//		for (Index t : pointList) {
//			if (Math.abs(t.value) > max) {
////			System.out.println("initYMax() t.value="+t.value);
//				max = t.value;
//			}
//		}
//	}
//	this.yMax = max;
//}
//
//if (this.colorIndexMatrix != null) {
//	max = Double.MIN_VALUE;
//	for (List<Index> pointList : this.colorIndexMatrix) {
//		for (Index t : pointList) {
//			if (t.value > max) {
//				max = t.value;
//			}
//		}
//	}
//	this.yMax = (max > this.yMax) ? max : this.yMax;
//}
//if (print)
//	System.out.println("initYMax() this.yMax=" + this.yMax);
//}

//public void initYMin() {
//double min = Double.MAX_VALUE;
//if (this.blackIndexMatrix != null) {
//	for (List<Index> pointList : this.blackIndexMatrix) {
//		for (Index t : pointList) {
//			if (t.value < min) {
////			System.out.println(t.value);
//				min = t.value;
//			}
//		}
//	}
//
//	this.yMin = min;
//}
//if (this.colorIndexMatrix != null) {
//	for (List<Index> pointList : this.colorIndexMatrix) {
//		for (Index t : pointList) {
//			if (t.value < min) {
//				min = t.value;
//			}
//		}
//	}
//	this.yMin = (min < this.yMin) ? min : this.yMin;
//}
//if (print)
//	System.out.println("initYMax() this.yMin=" + this.yMin);
//}
