package com.meritoki.library.prospero.model.histogram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class Histogram {

	public final int DEFAULT_SCALE = 1;
	public int width;
	public int height;
	public double scale;
	public List<Bar> barList = new ArrayList<>();
	public String title;
	public String xLabel;
	public String yLabel;
	public double xLength = 800;
	public double yLength = 300;
	public double yMin = 0;
	public double yMax = 1000;
	public double yInterval = 100;
	public int padding = 10;
	public int horizon = 450;
	public double increment;
	public double step = 10;
	public String fontName = "SanSerif";
	public int titleFontStyle = Font.BOLD;
	public int barFontStyle = Font.PLAIN;
	public int yIncrementFontStyle = Font.PLAIN;
	public int xLabelFontStyle = Font.BOLD;
	public int yLabelFontStyle = Font.BOLD;
	public int titleFontSize = 18;
	public int barFontSize = 16;
	public int yIncrementFontSize = 16;
	public int xLabelFontSize = 16;
	public int yLabelFontSize = 16;
	public Color barColor = Color.GRAY;
	

	public Histogram() {
		this.scale = DEFAULT_SCALE;
//		this.test2();
	}

	public void test1() {
		this.barList.add(new Bar(65.5, "300"));
		this.setXLabel("Hello World");
		this.setYLabel("Hello World");
		this.setTitle("Testing long title **********************");
	}

	public void test2() {
		this.barList.add(new Bar(65.5, "100"));
		this.barList.add(new Bar(10, "200"));
		this.barList.add(new Bar(25, "300"));
		this.barList.add(new Bar(32.3, "400"));
		this.barList.add(new Bar(77.7, "500"));
		this.barList.add(new Bar(32.3, "600"));
		this.barList.add(new Bar(100, "700"));
//		this.setXLabel("Hello World");
//		this.setYLabel("Hello World");
//		this.setTitle("Testing long title **********************");
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setXLabel(String xLabel) {
		this.xLabel = xLabel;
	}

	public void setYLabel(String yLabel) {
		this.yLabel = yLabel;
	}

	public void addBar(Bar bar) {
		this.barList.add(bar);
	}

	public void initYMax() {
		double max = 0;
		for (Bar t : this.barList) {
			if (t.value > max) {
				max = t.value;
			}
		}
		int power = 1;
		double value = 0;
		boolean flag = true;
		double ten;

		do {
//			value = Math.pow(10, power);
//			if(value > max) {
//				flag = false;
//			}
//			if(flag) {
//				power++;
//			}
			ten = Math.pow(10, power);
			for (int i = 1; i <= 10; i++) {
				value = i * ten;
				if (value > max) {
					flag = false;
					break;
				}
			}
			if (flag) {
				power++;
			}
		} while (flag);

		this.yMax = value;
		this.yInterval = this.yMax / this.step;
	}

	public void setYMax(double yMax) {
//		System.out.println("setYMax("+yMax+")");
		this.yMax = yMax;
		this.yInterval = this.yMax / this.step;
//		System.out.println("setYMax("+yMax+") yInterval="+this.yInterval);
	}

	public void paint(Graphics g, Image image) {
		Graphics graphics = image.getGraphics();
		graphics = image.getGraphics();
		Graphics2D g2d = (Graphics2D) graphics;
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, this.width, this.height);
		graphics.translate((int) (this.width / 2.0 - this.xLength / 2), (int) (this.height / 2.0 - this.yLength / 2));
		graphics.setColor(Color.black);
		graphics.setFont(new Font(this.fontName, this.titleFontStyle, this.titleFontSize));
		int text = graphics.getFontMetrics().stringWidth(this.title);
		int w2 = (int) (this.xLength / 2);
		int t = text / 2;
		int s = w2 - t;
		graphics.drawString(this.title, (int) (s), (int) (-64));
		graphics.drawRect(0, 0, (int) (xLength), (int) (yLength));
		this.increment = this.yLength / (this.yMax - this.yMin);
		graphics.setFont(new Font(this.fontName, this.yIncrementFontStyle, this.yIncrementFontSize));
		for (int i = (int) yMax; i >= yMin; i -= yInterval) {
			String incrementLabel = (int) (yMax - i) + "";
			graphics.drawLine(0, (int) (i * increment), (int) (3), (int) (i * increment));
			int z = graphics.getFontMetrics().stringWidth(incrementLabel);
			graphics.drawString(incrementLabel, (int) ((-5 - z)), (int) (((i * increment) + 5)));
		}
		this.horizon = (int) this.yLength;
		int size = this.barList.size();
		int w = (size > 0) ? (int) this.xLength / size : (int) this.xLength;
		int x;
		int y;
		int h;
		Bar bar;
		graphics.setFont(new Font(this.fontName, this.barFontStyle, this.barFontSize));
		for (int i = 0; i < size; i++) {
			bar = this.barList.get(i);
			h = (bar.value <= this.yMax) ? (int) (bar.value * increment) : (int) (this.yMax * increment);
			y = horizon - h;
			x = i * w;
			graphics.setColor(barColor);
			graphics.fillRect((int) ((x + this.padding)), (int) (y), (int) ((w - (this.padding * 2))), (int) (h));
			graphics.setColor(Color.BLACK);
			graphics.drawRect((int) ((x + this.padding)), (int) (y), (int) ((w - (this.padding * 2))), (int) (h));
			text = graphics.getFontMetrics().stringWidth(bar.label);
			w2 = w / 2;
			t = text / 2;
			s = w2 - t;
			graphics.drawString(bar.label, (int) ((x + s)), (int) ((horizon + 24)));
		}
		graphics.setFont(new Font(this.fontName, this.xLabelFontStyle, this.xLabelFontSize));
		text = graphics.getFontMetrics().stringWidth(this.xLabel);
		w2 = (int) this.xLength / 2;
		t = text / 2;
		s = w2 - t;
		graphics.drawString(this.xLabel, (int) (s), (int) ((horizon + 64)));
		AffineTransform at = new AffineTransform();
		at.setToRotation(Math.toRadians(-90));
		g2d.setTransform(at);
		// x becomes y position, y becomes x position
		graphics.setFont(new Font(this.fontName, this.yLabelFontStyle, this.yLabelFontSize));
		text = graphics.getFontMetrics().stringWidth(this.yLabel);
		int h2 = (int) -(this.height / 2)+64;//y-axis shift
		s = h2 - text;
		g2d.drawString(this.yLabel, (int) (s), (int) (((width / 2) - (xLength / 2) - (64))));
		image = image.getScaledInstance((int) (this.width * this.scale), (int) (this.height * this.scale),
				Image.SCALE_DEFAULT);
		x = (this.width - image.getWidth(null)) / 2;
		y = (this.height - image.getHeight(null)) / 2;
		g.drawImage(image, x, y, null);
	}
}
