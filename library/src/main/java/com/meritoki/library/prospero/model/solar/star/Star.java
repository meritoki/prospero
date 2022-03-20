package com.meritoki.library.prospero.model.solar.star;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.solar.unit.Orbital;

public class Star extends Orbital {

	public Star(String name) {
		super(name);
//		this.name = "star";
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		this.initVariableMap();
		graphics.setColor(this.color);
		Vector3D position = this.space.rectangular;
		double x = position.getX() * this.scale;
		double y = position.getY() * this.scale;
		double radius = 0;
		graphics.fillOval((int) (x - (radius / 2)), (int) (y - (radius / 2)), (int) radius, (int) radius);
		graphics.setColor(Color.WHITE);
		graphics.drawString(this.name.substring(0, 1).toUpperCase() + this.name.substring(1) + "", (int) x, (int) y);
		List<Variable> nodeList = this.getChildren();
		for(Variable n: nodeList) {
			n.paint(graphics);
		}
	}
}
