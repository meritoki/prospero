package org.meritoki.prospero.library.model.plot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.color.Chroma;
import org.meritoki.prospero.library.model.node.Grid;
import org.meritoki.prospero.library.model.unit.Table;

public class Plot { //extends JPanel {
	
	static Logger logger = LogManager.getLogger(Plot.class.getName());
	public final double DEFAULT_SCALE = 1;
	public int panelWidth;
	public int panelHeight;
	public double plotWidth = 512;
	public double plotHeight = 256;
	public double scale = DEFAULT_SCALE;
	public String title = "Plot";
	public String data = "Data";
	public String xLabel = "X";
	public String yLabel = "Y";
	public double yMin = 0;
	public double yMax = 1000;
	public long xMin = 0;
	public long xMax = 1000;
	public double yDifference;
	public long xDifference;
	public double yInterval = 100;
	public double xInterval = 100;
	public int padding = 10;
	public int horizon = 450;
	public double yIncrement;
	public double xIncrement;
	public double step = 10;
	public String fontName = "SanSerif";
	public int titleFontStyle = Font.BOLD;
	public int barFontStyle = Font.PLAIN;
	public int yIncrementFontStyle = Font.PLAIN;
	public int xLabelFontStyle = Font.BOLD;
	public int yLabelFontStyle = Font.BOLD;
	public int titleFontSize = 16;
	public int barFontSize = 16;
	public int yIncrementFontSize = 8;
	public int xLabelFontSize = 16;
	public int yLabelFontSize = 12;
	public Color barColor = Color.GRAY;
	public final boolean print = false;
	public final boolean detail = false;
	public Chroma chroma = new Chroma();
	public List<Table> tableList = new ArrayList<>();
	public Image image;
	
	public Plot() {
		this.scale = DEFAULT_SCALE;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public void setYMin(double min) {
		this.yMin = min;
	}
	
	public void setYMax(double max) {
		this.yMax = max;
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
	
	public void paint(Graphics g, Image image) {
		System.out.println("This called");
	}
	
	public Image getImage(Image image) {
		return null;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return this.image;
	}
	
//	@Override
//	public void paint(Graphics g) {
//		super.paint(g);
//		logger.info("paint("+(g != null)+")");
//		this.paint(g, this.createImage(this.getWidth(), 256));
//	}
}
