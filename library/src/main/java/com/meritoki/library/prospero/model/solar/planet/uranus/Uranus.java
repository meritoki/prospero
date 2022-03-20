package com.meritoki.library.prospero.model.solar.planet.uranus;

import java.awt.Color;
import java.util.Calendar;

import com.meritoki.library.prospero.model.solar.planet.Planet;
import com.meritoki.library.prospero.model.solar.star.sun.Sun;
import com.meritoki.library.prospero.model.solar.unit.Orbital;
import com.meritoki.library.prospero.model.solar.unit.Unit;

public class Uranus extends Planet {

    public Uranus(Sun sun) {
        super("Uranus");
        this.centroid = sun;
        this.mass = 8.681E25;
        this.radius = 25559;
        this.color = Color.MAGENTA;
        this.longitudeOfAscendingNode[0] = 74.0005;// o
        this.longitudeOfAscendingNode[1] = 1.3978E-5;// o
        this.inclination[0] = 0.7733;// i//0.00005
        this.inclination[1] = 1.9E-8;// i//0.00005
        this.argumentOfPeriapsis[0] = 96.6612;
        this.argumentOfPeriapsis[1] = 3.0565E-5;
        this.semiMajorAxis[0] = 19.18171;// * Unit.ASTRONOMICAL;// a//1.00000011
        this.semiMajorAxis[1] = -1.55E-8;// * Unit.ASTRONOMICAL;// a//1.00000011
        this.eccentricity[0] = 0.047318;// e//0.01671022
        this.eccentricity[1] = 7.45E-9;// e
        this.meanAnomaly[0] = 142.5905;
        this.meanAnomaly[1] = 0.011725806;
        this.orbitalPeriod = 30688.5;
        this.angularVelocity = -1.041365902144588e-04;
    }
}
