package org.meritoki.prospero.library.model.solar.planet.mars;

import java.awt.Color;

import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.solar.star.sun.Sun;
import org.meritoki.prospero.library.model.solar.unit.Unit;

public class Mars extends Planet {

    public Mars(Sun sun) {
        super("Mars");
        this.centroid = sun;
        this.radius = 3389.5;
        this.mass = 6.39e23;
        this.color = Color.RED;
        this.longitudeOfAscendingNode[0] = 49.5574;//o
        this.longitudeOfAscendingNode[1] = 2.11081E-5;//o
        this.inclination[0] = 1.8497;//i//0.00005
        this.inclination[1] = -1.78E-8;//i//0.00005
        this.argumentOfPeriapsis[0] = 286.5016;
        this.argumentOfPeriapsis[1] = 2.92961E-5;
        this.semiMajorAxis[0] =  1.523688;//* Unit.ASTRONOMICAL;//a//1.00000011
        this.semiMajorAxis[1]=0;
        this.eccentricity[0] = 0.093405;//e//0.01671022
        this.eccentricity[1] = 2.516E-9;//e
        this.meanAnomaly[0] = 18.6021;
        this.meanAnomaly[1] = 0.5240207766;
        this.orbitalPeriod = 686.980;
        this.angularVelocity = 7.088218127178316e-05;
    }
}