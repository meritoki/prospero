package org.meritoki.prospero.library.model.solar.moon;

import java.awt.Graphics;

import org.meritoki.prospero.library.model.node.Orbital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Moon extends Orbital {

	static Logger logger = LoggerFactory.getLogger(Moon.class.getName());
	
    public Moon(String name){
    	super(name);
    }
    
    @Override
	public void paint(Graphics g) throws Exception{
    	super.paint(g);
	}
}
//this.centroid = (Orbital)this.getRoot();
//logger.info(this.name+".paint(graphics) this.centroid="+this.centroid);
//Point point = this.projection.getPoint(this.space.getPoint());

//Vector3D position = this.getSpace(this.calendar).rectangular;
//double x = point.x * this.projection.scale;//position.getX() * scale;
//double y = point.y * this.projection.scale;//position.getZ() * scale;
//g.drawLine((int) x, (int) y, (int) (this.force.getX() * scale), (int) (this.force.getY() * scale));s
//g.setColor(this.color);
//double radius = 2;
//g.drawLine((int) x, (int) y, (int)this.centroid.position.getX(), (int)this.centroid.position.getY());
//x = x - (radius / 2);
//y = y - (radius / 2);
//g.fillOval((int) x, (int) y, (int) radius, (int) radius);
//g.setColor(Color.BLACK);
//g.drawString(this.name.substring(0, 1).toUpperCase() + this.name.substring(1) + "", (int) x, (int) y);
//List<Vector3D> vertexList = this.getOrbit();
//g.setColor(Color.white);
//radius = 5;
//for (int i = 1; i < vertexList.size(); i++) {
//	g.drawLine((int) (vertexList.get(i - 1).getX() * scale), (int) (vertexList.get(i - 1).getY() * scale),
//			(int) (vertexList.get(i).getX() * scale), (int) (vertexList.get(i).getY() * scale));
//}