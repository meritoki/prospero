package com.meritoki.library.prospero.model.solar.planet.neptune;

import java.awt.Color;
import java.util.Calendar;

import com.meritoki.library.prospero.model.solar.planet.Planet;
import com.meritoki.library.prospero.model.solar.star.sun.Sun;
import com.meritoki.library.prospero.model.solar.unit.Orbital;
import com.meritoki.library.prospero.model.solar.unit.Unit;

public class Neptune extends Planet {

    public Neptune(Sun sun) {
        super("Neptune");
        this.centroid = sun;
        this.mass = 1.024E26;
        this.radius = 24622;
        this.color = Color.BLUE;
        this.longitudeOfAscendingNode[0] = 131.6737;// o
        this.longitudeOfAscendingNode[1] = 0;// o
        this.inclination[0] = 1.7700;//i
        this.inclination[1] = -2.55E-7;//i
        this.argumentOfPeriapsis[0] = 272.8461;
        this.argumentOfPeriapsis[1] = -6.027E-6;
        this.semiMajorAxis[0] = 30.05826;// * Unit.ASTRONOMICAL;//a
        this.semiMajorAxis[1] = 3.313E-9;// * Unit.ASTRONOMICAL;
        this.eccentricity[0] = 0.008606;//e
        this.eccentricity[1] = 2.15E-9;// e
        this.meanAnomaly[0] = 260.2471;
        this.meanAnomaly[1] = 0.005995147;
        this.orbitalPeriod = 60182;
        this.angularVelocity =  1.083382527619075e-04;
    }
}