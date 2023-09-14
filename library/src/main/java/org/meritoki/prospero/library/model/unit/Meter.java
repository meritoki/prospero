/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.meritoki.prospero.library.model.node.color.Chroma;

/**
 * Assume Meter Implementation Works, For Now...
 * 
 * @author jorodriguez
 *
 */
public class Meter {

	public final double DEFAULT_SCALE = 1;
	public boolean inverted = false;
	public double scale;
	public double factor;
	public double max;
	public double min;
	public int startX;
	public int width = 32;
	public int height = 500;
	public String unit;
	public String fontName = "SanSerif";
	public int fontStyle = Font.PLAIN;
	public int fontSize = 14;
	public double increment;
	public Chroma chroma;
	public String format = "#.###E0";

	public Meter(double factor, int startX, double max, double min) {
		this.scale = DEFAULT_SCALE;
		this.factor = factor;
		this.max = max;
		this.min = min;
		this.startX = startX;
	}

	public Meter(double factor, int startX, double max, double min, String unit) {
		this.scale = DEFAULT_SCALE;
		this.factor = factor;
		this.max = max;
		this.min = min;
		this.startX = startX;
		this.unit = unit;
	}

	public Meter(double factor, int startX, double max, double min, String unit, double increment) {
		this.scale = DEFAULT_SCALE;
		this.factor = factor;
		this.max = max;
		this.min = min;
		this.startX = startX;
		this.unit = unit;
		this.increment = increment;
	}
	
	public Meter(double factor, int startX, double max, double min, String unit, double increment, String format) {
		this.scale = DEFAULT_SCALE;
		this.factor = factor;
		this.max = max;
		this.min = min;
		this.startX = startX;
		this.unit = unit;
		this.increment = increment;
		this.format = format;
	}

	public void setChroma(Chroma chroma) {
		this.chroma = chroma;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public int getPower(double number) {
//		System.out.println("getPower("+number+")");
		int power = 0;
		if (!Double.isInfinite(number) && number != 0) {
			String value = Double.toString(number);
			String[] array;

			if (!value.contains("E") && value.contains(".")) {
				array = value.split("\\.");
				power = 1;
				if (array[0].equals("0") || array[0].equals("-0")) {
					boolean flag = true;
					value = array[1];
					while (flag) {
						if (value.indexOf("0") == 0) {
							value = value.substring(1);
							power++;
						} else {
							flag = false;
							power = -power;
						}
					}
				}
			} else {
				array = value.split("E");
				power = Integer.parseInt(array[1]);
			}
		}
		return power;
	}

	public void paint(Graphics graphics) {
		graphics.setFont(new Font(this.fontName, this.fontStyle, this.fontSize));
		int meterWidth = this.width;
		int meterHeight = this.height;
		double difference = Math.abs(this.max - this.min);
		int divisor = (this.increment > 0) ? (int) ((difference) / this.increment) : 0;
		int interval = (divisor > 0) ? meterHeight / divisor : meterHeight / 10;
		int startX = this.startX + (int) (64 * this.scale);
		int startY = -(int) ((meterHeight / 2) * this.scale);
		double max = this.max;
		double min = this.min;
		DecimalFormat formatter = new DecimalFormat(format);
		int count = meterHeight / interval;
		double value = 0;
		for (int i = meterHeight; i >= 0; i--) {
			graphics.setColor(this.chroma.getColor(i * (difference / meterHeight), 0, difference));// 0, max
			graphics.drawLine(startX, startY + (int) ((meterHeight - i)), startX + (int) (meterWidth),
					startY + (int) (meterHeight - i));
			if (i % interval == 0) {
				graphics.setColor(Color.black);
				graphics.drawLine(startX + meterWidth, startY + (int) (meterHeight - i), startX + meterWidth + 4,
						startY + (int) (meterHeight - i));
				value = (divisor > 0) ? ((max - min) / divisor) * count : ((max - min) / 10) * count;
				value = (value == 0) ? Math.abs(value) : value;// Fix Negative Zeros
				value += min;// trying to apply min
				String s = formatter.format(value);
				s = s.replace("E0", "");
				graphics.drawString(s, startX + meterWidth + 12, startY + (int) (meterHeight - i) + 4);
				count--;
			}
		}
		graphics.setColor(Color.black);
		graphics.drawRect(startX, startY, (int) (meterWidth * this.scale), (int) (meterHeight * this.scale));
		if (this.unit != null) {
			int unitWidth = graphics.getFontMetrics().stringWidth(this.unit);
			graphics.drawString(this.unit, startX + (meterWidth / 2) - (unitWidth / 2), startY + meterHeight + 16);
		}
	}

}
//int maxPower = this.getPower(this.max);
//int minPower = this.getPower(this.min);
//double multiplier = 1;
//if (maxPower < 0) {//
//multiplier = Math.pow(10, Math.abs(maxPower));
//max = this.max * multiplier;
//// min = this.min * multiplier;
//} else {
//max = this.max;
//// min = this.min;
//}
//if (minPower < 0) {//
////		String powerString = "e" + minPower;
////		int powerWidth = graphics.getFontMetrics().stringWidth(powerString);
////		graphics.setColor(Color.black);
////		graphics.drawString(powerString, startX + (meterWidth / 2) - (powerWidth / 2), startY - 16);
//multiplier = Math.pow(10, Math.abs(minPower));
//// max = this.max * multiplier;
//min = this.min * multiplier;
//} else {
//// max = this.max;
//min = this.min;
//}
//(this.max > this.min)?(this.max -
// this.min):(this.min-this.max);
//		} else {
//			if (this.min > 0 || 0 > this.min) {
////				System.out.println("this.min="+min);
//				if (minPower < 0) {
//					multiplier = Math.pow(10, Math.abs(minPower));
//					min = this.min * multiplier;
//				} else {
//					min = this.min;
//				}
//				if (minPower < 0) {
//					String powerString = "e" + minPower;
//					int powerWidth = graphics.getFontMetrics().stringWidth(powerString);
//					graphics.drawString(powerString, startX + (meterWidth / 2) - (powerWidth / 2), startY - 16);
//				}
//			}
//			graphics.drawLine(startX + meterWidth, startY + meterHeight, startX + meterWidth + 4, startY + meterHeight);
//			graphics.drawString(formatter.format(min), startX + meterWidth + 5, startY + meterHeight + 4);
//		}
//if (this.max != 0 && maxPower < 0) {
//String powerString = "e" + maxPower;
//int powerWidth = graphics.getFontMetrics().stringWidth(powerString);
//graphics.drawString(powerString, startX + (meterWidth / 2) - (powerWidth / 2), startY - 16);
//}
//		if (this.max > 0 || 0 > this.max) {
//			System.out.println("this.max="+max);
//public Color getColor(double factor, double value, double size) {
//factor = 1;
//double power;
//if (inverted) {
//	power = (size - value) * factor / size;
//} else {
//	power = value * factor / size; // 0.9
//}
//double H = 0.2;// * 0.4; // Hue (note 0.4 = Green, see huge chart below)
//double S = power; // Saturation
//double B = 1-power; // Brightness
//Color color = Color.getHSBColor((float) H, (float) S, (float) B);
//return color;
//}

//public Color getColor(double factor, double value, double size) {
//double power;
//if (inverted) {
//	power = (size - value) * factor / size;
//} else {
//	power = value * factor / size; // 0.9
//}
//double H = power;// * 0.4; // Hue (note 0.4 = Green, see huge chart below)
//double S = 0.9; // Saturation
//double B = 0.9; // Brightness
//Color color = Color.getHSBColor((float) H, (float) S, (float) B);
//return color;
//}
//if (maxPower < 0) {
//multiplier = Math.pow(10, Math.abs(maxPower));
//max = this.max * multiplier;
//} else {
//max = this.max;
//}
//if (minPower < 0) {
//multiplier = Math.pow(10, Math.abs(minPower));
//min = this.min * multiplier;
//} else {
//min = this.min;
//}
