package org.meritoki.prospero.library.model.solar.star;

import java.awt.Graphics;

import org.meritoki.prospero.library.model.node.Grid;

public class Star extends Grid {

	public Star(String name) {
		super(name);
	}

	@Override
	public void paint(Graphics graphics) throws Exception {
		super.paint(graphics);
	}
}
//this.initVariableMap();
//graphics.setColor(this.color);
//Vector3D position = this.space.rectangular;
//double x = position.getX() * this.projection.scale;
//double y = position.getY() * this.projection.scale;
//double radius = 2;
//graphics.fillOval((int) (x - (radius / 2)), (int) (y - (radius / 2)), (int) radius, (int) radius);
//graphics.setColor(Color.BLACK);
//graphics.drawString(this.name.substring(0, 1).toUpperCase() + this.name.substring(1) + "", (int) x, (int) y);
//this.centroid = (Orbital)this.getRoot();
//super.paint(graphics);
//List<Variable> nodeList = this.getChildren();
//for(Variable n: nodeList) {
//	n.paint(graphics);
//}
