/*
 * MIT License

Copyright (c) 2019 Juan Salamanca

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.meritoki.prospero.library.model.color;

import java.awt.Color;

/**
 * A utility class that returns categorical gradients of a given color.The
 * returned gradient ranges from the current color brightness to 80% brightness.
 * 
 * @author juan salamanca
 *
 */
public class ColorGradient {
	/**
	 * Gets a gradient of a given color with the specified number of steps. The
	 * returned gradient ranges from the current brightness to 80% brightness.
	 * 
	 * @param c
	 *            the java.lang.Color
	 * @param steps
	 *            the number of gradient steps
	 * @return the set of colors
	 */
	public static Color[] getColorGradient(Color c, int steps) {
		Color[] palette = new Color[steps];
		float[] hsbVals = new float[3];

		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbVals);

		float step = 0.8f / (float) steps;

		for (int i = 0; i < steps; i++) {
			palette[i] = Color.getHSBColor(hsbVals[0], hsbVals[1], 1 - (step * i));
		}
		return palette;
	}

	/**
	 * Gets a specific color from a color gradient
	 * 
	 * @param c
	 *            the java.lang.Color
	 * @param totalSteps
	 *            the number of gradient steps
	 * @param position
	 *            the color position in the gradient scale
	 * @return the integer value of this RGB color. If no color is available, it returns black
	 */
	public static int getStepIntValueFromGradient(Color c, int totalSteps, int position) {
		int rtn = 0;
		Color[] palette = getColorGradient(c, totalSteps);

		try {
			return palette[position].getRGB();
		} catch (Exception np) {
			return rtn;
		}
	}
	
	/**
	 * Gets a specific color from a color gradient
	 * 
	 * @param c
	 *            the java.lang.Color
	 * @param totalSteps
	 *            the number of gradient steps
	 * @param position
	 *            the color position in the gradient scale
	 * @return the integer value of this RGB color. If no color is available, it returns black
	 */
	public static Color getStepColorFromGradient(Color c, int totalSteps, int position) {
		Color[] palette = getColorGradient(c, totalSteps);

		try {
			return palette[position];
		} catch (Exception np) {
			return Color.BLACK;
		}
	}
}
