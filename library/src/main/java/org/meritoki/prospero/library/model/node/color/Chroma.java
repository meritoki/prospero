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
package org.meritoki.prospero.library.model.node.color;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chroma {

	static Logger logger = LoggerFactory.getLogger(Chroma.class.getName());
	public double factor;
	public double hue = 1;
	public double saturation = 1;
	public double brightness = 1;
	public boolean hueFlag;
	public boolean saturationFlag;
	public boolean brightnessFlag;
	public boolean inverted;
	public Scheme scheme = null;
	public ColorMap colorMap = null;
	public boolean print = true;

	public Chroma() {
		this.initRainbow();
	}
	
	public void initRainbow() {
		this.factor = 0.8;
		this.hue = 0.8;
		this.hueFlag = true;
	}
	
	public void initGrayscale() {
		this.factor = 0.5;
		this.hue = 1.0;
		this.brightness = 1.0;
		this.saturation = 0.0;
		this.hueFlag = true;
		this.saturationFlag = false;
		this.brightnessFlag = true;
	}

	public Chroma(Scheme scheme) {
//		logger.info("Chroma("+scheme+")");
		this.scheme = scheme;
		if (this.scheme != null) {
			
			switch (this.scheme) {
			case VIRIDIS: {
				colorMap = ColorMap.getInstance();
				colorMap.setColorMap("viridis");
				break;
			}
			case INFERNO: {
				colorMap = ColorMap.getInstance();
				colorMap.setColorMap("inferno");
				break;
			}
			case MAGMA: {
				colorMap = ColorMap.getInstance();
				colorMap.setColorMap("magma");
				break;
			}
			case PLASMA: {
				colorMap = ColorMap.getInstance();
				colorMap.setColorMap("plasma");
				break;
			}
			case TURBO: {
				colorMap = ColorMap.getInstance();
				colorMap.setColorMap("turbo");
				break;
			}
			case RAINBOW: {
				colorMap = null;
				this.initRainbow();
				break;
			}
			case GRAYSCALE: {
				colorMap = null;
				this.initGrayscale();
				break;
			}
			default: {
				System.err.println("Chroma("+scheme+") invalid");
				colorMap = null;
				break;
			}
			}
		} else {
			this.initRainbow();
		}
	}

	/**
	 * This method is profoundly important and must work to generate legacy plots while also supporting new color maps
	 * some check must be performed to ensure value is between min and max;
	 * Cases:
	 * A) V:1, MIN:0, MAX:8 -
	 * B) V:1, MIN:2, MAX:8 - If V is less that min, then it must be drawn as white, it is not in the scale
	 * C) V:9, MIN:0, MAX:8 - If V is greater than max, then it must be drawn as the MAX value
	 * Three primary cases
	 * D) V:4: MIN:2: MAX:8
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public Color getColor(double value, double min, double max) {
//		logger.info("getColor("+value+", "+min+", "+max+")");
		Color color = Color.white;
		if(value < min) {
			color = Color.white;
		} else {
			if (value > max) {
				value = max;
			}
			if (colorMap != null) {
				try {
					color = colorMap.getMappedColor((float) max, (float) min, (float) value);
				} catch (Exception e) {
					System.err.println("getColor("+value+", "+min+", "+max);
					e.printStackTrace();
				}
			} else {
				double difference = (min > max) ? min - max : max - min;
				double power;
				power = ((value-min) * factor) / (difference);
				double hue = (hueFlag) ? power : this.hue;
				double saturation = (saturationFlag) ? power : this.saturation;
				double brightness = (brightnessFlag) ? 1 - power : this.brightness;
				color = Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
			}
		}
		return color;
	}
}
//logger.info("getColor")
//max = Math.abs(max);
//min = Math.abs(min);
//value = Math.abs(value);
//public Color getColor(double value, double min, double max) {
////System.out.println("getColor("+value+", "+min+", "+max);
////logger.info("getColor")
//Color color = Color.white;
//max = Math.abs(max);
//min = Math.abs(min);
//value = Math.abs(value);
//if (value > max) {
//value = max;
//}
//if (colorMap != null) {
//color = colorMap.getMappedColor((float) max, (float) min, (float) value);
//} else {
//double difference = (min > max) ? min - max : max - min;
//value = value - min;
//if (value >= 0) { // value >-0
//	double power;
//	if (inverted) {
//		power = ((difference - value) * factor) / (difference);
//	} else {
//		power = (value * factor) / (difference);
//	}
//	double hue = (hueFlag) ? power : this.hue;
//	double saturation = (saturationFlag) ? power : this.saturation;
//	double brightness = (brightnessFlag) ? 1 - power : this.brightness;
//	color = Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
//} else {
//	color = Color.WHITE;
//}
//}
//
//return color;
//}
//Implementation from Tile
//	public Color getColor(double factor, double max, double min) {
//		factor = 1;
//		Color color;
//		max = Math.abs(max);
//		min = Math.abs(min);
//		double difference = (min > max) ? min - max : max - min;
//		double value = this.value - min;
//		if (value >= 0) { // value >-0
//			double power;
//			if (inverted) {
//				power = ((difference - value) * factor) / (difference);
//			} else {
//				power = (value * factor) / (difference);
//			}
//			double hue = 0.2;
//			double saturation = power;
//			double brightness = 1-power;
//			color = Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
//		} else {
//			color = Color.WHITE;
//		}
//		return color;
//	}
//Implementation from Meter
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
