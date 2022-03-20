package com.meritoki.library.prospero.model.solar.planet.saturn;

import java.awt.Color;
import java.util.Calendar;

import com.meritoki.library.prospero.model.solar.planet.Planet;
import com.meritoki.library.prospero.model.solar.star.sun.Sun;
import com.meritoki.library.prospero.model.solar.unit.Orbital;
import com.meritoki.library.prospero.model.solar.unit.Unit;

public class Saturn extends Planet {

    public Saturn(Sun sun) {
        super("Saturn");
        this.centroid = sun;
        this.mass =5.683e26;//1.8986e27;// 5.683e26;//1.8986e27;//5.683e26;
        this.radius = 60268;
        this.color = Color.CYAN;
        this.longitudeOfAscendingNode[0] = 113.6634;// o
        this.longitudeOfAscendingNode[1] = 2.38980E-5;// o
        this.inclination[0] = 2.4886;// i//0.00005
        this.inclination[1] = -1.081E-7;// i//0.00005
        this.argumentOfPeriapsis[0] = 339.3939;
        this.argumentOfPeriapsis[1] = 2.97661E-5;
        this.semiMajorAxis[0] = 9.55475;// * Unit.ASTRONOMICAL;// a//1.00000011
        this.semiMajorAxis[1] = 0;
        this.eccentricity[0] = 0.055546;// e//0.01671022
        this.eccentricity[1] = -9.499E-9;// e
        this.meanAnomaly[0] = 316.9670;
        this.meanAnomaly[1] = 0.0334442282;
        this.orbitalPeriod = 10759.22;
        this.angularVelocity = 1.636246173744684e-04;
    }
}
