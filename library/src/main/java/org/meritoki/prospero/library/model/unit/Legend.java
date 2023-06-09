package org.meritoki.prospero.library.model.unit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Legend {

	static Logger logger = LoggerFactory.getLogger(Legend.class.getName());
	public final double DEFAULT_SCALE = 1;
	public int startX;
	public int width = 8;
	public int height = 500;
	public double scale;
	public String fontName = "SanSerif";
	public int fontStyle = Font.PLAIN;
	public int fontSize = 14;
	public Map<String, Color> keyMap = new TreeMap<>();

	public Legend(int startX) {
//		logger.info("Legend("+startX+")");
		this.scale = DEFAULT_SCALE;
		this.startX = startX;

	}

	public void setKeyMap(List<Event> eventList) {
		this.keyMap = new TreeMap<>();
		for (Event e : eventList) {
			Object color = e.attribute.get("color");
			if (color instanceof Color) {
				this.keyMap.put(e.id, (Color) color);
			}
		}
	}

	public void paint(Graphics graphics) {
		if (this.keyMap.size() > 0) {
			graphics.setFont(new Font(this.fontName, this.fontStyle, this.fontSize));
			int meterWidth = this.width;
			int meterHeight = this.height;
			int startX = this.startX + (int) (4 * this.scale);
			int startY = -(int) ((meterHeight / 2) * this.scale);
			double x = startX;
			double y = startY;
			double radius;
			double interval = meterHeight / this.keyMap.size();
			double sum = 0;
			for (Map.Entry<String, Color> entry : this.keyMap.entrySet()) {
				String key = entry.getKey();
				Color color = this.keyMap.get(key);
				radius = 8;
				double xRadius = (x * this.scale) - (radius / 2);
				double yRadius = ((y + sum) * this.scale) - (radius / 2);
				graphics.setColor(color);
				graphics.fillOval((int) xRadius, (int) yRadius, (int) radius, (int) radius);
				graphics.setColor(Color.BLACK);
				graphics.drawString(key, startX + meterWidth + 12, (int) (y + sum) + 4);
				sum += interval;
			}
		}
	}

}
