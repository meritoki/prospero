package org.meritoki.prospero.library.model.solar.planet.mercury;

import java.awt.Color;
import java.util.Calendar;

import org.meritoki.prospero.library.model.solar.planet.Planet;
import org.meritoki.prospero.library.model.solar.star.sun.Sun;
import org.meritoki.prospero.library.model.solar.unit.Orbital;
import org.meritoki.prospero.library.model.solar.unit.Unit;

public class Mercury extends Planet {
    
    public Mercury(Sun sun) {
        super("Mercury");
        this.centroid = sun;
        this.radius = 2439.7;
        this.mass = 3.30104E23;
        this.color = Color.green;
        this.longitudeOfAscendingNode[0] = 48.3313;//o
        this.longitudeOfAscendingNode[1] = 3.24587E-5;//o
        this.inclination[0] = 7.0047;//i
        this.inclination[1] = 5.00E-8;//i
        this.argumentOfPeriapsis[0] = 29.1241;
        this.argumentOfPeriapsis[1] = 1.01444E-5;
        this.semiMajorAxis[0] = 0.38709893;// * Unit.ASTRONOMICAL;//a
        this.semiMajorAxis[1] = 0;
        this.eccentricity[0] = 0.205635;//e
        this.eccentricity[1] = 5.59E-10;//e
        this.meanAnomaly[0] = 168.6562;
        this.meanAnomaly[1] = 4.0923344368;
        this.orbitalPeriod = 87.9691;
        this.angularVelocity = 1.240013441242619e-06;
    }
}
