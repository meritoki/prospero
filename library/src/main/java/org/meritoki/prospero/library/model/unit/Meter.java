package org.meritoki.prospero.library.model.unit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.meritoki.prospero.library.model.color.Chroma;


/**
 * Assume Meter Implementation Works, For Now...
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
	
	public void setChroma(Chroma chroma) {
		this.chroma = chroma;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void paint(Graphics graphics) {
		graphics.setFont(new Font(this.fontName, this.fontStyle, this.fontSize));
		int meterWidth = this.width;
		int meterHeight = this.height;
		double difference = Math.abs(this.max - this.min);//(this.max > this.min)?(this.max - this.min):(this.min-this.max);
		int divisor = (this.increment > 0) ? (int) ((difference) / this.increment) : 0;
		int interval = (divisor > 0) ? meterHeight / divisor : meterHeight / 10;
		int startX = this.startX + (int) (64 * this.scale);
		int startY = -(int) ((meterHeight / 2) * this.scale);
		int maxPower = this.getPower(this.max);
		int minPower = this.getPower(this.min);
		double multiplier = 1;
		double max = 0;
		double min = 0;
		NumberFormat formatter = new DecimalFormat("#0.000");
		int count = meterHeight / interval;
		double value = 0;
//		if (this.max > 0 || 0 > this.max) {
//			System.out.println("this.max="+max);
			if (maxPower < 0) {//
				multiplier = Math.pow(10, Math.abs(maxPower));
				max = this.max * multiplier;
				min = this.min * multiplier;
			} else {
				max = this.max;
				min = this.min;
			}

			for (int i = meterHeight; i >= 0; i--) {
				graphics.setColor(this.chroma.getColor(i * (difference / meterHeight), 0, difference));//0, max
				graphics.drawLine(startX, startY + (int) ((meterHeight - i)), startX + (int) (meterWidth),
						startY + (int) (meterHeight - i));
				if (i % interval == 0) {
					graphics.setColor(Color.black);
					graphics.drawLine(startX + meterWidth, startY + (int) (meterHeight - i), startX + meterWidth + 4,
							startY + (int) (meterHeight - i));
					value = (divisor > 0) ? ((max - min) / divisor) * count : ((max - min) / 10) * count;
					value = (value == 0) ? Math.abs(value) : value;
					value += min;// trying to apply min
					graphics.drawString(formatter.format(value), startX + meterWidth + 12,
							startY + (int) (meterHeight - i) + 4);
					count--;
				}
			}
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
		graphics.setColor(Color.black);
		graphics.drawRect(startX, startY, (int) (meterWidth * this.scale), (int) (meterHeight * this.scale));
		if (this.max != 0 && maxPower < 0) {
			String powerString = "e" + maxPower;
			int powerWidth = graphics.getFontMetrics().stringWidth(powerString);
			graphics.drawString(powerString, startX + (meterWidth / 2) - (powerWidth / 2), startY - 16);
		}

		if (this.unit != null) {
			int unitWidth = graphics.getFontMetrics().stringWidth(this.unit);
			graphics.drawString(this.unit, startX + (meterWidth / 2) - (unitWidth / 2), startY + meterHeight + 16);
		}
	}

	public int getPower(double number) {
		int power = 0;
		if (!Double.isInfinite(number)) {
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
	
//	public Color getColor(double factor, double value, double size) {
//		factor = 1;
//		double power;
//		if (inverted) {
//			power = (size - value) * factor / size;
//		} else {
//			power = value * factor / size; // 0.9
//		}
//		double H = 0.2;// * 0.4; // Hue (note 0.4 = Green, see huge chart below)
//		double S = power; // Saturation
//		double B = 1-power; // Brightness
//		Color color = Color.getHSBColor((float) H, (float) S, (float) B);
//		return color;
//	}

//	public Color getColor(double factor, double value, double size) {
//		double power;
//		if (inverted) {
//			power = (size - value) * factor / size;
//		} else {
//			power = value * factor / size; // 0.9
//		}
//		double H = power;// * 0.4; // Hue (note 0.4 = Green, see huge chart below)
//		double S = 0.9; // Saturation
//		double B = 0.9; // Brightness
//		Color color = Color.getHSBColor((float) H, (float) S, (float) B);
//		return color;
//	}
}

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
