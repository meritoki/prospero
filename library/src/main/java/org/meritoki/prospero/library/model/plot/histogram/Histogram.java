package org.meritoki.prospero.library.model.plot.histogram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.unit.Bar;
import org.meritoki.prospero.library.model.unit.Point;
import org.meritoki.prospero.library.model.unit.Table;

public class Histogram extends Plot {

	public final int DEFAULT_SCALE = 1;
	public List<Bar> barList = new ArrayList<>();

	public Histogram() {
		this.scale = DEFAULT_SCALE;
	}
	
	public void initTableList() {
		this.tableList.add(new Table("("+this.title+")", Bar.getTableModel(this.barList)));
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

	}

	public void setPanelWidth(int width) {
		this.panelWidth = width;
	}

	public void setPanelHeight(int height) {
		this.panelHeight = height;
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

	@Override
	public Image getImage(Image image) {
		Graphics graphics = (image != null) ? image.getGraphics() : null;
		if (graphics != null) {
//		Graphics graphics = image.getGraphics();
		graphics = image.getGraphics();
		Graphics2D g2d = (Graphics2D) graphics;
		graphics.setColor(Color.white);
		this.plotWidth = this.panelWidth - 256;
		this.plotHeight = this.panelHeight - 128;
		graphics.fillRect(0, 0, this.panelWidth, this.panelHeight);
		Point panelCenter = new Point(this.panelWidth / 2.0, this.panelHeight / 2.0);
		Point plotCenter = new Point(this.plotWidth / 2.0, this.plotHeight / 2.0);
//		graphics.translate((int) (this.width / 2.0 - this.xLength / 2), (int) (this.height / 2.0 - this.yLength / 2));
		graphics.translate((int) (panelCenter.x - plotCenter.x), (int) (panelCenter.y - plotCenter.y));
		graphics.setColor(Color.black);
		graphics.setFont(new Font(this.fontName, this.titleFontStyle, this.titleFontSize));
		this.yDifference = (this.yMax - this.yMin);//this.getYDifference();
		this.xDifference = (this.xMax - this.xMin);
		this.yInterval = this.yDifference / this.step;// works
		this.xInterval = this.xDifference / this.xDifference;// 1000 * 3600 * 24;//
		this.yIncrement = this.plotHeight / this.yDifference;
		this.xIncrement = this.plotWidth / this.xDifference;
		this.horizon = (int) this.plotHeight;
		int text = graphics.getFontMetrics().stringWidth(this.title);
		int w2 = (int) (this.plotWidth / 2);
		int t = text / 2;
		int s1 = w2 - t;
		graphics.drawString(this.title, (int) (s1), (int) (-32));
		graphics.drawRect(0, 0, (int) (this.plotWidth), (int) (this.plotHeight));
//		this.increment = this.yLength / (this.yMax - this.yMin);
		graphics.setFont(new Font(this.fontName, this.yIncrementFontStyle, this.yIncrementFontSize));
//		for (int i = (int) yMax; i >= yMin; i -= yInterval) {
		for (int s = 0; s <= this.step; s++) {// double i = this.yDifference; i >= 0; i -= yInterval) {
			double i = this.yDifference - (s * this.yInterval);
			String incrementLabel = (int) (yMax - i) + "";
			graphics.drawLine(0, (int) (i * yIncrement), (int) (3), (int) (i * yIncrement));
			int z = graphics.getFontMetrics().stringWidth(incrementLabel);
			graphics.drawString(incrementLabel, (int) ((-5 - z)), (int) (((i * yIncrement) + 5)));
		}
//		this.horizon = (int) this.yLength;
		int size = this.barList.size();
		int w = (size > 0) ? (int) this.plotWidth / size : (int) this.plotWidth;
		int x;
		int y;
		int h;
		Bar bar;
		graphics.setFont(new Font(this.fontName, this.barFontStyle, this.barFontSize));
		for (int i = 0; i < size; i++) {
			bar = this.barList.get(i);
			h = (bar.value <= this.yMax) ? (int) (bar.value * yIncrement) : (int) (this.yMax * yIncrement);
			y = horizon - h;
			x = i * w;
			graphics.setColor(barColor);
			graphics.fillRect((int) ((x + this.padding)), (int) (y), (int) ((w - (this.padding * 2))), (int) (h));
			graphics.setColor(Color.BLACK);
			graphics.drawRect((int) ((x + this.padding)), (int) (y), (int) ((w - (this.padding * 2))), (int) (h));
			text = graphics.getFontMetrics().stringWidth(bar.label);
			w2 = w / 2;
			t = text / 2;
			s1 = w2 - t;
			graphics.drawString(bar.label, (int) ((x + s1)), (int) ((horizon + 24)));
		}
		graphics.setFont(new Font(this.fontName, this.xLabelFontStyle, this.xLabelFontSize));
		text = graphics.getFontMetrics().stringWidth(this.xLabel);
		w2 = (int) this.plotWidth / 2;
		t = text / 2;
		s1 = w2 - t;
		graphics.drawString(this.xLabel, (int) (s1), (int) ((horizon + 64)));
		AffineTransform at = new AffineTransform();
		at.setToRotation(Math.toRadians(-90));
		g2d.setTransform(at);
		// x becomes y position, y becomes x position
		graphics.setFont(new Font(this.fontName, this.yLabelFontStyle, this.yLabelFontSize));
		text = graphics.getFontMetrics().stringWidth(this.yLabel);
		int h2 = (int) -(this.panelHeight / 2)+64;//y-axis shift
		s1 = h2 - text;
		g2d.drawString(this.yLabel, (int) (s1), (int) (((panelWidth / 2) - (this.plotWidth / 2) - (64))));
		image = image.getScaledInstance((int) (this.panelWidth * this.scale), (int) (this.panelHeight * this.scale),
				Image.SCALE_DEFAULT);
		x = (this.panelWidth - image.getWidth(null)) / 2;
		y = (this.panelHeight - image.getHeight(null)) / 2;
//		g.drawImage(image, x, y, null);
		}
		return image;
	}
}
//this.setXLabel("Hello World");
//this.setYLabel("Hello World");
//this.setTitle("Testing long title **********************");
//public int width;
//public int height;
//public double scale;
//public String title;
//public String xLabel;
//public String yLabel;
//public double xLength = 800;
//public double yLength = 300;
//public double yMin = 0;
//public double yMax = 1000;
//public double yInterval = 100;
//public int padding = 10;
//public int horizon = 450;
//public double increment;s
//public double step = 10;
//public String fontName = "SanSerif";
//public int titleFontStyle = Font.BOLD;
//public int barFontStyle = Font.PLAIN;
//public int yIncrementFontStyle = Font.PLAIN;
//public int xLabelFontStyle = Font.BOLD;
//public int yLabelFontStyle = Font.BOLD;
//public int titleFontSize = 18;
//public int barFontSize = 16;
//public int yIncrementFontSize = 16;
//public int xLabelFontSize = 16;
//public int yLabelFontSize = 16;
//public Color barColor = Color.GRAY;
