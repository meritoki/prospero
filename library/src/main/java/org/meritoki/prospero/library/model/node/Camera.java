package org.meritoki.prospero.library.model.node;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.cartography.Projection;
import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.unit.Cluster;
import org.meritoki.prospero.library.model.unit.Dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Camera {

	static Logger logger = LogManager.getLogger(Camera.class.getName());
	@JsonIgnore
	public Variable node;
	@JsonIgnore
	public Variable buffer;
	@JsonProperty
	public double azimuth;
	@JsonProperty
	public double elevation;
	@JsonProperty
	public double scale;
	@JsonProperty
	protected int xDelta;
	@JsonProperty
	protected int yDelta;
	@JsonIgnore
	public Map<String, Object> configuration = new TreeMap<>();
	@JsonIgnore
	public Image image;
	@JsonIgnore
	public Projection selected;

	public Camera(Variable node) {
		this.setNode(node);

	}
	
	@JsonIgnore
	public Image getImage() {
		return this.image;
	}
	
	@JsonIgnore
	public Image initImage(JPanel jpanel) throws Exception {
		Dimension dimension = new Dimension(jpanel.getWidth(), jpanel.getHeight());
		Image image = jpanel.createImage((int) dimension.width, (int) dimension.height);
		if(this.buffer != null) {
			this.buffer.dimension = dimension;
			Object object = this.configuration.get("cluster");
			if(object instanceof Cluster) {
				Cluster cluster = (Cluster)object;
				object = this.configuration.get("node");
				if(object instanceof Grid) {
					Grid grid = (Grid)object;
					grid.setCluster(cluster);
				}
			}
			image = this.buffer.getImage(image);
			this.setImage(image);
		}
		return image;
	}
	
	@JsonIgnore
	public Variable getNode() {
		return this.node;
	}
	
	@JsonIgnore
	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * 20230529 Not Functional, Has Defects
	 * Shows Earth AND Terra when Terra is selected
	 * @param node
	 */
	@JsonIgnore
	public void setNode(Variable node) {
		
//		if(this.node instanceof Terra) {
//			logger.info("setNode("+node+") this.node instanceof Terra");
//			Spheroid spheroid = ((Spheroid)this.node);
//			spheroid.setSelectable(false);
//			logger.debug("setNode("+node+") s.selectable="+spheroid.selectable);
//		}
		this.node = node;
		if(this.node instanceof Spheroid) {
			logger.info("setNode("+node+") this.node instanceof Spheroid");
			Spheroid spheroid = ((Spheroid)this.node);
			this.scale = spheroid.defaultScale;
			this.azimuth = 0;
			this.elevation = 0;
			spheroid.setScale(this.scale);
			spheroid.setAzimuth(this.azimuth);
			spheroid.setElevation(this.elevation);

		} 
		if(this.node instanceof Orbital) {
			logger.info("setNode("+node+") this.node instanceof Orbital");
			Orbital orbital = ((Orbital)this.node);
			orbital.setSelectable(true);
		}
	}

	@JsonIgnore
	public void mouseDragged(MouseEvent e) {
		logger.debug("mouseDragged(e)");
		int xDelta = e.getX();
		int yDelta = e.getY();
		this.azimuth -= xDelta - this.xDelta;
		this.elevation -= yDelta - this.yDelta;
//		logger.info("mouseDragged(e) azimuth="+azimuth);
//		logger.info("mouseDragged(e) elevation="+elevation);
		if (this.node instanceof Spheroid) {
			Spheroid s = (Spheroid) this.node;
			s.setAzimuth(this.azimuth);
			s.setElevation(this.elevation);
		}
		this.xDelta = xDelta;
		this.yDelta = yDelta;
	}

	public void keyPressed(KeyEvent e) {
		logger.debug("keyPressed(e)");
		if (this.node instanceof Spheroid) {
			Spheroid s = (Spheroid) this.node;
			if (e.isControlDown()) {
				switch (e.getKeyChar()) {
				case '+': {
					this.scale *= 2;
					s.setScale(this.scale);
					break;
				}
				case '-': {
					this.scale /= 2;
					s.setScale(this.scale);
					break;
				}
				case '1': {// bottom
					this.azimuth = 0;
					this.elevation = 0;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				case '2': {// top
					this.azimuth = 180;
					this.elevation = 0;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				case '3': {
					this.azimuth = 0;
					this.elevation = -90;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				case '4': {
					this.azimuth = 180;
					this.elevation = 90;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				case '5': {
					this.azimuth = 90;
					this.elevation = 0;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				case '6': {
					this.azimuth = -90;
					this.elevation = 0;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				case '7': {
					this.azimuth = 0;
					this.elevation = 55;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				case '8': {
					this.azimuth = 23;
					this.elevation = 35;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				case '9': {
					this.azimuth = -900;
					this.elevation = -200;
					s.setAzimuth(this.azimuth);
					s.setElevation(this.elevation);
					s.setScale(s.defaultScale);
					break;
				}
				}
			}
		}

	}
	
	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}
//this.scale = spheroid.getProjection().scale;
//this.azimuth = spheroid.getProjection().azimuth;
//this.elevation = spheroid.getProjection().elevation;
//this.node = node;
//if(this.node instanceof Spheroid) {
//	Spheroid spheroid = ((Spheroid)this.node);
////	this.scale = spheroid.defaultScale;
////	spheroid.setScale(this.scale);
//	this.scale = spheroid.getProjection().scale;
//	this.azimuth = spheroid.getProjection().azimuth;
//	this.elevation = spheroid.getProjection().elevation;
//}
//public Camera(Variable node, double scale, double azimuth, double elevation) {
//this.node = node;
//this.scale = scale;
//this.azimuth = azimuth;
//this.elevation = elevation;
//}
//this.configuration.put("scale",scale);
//this.configuration.put("azimuth",azimuth);
//this.configuration.put("elevation",elevation);
