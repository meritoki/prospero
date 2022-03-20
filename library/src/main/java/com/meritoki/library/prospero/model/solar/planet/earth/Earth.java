package com.meritoki.library.prospero.model.solar.planet.earth;

import java.awt.Color;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meritoki.library.prospero.model.node.Variable;
import com.meritoki.library.prospero.model.solar.moon.luna.Luna;
import com.meritoki.library.prospero.model.solar.planet.Planet;
import com.meritoki.library.prospero.model.solar.star.sun.Sun;
import com.meritoki.library.prospero.model.terra.Terra;
import com.meritoki.library.prospero.model.terra.cartography.Projection;

/**
 *
 * @author jorodriguez
 */
public class Earth extends Planet {
	
	public Terra terra = new Terra();

	public Earth(Sun sun) {
		super("Earth");
		this.centroid = sun;
		this.mass = 5.972e24;
		this.radius = 6378.137;
		this.color = Color.BLUE;
		this.longitudeOfAscendingNode[0] = 0;
		this.longitudeOfAscendingNode[1] = 0;
		this.inclination[0] = 0;
		this.inclination[1] = 0;
		this.argumentOfPeriapsis[0] = -282.9404;
		this.argumentOfPeriapsis[1] = -4.70935E-5;
		this.semiMajorAxis[0] = 1.000000;// * Unit.ASTRONOMICAL;
		this.semiMajorAxis[1] = 0;
		this.eccentricity[0] = -0.016709;
		this.eccentricity[1] = 1.151E-9;
		this.meanAnomaly[0] = 356.0470;
		this.meanAnomaly[1] = 0.9856002585;
		this.orbitalPeriod = 365.256363004;
		this.angularVelocity = 7.292115053925690e-05;
		this.obliquity = 23.439292;
		this.rotation = 23.9345;// hour
		this.addChild(new Luna(this));
		this.addChildren(this.terra.getChildren());
	}

	
	@Override
	@JsonIgnore
	public void setProjection(Projection projection) {
		this.terra.projection = projection;
		this.projection = projection;
		List<Variable> nodeList = this.getChildren();
		for(Variable n: nodeList) {
			n.setProjection(projection);
		}
	}
}
